package com.nhnacademy.system;

import java.util.HashMap;

public class NodeRedDictionary {
    private static NodeRedDictionary instance = new NodeRedDictionary();
    private HashMap<String, String> dictionary;

    private NodeRedDictionary() {
        super();
        setDictionary();
    }

    public static NodeRedDictionary getInstance() {
        if (instance == null)
            instance = new NodeRedDictionary();
        return instance;
    }

    private void setDictionary() {
        dictionary = new HashMap<>();
        dictionary.put("msg", "MSG");
        dictionary.put("full", "FULL");
        dictionary.put("flow", "FLOW");
    }

    public String getDictionary(String key) {
        return dictionary.get(key);
    }
}
