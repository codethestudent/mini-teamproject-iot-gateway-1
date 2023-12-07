package com.nhnacademy.node;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

import com.nhnacademy.exception.JSONMessageTypeException;
import com.nhnacademy.message.JsonMessage;
import com.nhnacademy.message.Message;
import com.nhnacademy.system.UndefinedJsonObject;
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
    private String[] complete;

    public DebugNode(String id, Boolean active, Boolean tosidebar, Boolean console, Boolean tostatus,
            TargetType type,
            String complete) {
        super(id);
        this.active = active;
        this.tosidebar = tosidebar;
        this.console = console;
        this.tostatus = tostatus;
        this.type = type;
        this.complete = parseComplete(complete);
    }

    public DebugNode(Boolean active, Boolean tosidebar, Boolean console, Boolean tostatus, TargetType type,
            String complete) {
        super();
        this.active = active;
        this.tosidebar = tosidebar;
        this.console = console;
        this.tostatus = tostatus;
        this.type = type;
        this.complete = parseComplete(complete);
    }

    public String[] parseComplete(String complete) {
        if (complete == null) {
            return new String[] { "payload" };
        }
        if (!complete.contains(".")) {
            return new String[] { complete };
        }
        return complete.split("\\.");
    }

    void toSidebarMethod(JsonMessage jsonMessage, TargetType type, String[] complete) {
        if (type == TargetType.MSG) {
            JSONObject destJsonObject = JsonMessage.getDestJsonObject(jsonMessage.getJsonObject(), complete);
            log.info(destJsonObject.get(complete[complete.length - 1]).toString());
        } else if (type == TargetType.FULL) {
            log.info(jsonMessage.getJsonObject().toString());
        }

    }

    @Override
    void process() {
        for (int i = 0; i < getInputWireCount(); i++) {
            Wire wire = getInputWire(i);
            if (wire == null || !wire.hasMessage() || active.equals(false))
                continue;

            Message message = wire.get();
            if (!(message instanceof JsonMessage))
                throw new JSONMessageTypeException(getId() + " : Message is not JsonMessage");

            if (tosidebar.equals(true)) {
                toSidebarMethod((JsonMessage) message, type, complete);
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
