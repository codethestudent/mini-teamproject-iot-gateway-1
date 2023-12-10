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
        dictionary.put("global", "GLOBAL");
        dictionary.put("switch", "SwitchNode");
        dictionary.put("debug", "DebugNode");
        dictionary.put("function", "FunctionNode");
    }

    public String getDictionary(String key) throws NullPointerException {
        if (key == null) {
            throw new NullPointerException("키값이 null입니다.");
        }
        if (!dictionary.containsKey(key)) {
            throw new NullPointerException("NodeRedDictionary에 없는 키값입니다.");
        }
        return dictionary.get(key);
    }
}
