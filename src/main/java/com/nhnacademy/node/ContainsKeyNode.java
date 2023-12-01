package com.nhnacademy.node;

import org.json.simple.JSONObject;

import com.nhnacademy.message.JsonMessage;

public class ContainsKeyNode extends InputOutputNode {

    private String[] values;

    public ContainsKeyNode(int inCount, int outCount, String value) {
        super(inCount, outCount);
        if (value.contains(".")) {
            values = value.split("\\.");
        } else {
            values = new String[1];
            values[0] = value;
        }

    }
    
    @Override
    void process() {
        for (int i = 0; i < getInputWireCount(); i++) {
            if ((getInputWire(i) != null) && (getInputWire(i).hasMessage())) {
                JsonMessage message = (JsonMessage) getInputWire(i).get();
                if (message instanceof JsonMessage) {
                    if (message.getJsonObject().containsKey(values[0])) {
                        JSONObject tempJson = (JSONObject) message.getJsonObject().get(values[0]);
                        for (int j = 1; j < values.length; j++) {
                            if (tempJson.containsKey(values[j])) {
                                tempJson = (JSONObject) tempJson.get(values[j]);
                                System.out.println(tempJson.toJSONString());
                            } else {
                                throw new RuntimeException("JsonMessage does not have key : " + values[j]);
                            }
                        }
                        for (int j = 0; j < getOutputWireCount(); j++) {
                            if (getOutputWire(j) != null) {
                                getOutputWire(j).put(message);
                            }
                        }
                    }
                }
            }
        }
    }

}
