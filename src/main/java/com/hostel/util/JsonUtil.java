package com.hostel.util;
import com.hostel.model.UserBooking;
import com.hostel.model.User;
import com.hostel.model.Hostel;
import com.hostel.model.Room;
import com.hostel.model.Booking;
import java.util.List;
import com.hostel.model.AvailableRoom;

public class JsonUtil {
    private static String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }

    public static String usersToJson(List<User> users) {
        StringBuilder json = new StringBuilder();
        json.append("[");
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            json.append("{");
            json.append("\"id\":").append(user.getId()).append(",");
            json.append("\"name\":\"").append(escapeJson(user.getName())).append("\",");
            json.append("\"email\":\"").append(escapeJson(user.getEmail())).append("\",");
            json.append("\"role\":\"").append(escapeJson(user.getRole())).append("\"");

            json.append("}");

            if (i < users.size() - 1) {
                json.append(",");
            }
        }

        json.append("]");

        return json.toString();
    }

    public static String hostelsToJson(List<Hostel> hostels) {
        
        StringBuilder json = new StringBuilder();
        
        json.append("[");
        for (int i = 0; i < hostels.size(); i++) {
            Hostel hostel = hostels.get(i);
            json.append("{");
            json.append("\"id\":").append(hostel.getId()).append(",");
            json.append("\"name\":\"").append(escapeJson(hostel.getName())).append("\",");
            json.append("\"location\":\"").append(escapeJson(hostel.getLocation())).append("\",");
            json.append("\"description\":\"").append(escapeJson(hostel.getDescription())).append("\"");

            json.append("}");
            if (i < hostels.size() - 1) {
                json.append(",");
            }
        }
        json.append("]");
        return json.toString();
    }

    
public static String roomsToJson(List<Room> rooms) {
    StringBuilder json = new StringBuilder();

    json.append("[");

    for (int i = 0; i < rooms.size(); i++) {
        Room room = rooms.get(i);

        json.append("{");

        json.append("\"id\":").append(room.getId()).append(",");
        json.append("\"hostelId\":").append(room.getHostelId()).append(",");
        json.append("\"hostelName\":\"").append(escapeJson(room.getHostelName())).append("\",");
        json.append("\"roomNumber\":\"").append(escapeJson(room.getRoomNumber())).append("\",");
        json.append("\"totalBeds\":").append(room.getTotalBeds()).append(",");
        json.append("\"roomType\":\"").append(escapeJson(room.getRoomType())).append("\",");
        json.append("\"accommodationFee\":").append(room.getAccommodationFee()).append(",");
        json.append("\"status\":\"").append(escapeJson(room.getStatus())).append("\"");
        
        json.append("}");

        if (i < rooms.size() - 1) {
            json.append(",");
        }
    }

    json.append("]");

    return json.toString();
}
  public static String bookingsToJson(List<Booking> bookings) {
    StringBuilder json = new StringBuilder();

    json.append("[");

    for (int i = 0; i < bookings.size(); i++) {
        Booking booking = bookings.get(i);

        json.append("{");

        json.append("\"id\":").append(booking.getId()).append(",");
        json.append("\"roomId\":").append(booking.getRoomId()).append(",");
        json.append("\"bookedUnder\":\"").append(escapeJson(booking.getBookedUnder())).append("\",");
        json.append("\"email\":\"").append(escapeJson(booking.getEmail())).append("\",");
        json.append("\"phone\":\"").append(escapeJson(booking.getPhone())).append("\",");
        json.append("\"checkInDate\":\"").append(booking.getCheckInDate()).append("\",");
        json.append("\"checkOutDate\":\"").append(booking.getCheckOutDate()).append("\",");
        json.append("\"stayMonths\":").append(booking.getStayMonths()).append(",");
        json.append("\"occupants\":").append(booking.getOccupants()).append(",");
        json.append("\"accommodationFee\":").append(booking.getAccommodationFee()).append(",");
        json.append("\"bookingStatus\":\"").append(escapeJson(booking.getBookingStatus())).append("\",");
        json.append("\"paymentStatus\":\"").append(escapeJson(booking.getPaymentStatus())).append("\",");
        json.append("\"roomNumber\":\"").append(escapeJson(booking.getRoomNumber())).append("\",");
        json.append("\"hostelName\":\"").append(escapeJson(booking.getHostelName())).append("\"");

        json.append("}");

        if (i < bookings.size() - 1) {
            json.append(",");
        }
    }

    json.append("]");

    return json.toString();
  }

  
public static String userBookingsToJson(List<UserBooking> bookings) {
    StringBuilder json = new StringBuilder();
    json.append("[");

    for (int i = 0; i < bookings.size(); i++) {
        UserBooking booking = bookings.get(i);

        json.append("{");

        json.append("\"id\":").append(booking.getId()).append(",");
        json.append("\"roomId\":").append(booking.getRoomId()).append(",");
        json.append("\"bookedUnder\":\"").append(escapeJson(booking.getBookedUnder())).append("\",");
        json.append("\"email\":\"").append(escapeJson(booking.getEmail())).append("\",");
        json.append("\"phone\":\"").append(escapeJson(booking.getPhone())).append("\",");
        json.append("\"checkInDate\":\"").append(booking.getCheckInDate()).append("\",");
        json.append("\"checkOutDate\":\"").append(booking.getCheckOutDate()).append("\",");
        json.append("\"stayMonths\":").append(booking.getStayMonths()).append(",");
        json.append("\"occupants\":").append(booking.getOccupants()).append(",");
        json.append("\"accommodationFee\":").append(booking.getAccommodationFee()).append(",");
        json.append("\"bookingStatus\":\"").append(escapeJson(booking.getBookingStatus())).append("\",");
        json.append("\"paymentStatus\":\"").append(escapeJson(booking.getPaymentStatus())).append("\",");
        json.append("\"roomNumber\":\"").append(escapeJson(booking.getRoomNumber())).append("\",");
        json.append("\"hostelName\":\"").append(escapeJson(booking.getHostelName())).append("\"");

        json.append("}");

        if (i < bookings.size() - 1) {
            json.append(",");
        }
    }

    json.append("]");
    return json.toString();
}

    public static String availableRoomsToJson(
        List<AvailableRoom> rooms) {

    StringBuilder json = new StringBuilder("[");

    for (int i = 0; i < rooms.size(); i++) {

        AvailableRoom room = rooms.get(i);

        json.append("{")
            .append("\"id\":").append(room.getId()).append(",")
            .append("\"hostelId\":").append(room.getHostelId()).append(",")
            .append("\"hostelName\":\"").append(escapeJson(room.getHostelName())).append("\",")
            .append("\"roomNumber\":\"").append(escapeJson(room.getRoomNumber())).append("\",")
            .append("\"totalBeds\":").append(room.getTotalBeds()).append(",")
            .append("\"roomType\":\"").append(escapeJson(room.getRoomType())).append("\",")
            .append("\"accommodationFee\":").append(room.getAccommodationFee()).append(",")
            .append("\"status\":\"").append(escapeJson(room.getStatus())).append("\",")
            .append("\"reservedBeds\":").append(room.getReservedBeds()).append(",")
            .append("\"remainingBeds\":").append(room.getRemainingBeds())
            .append("}");

        if (i < rooms.size() - 1) {
            json.append(",");
        }
    }

    json.append("]");

    return json.toString();
    }


}    