package com.nhnacademy.node;

public class ChangeNode extends InputOutputNode {
    enum ChangeType {
        set
    }

    enum PayloadType {
        msg
    }

    enum toType {
        msg
    }

    ChangeNode(int inCount, int outCount, ChangeType type, String payload, PayloadType payloadType, String to, toType toType  ) {
        super(inCount, outCount);
        //TODO Auto-generated constructor stub
    }
    public static void main(String[] args) {
        ChangeNode changeNode = new ChangeNode(1, 1, ChangeType.set, "msg", PayloadType.msg, "msg", toType.msg);
        System.out.println(changeNode.getId());
    }
}
