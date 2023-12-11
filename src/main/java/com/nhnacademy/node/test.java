package com.nhnacademy.node;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.nhnacademy.wire.Wire;

public class test {
    public static void main(String[] args) throws ParseException {
        JSONParser parser = new JSONParser();
        String json = "{\"id\":\"n1\",\"type\":\"inject\",\"z\":\"flow1\",\"name\":\"\",\"props\":[{\"p\":\"payload\"},{\"p\":\"topic\",\"v\":\"\",\"vt\":\"num\"}],\"repeat\":\"1\",\"crontab\":\"\",\"once\":true,\"onceDelay\":0.1,\"topic\":\"\",\"payload\":\"\",\"payloadType\":\"date\",\"x\":850,\"y\":480,\"wires\":[[]]}";
        InjectNode injectNode = InjectNode.generateNode((JSONObject) parser.parse(json));
        DebugNode debugNode = new DebugNode(true, true, true, true, "full", "payload");
        Wire wire = new Wire();
        injectNode.connectOutputWire(0, wire);
        debugNode.connectInputWire(wire);
        injectNode.start();
        debugNode.start();
    }
}
