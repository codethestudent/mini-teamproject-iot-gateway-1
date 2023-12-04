package com.nhnacademy.system;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SystemOption {
    private static SystemOption systemOption;

    private static final String DEFAULT_FILE_PATH = "src/main/resources/systemSetting.json";
    private static final String KEY_TOPIC = "topic";
    private static final String KEY_INPUT = "input";
    private static final String KEY_AN = "applicationName";
    private static final String KEY_SENSORS = "sensors";

    private CommandLine commandLine;

    private String filePath;
    private JSONObject jsonFile;
    private String[] args;

    private String topic;
    private String applicationName;
    private String[] sensors;

    private SystemOption(String filePath) {
        this.filePath = filePath;
        setInfo();
    }

    private SystemOption(String[] args) {
        this.args = args;
        filePath = DEFAULT_FILE_PATH;
        setInfo();
    }

    private void setInfo() {
        Options options;
        options = new Options();
        options.addOption("c", false, "command line");
        options.addOption("an", "an", true, "application name");
        options.addOption("s", true, "setting sensor");
        options.addOption("t", true, "setting topic");

        CommandLineParser parser = new DefaultParser();
        JSONObject input = getJSONFileValue(KEY_INPUT);

        try {
            commandLine = parser.parse(options, this.args);
            if (args != null && commandLine.hasOption("c")) {
                commandLineSetting(input);
                NodeRedSystem.getInstance().load(commandLine.getOptionValue("c"));
            } else {
                NodeRedSystem.getInstance().generateDefaultFlows("ems.nhnacademy.com", sensors);

            }

        } catch (ParseException e) {
            log.error("command line parsing error");
        }
    }

    private void commandLineSetting(JSONObject input) {

        if (commandLine.hasOption("an")) {
            applicationName = commandLine.getOptionValue("an");
        } else if (input != null && input.containsKey(KEY_AN)) {
            applicationName = (String) input.get(KEY_AN);
        } else {
            throw new NullPointerException("applicationName is null");
        }

        if (commandLine.hasOption("s")) {
            sensors = commandLine.getOptionValue("s").split(",");
        } else if (input != null && input.containsKey(KEY_SENSORS)) {
            JSONArray sensorsArr = (JSONArray) input.get(KEY_SENSORS);
            sensors = new String[sensorsArr.size()];

            int index = 0;
            for (Object sensorObj : sensorsArr) {
                sensors[index++] = sensorObj.toString();
            }

        } else {
            throw new NullPointerException("sensors is null");
        }

        if (commandLine.hasOption("t")) {
            topic = commandLine.getOptionValue("t");
        } else if (input != null && input.containsKey(KEY_TOPIC)) {
            topic = (String) input.get(KEY_TOPIC);
        } else {
            throw new NullPointerException("topic is null");
        }

    }

    private void jsonFileSetting(JSONObject input) {
        if (input != null) {
            topic = (String) input.get(KEY_TOPIC);
            applicationName = (String) input.get(KEY_AN);
            JSONArray sensorsArr = (JSONArray) input.get(KEY_SENSORS);
            sensors = new String[sensorsArr.size()];

            int index = 0;
            for (Object sensorObj : sensorsArr) {
                sensors[index++] = sensorObj.toString();
            }
        }
    }

    private JSONObject getJSONFileValue(String key) {
        JSONParser jsonParser = new JSONParser();
        JSONObject value = null;
        try {
            jsonFile = (JSONObject) jsonParser.parse(new FileReader(filePath));

            if (jsonFile.containsKey(key)) {
                if (jsonFile instanceof JSONObject) {
                    value = (JSONObject) jsonFile.get(key);
                } else {
                    throw new IllegalArgumentException();
                }
            } else {
                throw new NullPointerException("input is not exist");
            }

        } catch (IOException | org.json.simple.parser.ParseException e) {
            log.error("");
        }
        return value;
    }

    public static SystemOption getSystemOption() {
        return getSystemOption(DEFAULT_FILE_PATH);
    }

    public static SystemOption getSystemOption(String filePath) {
        if (systemOption == null) {
            systemOption = new SystemOption(filePath);
        }

        return systemOption;
    }

    public static SystemOption getSystemOption(String[] args) {
        if (systemOption == null && args.length > 0) {
            if (args[0].equals("-c")) {
                systemOption = new SystemOption(args);
            } else if ((new File(args[0])).exists()) {
                getSystemOption(args[0]);
            } else if (args[0].length() == 0) {
                getSystemOption();
            }
        } else {
            getSystemOption();
        }

        return systemOption;
    }

    public String getTopic() {
        return topic;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public String[] getSensors() {
        return sensors;
    }

    public String getInputServerUri() {
        return (String) ((JSONObject) jsonFile.get(KEY_INPUT)).get("server");
    }

    public String getOutputServerUri() {
        return (String) ((JSONObject) jsonFile.get("output")).get("server");
    }

}
