package com.hostel;
import com.sun.net.httpserver.HttpExchange;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SessionManager {
    private static final String COOKIE_NAME = "HOSTELBOOK_SESSION";

    // Maps each session ID to its authenticated user.
    private static final ConcurrentMap<String, UserSession> sessions =
            new ConcurrentHashMap<>();

    public static class UserSession {
        private final int userId;
        private final String role;

        public UserSession(int userId, String role) {
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

    // Create a unique session for the authenticated user.
    public static String createSession(int userId, String role) {
        String sessionId = UUID.randomUUID().toString();
        sessions.put(sessionId, new UserSession(userId, role));
        return sessionId;
    }

    // Read the session cookie and identify its user.
    public static UserSession getSession(HttpExchange exchange) {
        String cookieHeader = exchange.getRequestHeaders().getFirst("Cookie");
        if (cookieHeader == null) {
            return null;
        }
        for (String cookie : cookieHeader.split(";")) {
            String[] parts = cookie.trim().split("=", 2);
            if (parts.length == 2
                    && COOKIE_NAME.equals(parts[0])) {
                return sessions.get(parts[1]);
            }
        }
        return null;
    }

    // Send the session ID to the browser as an HTTP cookie.
    public static void sendSessionCookie(
            HttpExchange exchange, String sessionId) {

        exchange.getResponseHeaders().add(
                "Set-Cookie",
                COOKIE_NAME + "=" + sessionId
                        + "; Path=/; HttpOnly; SameSite=Lax"
        );
    }

    // Remove a session, for example, during logout.
    public static void invalidateSession(HttpExchange exchange) {
        UserSession session = getSession(exchange);

        String cookieHeader = exchange.getRequestHeaders()
                .getFirst("Cookie");

        if (cookieHeader != null) {
            for (String cookie : cookieHeader.split(";")) {
                String[] parts = cookie.trim().split("=", 2);

                if (parts.length == 2
                        && COOKIE_NAME.equals(parts[0])) {
                    sessions.remove(parts[1]);
                    break;
                }
            }
        }

        exchange.getResponseHeaders().add(
                "Set-Cookie",
                COOKIE_NAME
                        + "=; Path=/; HttpOnly; SameSite=Lax; Max-Age=0"
        );
    }
}
