package com.nhnacademy.system;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class SystemOption {
    private static SystemOption systemOption;

    private static String[] DEFAULT = { "--an", "\"application\"", "-s", "temperature,humidity" };
    private String[] args;
    private String applicationName;
    private String[] sensors;
    private Options options;

    private SystemOption(String[] args) {
        options = new Options();
        this.args = args;
        options.addOption("an", "an", true, "application name");
        options.addOption("s", true, "sensor");
        setInfo();
    }

    private void setInfo() {
        // applicationName = commandLine.split(" ")[1];
        // sensors = commandLine.split(" ")[3].split(",");
        CommandLineParser parser = new DefaultParser();
        CommandLine commandLine;
        try {
            commandLine = parser.parse(options, this.args);
            if (commandLine.hasOption("an")) {
                applicationName = commandLine.getOptionValue("an");
            }
            if (commandLine.hasOption("s")) {
                sensors = commandLine.getOptionValue("s").split(",");
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static SystemOption getSystemOption() {
        if (systemOption == null) {
            systemOption = new SystemOption(DEFAULT);
        }

        return systemOption;
    }

    public static SystemOption getSystemOption(String[] args) {
        if (systemOption == null) {
            systemOption = new SystemOption(args);
        }

        return systemOption;
    }

    public String getApplicationNamme() {
        return applicationName;
    }

    public String[] getSensors() {
        return sensors;
    }

\
}
