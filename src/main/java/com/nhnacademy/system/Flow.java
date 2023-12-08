package com.nhnacademy.system;

import org.json.simple.JSONObject;

public class Flow {
    String id;
    String label;
    JSONObject flowJsonObject;

    public Flow(String id, String label) {
        super();
        this.id = id;
        this.label = label;
        flowJsonObject = new JSONObject();
    }

    public String getId() {
        return id;
    }

    public JSONObject getFlowJsonObject() {
        return flowJsonObject;
    }
}
