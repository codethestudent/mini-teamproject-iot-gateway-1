package com.nhnacademy;

import com.nhnacademy.system.NodeRedSystem;

public class Main {
    public static void main(String[] args) {
        NodeRedSystem.getInstance().generateDefaultFlows("ems.nhnacademy.com",
                new String[] { "temperature", "humidity", "co2" });
    }
}
