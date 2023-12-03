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
    public enum targetType {
        msg, full
    }

    private Boolean active;
    private Boolean tosidebar;
    private Boolean console;
    private Boolean tostatus;
    private targetType type;
    private String[] complete;

    public DebugNode(String id, int inCount, Boolean active, Boolean tosidebar, Boolean console, Boolean tostatus,
            targetType type,
            String complete) {
        super(id, inCount);
        this.active = active;
        this.tosidebar = tosidebar;
        this.console = console;
        this.tostatus = tostatus;
        this.type = type;
        this.complete = parseComplete(complete);
    }

    public DebugNode(int inCount, Boolean active, Boolean tosidebar, Boolean console, Boolean tostatus, targetType type,
            String complete) {
        super(inCount);
        this.active = active;
        this.tosidebar = tosidebar;
        this.console = console;
        this.tostatus = tostatus;
        this.type = type;
        this.complete = parseComplete(complete);
    }

    public Boolean getActive() {
        return active;
    }

    public Boolean getTosidebar() {
        return tosidebar;
    }

    public Boolean getConsole() {
        return console;
    }

    public Boolean getTostatus() {
        return tostatus;
    }

    public targetType getType() {
        return type;
    }

    public String[] getComplete() {
        return complete;
    }

    public String[] parseComplete(String complete) {
        if (complete == null) {
            return new String[] { "payload" };
        }
        if (!complete.contains(".")) {
            return new String[] { complete };
        }
        String[] result = complete.split("\\.");
        return result;
    }

    @Override
    void process() {
        for (int i = 0; i < getInputWireCount(); i++) {
            Wire wire = getInputWire(i);
            if (wire != null) {
                while (wire.hasMessage()) {
                    Message message = wire.get();
                    if (!(message instanceof JsonMessage)) {
                        throw new JSONMessageTypeException();
                    }
                    JSONObject messagObject = ((JsonMessage) message).getJsonObject();
                    if (active) {
                        if (tosidebar) {
                            if (type == targetType.msg) {
                                JSONObject destJsonObject = UndefinedJsonObject.getDestJsonObject(
                                        messagObject, complete);

                                log.info(destJsonObject.get(complete[complete.length - 1]).toString());

                            } else if (type == targetType.full) {
                                log.info(messagObject.toString());
                            }
                        }
                        if (tostatus) {

                        }
                        if (console) {

                        }
                    }
                }
            }
        }
    }
}
