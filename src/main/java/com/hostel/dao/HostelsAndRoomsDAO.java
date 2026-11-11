package com.hostel.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.hostel.model.User;
import com.hostel.model.Hostel;
import com.hostel.model.Room;
import com.hostel.model.Booking;
import com.hostel.model.UserBooking;
import com.hostel.model.AvailableRoom;

import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDate;

import org.mindrot.jbcrypt.BCrypt;

public class HostelsAndRoomsDAO {
  
  static final String url = "jdbc:mysql://localhost:3306/jdbc_practice";
  static final String user = "root";
  static final String dbPassword = "John3:16";
  
  //Add hostel data to the database
  public static boolean insertHostel(String name, String location, String description) {
    String sql = "INSERT INTO hostels (name, location, description) VALUES (?, ?, ?)";
    try (
            Connection connection = DriverManager.getConnection(url, user, dbPassword);
            PreparedStatement statement = connection.prepareStatement(sql)
        ){
            statement.setString(1, name);
            statement.setString(2, location);
            statement.setString(3, description);

            int insertedRows = statement.executeUpdate();
            System.out.println(insertedRows + " hostel inserted.");
    
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
  //View hostels from the database
  public static List<Hostel> getLatestHostels() {
    List<Hostel> hostels = new ArrayList<>();
        String sql = "SELECT * FROM hostels ORDER BY id DESC LIMIT 15";
        try (
          Connection connection = DriverManager.getConnection(url, user, dbPassword);
          PreparedStatement statement = connection.prepareStatement(sql)
        ){
          ResultSet resultSet = statement.executeQuery();
          while (resultSet.next()) {
              Hostel hostel = new Hostel(resultSet.getInt("id"), resultSet.getString("name"), resultSet.getString("location"), resultSet.getString("description"));
              hostels.add(hostel);
          }

        }catch (SQLException e) {
          e.printStackTrace();
        }
        return hostels;
    }
  //Add room data to the database
  public static boolean insertRoom(int hostelId,String roomNumber,int totalBeds,String roomType, BigDecimal accommodationFee,String status) {

    String sql = "INSERT INTO rooms (hostel_id, room_number, total_beds, room_type, accommodation_fee, status) VALUES (?, ?, ?, ?, ?, ?)";

    try (
        Connection connection = DriverManager.getConnection(url, user, dbPassword);
        PreparedStatement statement = connection.prepareStatement(sql)
    ) {
        statement.setInt(1, hostelId);
        statement.setString(2, roomNumber);
        statement.setInt(3, totalBeds);
        statement.setString(4, roomType);
        statement.setBigDecimal(5, accommodationFee);
        statement.setString(6, status);

        return statement.executeUpdate() > 0;

    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
  }
  

  //View rooms from the database
  public static List<Room> getLatestRooms() {
    List<Room> rooms = new ArrayList<>();
    String sql = "SELECT r.*, h.name AS hostel_name FROM rooms r JOIN hostels h ON r.hostel_id = h.id ORDER BY r.id DESC LIMIT 15";
    try (
        Connection connection = DriverManager.getConnection(url, user, dbPassword);
        PreparedStatement statement = connection.prepareStatement(sql)
    ) {
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
           Room room = new Room(
                resultSet.getInt("id"),
                resultSet.getInt("hostel_id"),
                resultSet.getString("room_number"),
                resultSet.getInt("total_beds"),
                resultSet.getString("room_type"),
                resultSet.getBigDecimal("accommodation_fee"),
                resultSet.getString("status"),
                resultSet.getString("hostel_name")
                );

            rooms.add(room);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return rooms;
  }


                 
  //View rooms from the database
  public static List<Booking> getLatestBookings() {

    List<Booking> bookings = new ArrayList<>();

    String sql = """
        SELECT
            b.id,
            b.room_id,
            b.booked_under,
            b.email,
            b.phone,
            b.check_in_date,
            b.check_out_date,
            b.stay_months,
            b.occupants,
            b.accommodation_fee,
            b.booking_status,
            b.payment_status,
            r.room_number,
            h.name AS hostel_name
        FROM bookings b
        JOIN rooms r ON b.room_id = r.id
        JOIN hostels h ON r.hostel_id = h.id
        ORDER BY b.created_at DESC, b.id DESC
        LIMIT 5
        """;

    try  (
        Connection connection = DriverManager.getConnection(url, user, dbPassword);
        PreparedStatement statement = connection.prepareStatement(sql)
    ) {
         ResultSet resultSet = statement.executeQuery(); 
        while (resultSet.next()) {

            Booking booking = new Booking(
                resultSet.getInt("id"),
                resultSet.getInt("room_id"),
                resultSet.getString("booked_under"),
                resultSet.getDate("check_in_date").toLocalDate(),
                resultSet.getDate("check_out_date").toLocalDate(),
                resultSet.getInt("stay_months"),
                resultSet.getInt("occupants"),
                resultSet.getBigDecimal("accommodation_fee"),
                resultSet.getString("booking_status"),
                resultSet.getString("payment_status"),
                resultSet.getString("email"),
                resultSet.getString("phone"),
                resultSet.getString("room_number"),
                resultSet.getString("hostel_name")
            );

            bookings.add(booking);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return bookings;
  }

  //Users View rooms from the database
  // Users view their own recent bookings
    public static List<UserBooking> getLatestUserBookings(int userId) {
        List<UserBooking> userBookings = new ArrayList<>();

        String sql = """
        SELECT
            b.id,
            b.room_id,
            b.booked_under,
            b.email,
            b.phone,
            b.check_in_date,
            b.check_out_date,
            b.stay_months,
            b.occupants,
            b.accommodation_fee,
            b.booking_status,
            b.payment_status,
            r.room_number,
            h.name AS hostel_name
        FROM bookings b
        JOIN rooms r ON b.room_id = r.id
        JOIN hostels h ON r.hostel_id = h.id
        WHERE b.user_id = ?
        ORDER BY b.created_at DESC, b.id DESC
        LIMIT 5
        """;

        try (
            Connection connection = DriverManager.getConnection(url, user, dbPassword);
            PreparedStatement statement = connection.prepareStatement(sql)
    ) {
        statement.setInt(1, userId);
        try (ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
            UserBooking userBooking = new UserBooking(
                resultSet.getInt("id"),
                resultSet.getInt("room_id"),
                resultSet.getString("booked_under"),
                resultSet.getDate("check_in_date").toLocalDate(),
                resultSet.getDate("check_out_date").toLocalDate(),
                resultSet.getInt("stay_months"),
                resultSet.getInt("occupants"),
                resultSet.getBigDecimal("accommodation_fee"),
                resultSet.getString("booking_status"),
                resultSet.getString("payment_status"),
                resultSet.getString("email"),
                resultSet.getString("phone"),
                resultSet.getString("room_number"),
                resultSet.getString("hostel_name")
            );
                userBookings.add(userBooking);
            }
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return userBookings;
    }

    public static List<AvailableRoom> getAvailableBookingRooms(
        LocalDate checkInDate,
        LocalDate checkOutDate) {

    List<AvailableRoom> availableRooms = new ArrayList<>();

    String sql = """
        SELECT
            r.id,
            r.hostel_id,
            r.room_number,
            r.total_beds,
            r.room_type,
            r.accommodation_fee,
            r.status,
            h.name AS hostel_name,

            COALESCE(SUM(b.occupants), 0) AS reserved_beds,

            r.total_beds - COALESCE(SUM(b.occupants), 0)
                AS remaining_beds

        FROM rooms r

        JOIN hostels h
            ON r.hostel_id = h.id

        LEFT JOIN bookings b
            ON b.room_id = r.id
            AND b.booking_status IN ('PENDING', 'CONFIRMED')
            AND b.check_in_date < ?
            AND b.check_out_date > ?

        WHERE r.status = 'AVAILABLE'

        GROUP BY
            r.id,
            r.hostel_id,
            r.room_number,
            r.total_beds,
            r.room_type,
            r.accommodation_fee,
            r.status,
            h.name

        HAVING remaining_beds > 0

        ORDER BY r.id DESC
        """;

    try (Connection connection = DriverManager.getConnection(url, user, dbPassword);
         PreparedStatement pstmt = connection.prepareStatement(sql)) {

        // Checkout date is the first placeholder.
        pstmt.setDate(1, java.sql.Date.valueOf(checkOutDate));

        // Check-in date is the second placeholder.
        pstmt.setDate(2, java.sql.Date.valueOf(checkInDate));

        try (ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {

                AvailableRoom room = new AvailableRoom(
                    rs.getInt("id"),
                    rs.getInt("hostel_id"),
                    rs.getString("room_number"),
                    rs.getInt("total_beds"),
                    rs.getString("room_type"),
                    rs.getBigDecimal("accommodation_fee"),
                    rs.getString("status"),
                    rs.getString("hostel_name"),
                    rs.getInt("reserved_beds"),
                    rs.getInt("remaining_beds")
                );

                availableRooms.add(room);
            }
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return availableRooms;
}
}