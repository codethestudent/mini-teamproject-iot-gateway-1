package com.nhnacademy.system;

import java.lang.reflect.InvocationTargetException;

import org.json.simple.JSONObject;

public class UndefinedJsonObject<V, K> extends JSONObject {
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

    @Override
    public String toString() {
        return "undefined";
    }

    @Override
    public V get(Object key) {
        return (V) this;
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

    @Override
    public boolean containsKey(Object key) {
        return false;
    }
}
