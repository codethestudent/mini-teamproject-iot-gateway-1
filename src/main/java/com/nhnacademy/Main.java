package com.nhnacademy;

import com.nhnacademy.node.ContainsKeyNode;
import com.nhnacademy.node.FunctionNode;
import com.nhnacademy.node.MqttInNode;
import com.nhnacademy.node.MqttOutNode;
import com.nhnacademy.system.SystemOption;
import com.nhnacademy.wire.Wire;

public class Main {
    public static void main(String[] args) {
        if (args.length != 0) {
            SystemOption.getSystemOption(args);
        }
        MqttInNode mqttInNode = new MqttInNode(args);
        Wire wire1 = new Wire();
        ContainsKeyNode containsObject = new ContainsKeyNode(1, 1, "object");
        mqttInNode.connectOutputWire(0, wire1);
        containsObject.connectInputWire(0, wire1);
        ContainsKeyNode containsDeviceInfo = new ContainsKeyNode(1, 1, "deviceInfo");
        Wire wire2 = new Wire();
        containsObject.connectOutputWire(0, wire2);
        containsDeviceInfo.connectInputWire(0, wire2);

    }
}
