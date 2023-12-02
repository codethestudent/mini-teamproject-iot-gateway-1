package com.nhnacademy.node;

public class ChangeNode extends InputOutputNode {
    enum ChangeType {
        set
    }

    enum PayloadType {
        msg
    }

    enum ToType {
        msg
    }

    private ChangeType changeType;
    private String payload;
    private PayloadType payloadType;
    private String to;
    private ToType toType;

    ChangeNode(int inCount, int outCount, int outWireCount, String id, ChangeType type, String payload,
            PayloadType payloadType,
            String to, ToType toType) {
        super(id, inCount, outCount, outWireCount);
        this.changeType = type;
        this.payload = payload;
        this.payloadType = payloadType;
        this.to = to;
        this.toType = toType;
    }

    public ChangeType getChangeType() {
        return changeType;
    }

    public String getPayload() {
        return payload;
    }

    public PayloadType getPayloadType() {
        return payloadType;
    }

    public String getTo() {
        return to;
    }

    public ToType getToType() {
        return toType;
    }

    @Override
    public void process(){
        
    }
}
