package com.hostel.service;
import com.hostel.dao.AuthenticationDAO;
import com.hostel.model.User;
import java.util.List;
public class AuthenticationService {

    private static boolean authenticated = false;
    private static String currentRole = "";

    public static boolean login(String email, String password) {
        String role = AuthenticationDAO.checkLogin(email, password);
        if (role != null) {
            
            authenticated = true;
            currentRole = role;
            return true;
        }
        return false;
    }


    public static boolean register(String name,String email,String password) {
        return AuthenticationDAO.insertRegisterData(name,email,password);
    }
    public static boolean isAuthenticated() {
        return authenticated;
    }
    public static String getCurrentRole() {
        return currentRole;
    }
    public static List<User> getLatestUsers() {
        return AuthenticationDAO.getLatestUsers();
    }
}

