
package com.hostel;

public class LoginResult {

    private final String sessionId;
    private final String role;

    public LoginResult(String sessionId, String role) {
        this.sessionId = sessionId;
        this.role = role;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getRole() {
        return role;
    }
}
