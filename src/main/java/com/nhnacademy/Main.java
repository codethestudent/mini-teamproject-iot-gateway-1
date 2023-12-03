package com.nhnacademy;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.nhnacademy.node.Broker;
import com.nhnacademy.node.ChangeNode;
import com.nhnacademy.node.DebugNode;
import com.nhnacademy.node.MqttInNode;
import com.nhnacademy.node.SwitchNode;
import com.nhnacademy.node.TemplateNode;
import com.nhnacademy.node.DebugNode.targetType;
import com.nhnacademy.node.SwitchNode.propertyType;
import com.nhnacademy.node.TemplateNode.fieldType;
import com.nhnacademy.wire.Wire;

public class Main {
    public static void main(String[] args) {
        Broker broker = new Broker("1", "ems.nhnacademy.com", 1883, true, 60, true);
        MqttInNode mqttInNode = new MqttInNode("test", 2, "application/#", 2, broker);
        try {
            broker.connect();
        } catch (MqttException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        SwitchNode switchNode1 = new SwitchNode("object", 1, 1, 1, "payload", propertyType.msg, new JSONArray() {
            {
                add(new JSONObject() {
                    {
                        put("t", "hask");
                        put("v", "object");
                        put("vt", "str");
                    }
                });
            }
        }, true, 1);
        SwitchNode switchNode2 = new SwitchNode("deviceinfo", 1, 1, 1, "payload", propertyType.msg, new JSONArray() {
            {
                add(new JSONObject() {
                    {
                        put("t", "hask");
                        put("v", "deviceInfo");
                        put("vt", "str");
                    }
                });
            }
        }, true, 1);
        Wire wire1 = new Wire();
        Wire wire2 = new Wire();
        mqttInNode.connectOutputWire(0, wire1);
        switchNode1.connectInputWire(0, wire1);
        switchNode1.connectOutputWire(0, 0, wire2);
        switchNode2.connectInputWire(0, wire2);
        SwitchNode switchNode3 = new SwitchNode("nhnacademygyeongnam", 1, 1, 1, "payload.deviceInfo.tenantName",
                propertyType.msg, new JSONArray() {
                    {
                        add(new JSONObject() {
                            {
                                put("t", "eq");
                                put("v", "NHN Academy 경남");
                                put("vt", "str");
                            }
                        });
                    }
                }, true, 1);
        Wire wire3 = new Wire();
        switchNode2.connectOutputWire(0, 0, wire3);
        switchNode3.connectInputWire(0, wire3);
        SwitchNode switchNode4 = new SwitchNode("site", 1, 1, 1, "payload.deviceInfo.tags", propertyType.msg,
                new JSONArray() {
                    {
                        add(new JSONObject() {
                            {
                                put("t", "hask");
                                put("v", "site");
                                put("vt", "str");
                            }
                        });
                    }
                }, true, 1);
        Wire wire4 = new Wire();
        switchNode3.connectOutputWire(0, 0, wire4);
        switchNode4.connectInputWire(0, wire4);
        SwitchNode switchNode5 = new SwitchNode("branch", 1, 1, 1, "payload.deviceInfo.tags", propertyType.msg,
                new JSONArray() {
                    {
                        add(new JSONObject() {
                            {
                                put("t", "hask");
                                put("v", "branch");
                                put("vt", "str");
                            }
                        });
                    }
                }, true, 1);
        Wire wire5 = new Wire();
        switchNode4.connectOutputWire(0, 0, wire5);
        switchNode5.connectInputWire(0, wire5);
        SwitchNode switchNode6 = new SwitchNode("place", 1, 1, 1, "payload.deviceInfo.tags", propertyType.msg,
                new JSONArray() {
                    {
                        add(new JSONObject() {
                            {
                                put("t", "hask");
                                put("v", "place");
                                put("vt", "str");
                            }
                        });
                    }
                }, true, 1);
        Wire wire6 = new Wire();
        switchNode5.connectOutputWire(0, 0, wire6);
        switchNode6.connectInputWire(0, wire6);
        ChangeNode changeNode1 = new ChangeNode("insert time", 1, 1, 2, new JSONArray() {
            {
                add(new JSONObject() {
                    {
                        put("t", "set");
                        put("p", "payload.tempPayload.payload.time");
                        put("pt", "msg");
                        put("to", "");
                        put("tot", "date");
                    }
                });
            }
        });
        Wire wire7 = new Wire();
        switchNode6.connectOutputWire(0, 0, wire7);
        changeNode1.connectInputWire(0, wire7);
        SwitchNode switchNode7 = new SwitchNode("has temperature", 1, 1, 1, "payload.object", propertyType.msg,
                new JSONArray() {
                    {
                        add(new JSONObject() {
                            {
                                put("t", "hask");
                                put("v", "temperature");
                                put("vt", "str");
                            }
                        });
                    }
                }, true, 1);
        Wire wire8 = new Wire();
        changeNode1.connectOutputWire(0, 0, wire8);
        switchNode7.connectInputWire(0, wire8);
        ChangeNode changeNode2 = new ChangeNode("insert temperature", 1, 1, 1, new JSONArray() {
            {
                add(new JSONObject() {
                    {
                        put("t", "set");
                        put("p", "payload.tempPayload.payload.temperature");
                        put("pt", "msg");
                        put("to", "payload.object.temperature");
                        put("tot", "msg");
                        put("dc", true);
                    }
                });
            }
        });
        Wire wire9 = new Wire();
        switchNode7.connectOutputWire(0, 0, wire9);
        changeNode2.connectInputWire(0, wire9);
        TemplateNode templateNode = new TemplateNode(1, 1, "payload.tempPayload.topic", fieldType.msg,
                "data/s/{{payload.deviceInfo.tags.site}}/b/{{payload.deviceInfo.tenantName}}/p/{{payload.deviceInfo.tags.place}}/n/{{payload.deviceInfo.deviceName}}/e/temperature");
        Wire wire12 = new Wire();
        changeNode2.connectOutputWire(0, 0, wire12);
        templateNode.connectInputWire(0, wire12);
        ChangeNode changeNode4 = new ChangeNode("set topic", 1, 1, 1, new JSONArray() {
            {
                add(new JSONObject() {
                    {
                        put("t", "set");
                        put("p", "topic");
                        put("pt", "msg");
                        put("to", "payload.tempPayload.topic");
                        put("tot", "msg");
                        put("dc", true);
                    }
                });
            }
        });
        Wire wire10 = new Wire();
        templateNode.connectOutputWire(0, 0, wire10);
        changeNode4.connectInputWire(0, wire10);

        ChangeNode changeNode3 = new ChangeNode("set payload", 1, 1, 1, new JSONArray() {
            {
                add(new JSONObject() {
                    {
                        put("t", "set");
                        put("p", "payload");
                        put("pt", "msg");
                        put("to", "payload.tempPayload.payload");
                        put("tot", "msg");
                        put("dc", true);
                    }
                });
            }
        });
        Wire wire13 = new Wire();
        changeNode4.connectOutputWire(0, 0, wire13);
        changeNode3.connectInputWire(0, wire13);

        DebugNode debugNode = new DebugNode("debug", 1, true, true, true, true, targetType.full, "true");
        Wire wire11 = new Wire();
        changeNode3.connectOutputWire(0, 0, wire11);
        debugNode.connectInputWire(0, wire11);
        mqttInNode.start();
        switchNode1.start();
        switchNode2.start();
        switchNode3.start();
        switchNode4.start();
        switchNode5.start();
        switchNode6.start();
        changeNode1.start();
        switchNode7.start();
        changeNode2.start();
        changeNode3.start();
        debugNode.start();
        templateNode.start();
        changeNode4.start();
    }
}
