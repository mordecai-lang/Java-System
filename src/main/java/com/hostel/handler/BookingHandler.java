
package com.hostel.handler;

import com.sun.net.httpserver.HttpExchange;
import com.hostel.SessionManager;
import com.hostel.service.BookingService;
import com.hostel.util.FormDataParser;
import com.hostel.util.ResponseUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.DateTimeException;
import java.util.Map;
import java.util.List;

import com.hostel.model.Booking;
import com.hostel.model.UserBooking;
import com.hostel.util.JsonUtil;
import com.hostel.util.JsonResponseUtil;


import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.DateTimeException;
import java.time.format.DateTimeParseException;
import java.util.HashMap;


import com.hostel.model.AvailableRoom;

public class BookingHandler {
    public static void handleBookRoom(HttpExchange exchange)throws IOException {
        // 1. Only accept POST requests.
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.getResponseHeaders().set("Allow", "POST");

            ResponseUtil.send(exchange, 405, "Only POST requests are accepted.");
            return;
        }

        // 2. Read and parse the URL-encoded form.
        String body = new String(exchange.getRequestBody().readAllBytes(),StandardCharsets.UTF_8);
        Map<String, String> data;
        try {
            data = FormDataParser.parse(body);
        } catch (Exception e) {
            ResponseUtil.send(exchange, 400, "Invalid form data.");
            return;
        }

        // 3. Read submitted fields.
        // Accept existing frontend names as well as the agreed names.
        String roomIdStr = data.get("roomId");
        String checkInStr = firstNonBlank(data.get("checkInDate"), data.get("checkIn"));
        String monthsStr = data.get("stayMonths");
        String occupantsStr = data.get("occupants");
        String bookedUnder = firstNonBlank(data.get("bookedUnder"), data.get("fullName"));
        String email = data.get("email");
        String phone = data.get("phone");

        // 4. Validate required text fields.
        if (isBlank(roomIdStr)
                || isBlank(checkInStr)
                || isBlank(monthsStr)
                || isBlank(bookedUnder)
                || isBlank(email)
                || isBlank(phone)) {

            ResponseUtil.send(
                exchange, 400,
                "Please provide the room, check-in date, duration, name, email and phone."
            );
            return;
        }

        // 5. Convert numeric fields.
        int roomId;
        int stayMonths;
        int occupants;

        try {
            roomId = Integer.parseInt(roomIdStr);
            stayMonths = Integer.parseInt(monthsStr);

            // The frontend may omit occupants when it only allows one.
            occupants = isBlank(occupantsStr)
                    ? 1
                    : Integer.parseInt(occupantsStr);

        } catch (NumberFormatException e) {
            ResponseUtil.send(
                exchange, 400,
                "Room ID, duration and occupants must be valid numbers."
            );
            return;
        }

        if (roomId <= 0) {
            ResponseUtil.send(exchange, 400, "Invalid room selected.");
            return;
        }

        if (stayMonths < 1 || stayMonths > 12) {
            ResponseUtil.send(exchange, 400,
                "Stay duration must be between 1 and 12 months."
            );
            return;
        }

        if (occupants < 1) {
            ResponseUtil.send(
                exchange, 400,
                "At least one occupant is required."
            );
            return;
        }

        bookedUnder = bookedUnder.trim();
        email = email.trim();
        phone = phone.trim();

        if (bookedUnder.length() > 150
                || email.length() > 255
                || phone.length() > 20) {
            ResponseUtil.send(
                exchange, 400,
                "One or more fields exceed the allowed length."
            );
            return;
        }

