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
import com.hostel.model.Hostel;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.DateTimeException;
import java.time.format.DateTimeParseException;
import java.util.HashMap;


import com.hostel.model.AvailableRoom;

public class BookingHandler {

    public static void handleBookRoom(HttpExchange exchange) throws IOException {
    // 1. Only accept POST requests.
    if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
        exchange.getResponseHeaders().set("Allow", "POST");

        ResponseUtil.send(
            exchange, 405,
            "Only POST requests are accepted."
        );
        return;
    }

    // 2. Read and parse the URL-encoded form.
    String body = new String(
        exchange.getRequestBody().readAllBytes(),
        StandardCharsets.UTF_8
    );

    Map<String, String> data;

    try {
        data = FormDataParser.parse(body);
    } catch (Exception e) {
        ResponseUtil.send(exchange, 400, "Invalid form data.");
        return;
    }

    // 3. Read submitted fields.
    String hostelIdStr = data.get("hostelId");

    String checkInStr = firstNonBlank(
        data.get("checkInDate"),
        data.get("checkIn")
    );

    String monthsStr = data.get("stayMonths");
    String occupantsStr = data.get("occupants");

    String bookedUnder = firstNonBlank(
        data.get("bookedUnder"),
        data.get("fullName")
    );

    String email = data.get("email");
    String phone = data.get("phone");

    // 4. Validate required text fields.
    if (isBlank(hostelIdStr)
            || isBlank(checkInStr)
            || isBlank(monthsStr)
            || isBlank(bookedUnder)
            || isBlank(email)
            || isBlank(phone)) {

        ResponseUtil.send(
            exchange, 400,
            "Please provide the hostel, check-in date, duration, name, email and phone."
        );
        return;
    }

    // 5. Convert numeric fields.
    int hostelId;
    int stayMonths;
    int occupants;

    try {
        hostelId = Integer.parseInt(hostelIdStr);
        stayMonths = Integer.parseInt(monthsStr);

        occupants = isBlank(occupantsStr)
            ? 1
            : Integer.parseInt(occupantsStr);

    } catch (NumberFormatException e) {
        ResponseUtil.send(
            exchange, 400,
            "Hostel ID, duration and occupants must be valid numbers."
        );
        return;
    }

    if (hostelId <= 0) {
        ResponseUtil.send(
            exchange, 400,
            "Invalid hostel selected."
        );
        return;
    }

    if (stayMonths < 1 || stayMonths > 12) {
        ResponseUtil.send(
            exchange, 400,
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
            exchange, 400,
            "Please provide a valid email address."
        );
        return;
    }

    // 6. Parse the check-in date.
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

