package com.hostel.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.hostel.model.User;
import java.util.ArrayList;
import java.util.List;

import org.mindrot.jbcrypt.BCrypt;

public class HostelsAndRoomsDAO {
  
  static final String url = "jdbc:mysql://localhost:3306/jdbc_practice";
  static final String user = "root";
  static final String dbPassword = "John3:16";
  
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

  public static List<Hostel> getLatestHostels() {
    List<Hostel> hostels = new ArrayList<>();
      String sql = "SELECT * FROM hostels ORDER BY id DESC LIMIT 10";
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

} 