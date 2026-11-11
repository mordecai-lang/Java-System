package com.hostel.service;
import com.hostel.LoginResult;
import com.hostel.AuthenticationResult;
import com.hostel.SessionManager;
import com.hostel.dao.AuthenticationDAO;
import com.hostel.model.User;

import java.util.List;

public class AuthenticationService {

    public static LoginResult login(String email, String password) {
        AuthenticationResult result =
            AuthenticationDAO.checkLogin(email, password);

        if (result == null) {
            return null;
        }

        String sessionId = SessionManager.createSession(
            result.getUserId(),
            result.getRole()
        );

        return new LoginResult(sessionId, result.getRole());
    }


    public static boolean register(
            String name, String email, String password) {

        return AuthenticationDAO.insertRegisterData(
                name, email, password
        );
    }

    public static List<User> getLatestUsers() {
        return AuthenticationDAO.getLatestUsers();
    }
}
