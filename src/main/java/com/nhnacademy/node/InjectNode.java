package com.nhnacademy.node;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.nhnacademy.message.JsonMessage;

public class InjectNode extends InputNode {
    JSONArray props;
    int repeat;
    boolean once;
    double onceDelay;
    String topic;
    String payload;
    String payloadType;

    public InjectNode(String id, JSONArray props, int repeat, boolean once, double onceDelay,
            String topic, String payload, String payloadType) {
        super(id, 1);
        this.props = props;
        this.repeat = repeat;
        this.once = once;
        this.onceDelay = onceDelay;
        this.topic = topic;
        this.payload = payload;
        this.payloadType = payloadType;
    }

    public InjectNode(JSONArray props, int repeat, boolean once, double onceDelay, String topic,
            String payload, String payloadType) {
        super(1);
        this.props = props;
        this.repeat = repeat;
        this.once = once;
        this.onceDelay = onceDelay;
        this.topic = topic;
        this.payload = payload;
        this.payloadType = payloadType;
    }

    public static InjectNode generateNode(JSONObject jsonObject) {
        String id = (String) jsonObject.get("id");
        JSONArray props = (JSONArray) jsonObject.get("props");
        int repeat = Integer.parseInt((String) jsonObject.get("repeat"));
        boolean once = (boolean) jsonObject.get("once");
        double onceDelay = (Double) jsonObject.get("onceDelay");
        String payload = null;
        String payloadType = null;
        String topic = (String) jsonObject.get("topic");
        if (((JSONObject) props.get(0)).get("p").equals("payload")) {
            payload = (String) jsonObject.get("payload");
            payloadType = (String) jsonObject.get("payloadType");
        }
        return new InjectNode(id, props, repeat, once, onceDelay, topic, payload, payloadType);
    }

    private void makeThread() {
        if (once) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep((long) onceDelay * 1000);
                        injectMessage();
                        Thread.currentThread().interrupt();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.setDaemon(true);
            thread.start();
        }
        if (repeat > 0) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!Thread.currentThread().isInterrupted()) {
                        injectMessage();
                        try {
                            Thread.sleep((long) repeat * 1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            thread.setDaemon(true);
            thread.start();
        }
    }

    private synchronized void injectMessage() {
        JSONObject jsonObject = new JSONObject();
        for (int i = 0; i < props.size(); i++) {
            // props의 첫번째 값이 payload일 경우와 아닐 경우를 나누어서 처리
            JSONObject prop = (JSONObject) props.get(i);
            if (i == 0 && prop.get("p").equals("payload")) {
                if (payloadType.equals("date")) {
                    jsonObject.put("payload", System.currentTimeMillis());
                } // ...
            } else {
                String p = (String) prop.get("p");
                String v = (String) prop.get("v");
                String vt = (String) prop.get("vt");
                if (vt.equals("date")) {
                    jsonObject.put(p, System.currentTimeMillis());
                } // ...
            }
        }
        output(new JsonMessage(jsonObject), 0);
    }

    @Override
    void preprocess() {
        makeThread();
    }

}
