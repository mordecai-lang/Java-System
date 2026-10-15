package com.hostel.util;

import com.hostel.model.User;
import java.util.List;

public class JsonUtil {

    public static String usersToJson(List<User> users) {

        StringBuilder json = new StringBuilder();

        json.append("[");

        for (int i = 0; i < users.size(); i++) {

            User user = users.get(i);

            json.append("{");

            json.append("\"id\":").append(user.getId()).append(",");
            json.append("\"name\":\"").append(user.getName()).append("\",");
            json.append("\"email\":\"").append(user.getEmail()).append("\",");
            json.append("\"role\":\"").append(user.getRole()).append("\"");

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
            json.append("\"name\":\"").append(hostel.getName()).append("\",");
            json.append("\"location\":\"").append(hostel.getLocation()).append("\",");
            json.append("\"description\":\"").append(hostel.getDescription()).append("\"");

            json.append("}");
            if (i < hostels.size() - 1) {
                json.append(",");
            }
        
        }
        json.append("]");
        return json.toString();
    }
}