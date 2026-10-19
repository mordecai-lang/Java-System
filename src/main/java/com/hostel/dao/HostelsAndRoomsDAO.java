package com.hostel.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.hostel.model.User;
import com.hostel.model.Hostel;
import com.hostel.model.Room;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

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
}