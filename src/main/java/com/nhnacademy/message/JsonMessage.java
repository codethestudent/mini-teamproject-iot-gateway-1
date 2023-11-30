package com.nhnacademy.message;

import org.json.simple.JSONObject;

public class JsonMessage extends Message {
    JSONObject jsonObject;
    String id;

    public JsonMessage(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

}
