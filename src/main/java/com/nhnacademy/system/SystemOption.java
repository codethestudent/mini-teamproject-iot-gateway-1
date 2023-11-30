package com.nhnacademy.system;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
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
        options.addOption("s", true, "sensor");
        options.addOption("t", true, "topic");

        JSONParser jsonParser = new JSONParser();
        CommandLineParser parser = new DefaultParser();
        try {
            jsonFile = (JSONObject) jsonParser.parse(new FileReader(filePath));

            JSONObject input;
            if (jsonFile.containsKey("input")) {
                input = (JSONObject) jsonFile.get("input");
            } else {
                throw new NullPointerException("input is null");
            }

            CommandLine commandLine = parser.parse(options, this.args);
            if (args != null && commandLine.hasOption("c")) {
                if (commandLine.hasOption("an")) {
                    applicationName = commandLine.getOptionValue("an");
                } else if (input.containsKey("applicationName")) {
                    applicationName = (String) input.get("applicationName");
                } else {
                    throw new NullPointerException("applicationName is null");
                }

                if (commandLine.hasOption("s")) {
                    sensors = commandLine.getOptionValue("s").split(",");
                } else if (input.containsKey("sensors")) {
                    JSONArray sensorsArr = (JSONArray) input.get("sensors");
                    sensors = new String[sensorsArr.size()];

                    int index = 0;
                    Iterator<String> sensorsIter = sensorsArr.iterator();
                    while (sensorsIter.hasNext()) {
                        sensors[index++] = sensorsIter.next();
                    }
                } else {
                    throw new NullPointerException("sensors is null");
                }

                if (commandLine.hasOption("t")) {
                    topic = commandLine.getOptionValue("t");
                } else if (input.containsKey("topic")) {
                    topic = (String) input.get("topic");
                } else {
                    throw new NullPointerException("topic is null");
                }

            } else {
                topic = (String) input.get("topic");
                applicationName = (String) input.get("applicationName");
                JSONArray sensorsArr = (JSONArray) input.get("sensors");
                sensors = new String[sensorsArr.size()];

                int index = 0;
                Iterator<String> sensorsIter = sensorsArr.iterator();
                while (sensorsIter.hasNext()) {
                    sensors[index++] = sensorsIter.next();
                }
            }

        } catch (ParseException | IOException | org.json.simple.parser.ParseException e) {
            e.printStackTrace();
        }
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
        return (String) ((JSONObject) jsonFile.get("input")).get("server");
    }

    public String getOutputServerUri() {
        return (String) ((JSONObject) jsonFile.get("output")).get("server");
    }

}
