package com.nhnacademy.node;

import org.json.simple.JSONObject;

import com.nhnacademy.exception.NonJSONObjectTypeException;
import com.nhnacademy.message.JsonMessage;
import com.nhnacademy.system.UndefinedJsonObject;
import com.nhnacademy.wire.Wire;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TemplateNode extends InputOutputNode {
    public enum fieldType {
        msg
    }

    private String field;
    private fieldType type;
    private String template;
    private String[] fields;

    public TemplateNode(String id, int inWireCount, int outWireCount, String field, fieldType type,
            String template) {
        super(id, inWireCount, 1, outWireCount);
        this.field = field;
        this.type = type;
        this.template = template;
    }

    public TemplateNode(int inWireCount, int outWireCount, String field, fieldType type,
            String template) {
        super(inWireCount, 1, outWireCount);
        this.field = field;
        this.type = type;
        this.template = template;
    }

    String[] getFields(String field) {
        if (field.length() == 0)
            return new String[0];
        if (!field.contains("."))
            return new String[] { field };
        String[] fields = field.split("\\.");
        return fields;
    }

    @Override
    void preprocess() {
        fields = getFields(field);
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
    void process() {
        for (int i = 0; i < getInputWireCount(); i++) {
            Wire wire = getInputWire(i);
            if (wire == null)
                continue;
            JsonMessage message = (JsonMessage) wire.get();
            if (!(message instanceof JsonMessage))
                continue;
            JSONObject jsonObject = message.getJsonObject();
            String result = "";
            String s = template;
            JSONObject destJsonObject = null;

            while (s.contains("{{")) {
                int start = s.indexOf("{{");
                int end = s.indexOf("}}");
                String templateField = s.substring(start + 2, end);
                result += s.substring(0, start);
                String[] templateFields = getFields(templateField);
                try {
                    destJsonObject = UndefinedJsonObject.getDestJsonObject(jsonObject, templateFields);
                } catch (NonJSONObjectTypeException e) {
                    e.printStackTrace();
                }
                if (destJsonObject instanceof UndefinedJsonObject)
                    continue;
                result += destJsonObject.get(templateFields[templateFields.length - 1]).toString();
                s = s.substring(end + 2);
            }
            result += s;
            try {
                checkJsonDestAvailable(jsonObject, fields);
                destJsonObject = getDestWithMakeRootJsonObject(jsonObject, fields);
                destJsonObject.put(fields[fields.length - 1], result);
            } catch (NonJSONObjectTypeException e) {
                // log.info("Cannot set property of non-object type: " + field);
            }

            output(0, new JsonMessage(jsonObject));
        }
    }
}
