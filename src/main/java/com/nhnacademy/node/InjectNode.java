package com.nhnacademy.node;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class InjectNode extends InputNode {
    JSONArray props;
    int repeat;
    boolean once;
    double onceDelay;
    String topic;
    String payload;
    String payloadType;

    public InjectNode(String id, int outCount, JSONArray props, int repeat, boolean once, double onceDelay,
            String topic, String payload, String payloadType) {
        super(id, outCount);
        this.props = props;
        this.repeat = repeat;
        this.once = once;
        this.onceDelay = onceDelay;
        this.topic = topic;
        this.payload = payload;
        this.payloadType = payloadType;

    }

    private void makeThread() {
        if (once) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep((long) onceDelay);
                        injectMessage();
                        Thread.currentThread().interrupt();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }
        if (repeat > 0) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    injectMessage();
                    try {
                        Thread.sleep(repeat);
                        Thread.currentThread().interrupt();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }
    }

    private synchronized void injectMessage() {
        JSONObject jsonObject = new JSONObject();
    }

}
