package com.nhnacademy.node;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.nhnacademy.exception.JSONMessageTypeException;
import com.nhnacademy.exception.PropertyEmptyException;
import com.nhnacademy.exception.RulesFormatViolationException;
import com.nhnacademy.message.JsonMessage;
import com.nhnacademy.message.Message;
import com.nhnacademy.system.UndefinedJsonObject;
import com.nhnacademy.wire.Wire;

public class SwitchNode extends InputOutputNode {
    public enum propertyType {
        msg
    }

    private String property;
    private String[] propertys;
    private propertyType type;
    private JSONArray rules;
    private Boolean checkall;
    // private int outputs;

    public SwitchNode(String id, int inWireCount, int outCount, int outWireCount, String property, propertyType type,
            JSONArray rules, Boolean checkall)
            throws RulesFormatViolationException, PropertyEmptyException {
        super(id, inWireCount, outCount, outWireCount);
        this.property = property;
        this.type = type;
        this.rules = rules;
        this.checkall = checkall;
        // this.outputs = outpus;
        checkRules();
        propertys = splitProperts(property);
    }

    public SwitchNode(int inWireCount, int outCount, int outWireCount, String property, propertyType type,
            JSONArray rules, Boolean checkall)
            throws RulesFormatViolationException, PropertyEmptyException {
        super(inWireCount, outCount, outWireCount);
        this.property = property;
        this.type = type;
        this.rules = rules;
        this.checkall = checkall;
        // this.outputs = outpus;
        checkRules();
        propertys = splitProperts(property);
    }

    void checkRules() throws RulesFormatViolationException {

        for (Object rule : this.rules) {
            if (!(rule instanceof JSONObject)) {
                throw new RulesFormatViolationException("rule is not JSONObject");
            }
            JSONObject ruleObj = (JSONObject) rule;
            if (!ruleObj.containsKey("t")) {
                throw new RulesFormatViolationException("rule does not have t");
            }
            if (!(ruleObj.get("t") instanceof String)) {
                throw new RulesFormatViolationException("t is not String");
            }
            if (!(ruleObj.get("t").equals("eq") || ruleObj.get("t").equals("cont")
                    || ruleObj.get("t").equals("hask"))) {
                throw new RulesFormatViolationException("t is not eq or cont or hask");
            }
            if (!ruleObj.containsKey("v")) {
                throw new RulesFormatViolationException("rule does not have v");
            }
            if (!(ruleObj.get("v") instanceof String)) {
                throw new RulesFormatViolationException("v is not String");
            }
            if (!ruleObj.containsKey("vt")) {
                throw new RulesFormatViolationException("rule does not have vt");
            }
            if (!(ruleObj.get("vt") instanceof String)) {
                throw new RulesFormatViolationException("vt is not String");
            }
            if (!(ruleObj.get("vt").equals("msg") || ruleObj.get("vt").equals("str"))) {
                throw new RulesFormatViolationException("vt is not msg or str");
            }
            if (ruleObj.get("vt").equals("msg") && (ruleObj.get("v").equals("") || ruleObj.get("v") == null)) {
                throw new RulesFormatViolationException("vt is msg but v is empty");
            }
        }
    }

    String[] splitProperts(String property) {
        if (this.property == null) {
            throw new PropertyEmptyException();
        }
        if (this.property.equals("")) {
            throw new PropertyEmptyException();
        }
        if (!this.property.contains(".")) {
            return new String[] { this.property };
        } else {
            return this.property.split("\\.");
        }
    }

    public String getProperty() {
        return property;
    }

    public propertyType getType() {
        return type;
    }

    public JSONArray getRules() {
        return rules;
    }

    public Boolean getCheckall() {
        return checkall;
    }

    // public int getOutputs() {
    //     return outputs;
    // }

    @Override
    void process() {
        for (int i = 0; i < getInputWireCount(); i++) {
            Wire wire = getInputWire(i);
            if (wire != null) {
                while (wire.hasMessage()) {
                    Message message = wire.get();
                    if (!(message instanceof JsonMessage)) {
                        continue;
                    }
                    JSONObject destJsonObject = UndefinedJsonObject
                            .getDestJsonObject(((JsonMessage) message).getJsonObject(), propertys);
                    for (int j = 0; j < rules.size(); j++) {
                        JSONObject rule = (JSONObject) rules.get(j);
                        if (rule.get("t").equals("eq")) {
                            if (rule.get("vt").equals("str")) {
                                if (destJsonObject.get(propertys[propertys.length - 1]).equals(rule.get("v"))) {
                                    output(j, message);
                                }
                            } else if (rule.get("vt").equals("msg")) {
                                String[] v = splitProperts(rule.get("v").toString());
                                JSONObject messageDestJsonObject = UndefinedJsonObject
                                        .getDestJsonObject(((JsonMessage) message).getJsonObject(), v);
                                if (destJsonObject.get(propertys[propertys.length - 1])
                                        .equals(messageDestJsonObject.get(v[v.length - 1]))) {
                                    output(j, message);
                                }
                            }
                        } else if (rule.get("t").equals("cont")) {
                            //미구현
                        } else if (rule.get("t").equals("hask")) {
                            if (rule.get("vt").equals("str")) {
                                if (((JSONObject) destJsonObject.get(propertys[propertys.length - 1]))
                                        .containsKey(rule.get("v"))) {
                                    output(j, message);
                                } else if (rule.get("vt").equals("msg")) {
                                    // String[] v = splitProperts(rule.get("v").toString());
                                    // JSONObject messageDestJsonObject = UndefinedJsonObject
                                    // .getDestJsonObject(((JsonMessage) message).getJsonObject(), v);
                                    // if (messageDestJsonObject instanceof UndefinedJsonObject) {
                                    // continue;
                                    // }
                                    // if (destJsonObject
                                    // .containsKey(messageDestJsonObject.get(v[v.length - 1].toString()))) {
                                    // output(j, message);
                                    // }
                                }
                            }
                        }
                    }

                }
            }
        }
    }
}
