package com.nhnacademy.system;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.FileReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.nhnacademy.node.ActiveNode;
import com.nhnacademy.node.ChangeNode;
import com.nhnacademy.node.DebugNode;
import com.nhnacademy.node.MqttInNode;
import com.nhnacademy.node.MqttOutNode;
import com.nhnacademy.node.SwitchNode;
import com.nhnacademy.node.TemplateNode;
import com.nhnacademy.wire.Wire;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NodeRedSystem {
    private static NodeRedSystem instance = new NodeRedSystem();
    ArrayList<ActiveNode> nodes = new ArrayList<>();
    ArrayList<Broker> brokers = new ArrayList<>();
    ArrayList<Flow> flows = new ArrayList<>();

    private NodeRedSystem() {
        super();
    }

    public static NodeRedSystem getInstance() {
        if (instance == null)
            instance = new NodeRedSystem();
        return instance;
    }

    public Flow getFlow(String id) {
        for (Flow flow : flows) {
            if (flow.getId().equals(id)) {
                return flow;
            }
        }
        throw new NullPointerException("There is no flow with id " + id);
    }

    public void load(String path) {
        try {
            Reader reader = new FileReader(path);
            JSONParser parser = new JSONParser();
            JSONArray jsonArray = (JSONArray) parser.parse(reader);
            String z;
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                String type = (String) jsonObject.get("type");
                switch (type) {
                    case "tab":
                        break;
                    case "mqtt-broker":
                        String brokerId = (String) jsonObject.get("id");
                        String brokerBroker = (String) jsonObject.get("broker");
                        int brokerPort = Integer.parseInt((String) jsonObject.get("port"));
                        Boolean brokerAutoConnect = (Boolean) jsonObject.get("autoConnect");
                        int brokerKeepalive = Integer.parseInt((String) jsonObject.get("keepalive"));
                        Boolean brokerCleansession = (Boolean) jsonObject.get("cleansession");
                        Broker broker = new Broker(brokerId, brokerBroker, brokerPort, brokerAutoConnect,
                                brokerKeepalive, brokerCleansession);
                        brokers.add(broker);
                        break;
                    case "mqtt in":
                        String mqttInId = (String) jsonObject.get("id");
                        String mqttInTopic = (String) jsonObject.get("topic");
                        int mqttInQos = Integer.parseInt((String) jsonObject.get("qos"));
                        String mqttInBroker = (String) jsonObject.get("broker");
                        Broker mqttInBrokerObj = null;
                        for (int j = 0; j < brokers.size(); j++) {
                            if (brokers.get(j).getId().equals(mqttInBroker)) {
                                mqttInBrokerObj = brokers.get(j);
                                break;
                            }
                        }
                        MqttInNode mqttInNode = new MqttInNode(mqttInId, mqttInTopic, mqttInQos, mqttInBrokerObj);
                        nodes.add(mqttInNode);
                        break;
                    case "mqtt out":
                        String mqttOutId = (String) jsonObject.get("id");
                        // String mqttOutTopic = (String) jsonObject.get("topic");
                        // if (jsonObject.get("qos").equals("")) {

                        // }
                        // int mqttOutQos = Integer.parseInt((String) jsonObject.get("qos"));
                        String mqttOutBroker = (String) jsonObject.get("broker");
                        Broker mqttOutBrokerObj = null;
                        for (int j = 0; j < brokers.size(); j++) {
                            if (brokers.get(j).getId().equals(mqttOutBroker)) {
                                mqttOutBrokerObj = brokers.get(j);
                                break;
                            }
                        }
                        MqttOutNode mqttOutNode = new MqttOutNode(mqttOutId,
                                mqttOutBrokerObj);
                        nodes.add(mqttOutNode);
                        break;
                    case "switch":
                        String switchId = (String) jsonObject.get("id");
                        String switchProperty = (String) jsonObject.get("property");
                        String switchPropertyType = (String) jsonObject.get("propertyType");
                        JSONArray switchRules = (JSONArray) jsonObject.get("rules");
                        Boolean switchCheckall = Boolean.parseBoolean((String) jsonObject.get("checkall"));
                        z = (String) jsonObject.get("z");
                        SwitchNode switchNode = new SwitchNode(switchId, 10, switchProperty,
                                switchPropertyType, switchRules, switchCheckall, z);
                        nodes.add(switchNode);
                        break;
                    case "change":
                        String changeId = (String) jsonObject.get("id");
                        JSONArray changeRules = (JSONArray) jsonObject.get("rules");
                        z = (String) jsonObject.get("z");
                        ChangeNode changeNode = new ChangeNode(changeId, 10, changeRules, z);
                        nodes.add(changeNode);
                        break;
                    case "debug":
                        String debugId = (String) jsonObject.get("id");
                        Boolean debugActive = (Boolean) jsonObject.get("active");
                        Boolean debugTosidebar = (Boolean) jsonObject.get("tosidebar");
                        Boolean debugConsole = (Boolean) jsonObject.get("console");
                        Boolean debugTostatus = (Boolean) jsonObject.get("tostatus");
                        String debugTargetType = (String) jsonObject.get("targetType");
                        String debugComplete = (String) jsonObject.get("complete");
                        DebugNode debugNode = new DebugNode(debugId, debugActive, debugTosidebar, debugConsole,
                                debugTostatus, debugTargetType, debugComplete);
                        nodes.add(debugNode);
                        break;
                    case "template":
                        String templateId = (String) jsonObject.get("id");
                        String templateField = (String) jsonObject.get("field");
                        String templateFieldType = (String) jsonObject.get("fieldType");
                        String templateTemplate = (String) jsonObject.get("template");
                        TemplateNode templateNode = new TemplateNode(templateId, templateField,
                                templateFieldType, templateTemplate);
                        nodes.add(templateNode);
                        break;
                    default:
                        log.info(type + " is not supported yet");
                        break;
                }
            }

            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                String type = (String) jsonObject.get("type");
                switch (type) {
                    case "tab":
                        break;
                    case "mqtt-broker":
                        break;
                    case "mqtt in":
                        MqttInNode mqttInNode = null;
                        for (int j = 0; j < nodes.size(); j++) {
                            if (nodes.get(j).getId().equals(jsonObject.get("id"))) {
                                mqttInNode = (MqttInNode) nodes.get(j);
                                break;
                            }
                        }
                        String brokerId = (String) jsonObject.get("broker");
                        for (int j = 0; j < brokers.size(); j++) {
                            if (brokers.get(j).getId().equals(brokerId)) {
                                mqttInNode.setBroker(brokers.get(j));
                                break;
                            }
                        }
                        JSONArray wiress = (JSONArray) jsonObject.get("wires");
                        for (int j = 0; j < wiress.size(); j++) {
                            JSONArray wires = (JSONArray) wiress.get(j);
                            for (int k = 0; k < wires.size(); k++) {
                                String wireId = (String) wires.get(k);
                                for (int l = 0; l < nodes.size(); l++) {
                                    if (nodes.get(l).getId().equals(wireId)) {
                                        Wire wire = new Wire();
                                        mqttInNode.connectOutputWire(j, wire);
                                        nodes.get(l).getClass()
                                                .getMethod("connectInputWire", Wire.class)
                                                .invoke(nodes.get(l), wire);
                                    }
                                }
                            }
                        }
                        break;
                    case "mqtt out":
                        MqttOutNode mqttOutNode = null;
                        for (int j = 0; j < nodes.size(); j++) {
                            if (nodes.get(j).getId().equals(jsonObject.get("id"))) {
                                mqttOutNode = (MqttOutNode) nodes.get(j);
                                break;
                            }
                        }
                        String mqttOutBrokerId = (String) jsonObject.get("broker");
                        for (int j = 0; j < brokers.size(); j++) {
                            if (brokers.get(j).getId().equals(mqttOutBrokerId)) {
                                mqttOutNode.setBroker(brokers.get(j));
                                break;
                            }
                        }
                        break;
                    case "switch":
                        SwitchNode switchNode = null;
                        for (int j = 0; j < nodes.size(); j++) {
                            if (nodes.get(j).getId().equals((String) jsonObject.get("id"))) {
                                switchNode = (SwitchNode) nodes.get(j);
                                break;
                            }
                        }
                        JSONArray switchWires = (JSONArray) jsonObject.get("wires");
                        for (int j = 0; j < switchWires.size(); j++) {
                            JSONArray wires = (JSONArray) switchWires.get(j);
                            for (int k = 0; k < wires.size(); k++) {
                                String wireId = (String) wires.get(k);
                                for (int l = 0; l < nodes.size(); l++) {
                                    if (nodes.get(l).getId().equals(wireId)) {
                                        Wire wire = new Wire();
                                        switchNode.connectOutputWire(j, wire);
                                        nodes.get(l).getClass()
                                                .getMethod("connectInputWire", Wire.class)
                                                .invoke(nodes.get(l), wire);
                                    }
                                }
                            }
                        }
                        break;
                    case "change":
                        ChangeNode changeNode = null;
                        for (int j = 0; j < nodes.size(); j++) {
                            if (nodes.get(j).getId().equals(jsonObject.get("id"))) {
                                changeNode = (ChangeNode) nodes.get(j);
                                break;
                            }
                        }
                        JSONArray changeWires = (JSONArray) jsonObject.get("wires");
                        for (int j = 0; j < changeWires.size(); j++) {
                            JSONArray wires = (JSONArray) changeWires.get(j);
                            for (int k = 0; k < wires.size(); k++) {
                                String wireId = (String) wires.get(k);
                                for (int l = 0; l < nodes.size(); l++) {
                                    if (nodes.get(l).getId().equals(wireId)) {
                                        Wire wire = new Wire();
                                        changeNode.connectOutputWire(j, wire);
                                        nodes.get(l).getClass()
                                                .getMethod("connectInputWire", Wire.class)
                                                .invoke(nodes.get(l), wire);
                                    }
                                }
                            }
                        }
                        break;
                    case "template":
                        TemplateNode templateNode = null;
                        for (int j = 0; j < nodes.size(); j++) {
                            if (nodes.get(j).getId().equals(jsonObject.get("id"))) {
                                templateNode = (TemplateNode) nodes.get(j);
                                break;
                            }
                        }
                        JSONArray templateWires = (JSONArray) jsonObject.get("wires");
                        for (int j = 0; j < templateWires.size(); j++) {
                            JSONArray wires = (JSONArray) templateWires.get(j);
                            for (int k = 0; k < wires.size(); k++) {
                                String wireId = (String) wires.get(k);
                                for (int l = 0; l < nodes.size(); l++) {
                                    if (nodes.get(l).getId().equals(wireId)) {
                                        Wire wire = new Wire();
                                        templateNode.connectOutputWire(j, wire);
                                        nodes.get(l).getClass()
                                                .getMethod("connectInputWire", Wire.class)
                                                .invoke(nodes.get(l), wire);
                                    }
                                }
                            }
                        }
                        break;
                    case "debug":
                        break;
                    default:
                        log.info(type + " is not supported yet");
                        break;
                }
            }

            for (int i = 0; i < nodes.size(); i++) {
                nodes.get(i).start();
            }

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // public void generateDefaultFlows(String host, String[] sensors) {
    // Broker broker = new Broker("1", host, 1883, true, 60, true);
    // MqttInNode mqttInNode = new MqttInNode("test", "application/#", 2, broker);
    // mqttInNode.start();

    // SwitchNode switchNode1 = new SwitchNode("object", 1, "payload",
    // PropertyType.MSG, new JSONArray() {
    // {
    // add(new JSONObject() {
    // {
    // put("t", "hask");
    // put("v", "object");
    // put("vt", "str");
    // }
    // });
    // }
    // }, true);
    // SwitchNode switchNode2 = new SwitchNode("deviceinfo", 1, 1, 1, "payload",
    // PropertyType.MSG, new JSONArray() {
    // {
    // add(new JSONObject() {
    // {
    // put("t", "hask");
    // put("v", "deviceInfo");
    // put("vt", "str");
    // }
    // });
    // }
    // }, true);
    // Wire wire1 = new Wire();
    // Wire wire2 = new Wire();
    // mqttInNode.connectOutputWire(0, wire1);
    // switchNode1.connectInputWire(wire1);
    // switchNode1.connectOutputWire(0, wire2);
    // switchNode2.connectInputWire( wire2);
    // SwitchNode switchNode3 = new SwitchNode("nhnacademygyeongnam", 1, 1, 1,
    // "payload.deviceInfo.tenantName",
    // propertyType.msg, new JSONArray() {
    // {
    // add(new JSONObject() {
    // {
    // put("t", "eq");
    // put("v", "NHN Academy 경남");
    // put("vt", "str");
    // }
    // });
    // }
    // }, true);
    // Wire wire3 = new Wire();
    // switchNode2.connectOutputWire(0, 0, wire3);
    // switchNode3.connectInputWire(0, wire3);
    // SwitchNode switchNode4 = new SwitchNode("site", 1, 1, 1,
    // "payload.deviceInfo.tags", propertyType.msg,
    // new JSONArray() {
    // {
    // add(new JSONObject() {
    // {
    // put("t", "hask");
    // put("v", "site");
    // put("vt", "str");
    // }
    // });
    // }
    // }, true);
    // Wire wire4 = new Wire();
    // switchNode3.connectOutputWire(0, 0, wire4);
    // switchNode4.connectInputWire(0, wire4);
    // SwitchNode switchNode5 = new SwitchNode("branch", 1, 1, 1,
    // "payload.deviceInfo.tags", propertyType.msg,
    // new JSONArray() {
    // {
    // add(new JSONObject() {
    // {
    // put("t", "hask");
    // put("v", "branch");
    // put("vt", "str");
    // }
    // });
    // }
    // }, true);
    // Wire wire5 = new Wire();
    // switchNode4.connectOutputWire(0, 0, wire5);
    // switchNode5.connectInputWire(0, wire5);
    // SwitchNode switchNode6 = new SwitchNode("place", 1, 1, 1,
    // "payload.deviceInfo.tags", propertyType.msg,
    // new JSONArray() {
    // {
    // add(new JSONObject() {
    // {
    // put("t", "hask");
    // put("v", "place");
    // put("vt", "str");
    // }
    // });
    // }
    // }, true);
    // Wire wire6 = new Wire();
    // switchNode5.connectOutputWire(0, 0, wire6);
    // switchNode6.connectInputWire(0, wire6);
    // ChangeNode changeNode1 = new ChangeNode("insert time", 1, 1, sensors.length,
    // new JSONArray() {
    // {
    // add(new JSONObject() {
    // {
    // put("t", "set");
    // put("p", "payload.tempPayload.payload.time");
    // put("pt", "msg");
    // put("to", "");
    // put("tot", "date");
    // }
    // });
    // }
    // });

    // Wire wire7 = new Wire();
    // switchNode6.connectOutputWire(0, 0, wire7);
    // changeNode1.connectInputWire(0, wire7);
    // switchNode1.start();
    // switchNode2.start();
    // switchNode3.start();
    // switchNode4.start();
    // switchNode5.start();
    // switchNode6.start();
    // changeNode1.start();

    // MqttOutNode mqttOutNode = new MqttOutNode("mqtt out", sensors.length,
    // broker);
    // mqttOutNode.start();
    // for (int i = 0; i < sensors.length; i++) {
    // String sensor = sensors[i];
    // SwitchNode switchNode7 = new SwitchNode("has " + sensor, 1, 1, 1,
    // "payload.object", propertyType.msg,
    // new JSONArray() {
    // {
    // add(new JSONObject() {
    // {
    // put("t", "hask");
    // put("v", sensor);
    // put("vt", "str");
    // }
    // });
    // }
    // }, true);
    // Wire wire8 = new Wire();
    // changeNode1.connectOutputWire(0, i, wire8);
    // switchNode7.connectInputWire(0, wire8);
    // ChangeNode changeNode2 = new ChangeNode("insert " + sensor, 1, 1, 1, new
    // JSONArray() {
    // {
    // add(new JSONObject() {
    // {
    // put("t", "set");
    // put("p", "payload.tempPayload.payload." + sensor);
    // put("pt", "msg");
    // put("to", "payload.object." + sensor);
    // put("tot", "msg");
    // put("dc", true);
    // }
    // });
    // }
    // });
    // Wire wire9 = new Wire();
    // switchNode7.connectOutputWire(0, 0, wire9);
    // changeNode2.connectInputWire(0, wire9);
    // TemplateNode templateNode = new TemplateNode("insert template" + sensor, 1,
    // 1, "payload.tempPayload.topic",
    // fieldType.msg,
    // "data/s/{{payload.deviceInfo.tags.site}}/b/{{payload.deviceInfo.tenantName}}/p/{{payload.deviceInfo.tags.place}}/n/{{payload.deviceInfo.deviceName}}/e/"
    // + sensor);
    // Wire wire12 = new Wire();
    // changeNode2.connectOutputWire(0, 0, wire12);
    // templateNode.connectInputWire(0, wire12);
    // ChangeNode changeNode4 = new ChangeNode("add topic", 1, 1, 1, new JSONArray()
    // {
    // {
    // add(new JSONObject() {
    // {
    // put("t", "set");
    // put("p", "topic");
    // put("pt", "msg");
    // put("to", "payload.tempPayload.topic");
    // put("tot", "msg");
    // put("dc", true);
    // }
    // });
    // }
    // });
    // Wire wire10 = new Wire();
    // templateNode.connectOutputWire(0, 0, wire10);
    // changeNode4.connectInputWire(0, wire10);
    // ChangeNode changeNode3 = new ChangeNode("add payload", 1, 1, 2, new
    // JSONArray() {
    // {
    // add(new JSONObject() {
    // {
    // put("t", "set");
    // put("p", "payload");
    // put("pt", "msg");
    // put("to", "payload.tempPayload.payload");
    // put("tot", "msg");
    // put("dc", true);
    // }
    // });
    // }
    // });
    // Wire wire13 = new Wire();
    // changeNode4.connectOutputWire(0, 0, wire13);
    // changeNode3.connectInputWire(0, wire13);
    // Wire wire11 = new Wire();
    // changeNode3.connectOutputWire(0, 0, wire11);
    // mqttOutNode.connectInputWire(i, wire11);
    // DebugNode debugNode = new DebugNode("debug", 1, true, true, true, true,
    // targetType.full, "true");
    // Wire wire14 = new Wire();
    // changeNode3.connectOutputWire(0, 1, wire14);
    // debugNode.connectInputWire(0, wire14);
    // switchNode7.start();
    // changeNode2.start();
    // templateNode.start();
    // changeNode4.start();
    // changeNode3.start();
    // debugNode.start();
    // }
    // }

    public static void main(String[] args) {
        NodeRedSystem.getInstance().load("src/main/resources/flows.json");
    }
}
