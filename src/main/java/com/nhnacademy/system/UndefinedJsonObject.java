package com.nhnacademy.system;

import java.lang.reflect.InvocationTargetException;

import org.json.simple.JSONObject;

public class UndefinedJsonObject<V, K> extends JSONObject {
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
        for (String key : keys) {
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
            if (key.equals(keys[keys.length - 1])) {
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

    @Override
    public String toString() {
        return "Undefined";
    }

    @Override
    public V get(Object key) {
        return (V) "Undefined";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof String) {
            return obj.equals("");
        }
        if (obj instanceof UndefinedJsonObject) {
            return true;
        }
        return false;
    }
}
