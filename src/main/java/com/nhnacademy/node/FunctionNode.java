package com.nhnacademy.node;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.json.simple.JSONObject;

import com.nhnacademy.message.JsonMessage;
import com.nhnacademy.wire.Wire;

public class FunctionNode extends InputOutputNode {
    static String application_name = "application";
    static String[] sensors = { "temperature", "humidity" };
    static List<String> sensorList = Arrays.asList(sensors);

    public FunctionNode(int inCount, int outCount) {
        super(inCount, outCount);
        // TODO Auto-generated constructor stub
    }

    @Override
    void process() {
        for (int i = 0; i < getInputWireCount(); i++) {
            if ((getInputWire(i) != null) && (getInputWire(i).hasMessage())) {
                JsonMessage message = (JsonMessage) getInputWire(i).get();
                if (message instanceof JsonMessage) {
                    JSONObject jsonObj = message.getJsonObject();
                    if (jsonObj.containsKey("object")) {
                        HashMap<String, String> deviceinfo = new HashMap<>();
                        HashMap<String, String> value = new HashMap<>();
                        for (Object key : ((JSONObject) jsonObj.get("deviceInfo")).keySet())
                            deviceinfo.put(key.toString(),
                                    ((JSONObject) jsonObj.get("deviceInfo")).get(key).toString());
                        for (Object key : ((JSONObject) jsonObj.get("object")).keySet())
                            value.put(key.toString(), ((JSONObject) jsonObj.get("object")).get(key).toString());
                        for (String sensor : sensorList) {
                            if (value.containsKey(sensor) && ((JSONObject) (((JSONObject) jsonObj.get("deviceInfo"))
                                    .get("tags"))).containsKey("site")) {
                                JSONObject resultJson = new JSONObject();
                                try {
                                    resultJson.put("topic",
                                            "data/s/"
                                                    + ((JSONObject) (((JSONObject) jsonObj.get("deviceInfo"))
                                                            .get("tags")))
                                                            .get("site")
                                                            .toString()
                                                    + "/b/"
                                                    + ((JSONObject) jsonObj.get("deviceInfo")).get("tenantName")
                                                            .toString()
                                                    + "/p/"
                                                    + ((JSONObject) (((JSONObject) jsonObj.get("deviceInfo"))
                                                            .get("tags")))
                                                            .get("place").toString()
                                                    + "/n/"
                                                    + ((JSONObject) jsonObj.get("deviceInfo")).get("deviceName")
                                                            .toString()
                                                            .split("\\(")[0]
                                                    + "/e/" + sensor);
                                } catch (Exception e) {
                                    System.out.println(jsonObj.toJSONString());
                                }

                                JSONObject payloadJson = new JSONObject();
                                payloadJson.put("time", System.currentTimeMillis());
                                payloadJson.put("value", Float.parseFloat(value.get(sensor)));
                                resultJson.put("payload", payloadJson);
                                System.out.println(resultJson);
                                output(new JsonMessage(resultJson));
                            }
                        }
                    }
                }
            }
        }
    }
}
