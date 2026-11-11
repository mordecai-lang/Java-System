package com.hostel;
public class AuthenticationResult {

    private final int userId;
    private final String role;

    public AuthenticationResult(int userId, String role) {
        this.userId = userId;
        this.role = role;
    }

    public int getUserId() {
        return userId;
    }

    public String getRole() {
        return role;
    }
}
