package com.gft.digitalbank.exchange.solution;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Json {
    public static String getValue(String propertyName, String json) {
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(json).getAsJsonObject();
        String value = obj.get(propertyName).getAsString();
        return value;
    }
}
