
package com.hostel.service;

import java.math.BigDecimal;
import java.util.List;

import com.hostel.model.Hostel;
import com.hostel.model.Room;
import com.hostel.dao.AuthenticationDAO;
import com.hostel.dao.HostelsAndRoomsDAO;

public class AdminService {

    public static boolean adduser(
            String name, String email, String password, String role,
            String currentRole) {

        if (!"ADMIN".equals(currentRole)) {
            return false;
        }

        return AuthenticationDAO.insertUser(name, email, password, role);
    }

    public static boolean removeuser(String email, String currentRole) {

        if (!"ADMIN".equals(currentRole)) {
            return false;
        }

        return AuthenticationDAO.deleteUser(email);
    }

    public static boolean addHostel(
            String name, String location, String description,
            String currentRole) {

        if (!"ADMIN".equals(currentRole)) {
            return false;
        }

        return HostelsAndRoomsDAO.insertHostel(name, location, description);
    }

    public static List<Hostel> getLatestHostels() {
        return HostelsAndRoomsDAO.getLatestHostels();
    }

    public static boolean addRoom(
            int hostelId, String roomNumber, int totalBeds,
            String roomType, BigDecimal accommodationFee,
            String status, String currentRole) {

        if (!"ADMIN".equals(currentRole)) {
            return false;
        }

        return HostelsAndRoomsDAO.insertRoom(
                hostelId, roomNumber, totalBeds,
                roomType, accommodationFee, status);
    }

    public static List<Room> getLatestRooms() {
        return HostelsAndRoomsDAO.getLatestRooms();
    }
}
