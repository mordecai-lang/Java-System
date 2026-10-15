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

public class AuthenticationDAO {

    static final String url = "jdbc:mysql://localhost:3306/jdbc_practice";
    static final String user = "root";
    static final String dbPassword = "John3:16";


    public static String checkLogin(String email, String password) {

        String sql = "SELECT password, role FROM authentication WHERE email = ?";

        try (
            Connection connection = DriverManager.getConnection(url, user, dbPassword);
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            
            statement.setString(1, email);
            ResultSet result = statement.executeQuery();
            
            if (result.next()) {
                String storedHash = result.getString("password");
                String role = result.getString("role");
                if (BCrypt.checkpw(password, storedHash)) {
                    return role;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    public static boolean insertRegisterData(String name,String email,String password) {

        String sql = "INSERT INTO authentication (name, email, password) VALUES (?, ?, ?)";
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));

        try (
            Connection connection = DriverManager.getConnection(url, user, dbPassword);
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, name);
            statement.setString(2, email);
            statement.setString(3, hashedPassword);

            int insertedRows = statement.executeUpdate();
            System.out.println(insertedRows + " registration inserted.");
        
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean insertUser(String name, String email, String password, String role) {
        String sql = "INSERT INTO authentication (name, email, password, role) VALUES (?, ?, ?, ?)";
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));

        try (
            Connection connection = DriverManager.getConnection(url, user, dbPassword);
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, name);
            statement.setString(2, email);
            statement.setString(3, hashedPassword);
            statement.setString(4, role);

            int insertedRows = statement.executeUpdate();
            System.out.println(insertedRows + " user inserted.");
        
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean deleteUser(String email) {
        String sql = "DELETE FROM authentication WHERE email = ?";
    
        try (
            Connection connection = DriverManager.getConnection(url, user, dbPassword);
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, email);
            System.out.println("Email being sent to DELETE: [" + email + "]");
            int deletedRows = statement.executeUpdate();
            System.out.println(deletedRows + " user deleted.");
            if (deletedRows > 0) {
                return true;
            }
            return false;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<User> getLatestUsers() {
        
        String sql = "SELECT id, name, email, role FROM authentication ORDER BY id DESC LIMIT 10";
        List<User> users = new ArrayList<>();
        try (
            Connection connection = DriverManager.getConnection(url, user, dbPassword);
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                User user = new User( result.getInt("id"),result.getString("name"),result.getString("email"),result.getString("role"));
                users.add(user);
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
}