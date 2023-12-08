package com.nhnacademy.node;

import org.json.simple.JSONObject;

import com.nhnacademy.exception.NonJSONObjectTypeException;
import com.nhnacademy.message.JsonMessage;
import com.nhnacademy.message.Message;
import com.nhnacademy.system.NodeRedSystem;
import com.nhnacademy.system.UndefinedJsonObject;
import com.nhnacademy.wire.Wire;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TemplateNode extends InputOutputNode {
    public enum FieldType {
        MSG, FLOW, GLOBAL
    }

    private String field;
    private FieldType fieldType;
    private String template;
    private String[] keys;

    public TemplateNode(String id, String field, FieldType type,
            String template) {
        super(id, 1);
        this.field = field;
        this.fieldType = type;
        this.template = template;
    }

    public TemplateNode(String field, FieldType type,
            String template) {
        super(1);
        this.field = field;
        this.fieldType = type;
        this.template = template;
    }

    @Override
    void preprocess() {
        keys = JsonMessage.splitKeys(field);
    }

    @Override
    void process() {
        for (int i = 0; i < getInputWireCount(); i++) {
            Wire wire = getInputWire(i);
            if (wire == null || !wire.hasMessage())
                continue;
            Message message = wire.get();
            if (!(message instanceof JsonMessage))
                continue;
            JSONObject jsonObject = ((JsonMessage) message).getJsonObject();
            JSONObject propertyJsonObject = null;
            if (fieldType.equals(FieldType.MSG)) {
                propertyJsonObject = jsonObject;
            } else if (fieldType.equals(FieldType.FLOW)) {
                // propertyJsonObject = NodeRedSystem.getInstance().getFlow(id);
            } else if (fieldType.equals(FieldType.GLOBAL)) {
                // propertyJsonObject = message.getGlobalJsonObject();
            }
            String result = "";
            String s = template;
            JSONObject destJsonObject = null;

            while (s.contains("{{")) {
                int start = s.indexOf("{{");
                int end = s.indexOf("}}");
                String templateField = s.substring(start + 2, end);
                result += s.substring(0, start);
                String[] templateFields = JsonMessage.splitKeys(templateField);
                destJsonObject = JsonMessage.getDestJsonObject(propertyJsonObject, templateFields);
                if (destJsonObject instanceof UndefinedJsonObject)
                    continue;
                result += destJsonObject.get(templateFields[templateFields.length - 1]).toString();
                s = s.substring(end + 2);
            }
            result += s;
            try {
                destJsonObject = JsonMessage.getDestWithCheckAndMakeJsonObject(destJsonObject, keys);
                destJsonObject.put(keys[keys.length - 1], result);
            } catch (NonJSONObjectTypeException e) {
                // log.info("Cannot set property of non-object type: " + field);
            }
            output(0, message);
        }
    }
}
