package com.nhnacademy.node;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.nhnacademy.exception.JSONMessageTypeException;
import com.nhnacademy.exception.NonJSONObjectTypeException;
import com.nhnacademy.exception.PropertyEmptyException;
import com.nhnacademy.exception.RulesFormatViolationException;
import com.nhnacademy.message.JsonMessage;
import com.nhnacademy.message.Message;
import com.nhnacademy.system.UndefinedJsonObject;
import com.nhnacademy.wire.Wire;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChangeNode extends InputOutputNode {
    JSONArray rules;

    public ChangeNode(String id, int inCount, int outCount, int outWireCount, JSONArray rules)
            throws RulesFormatViolationException {
        super(id, inCount, outCount, outWireCount);
        this.rules = rules;
        checkRules();
    }

    public ChangeNode(int inCount, int outCount, int outWireCount, JSONArray rules)
            throws RulesFormatViolationException {
        super(inCount, outCount, outWireCount);
        this.rules = rules;
        checkRules();
    }

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

    @Override
    public void output(int index, Message message) {
        output(message);
    }

    void output(Message message) {
        for (int i = 0; i < getOutputWireCount(); i++) {
            if (outputWires[0][i] != null) {
                outputWires[0][i].put(message);
            }
        }
    }

    String[] splitProperts(String property) {
        if (property == null) {
            throw new PropertyEmptyException();
        }
        if (property.equals("")) {
            throw new PropertyEmptyException();
        }
        if (!property.contains(".")) {
            return new String[] { property };
        } else {
            return property.split("\\.");
        }
    }

    void checkJsonDestAvailable(JSONObject jsonObject, String[] keys) throws NonJSONObjectTypeException {
        if (!(jsonObject instanceof JSONObject)) {
            throw new NonJSONObjectTypeException();
        }
        JSONObject destJsonObject = jsonObject;
        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            if (!destJsonObject.containsKey(key)) {
                break;
            }
            if (i == keys.length - 1) {
                break;
            }
            if (!(destJsonObject.get(key) instanceof JSONObject)) {
                throw new NonJSONObjectTypeException();
            }
            destJsonObject = (JSONObject) destJsonObject.get(key);
        }
    }

    // 주어진 키의 루트를 생성하며 마지막 키를 제외한 JSONObject를 반환
    JSONObject getDestWithMakeRootJsonObject(JSONObject jsonObject, String[] propertys) {
        JSONObject destJsonObject = jsonObject;
        for (String property : propertys) {
            if (property.equals(propertys[propertys.length - 1])) {
                break;
            }
            if (!destJsonObject.containsKey(property)) {
                destJsonObject.put(property, new JSONObject());
            }
            destJsonObject = (JSONObject) destJsonObject.get(property);
        }
        return destJsonObject;
    }

    @Override
    public void process() {
        for (int i = 0; i < getInputWireCount(); i++) {
            Wire wire = getInputWire(i);
            if (wire != null) {
                while (wire.hasMessage()) {
                    Message message = wire.get();
                    if (!(message instanceof JsonMessage)) {
                        continue;
                    }
                    for (int j = 0; j < rules.size(); j++) {
                        JSONObject rule = (JSONObject) rules.get(j);
                        JSONObject messageJsonObject = ((JsonMessage) message).getJsonObject();
                        String[] p = splitProperts((String) rule.get("p"));
                        try {
                            if (rule.get("t").equals("set")) {
                                if (rule.get("pt").equals("msg")) {
                                    if (rule.get("tot").equals("str")) {
                                        // 미구현
                                    } else if (rule.get("tot").equals("msg")) {
                                        String[] toP = splitProperts((String) rule.get("to"));
                                        JSONObject toJsonObject = UndefinedJsonObject
                                                .getDestJsonObject(messageJsonObject, toP);
                                        if (rule.get("dc").equals(true)) {
                                            if (toJsonObject.containsKey(toP[toP.length - 1])) {
                                                checkJsonDestAvailable(messageJsonObject, p);
                                                JSONObject destJsonObject = getDestWithMakeRootJsonObject(
                                                        messageJsonObject, p);
                                                // jsonobject인 경우 아닌경우 구분
                                                if (toJsonObject.get(toP[toP.length - 1]) instanceof JSONObject) {
                                                    destJsonObject.put(p[p.length - 1],
                                                            JsonMessage.getDeepCopyJsonObject((JSONObject) toJsonObject
                                                                    .get(toP[toP.length - 1])));
                                                } else {
                                                    destJsonObject.put(p[p.length - 1],
                                                            toJsonObject.get(toP[toP.length - 1]));
                                                }
                                            }
                                        } else if (rule.get("dc").equals(false)) {
                                            // 미구현
                                        }
                                    } else if (rule.get("tot").equals("date")) {
                                        checkJsonDestAvailable(messageJsonObject, p);
                                        JSONObject destJsonObject = getDestWithMakeRootJsonObject(
                                                messageJsonObject, p);
                                        destJsonObject.put(p[p.length - 1], System.currentTimeMillis());
                                    } else if (rule.get("tot").equals("num")) {
                                        // 미구현
                                    } else if (rule.get("tot").equals("bool")) {
                                        // 미구현
                                    } // ...
                                } else if (rule.get("pt").equals("flow")) {
                                    // 미구현
                                } else if (rule.get("pt").equals("global")) {
                                    // 미구현
                                }

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
                        output(new JsonMessage(messageJsonObject));
                    }
                }
            }
        }
    }
}
