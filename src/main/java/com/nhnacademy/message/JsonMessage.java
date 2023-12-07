package com.nhnacademy.message;

import java.lang.reflect.InvocationTargetException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.nhnacademy.system.UndefinedJsonObject;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonMessage extends Message {
    JSONObject jsonObject;

    public JsonMessage(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public JSONObject getJsonObject() {
        return getDeepCopyJsonObject(jsonObject);
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public Object getPayload() {
        if (!jsonObject.containsKey("payload"))
            return new UndefinedJsonObject<>();
        return getJsonObject().get("payload");
    }

    public String getTopic() {
        if (!jsonObject.containsKey("topic"))
            return "Undefined";
        return jsonObject.get("topic").toString();
    }

    public static JSONObject getDeepCopyJsonObject(JSONObject jsonObject) {
        JSONParser parser = new JSONParser();
        JSONObject result = new JSONObject();
        Object obj;
        try {
            obj = parser.parse(jsonObject.toString());
            result = (JSONObject) obj;
        } catch (ParseException e) {
            log.error("ParseException", e);
        }
        return result;
    }

    // 주어진 키의 마지막 키를 제외한 JSONObject를 반환
    // 주어진 키가 없으면 UndefinedJsonObject를 반환
    public static JSONObject getDestJsonObject(Object jsonObject, String[] keys) {
        if (!(jsonObject instanceof JSONObject)) {
            try {
                return UndefinedJsonObject.class.getConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        JSONObject destJsonObject = (JSONObject) jsonObject;
        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            if (!destJsonObject.containsKey(key)) {
                try {
                    destJsonObject = UndefinedJsonObject.class.getConstructor().newInstance();
                    break;
                } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                        | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (i == keys.length - 1) {
                break;
            }
            if (!(destJsonObject.get(key) instanceof JSONObject)) {
                try {
                    destJsonObject = UndefinedJsonObject.class.getConstructor().newInstance();
                } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                        | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
            }
            destJsonObject = (JSONObject) destJsonObject.get(key);
        }
        return destJsonObject;
    }
}
