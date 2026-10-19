package com.hostel.service;
import java.math.BigDecimal;
import com.hostel.model.Hostel;
import java.sql.Connection;
import java.util.List;
import com.hostel.model.Hostel;
import com.hostel.model.Room;
import com.hostel.dao.AuthenticationDAO;
import com.hostel.dao.HostelsAndRoomsDAO;

public class AdminService {

    private static boolean authenticated = false;
    private static String currentRole = "";

    public static boolean adduser(String name, String email, String password, String role) {
        // Check authentication
        if (!AuthenticationService.isAuthenticated()) {
            return false;
        }
        // Check authorization
        if (!AuthenticationService.getCurrentRole().equals("ADMIN")) {
            return false;
        }
        return AuthenticationDAO.insertUser(name,email, password, role);
       
    }

    public static boolean removeuser(String email) {
        // Check authentication
        if (!AuthenticationService.isAuthenticated()) {
            return false;
        }
        // Check authorization
        if (!AuthenticationService.getCurrentRole().equals("ADMIN")) {
            return false;
        }
        return AuthenticationDAO.deleteUser(email);
       
    }

    public static boolean addHostel(String name, String location, String description) {
        // Check authentication
        if (!AuthenticationService.isAuthenticated()) {
            return false;
        }
        // Check authorization
        if (!AuthenticationService.getCurrentRole().equals("ADMIN")) {
            return false;
        }
        return HostelsAndRoomsDAO.insertHostel(name, location, description);
    }
    
    public static List<Hostel> getLatestHostels() {
        return HostelsAndRoomsDAO.getLatestHostels();
    }

    public static boolean addRoom(int hostelId, String roomNumber, int totalBeds, String roomType, BigDecimal accommodationFee, String status) {
        // Check authentication
        if (!AuthenticationService.isAuthenticated()) {
            return false;
        }
        // Check authorization
        if (!AuthenticationService.getCurrentRole().equals("ADMIN")) {
            return false;
        }
        return HostelsAndRoomsDAO.insertRoom(hostelId, roomNumber, totalBeds, roomType, accommodationFee, status);
    }

    public static List<Room> getLatestRooms() {
        return HostelsAndRoomsDAO.getLatestRooms();
    }
}