    // 7. Retrieve the logged-in user and delegate to the service.
    try {

        SessionManager.UserSession session =
            SessionManager.getSession(exchange);

        if (session == null) {
            ResponseUtil.send(
                exchange, 401,
                "Please log in to submit an application."
            );
            return;
        }

        BookingService.BookingResult result =
            BookingService.createBooking(
                session.getUserId(),
                session.getRole(),
                hostelId,
                checkInDate,
                stayMonths,
                occupants,
                bookedUnder,
                email,
                phone
            );

        // 8. Handle the service result.
        switch (result) {

            case CREATED:
                ResponseUtil.send(
                    exchange, 201,
                    "Application submitted successfully. Payment is still pending."
                );
                break;

            case HOSTEL_UNAVAILABLE:
                ResponseUtil.send(
                    exchange, 409,
                    "This hostel has no remaining application spaces."
                );
                break;

            case INVALID_HOSTEL:
                ResponseUtil.send(
                    exchange, 400,
                    "The selected hostel does not exist or cannot be booked."
                );
                break;

            default:
                ResponseUtil.send(
                    exchange, 500,
                    "Application could not be completed."
                );
        }

    } catch (SecurityException e) {

        ResponseUtil.send(
            exchange, 401,
            "Please log in to submit an application."
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
            "A server error occurred while submitting the application."
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
        public static void handleAvailableBookingRooms(HttpExchange exchange)
        throws IOException {

    System.out.println(
        "DEBUG: /availableBookingRooms endpoint was requested!"
    );

    if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
        ResponseUtil.send(exchange, 405, "Only GET requests are accepted.");
        return;
    }

    try {

        LocalDate checkInDate;
        LocalDate checkOutDate;

        String query = exchange.getRequestURI().getRawQuery();

        // -------------------------------------------------
        // CASE 1: Initial page load - no query parameters.
        // Check availability for today.
        // -------------------------------------------------

        if (query == null || query.isBlank()) {

            checkInDate = LocalDate.now();
            checkOutDate = checkInDate.plusDays(1);

            System.out.println(
                "Checking current room availability: "
                + checkInDate + " to " + checkOutDate
            );

        } else {

            // -------------------------------------------------
            // CASE 2: User selected booking dates.
            // -------------------------------------------------

            Map<String, String> params = new HashMap<>();

            for (String pair : query.split("&")) {

                String[] parts = pair.split("=", 2);

                String key = URLDecoder.decode(
                    parts[0],
                    StandardCharsets.UTF_8
                );

                String value = parts.length > 1
                    ? URLDecoder.decode(
                        parts[1],
                        StandardCharsets.UTF_8
                    )
                    : "";

                params.put(key, value);
            }

            String checkInValue = params.get("checkInDate");
            String stayMonthsValue = params.get("stayMonths");

            if (checkInValue == null || checkInValue.isBlank()
                    || stayMonthsValue == null
                    || stayMonthsValue.isBlank()) {

                ResponseUtil.send(
                    exchange,
                    400,
                    "checkInDate and stayMonths are required together."
                );
                return;
            }

            checkInDate = LocalDate.parse(checkInValue);

            int stayMonths = Integer.parseInt(stayMonthsValue);

            if (stayMonths < 1 || stayMonths > 12) {
                ResponseUtil.send(
                    exchange,
                    400,
                    "Stay duration must be between 1 and 12 months."
                );
                return;
            }

            if (checkInDate.isBefore(LocalDate.now())) {
                ResponseUtil.send(
                    exchange,
                    400,
                    "Check-in date cannot be in the past."
                );
                return;
            }

            checkOutDate = checkInDate.plusMonths(stayMonths);

            System.out.println(
                "Checking date-specific availability: "
                + checkInDate + " to " + checkOutDate
            );
        }

        // -------------------------------------------------
        // GET AVAILABLE ROOMS FROM THE EXISTING SERVICE.
        // -------------------------------------------------

        List<AvailableRoom> availableRooms =
            BookingService.getAvailableBookingRooms(
                checkInDate,
                checkOutDate
            );

        String json = JsonUtil.availableRoomsToJson(availableRooms);

        JsonResponseUtil.send(exchange, 200, json);

        System.out.println(
            "Available rooms returned: " + availableRooms.size()
        );

    } catch (DateTimeParseException | NumberFormatException e) {

        ResponseUtil.send(
            exchange,
            400,
            "Invalid check-in date or stay duration."
        );

    } catch (DateTimeException e) {

        ResponseUtil.send(
            exchange,
            400,
            "Invalid booking date range."
        );

    } catch (Exception e) {

        e.printStackTrace();

        ResponseUtil.send(
            exchange,
            500,
            "Could not retrieve available rooms."
        );
        }
    }
    
    
    public static void handleAvailableApplications(HttpExchange exchange) throws IOException {
    if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
        ResponseUtil.send(exchange, 405, "Accepting only GET");
        return;
    }
    List<Hostel> hostels = BookingService.getAvailableApplications();

    String json = JsonUtil.hostelsToJson(hostels);
    JsonResponseUtil.send(exchange, 200, json);
  }



}
