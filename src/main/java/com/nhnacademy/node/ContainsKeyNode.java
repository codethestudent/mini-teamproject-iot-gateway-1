package com.nhnacademy.node;

import org.json.JSONObject;

import com.nhnacademy.message.JsonMessage;

public class ContainsKeyNode extends InputOutputNode {

    private String[] values;

    public ContainsKeyNode(int inCount, int outCount, String[] values) {
        super(inCount, outCount);
        this.values = values;
    }

    @Override
    void preprocess() {
        // TODO Auto-generated method stub
    }

    @Override
    void process() {
        for (int i = 0; i < getInputWireCount(); i++) {
            if ((getInputWire(i) != null) && (getInputWire(i).hasMessage())) {
                JsonMessage message = (JsonMessage) getInputWire(i).get();
                if (message instanceof JsonMessage) {
                    if (message.getJsonObject().containsKey(values[0])) {
                        JSONObject tempJson = (JSONObject) message.getJsonObject().get(values[0]);
                        for (int j = 1; i < values.length; j++) {
                            if (tempJson.) {
                                tempJson = (JsonMessage) tempJsonMessage.getJsonObject().get(values[j]);
                            } else {
                                throw new RuntimeException("JsonMessage does not have key : " + values[j]);
                            }
                        }
                        for (int j = 0; j < getOutputWireCount(); j++) {
                            if (getOutputWire(j) != null) {
                                System.out.println(tempJsonMessage.getJsonObject().toString());
                                getOutputWire(j).put(tempJsonMessage);
                            }
                        }
                    }
                }
            }
        }
    }

}
