package com.nhnacademy.system;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.nhnacademy.node.InputNode;
import com.nhnacademy.node.InputOutputNode;
import com.nhnacademy.node.MqttInNode;
import com.nhnacademy.node.MqttOutNode;
import com.nhnacademy.node.Node;
import com.nhnacademy.node.OutputNode;
import com.nhnacademy.wire.Wire;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SystemOption {

    private static final String DEFAULT_FLOW_FILE_PATH = "src/main/resources/flows.json";
    private static final String CLASS_PATH = "com.nhnacademy.node.";
    private static final String CLASS_NAMES = "{\n" + //
            "    \"mqtt in\": \"MqttInNode\",\n" + //
            "    \"mqtt out\": \"MqttOutNode\",\n" + //
            "    \"functionNode\": \"FunctionNode\"\n" + //
            "}";

    private static JSONParser jsonParser = new JSONParser();

    private HashMap<String, Object> nodeList;
    private HashMap<String, JSONArray> wireInfo;
    private JSONArray nodesInfo;
    private String filePath;
    private boolean isCommandMode = false;

    private String applicationName;
    private String[] sensors;

    public SystemOption(String[] args) {
        nodeList = new HashMap<>();
        wireInfo = new HashMap<>();
        filePath = DEFAULT_FLOW_FILE_PATH;
        inspectCommandLine(args);
    }

    private void inspectCommandLine(String[] args) {
        if (args.length > 0) {
            Options options = new Options();
            options.addOption("c", false, "command line");
            options.addOption("an", "an", true, "application name");
            options.addOption("s", true, "setting sensor");

            try {
                nodesInfo = (JSONArray) jsonParser.parse(new FileReader(filePath));
                CommandLineParser parser = new DefaultParser();
                CommandLine commandLine = parser.parse(options, args);
                if (commandLine.hasOption("c")) {
                    isCommandMode = true;
                    if (commandLine.hasOption("an")) {
                        applicationName = commandLine.getOptionValue("an");

                    }
                    if (commandLine.hasOption("s")) {
                        sensors = commandLine.getOptionValue("s").split(",");
                    }

                } else if (new File(args[0]).exists()) {
                    filePath = args[0];

                } else {
                    throw new IllegalArgumentException("Invalid command line argument format");
                }
            } catch (ParseException e) {
                log.error("commandLine parsing error");
            } catch (IOException | org.json.simple.parser.ParseException e) {
                log.error("JSON File parsing error");
            }
        }
    }

    public void createNodes() {
        for (Object obj : nodesInfo) {
            if (!(obj instanceof JSONObject)) {
                continue;
            }
            log.debug(obj.toString());
            JSONObject node = (JSONObject) obj;

            if (node.containsKey("id") && node.containsKey("type")) {
                Object instance = getInstance(node);
                if (isCommandMode) {
                    node.put("an", applicationName);
                    node.put("sensors", sensors);
                }

                nodeList.put(node.get("id").toString(), instance);
                wireInfo.put(node.get("id").toString(), (JSONArray) node.get("wires"));
                // log.trace(nodeList.toString());
                // log.info(wireInfo.toString());
            }
        }
    }

    private Object getInstance(JSONObject node) {
        Object instance = null;

        try {
            JSONObject classNames = (JSONObject) jsonParser.parse(CLASS_NAMES);
            Class<?> nodeClass = Class.forName(CLASS_PATH + classNames.get(node.get("type").toString()));
            // log.info(Arrays.toString(nodeClass.getConstructors()));

            for (Constructor<?> constructor : nodeClass.getConstructors()) {
                if (constructor.getParameterTypes()[0] == int.class
                        && constructor.getParameterTypes()[1] == JSONObject.class) {
                    if (nodeClass.equals(MqttInNode.class)) {
                        instance = constructor.newInstance(((JSONArray) node.get("wires")).size(), node);
                    } else if(nodeClass.equals(MqttOutNode.class)){
                        instance = constructor.newInstance(((JSONArray) node.get("wires")).size() + 1, node);
                    }
                    break;

                } else if (constructor.getParameterTypes()[0] == int.class
                        && constructor.getParameterTypes()[1] == int.class
                        && constructor.getParameterTypes()[2] == JSONObject.class) {
                    instance = constructor.newInstance(1, ((JSONArray) node.get("wires")).size(), node);
                    break;
                }
            }
            if (instance == null) {
                throw new IllegalArgumentException("No suitable constructor found for " + nodeClass.getSimpleName());
            }

        } catch (Exception e) {
            log.error("Error creating instance of {}: {}", node.get("type"), e.getMessage());
            e.printStackTrace();
        }

        return instance;
    }

    public void createFlow() {
        /*
         * inputNode면 connectOutputWire()
         * outputNode면 connectInputWire()
         * inputOutputNode면 둘다
         */
        log.info(nodeList.entrySet().toString());
        log.info(wireInfo.entrySet().toString());
        for (Map.Entry<String, Object> entry : nodeList.entrySet()) {
            String id = entry.getKey();
            Object node = entry.getValue();
            if (node == null){
                continue;
            }
            if(node instanceof InputNode){
                InputNode inputNode = (InputNode) node;

            }
        }
    }
}