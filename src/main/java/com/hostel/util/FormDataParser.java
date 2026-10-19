package com.hostel.util;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class FormDataParser {
        public static Map<String, String> parse(String body)throws IOException {
            Map<String, String> data = new HashMap<>();
            
            if (body == null || body.isEmpty()) {
                return data;
            }

            String[] pairs = body.split("&");
        
            for (String pair : pairs) {
                if (pair.isEmpty()) {
                    continue;
            }
            // Split at the first equals sign only.
            int separator = pair.indexOf('=');
            String encodedKey = separator >= 0 ? pair.substring(0, separator): pair;
            String encodedValue = separator >= 0? pair.substring(separator + 1): "";

            String key = URLDecoder.decode(encodedKey, StandardCharsets.UTF_8);
            String value = URLDecoder.decode(encodedValue, StandardCharsets.UTF_8);
            data.put(key, value);
        }

        return data;
    }
}