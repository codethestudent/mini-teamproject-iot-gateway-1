package com.nhnacademy.message;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.nhnacademy.system.UndefinedJsonObject;

public class JsonMessage extends Message {
    JSONObject jsonObject;

    public JsonMessage(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public JSONObject getJsonObject() {
        return getDeepCopyJsonObject(jsonObject);
    }

    public static JSONObject getDeepCopyJsonObject(JSONObject jsonObject) {
        JSONParser parser = new JSONParser();
        JSONObject result = new JSONObject();
        Object obj;
        try {
            obj = parser.parse(jsonObject.toString());
            result = (JSONObject) obj;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public Object getPayload() {
        if (!jsonObject.containsKey("payload"))
            return new UndefinedJsonObject<>();
        return jsonObject.get("payload");
    }

    public String getTopic() {
        if (!jsonObject.containsKey("topic"))
            return "Undefined";
        return jsonObject.get("topic").toString();
    }
}
