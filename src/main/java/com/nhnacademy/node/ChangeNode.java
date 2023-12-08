package com.nhnacademy.node;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.nhnacademy.exception.NonJSONObjectTypeException;
import com.nhnacademy.exception.PropertyEmptyException;
import com.nhnacademy.exception.RulesFormatViolationException;
import com.nhnacademy.message.JsonMessage;
import com.nhnacademy.message.Message;
import com.nhnacademy.system.NodeRedSystem;
import com.nhnacademy.wire.Wire;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChangeNode extends InputOutputNode {
    JSONArray rules;
    String flowId;

    public ChangeNode(String id, int outCount, JSONArray rules, String z)
            throws RulesFormatViolationException {
        super(id, outCount);
        this.rules = rules;
        this.flowId = z;
        checkRules();
    }

    public ChangeNode(int outCount, JSONArray rules, String z)
            throws RulesFormatViolationException {
        super(outCount);
        this.rules = rules;
        this.flowId = z;
        checkRules();
    }

    // 수정중
    void checkRules() throws RulesFormatViolationException {
        if (this.rules == null) {
            throw new RulesFormatViolationException("rules is null");
        }
        if (this.rules.size() == 0) {
            throw new RulesFormatViolationException("rules is empty");
        }
        for (Object rule : this.rules) {
            if (!(rule instanceof JSONObject)) {
                throw new RulesFormatViolationException("rule is not JSONObject");
            }
            JSONObject ruleObj = (JSONObject) rule;
            if (!ruleObj.containsKey("t")) {
                throw new RulesFormatViolationException("rule does not have t");
            }
            if (!(ruleObj.get("t").equals("set") || ruleObj.get("t").equals("change") || ruleObj.get("t").equals("move")
                    || ruleObj.get("t").equals("delete"))) {
                throw new RulesFormatViolationException("t parameter is not valid");
            }
            if (ruleObj.get("t").equals("set")) {
                if (!ruleObj.containsKey("p")) {
                    throw new RulesFormatViolationException("rule does not have p");
                }
                if (!(ruleObj.get("p") instanceof String)) {
                    throw new RulesFormatViolationException("p parameter is not String");
                }
                if (!ruleObj.containsKey("pt")) {
                    throw new RulesFormatViolationException("rule does not have pt");
                }
                if (!(ruleObj.get("pt").equals("msg") || ruleObj.get("pt").equals("flow")
                        || ruleObj.get("pt").equals("global"))) {
                    throw new RulesFormatViolationException("pt parameter is not valid");
                }
                if (!ruleObj.containsKey("to")) {
                    throw new RulesFormatViolationException("rule does not have to");
                }
                if (!ruleObj.containsKey("tot")) {
                    throw new RulesFormatViolationException("rule does not have tot");
                }
                if (!(ruleObj.get("tot").equals("str") || ruleObj.get("tot").equals("num")
                        || ruleObj.get("tot").equals("bool") || ruleObj.get("tot").equals("date")
                        || ruleObj.get("tot").equals("msg"))) {
                    throw new RulesFormatViolationException("tot parameter is not valid");
                }
                if (ruleObj.get("tot").equals("msg") || ruleObj.get("tot").equals("flow")
                        || ruleObj.get("tot").equals("global")) {
                    if (!ruleObj.containsKey("dc")) {
                        throw new RulesFormatViolationException("rule does not have dc");
                    }
                    if (!(ruleObj.get("dc") instanceof Boolean)) {
                        throw new RulesFormatViolationException("dc parameter is not Boolean");
                    }
                }

            } else if (ruleObj.get("t").equals("change")) {
                // 미구현
            } else if (ruleObj.get("t").equals("move")) {

            } else if (ruleObj.get("t").equals("delete")) {

            }
        }
    }

    void output(Message message) {
        for (int i = 0; i < getOutputWireCount(0); i++) {
            output(0, message);
        }
    }

    @Override
    public void process() {
        for (int i = 0; i < getInputWireCount(); i++) {
            Wire wire = getInputWire(i);
            if (wire == null || !wire.hasMessage())
                continue;
            Message message = wire.get();
            if (!(message instanceof JsonMessage)) {
                continue;
            }
            JSONObject messagJsonObject = ((JsonMessage) message).getJsonObject();
            for (int j = 0; j < rules.size(); j++) {
                JSONObject rule = (JSONObject) rules.get(j);
                String[] keys = JsonMessage.splitKeys((String) rule.get("p"));
                JSONObject destJsonObject = null;
                if (rule.get("pt").equals("msg")) {
                    destJsonObject = JsonMessage
                            .getDestWithCheckAndMakeJsonObject(messagJsonObject, keys);
                } else if (rule.get("pt").equals("flow")) {
                    destJsonObject = NodeRedSystem.getInstance().getFlow(flowId)
                            .getFlowJsonObject();
                } else if (rule.get("pt").equals("global")) {
                    // 미구현
                }
                try {
                    if (rule.get("t").equals("set")) {
                        if (rule.get("tot").equals("str")) {
                            // 미구현
                        } else if (rule.get("tot").equals("msg")) {
                            String[] toKeys = JsonMessage.splitKeys((String) rule.get("to"));
                            JSONObject toJsonObject = JsonMessage
                                    .getDestJsonObject(messagJsonObject, toKeys);
                            if (rule.get("dc").equals(true)) {
                                if (toJsonObject.containsKey(toKeys[toKeys.length - 1])) {

                                    // deep copy의 경우 JSONObject의 경우에만 적용해야해서 toJsonObject가 JSONObject인 경우와 아닌 경우를
                                    // 나누어서 처리
                                    if (toJsonObject.get(
                                            toKeys[toKeys.length - 1]) instanceof JSONObject) {
                                        destJsonObject.put(keys[keys.length - 1],
                                                JsonMessage.getDeepCopyJsonObject((JSONObject) toJsonObject
                                                        .get(toKeys[toKeys.length - 1])));
                                    } else {
                                        destJsonObject.put(keys[keys.length - 1],
                                                toJsonObject.get(toKeys[toKeys.length - 1]));
                                    }
                                }
                            } else if (rule.get("dc").equals(false)) {
                                // 미구현
                            }
                        } else if (rule.get("tot").equals("date")) {
                            destJsonObject.put(keys[keys.length - 1], System.currentTimeMillis());
                        } else if (rule.get("tot").equals("num")) {
                            // 미구현
                        } else if (rule.get("tot").equals("bool")) {
                            // 미구현
                        } else if (rule.get("tot").equals("flow")) {
                            // 미구현
                        } else if (rule.get("tot").equals("global")) {
                            // 미구현
                        } // ...

                    } else if (rule.get("t").equals("change")) {
                        // 미구현
                    } else if (rule.get("t").equals("move")) {
                        // 미구현
                    } else if (rule.get("t").equals("delete")) {
                        // 미구현
                    }
                } catch (NonJSONObjectTypeException e) {
                    log.info("Cannot set property of non-object type: " + rule.get("p"));
                } catch (PropertyEmptyException e) {
                    log.info("property is empty");
                }
                output(new JsonMessage(messagJsonObject));
            }

        }
    }
}
