package com.nhnacademy.node;

import org.json.simple.JSONObject;

import com.nhnacademy.exception.JSONMessageTypeException;
import com.nhnacademy.message.JsonMessage;
import com.nhnacademy.message.Message;
import com.nhnacademy.wire.Wire;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DebugNode extends OutputNode {
    public enum TargetType {
        MSG, FULL
    }

    private Boolean active;
    private Boolean tosidebar;
    private Boolean console;
    private Boolean tostatus;
    private TargetType type;
    private String[] keys;

    public DebugNode(String id, Boolean active, Boolean tosidebar, Boolean console, Boolean tostatus,
            TargetType type,
            String complete) {
        super(id);
        this.active = active;
        this.tosidebar = tosidebar;
        this.console = console;
        this.tostatus = tostatus;
        this.type = type;
        this.keys = JsonMessage.splitKeys(complete);
    }

    public DebugNode(Boolean active, Boolean tosidebar, Boolean console, Boolean tostatus, TargetType type,
            String complete) {
        super();
        this.active = active;
        this.tosidebar = tosidebar;
        this.console = console;
        this.tostatus = tostatus;
        this.type = type;
        this.keys = JsonMessage.splitKeys(complete);
    }

    public DebugNode generate(JSONObject jsonObject) {
        String id = (String) jsonObject.get("id");
        Boolean active = (Boolean) jsonObject.get("active");
        Boolean tosidebar = (Boolean) jsonObject.get("tosidebar");
        Boolean console = (Boolean) jsonObject.get("console");
        Boolean tostatus = (Boolean) jsonObject.get("tostatus");
        TargetType type = TargetType.valueOf((String) jsonObject.get("type"));
        String complete = (String) jsonObject.get("complete");
        return new DebugNode(id, active, tosidebar, console, tostatus, type, complete);
    }

    // 참고용
    // public String[] parseComplete(String keys) {
    // if (keys == null) {
    // return new String[] { "payload" };
    // }
    // if (!keys.contains(".")) {
    // return new String[] { keys };
    // }
    // return keys.split("\\.");
    // }

    @Override
    void process() {
        for (int i = 0; i < getInputWireCount(); i++) {
            Wire wire = getInputWire(i);
            if (wire == null || !wire.hasMessage() || active.equals(false))
                continue;

            Message message = wire.get();
            if (!(message instanceof JsonMessage))
                throw new JSONMessageTypeException(getId() + " : Message is not JsonMessage");
            JSONObject messagJsonObject = ((JsonMessage) message).getJsonObject();
            if (tosidebar.equals(true)) {
                if (type == TargetType.MSG) {
                    JSONObject destJsonObject = JsonMessage.getDestJsonObject(messagJsonObject, keys);
                    log.info(destJsonObject.get(keys[keys.length - 1]).toString());
                } else if (type == TargetType.FULL) {
                    log.info(messagJsonObject.toString());
                }
            }
            if (tostatus.equals(true)) {
                // 미구현
            }
            if (console.equals(true)) {
                // 미구현
            }

        }
    }
}
