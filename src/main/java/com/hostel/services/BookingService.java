
package com.hostel.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.Statement;
import java.time.LocalDate;

public class BookingService {

    // Use the same database and credentials as your existing DAO.
    private static final String DB_URL = "jdbc:mysql://localhost:3306/jdbc_practice";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "John3:16";

    public enum BookingResult {
        CREATED,
        ROOM_UNAVAILABLE,
        INVALID_ROOM
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                DB_URL, DB_USER, DB_PASSWORD
        );
    }

    public static BookingResult createBooking(
            int roomId,
            LocalDate checkInDate,
            int stayMonths,
            int occupants,
            String bookedUnder,
            String email,
            String phone
    ) throws SQLException {

        // 1. Authentication checks.
        if (!AuthenticationService.isAuthenticated()) {
            throw new SecurityException("Login is required to book a room.");
        }

        String role = AuthenticationService.getCurrentRole();

        if (role == null || role.isBlank()) {
            throw new SecurityException("Unable to verify the user's role.");
        }

        // If only ordinary users may book, uncomment and adapt this check
        // to match the exact role names stored by your application.
        //
        // if (!"USER".equalsIgnoreCase(role)) {
        //     throw new SecurityException("Only users can book rooms.");
        // }

        // 2. Validate values again in the service.
        // Never rely only on validation in JavaScript or the handler.
        if (roomId <= 0
                || checkInDate == null
                || stayMonths < 1
                || stayMonths > 12
                || occupants < 1
                || isBlank(bookedUnder)
                || isBlank(email)
                || isBlank(phone)) {
            throw new IllegalArgumentException(
                    "One or more booking details are invalid."
            );
        }

        if (checkInDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Check-in date cannot be in the past."
            );
        }

        final LocalDate checkOutDate;

        try {
            checkOutDate = checkInDate.plusMonths(stayMonths);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException(
                    "Unable to calculate the checkout date.", e
            );
        }

        if (!checkInDate.isBefore(checkOutDate)) {
            throw new IllegalArgumentException(
                    "Checkout date must be after check-in date."
            );
        }

        // 3. Start a transaction.
        try (Connection conn = getConnection()) {

            conn.setAutoCommit(false);

            try {
                int totalBeds;
                BigDecimal monthlyFee;
                String roomStatus;

                /*
                 * Lock this room's row until the transaction finishes.
                 * Other booking transactions for the same room must wait.
                 */
                String roomSql = "SELECT total_beds, accommodation_fee, status " +
                                 "FROM rooms " +
                                 "WHERE id = ? " +
                                 "FOR UPDATE";
                try (PreparedStatement ps =
                             conn.prepareStatement(roomSql)) {

                    ps.setInt(1, roomId);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next()) {
                            conn.rollback();
                            return BookingResult.INVALID_ROOM;
                        }

                        totalBeds = rs.getInt("total_beds");
                        monthlyFee = rs.getBigDecimal("accommodation_fee");
                        roomStatus = rs.getString("status");
                    }
                }

                // 4. The room must be operationally available.
                if (!"AVAILABLE".equalsIgnoreCase(roomStatus)
                        || totalBeds <= 0
                        || monthlyFee == null
                        || monthlyFee.signum() < 0) {

                    conn.rollback();
                    return BookingResult.ROOM_UNAVAILABLE;
                }

                /*
                 * 5. Count beds reserved by overlapping bookings.
                 *
                 * Two stays overlap when:
                 * existing check-in < proposed checkout
                 * AND existing checkout > proposed check-in.
                 *
                 * PENDING bookings also reserve beds in this design.
                 */
                String capacitySql ="SELECT COALESCE(SUM(occupants), 0) AS reserved_beds " +
                                    "FROM bookings " +
                                     "WHERE room_id = ? " +
                                    "AND booking_status IN ('PENDING', 'CONFIRMED') " +
                                    "AND check_in_date < ? " +
                                     "AND check_out_date > ?";

                int reservedBeds;

                try (PreparedStatement ps =
                             conn.prepareStatement(capacitySql)) {

                    ps.setInt(1, roomId);
                    ps.setDate(2, Date.valueOf(checkOutDate));
                    ps.setDate(3, Date.valueOf(checkInDate));

                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        reservedBeds = rs.getInt("reserved_beds");
                    }
                }

                int availableBeds = totalBeds - reservedBeds;

                // 6. Reject the booking if there are not enough beds.
                if (availableBeds < occupants) {
                    conn.rollback();
                    return BookingResult.ROOM_UNAVAILABLE;
                }

                // 7. Calculate the total from the database's monthly fee.
                BigDecimal totalFee =
                        monthlyFee.multiply(BigDecimal.valueOf(stayMonths));

                /*
                 * 8. Insert the booking.
                 * Payment has NOT been made at this stage.
                 */
                String insertSql ="INSERT INTO bookings (" +
                               "room_id, booked_under, email, phone, " +
                               "check_in_date, check_out_date, stay_months, " +
                               "occupants, accommodation_fee, booking_status, payment_status" +
                                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 'PENDING', 'PENDING')";

                try (PreparedStatement ps =
                             conn.prepareStatement(
                                     insertSql,
                                     Statement.RETURN_GENERATED_KEYS
                             )) {

                    ps.setInt(1, roomId);
                    ps.setString(2, bookedUnder.trim());
                    ps.setString(3, email.trim());
                    ps.setString(4, phone.trim());
                    ps.setDate(5, Date.valueOf(checkInDate));
                    ps.setDate(6, Date.valueOf(checkOutDate));
                    ps.setInt(7, stayMonths);
                    ps.setInt(8, occupants);
                    ps.setBigDecimal(9, totalFee);

                    int rowsInserted = ps.executeUpdate();

                    if (rowsInserted != 1) {
                        conn.rollback();
                        throw new SQLException(
                                "The booking was not inserted."
                        );
                    }
                }

                // 9. Commit only after the insert succeeds.
                conn.commit();

                return BookingResult.CREATED;

            } catch (SQLException | RuntimeException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
