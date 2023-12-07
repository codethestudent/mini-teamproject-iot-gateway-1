package com.nhnacademy.node;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class test {
    public static void main(String[] args) {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");
        JSONObject jsonObject = new JSONObject();
        JSONParser parser = new JSONParser();
        String s = "{\"topic\":\"application/9a87910d-fc6c-4f10-a6fd-a626a1ca6b4a/device/24e124785c421885/event/up\",\"payload\":{\"deduplicationId\":\"c2007a4a-cdea-4186-b72c-36050a750375\",\"time\":\"2023-12-05T04:20:33.504+00:00\",\"deviceInfo\":{\"tenantId\":\"a3e5ac43-3f8a-45da-91a6-e07b18cc6646\",\"tenantName\":\"NHN Academy 경남\",\"applicationId\":\"9a87910d-fc6c-4f10-a6fd-a626a1ca6b4a\",\"applicationName\":\"NHNAcademyEMS\",\"deviceProfileId\":\"63c902e6-d73a-4edc-9ab6-8272d780fb95\",\"deviceProfileName\":\"EM320-TH-OTTA\",\"deviceName\":\"강의실A(EM320-TH-421885)\",\"devEui\":\"24e124785c421885\",\"deviceClassEnabled\":\"CLASS_A\",\"tags\":{\"site\":\"nhnacademy\",\"branch\":\"gyeongnam\",\"place\":\"class_a\"}},\"devAddr\":\"01aff431\",\"adr\":true,\"dr\":2,\"fCnt\":170342,\"fPort\":85,\"confirmed\":false,\"data\":\"AXUmA2fmAARoPw==\",\"object\":{\"batteryLevel\":38,\"temperature\":23,\"humidity\":31.5},\"rxInfo\":[{\"gatewayId\":\"24e124fffef5dccc\",\"uplinkId\":40270,\"time\":\"2023-12-05T04:20:33.504082+00:00\",\"timeSinceGpsEpoch\":\"1385785251.504s\",\"rssi\":-81,\"snr\":13,\"channel\":6,\"rfChain\":1,\"location\":{},\"context\":\"J44MIA==\",\"metadata\":{\"region_common_name\":\"KR920\",\"region_config_id\":\"kr920\"},\"crcStatus\":\"CRC_OK\"},{\"gatewayId\":\"24e124fffef79114\",\"uplinkId\":17277,\"time\":\"2023-12-05T04:20:33.506458+00:00\",\"timeSinceGpsEpoch\":\"1385785251.506s\",\"rssi\":-42,\"snr\":13,\"channel\":6,\"rfChain\":1,\"location\":{},\"context\":\"XVkaTw==\",\"metadata\":{\"region_config_id\":\"kr920\",\"region_common_name\":\"KR920\"},\"crcStatus\":\"CRC_OK\"}],\"txInfo\":{\"frequency\":923300000,\"modulation\":{\"lora\":{\"bandwidth\":125000,\"spreadingFactor\":10,\"codeRate\":\"CR_4_5\"}}}},\"qos\":0,\"retain\":false,\"_msgid\":\"4ee3cbaa0d97ea19\"}";
        try {
            jsonObject = (JSONObject) parser.parse(s);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        try {
            engine.put("msg", jsonObject);
            engine.eval("var value = {};\n" + //
                    "for (var o in msg.payload.object)\n" + //
                    "    value[o] = msg.payload.object[o]\n" + //
                    "msg.topic = \"data/d/\" + msg.payload.deviceInfo[\"devEui\"] + \"/p/\" + String(msg.payload.deviceInfo[\"deviceName\"]).split('(')[0] + \"/e/temperature_humidity\";\n"
                    + //
                    "msg.payload = {\n" + //
                    "    time:new Date().getTime()\n" + //
                    "}\n" + //
                    "for (var o in value)\n" + //
                    "    msg.payload[o] = value[o];\n"
                    );
            Object o = engine.get("msg");
            System.out.println(o.toString());
        } catch (ScriptException e) {
            System.err.println(e.getMessage());
        }
    }
}
