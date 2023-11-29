package com.nhnacademy;

import com.nhnacademy.node.FunctionNode;
import com.nhnacademy.node.MqttInNode;
import com.nhnacademy.node.MqttOutNode;

public class test {
    public static void main(String[] args) {
        MqttInNode mqttInNode = new MqttInNode();
        FunctionNode functionNode = new FunctionNode(1, 1);
        MqttOutNode mqttOutNode = new MqttOutNode("mqttOutNode", 1, "tcp://localhost");
        mqttInNode.connectOutputWire(0, functionNode.getInputWire(0));
        functionNode.connectInputWire(0, mqttInNode.getoutputWire(0));
        functionNode.connectOutputWire(0, mqttOutNode.getInputWire(0));
        mqttOutNode.connectInputWire(0, functionNode.getOutputWire(0));
        mqttInNode.start();
        functionNode.start();
        mqttOutNode.start();
    }
}
