
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
import com.hostel.model.Hostel;
import java.time.LocalDate;
import java.util.List;
import com.hostel.model.AvailableRoom;
import com.hostel.dao.HostelsAndRoomsDAO;

import com.hostel.dao.HostelsAndRoomsDAO;
import com.hostel.model.Booking;
import com.hostel.model.UserBooking;
import java.util.List;

public class BookingService {

    // Use the same database and credentials as your existing DAO.
    private static final String DB_URL = "jdbc:mysql://localhost:3306/jdbc_practice";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "John3:16";

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                DB_URL, DB_USER, DB_PASSWORD
        );
    }

    
    
    public enum BookingResult {
    CREATED,
    HOSTEL_UNAVAILABLE,
    INVALID_HOSTEL
}

public static BookingResult createBooking(
    int userId,
    String role,
    int hostelId,
    LocalDate checkInDate,
    int stayMonths,
    int occupants,
    String bookedUnder,
    String email,
    String phone
) throws SQLException {

    // 1. Validate the user session.
    if (userId <= 0) {
        throw new SecurityException("Login is required to apply.");
    }

    if (role == null || role.isBlank()) {
        throw new SecurityException("Unable to verify the user's role.");
    }

    // 2. Validate submitted application details.
    if (hostelId <= 0
            || checkInDate == null
            || stayMonths < 1
            || stayMonths > 12
            || occupants < 1
            || isBlank(bookedUnder)
            || isBlank(email)
            || isBlank(phone)) {

        throw new IllegalArgumentException(
            "One or more application details are invalid."
        );
    }

    if (checkInDate.isBefore(LocalDate.now())) {
        throw new IllegalArgumentException(
            "Check-in date cannot be in the past."
        );
    }

    // 3. Calculate checkout date.
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

    // 4. Start database transaction.
    try (Connection conn = getConnection()) {

        conn.setAutoCommit(false);

        try {

            int applicationCapacity;
            BigDecimal monthlyFee;

            /*
             * 5. Retrieve and lock the selected hostel.
             * The lock prevents simultaneous applications from
             * exceeding the hostel's application capacity.
             */
            String hostelSql = """
                SELECT
                    application_capacity,
                    accommodation_fee
                FROM hostels
                WHERE id = ?
                FOR UPDATE
                """;

            try (PreparedStatement ps =
                    conn.prepareStatement(hostelSql)) {

                ps.setInt(1, hostelId);

                try (ResultSet rs = ps.executeQuery()) {

                    if (!rs.next()) {
                        conn.rollback();
                        return BookingResult.INVALID_HOSTEL;
                    }

                    applicationCapacity =
                        rs.getInt("application_capacity");

                    monthlyFee =
                        rs.getBigDecimal("accommodation_fee");
                }
            }

            // 6. Validate hostel capacity and accommodation fee.
            if (applicationCapacity <= 0
                    || monthlyFee == null
                    || monthlyFee.signum() < 0) {

                conn.rollback();
                return BookingResult.HOSTEL_UNAVAILABLE;
            }

            /*
             * 7. Count applications already consuming capacity.
             *
             * PENDING and CONFIRMED consume application spaces.
             * CANCELLED and COMPLETED do not.
             */
            String capacitySql = """
                SELECT COUNT(*) AS occupied_applications
                FROM bookings
                WHERE hostel_id = ?
                AND booking_status IN ('PENDING', 'CONFIRMED')
                """;

            int occupiedApplications;

            try (PreparedStatement ps =
                    conn.prepareStatement(capacitySql)) {

                ps.setInt(1, hostelId);

                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();

                    occupiedApplications =
                        rs.getInt("occupied_applications");
                }
            }

            // 8. Check whether an application space remains.
            int availableApplications =
                applicationCapacity - occupiedApplications;

            if (availableApplications <= 0) {
                conn.rollback();
                return BookingResult.HOSTEL_UNAVAILABLE;
            }

            // 9. Calculate the total accommodation fee.
            BigDecimal totalFee =
                monthlyFee.multiply(
                    BigDecimal.valueOf(stayMonths)
                );

            /*
             * 10. Insert the application.
             * Payment has NOT been made at this stage.
             */
            String insertSql = """
                INSERT INTO bookings (
                    user_id,
                    hostel_id,
                    booked_under,
                    email,
                    phone,
                    check_in_date,
                    check_out_date,
                    stay_months,
                    occupants,
                    accommodation_fee,
                    booking_status,
                    payment_status
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'PENDING', 'PENDING')
                """;

            try (PreparedStatement ps =
                    conn.prepareStatement(insertSql)) {

                ps.setInt(1, userId);
                ps.setInt(2, hostelId);
                ps.setString(3, bookedUnder.trim());
                ps.setString(4, email.trim());
                ps.setString(5, phone.trim());
                ps.setDate(6, Date.valueOf(checkInDate));
                ps.setDate(7, Date.valueOf(checkOutDate));
                ps.setInt(8, stayMonths);
                ps.setInt(9, occupants);
                ps.setBigDecimal(10, totalFee);

                int rowsInserted = ps.executeUpdate();

                if (rowsInserted != 1) {
                    conn.rollback();

                    throw new SQLException(
                        "The application was not inserted."
                    );
                }
            }

            // 11. Commit only after successful insertion.
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
    
    public static List<Booking> getLatestBookings() {
        return HostelsAndRoomsDAO.getLatestBookings();
    }
    
     
    // Users view their own recent bookings
    public static List<UserBooking> getLatestUserBookings(int userId) {
        return HostelsAndRoomsDAO.getLatestUserBookings(userId);
    }


    public static List<AvailableRoom> getAvailableBookingRooms(LocalDate checkInDate,LocalDate checkOutDate) {
    return HostelsAndRoomsDAO.getAvailableBookingRooms(checkInDate,checkOutDate);
    }

    public static List<Hostel> getAvailableApplications() {
        return HostelsAndRoomsDAO.getAvailableApplications();
    }

}
