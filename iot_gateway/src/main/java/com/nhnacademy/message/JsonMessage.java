package com.nhnacademy.message;

import org.json.simple.JSONObject;

public class JsonMessage extends Message {
    JSONObject jsonObject;

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

}
