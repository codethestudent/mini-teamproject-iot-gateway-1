package com.nhnacademy.node;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.nhnacademy.exception.PropertyEmptyException;
import com.nhnacademy.exception.RulesFormatViolationException;
import com.nhnacademy.message.JsonMessage;
import com.nhnacademy.message.Message;
import com.nhnacademy.system.NodeRedSystem;
import com.nhnacademy.wire.Wire;

public class SwitchNode extends InputOutputNode {
    private String property;
    private String[] keys;
    private String propertyType;
    private JSONArray rules;
    private Boolean checkall;
    private String flowId;

    public SwitchNode(String id, int outCount, String property, String type,
            JSONArray rules, Boolean checkall, String flowId)
            throws RulesFormatViolationException, PropertyEmptyException {
        super(id, outCount);
        this.property = property;
        this.propertyType = type;
        this.rules = rules;
        this.checkall = checkall;
        this.flowId = flowId;
        checkRules();
        keys = JsonMessage.splitKeys(property);
    }

    public SwitchNode(int outCount, String property, String type,
            JSONArray rules, Boolean checkall, String flowId)
            throws RulesFormatViolationException, PropertyEmptyException {
        super(outCount);
        this.property = property;
        this.propertyType = type;
        this.rules = rules;
        this.checkall = checkall;
        this.flowId = flowId;
        checkRules();
        keys = JsonMessage.splitKeys(property);
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

    @Override
    void process() {
        for (int i = 0; i < getInputWireCount(); i++) {
            Wire wire = getInputWire(i);
            if (wire == null || !wire.hasMessage())
                continue;
            Message message = wire.get();
            if (!(message instanceof JsonMessage)) {
                continue;
            }
            JSONObject messagJsonObject = ((JsonMessage) message).getJsonObject();
            JSONObject destJsonObject = null;

            if (propertyType.equals("msg")) {
                destJsonObject = JsonMessage.getDestJsonObject(messagJsonObject, keys);
            } else if (propertyType.equals("flow")) {
                destJsonObject = NodeRedSystem.getInstance().getFlow(flowId).getFlowJsonObject();
            } else if (propertyType.equals("global")) {
                // 미구현
            }

            for (int j = 0; j < rules.size(); j++) {
                JSONObject rule = (JSONObject) rules.get(j);
                if (rule.get("t").equals("eq")) {
                    if (rule.get("vt").equals("str")) {
                        if (destJsonObject.get(keys[keys.length - 1]).equals(rule.get("v"))) {
                            output(j, message);
                        }
                    } else if (rule.get("vt").equals("msg")) {
                        String[] vkeys = JsonMessage.splitKeys(rule.get("v").toString());
                        JSONObject vDestObject = JsonMessage.getDestJsonObject(messagJsonObject, vkeys);
                        if (destJsonObject.get(keys[keys.length - 1])
                                .equals(vDestObject.get(vkeys[vkeys.length - 1]))) {
                            output(j, message);
                        }
                    } else if (rule.get("vt").equals("flow")) {
                        // 미구현
                    } else if (rule.get("vt").equals("global")) {
                        // 미구현
                    } // ...
                } else if (rule.get("t").equals("cont")) {
                    // 미구현
                } else if (rule.get("t").equals("hask")) {
                    if (rule.get("vt").equals("str")) {
                        if (((JSONObject) destJsonObject.get(keys[keys.length - 1]))
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
