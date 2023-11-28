package com.nhnacademy.system;

public class SystemOption {
    private static SystemOption systemOption;

    private static String DEFAULT = "--an \"application\" -s temperature,humidity";
    private String commandLine;
    private String applicationName;
    private String[] sensors;

    private SystemOption(String commandLine) {
        this.commandLine = commandLine;
        setInfo();
    }

    private void setInfo() {
        applicationName = commandLine.split(" ")[1];
        sensors = commandLine.split(" ")[3].split(",");
    }

    public static SystemOption getSystemOption() {
        if (systemOption == null) {
            systemOption = new SystemOption(DEFAULT);
        }

        return systemOption;
    }

    public SystemOption getSystemOption(String commandLine) {
        if (systemOption == null) {
            systemOption = new SystemOption(commandLine);
        }

        return systemOption;
    }

    public String getApplicationNamme() {
        return applicationName;
    }

    public String[] getSensors() {
        return sensors;
    }

}