        if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            ResponseUtil.send(
                exchange, 400, "Please provide a valid email address."
            );
            return;
        }

        // 6. Parse the date supplied by an HTML date input.
        LocalDate checkInDate;

        try {
            checkInDate = LocalDate.parse(checkInStr);
        } catch (DateTimeException e) {
            ResponseUtil.send(
                exchange, 400,
                "Invalid check-in date. Use the YYYY-MM-DD format."
            );
            return;
        }

        if (checkInDate.isBefore(LocalDate.now())) {
            ResponseUtil.send(
                exchange, 400,
                "The check-in date cannot be in the past."
            );
            return;
        }

        // 7. Delegate booking and database checks to the service.
        // The service must calculate checkout and fee using DB data,
        // check overlapping reservations, and insert transactionally.
        // Check overlapping reservations and insert transactionally.
        try {
            SessionManager.UserSession session =
                SessionManager.getSession(exchange);

            if (session == null) {
                ResponseUtil.send(
                exchange, 401,
                "Please log in to book a room."
                );
                return;
                }

            BookingService.BookingResult result = BookingService.createBooking(
                    session.getUserId(),
                    session.getRole(),
                    roomId,
                    checkInDate,
                    stayMonths,
                    occupants,
                    bookedUnder,
                    email,
                    phone
            );

            switch (result) {
                case CREATED:
                    ResponseUtil.send(
                        exchange, 201,
                        "Booking created. Payment is still pending."
                    );
                    break;

                case ROOM_UNAVAILABLE:
                    ResponseUtil.send(
                        exchange, 409,
                        "This room is unavailable for the selected dates or does not have enough beds."
                    );
                    break;

                case INVALID_ROOM:
                    ResponseUtil.send(
                        exchange, 400,
                        "The selected room does not exist or cannot be booked."
                    );
                    break;

                default:
                    ResponseUtil.send(
                        exchange, 500,
                        "Booking could not be completed."
                    );
            }

        }catch (SecurityException e) {
            ResponseUtil.send(
                exchange, 401,
                "Please log in to book a room."
            );

        } catch (IllegalArgumentException e) {
            ResponseUtil.send(
                exchange, 400,
                e.getMessage()
            );

        } catch (Exception e) {
            e.printStackTrace();

            ResponseUtil.send(
                exchange, 500,
                "A server error occurred while creating the booking."
            );
        }

    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static String firstNonBlank(String first, String second) {
        return !isBlank(first) ? first : second;
    }

      //Admin Viewing Bookings
    public static void handleViewRecentBookings(HttpExchange exchange) throws IOException {
      if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
        ResponseUtil.send(exchange, 405, "Accepting only GET");
        return;
      }
      List<Booking> bookings = BookingService.getLatestBookings();

      String json = JsonUtil.bookingsToJson(bookings);
      JsonResponseUtil.send(exchange, 200, json);
    }

     
    // User Viewing Their Own Bookings
    public static void handleViewActualRecentBookings(
            HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            ResponseUtil.send(exchange, 405, "Accepting only GET");
            return;
        }
        SessionManager.UserSession session = SessionManager.getSession(exchange);
        if (session == null) {
            ResponseUtil.send(exchange, 401, "Please log in first.");
            return;
        }
        int userId = session.getUserId();

        List<UserBooking> userBookings =BookingService.getLatestUserBookings(userId);
        
        String json = JsonUtil.userBookingsToJson(userBookings);
        JsonResponseUtil.send(exchange, 200, json);
        
        }

        // User viewing rooms available for their selected booking dates
public static void handleAvailableBookingRooms(
        HttpExchange exchange) throws IOException {

    if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
        ResponseUtil.send(exchange, 405, "Accepting only GET");
        return;
    }

    try {
        // 1. Read query parameters from the URL
        String query = exchange.getRequestURI().getRawQuery();

        if (query == null || query.isBlank()) {
            ResponseUtil.send(exchange, 400,
                    "Missing checkInDate and stayMonths.");
            return;
        }

        Map<String, String> params = new HashMap<>();

        for (String pair : query.split("&")) {
            String[] parts = pair.split("=", 2);

            String key = URLDecoder.decode(
                    parts[0], StandardCharsets.UTF_8);

            String value = parts.length > 1
                    ? URLDecoder.decode(parts[1], StandardCharsets.UTF_8)
                    : "";

            params.put(key, value);
        }

        // 2. Extract the booking dates
        String checkInValue = params.get("checkInDate");
        String stayMonthsValue = params.get("stayMonths");

        if (isBlank(checkInValue) || isBlank(stayMonthsValue)) {
            ResponseUtil.send(exchange, 400,
                    "checkInDate and stayMonths are required.");
            return;
        }

        LocalDate checkInDate = LocalDate.parse(checkInValue);
        int stayMonths = Integer.parseInt(stayMonthsValue);

        if (stayMonths < 1 || stayMonths > 12) {
            ResponseUtil.send(exchange, 400,
                    "Stay duration must be between 1 and 12 months.");
            return;
        }

        if (checkInDate.isBefore(LocalDate.now())) {
            ResponseUtil.send(exchange, 400,
                    "Check-in date cannot be in the past.");
            return;
        }

        // 3. Calculate checkout date
        LocalDate checkOutDate = checkInDate.plusMonths(stayMonths);

        // 4. Ask BookingService for rooms available during this period
        List<AvailableRoom> availableRooms =
                BookingService.getAvailableBookingRooms(
                        checkInDate, checkOutDate);

        // 5. Return the available rooms as JSON
        String json = JsonUtil.availableRoomsToJson(availableRooms);

        JsonResponseUtil.send(exchange, 200, json);

    } catch (DateTimeParseException | NumberFormatException e) {
        ResponseUtil.send(exchange, 400,
                "Invalid check-in date or stay duration.");

    } catch (DateTimeException e) {
        ResponseUtil.send(exchange, 400,
                "Invalid booking date range.");
    }
}

}
