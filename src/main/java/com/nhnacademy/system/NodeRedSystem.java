package com.nhnacademy.system;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.FileReader;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.nhnacademy.node.ActiveNode;
import com.nhnacademy.node.Broker;
import com.nhnacademy.node.ChangeNode;
import com.nhnacademy.node.DebugNode;
import com.nhnacademy.node.MqttInNode;
import com.nhnacademy.node.MqttOutNode;
import com.nhnacademy.node.SwitchNode;
import com.nhnacademy.node.TemplateNode;
import com.nhnacademy.node.SwitchNode.propertyType;
import com.nhnacademy.node.TemplateNode.fieldType;
import com.nhnacademy.wire.Wire;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NodeRedSystem {
    private static NodeRedSystem instance = new NodeRedSystem();
    ArrayList<ActiveNode> nodes = new ArrayList<>();
    ArrayList<Broker> brokers = new ArrayList<>();

    private NodeRedSystem() {
        super();
    }

    public static NodeRedSystem getInstance() {
        if (instance == null)
            instance = new NodeRedSystem();
        return instance;
    }

    public void load(String path) {
        try {
            Reader reader = new FileReader(path);
            JSONParser parser = new JSONParser();
            JSONArray jsonArray = (JSONArray) parser.parse(reader);
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
                        MqttInNode mqttInNode = new MqttInNode(mqttInId, 10, mqttInTopic, mqttInQos, mqttInBrokerObj);
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
                        MqttOutNode mqttOutNode = new MqttOutNode(mqttOutId, 10,
                                mqttOutBrokerObj);
                        nodes.add(mqttOutNode);
                        break;
                    case "switch":
                        String switchId = (String) jsonObject.get("id");
                        String switchProperty = (String) jsonObject.get("property");
                        String switchPropertyType = (String) jsonObject.get("propertyType");
                        JSONArray switchRules = (JSONArray) jsonObject.get("rules");
                        Boolean switchCheckall = Boolean.parseBoolean((String) jsonObject.get("checkall"));
                        SwitchNode switchNode = new SwitchNode(switchId, 10, 10, 10, switchProperty,
                                propertyType.valueOf(switchPropertyType), switchRules, switchCheckall);
                        nodes.add(switchNode);
                        break;
                    case "change":
                        String changeId = (String) jsonObject.get("id");
                        JSONArray changeRules = (JSONArray) jsonObject.get("rules");
                        ChangeNode changeNode = new ChangeNode(changeId, 10, 10, 10, changeRules);
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
                        DebugNode debugNode = new DebugNode(debugId, 10, debugActive, debugTosidebar, debugConsole,
                                debugTostatus, DebugNode.targetType.valueOf(debugTargetType), debugComplete);
                        nodes.add(debugNode);
                        break;
                    case "template":
                        String templateId = (String) jsonObject.get("id");
                        String templateField = (String) jsonObject.get("field");
                        String templateFieldType = (String) jsonObject.get("fieldType");
                        String templateTemplate = (String) jsonObject.get("template");
                        TemplateNode templateNode = new TemplateNode(templateId, 10, 10, templateField,
                                fieldType.valueOf(templateFieldType), templateTemplate);
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
                            if (nodes.get(j).getId().equals((String) jsonObject.get("id"))) {
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
                                        mqttInNode.connectOutputWire(k, wire);
                                        for (int m = 0; m < 10; m++) {
                                            if (nodes.get(l).getClass().getMethod("getInputWire", int.class)
                                                    .invoke(nodes.get(l), m) == null) {
                                                nodes.get(l).getClass()
                                                        .getMethod("connectInputWire", int.class, Wire.class)
                                                        .invoke(nodes.get(l), m, wire);
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        break;
                    case "mqtt out":
                        MqttOutNode mqttOutNode = null;
                        for (int j = 0; j < nodes.size(); j++) {
                            if (nodes.get(j).getId().equals((String) jsonObject.get("id"))) {
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
                                        switchNode.connectOutputWire(j, k, wire);
                                        for (int m = 0; m < 10; m++) {
                                            if (nodes.get(l).getClass().getMethod("getInputWire", int.class)
                                                    .invoke(nodes.get(l), m) == null) {
                                                nodes.get(l).getClass()
                                                        .getMethod("connectInputWire", int.class, Wire.class)
                                                        .invoke(nodes.get(l), m, wire);
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        break;
                    case "change":
                        ChangeNode changeNode = null;
                        for (int j = 0; j < nodes.size(); j++) {
                            if (nodes.get(j).getId().equals((String) jsonObject.get("id"))) {
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
                                        changeNode.connectOutputWire(j, k, wire);
                                        for (int m = 0; m < 10; m++) {
                                            if (nodes.get(l).getClass().getMethod("getInputWire", int.class)
                                                    .invoke(nodes.get(l), m) == null) {
                                                nodes.get(l).getClass()
                                                        .getMethod("connectInputWire", int.class, Wire.class)
                                                        .invoke(nodes.get(l), m, wire);
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        break;
                    case "template":
                        TemplateNode templateNode = null;
                        for (int j = 0; j < nodes.size(); j++) {
                            if (nodes.get(j).getId().equals((String) jsonObject.get("id"))) {
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
                                        templateNode.connectOutputWire(j, k, wire);
                                        for (int m = 0; m < 10; m++) {
                                            if (nodes.get(l).getClass().getMethod("getInputWire", int.class)
                                                    .invoke(nodes.get(l), m) == null) {
                                                nodes.get(l).getClass()
                                                        .getMethod("connectInputWire", int.class, Wire.class)
                                                        .invoke(nodes.get(l), m, wire);
                                                break;
                                            }
                                        }
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

    public static void main(String[] args) {
        NodeRedSystem.getInstance().load("src/main/resources/flows.json");
    }
}
