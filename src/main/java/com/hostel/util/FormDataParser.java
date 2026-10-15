package com.hostel.util;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class FormDataParser {
    public static Map<String, String> parse(String body) throws IOException {
        Map<String, String> data = new HashMap<>();
        
        String[] pairs = body.split("&");
        for (String pair : pairs) {
            
            String[] keyValue = pair.split("=");
            String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
            String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
            
            data.put(key, value);
        }
        return data;
        }
}