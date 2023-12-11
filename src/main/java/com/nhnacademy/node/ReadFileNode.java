package com.nhnacademy.node;

import org.json.simple.JSONObject;

import com.nhnacademy.message.JsonMessage;
import com.nhnacademy.message.Message;

public class ReadFileNode extends InputOutputNode{
    String filename;
    protected ReadFileNode(String id, String filename) {
        super(1);
        this.filename = filename;
    }

    public ReadFileNode generateNode(JSONObject jsonObject) {
        String id = (String) jsonObject.get("id");
        String filename = (String) jsonObject.get("filename");
        return new ReadFileNode(id, filename);
    }

    @Override
    void process() {
        for(int i = 0;i<getInputWireCount();i++){
            if(getInputWire(i) == null || !getInputWire(i).hasMessage())
                continue;
            Message message = getInputWire(i).get();
            if(!(message instanceof JsonMessage))
                continue;
            JSONObject jsonObject = ((JsonMessage) message).getJsonObject();
        }
    }
    public static void main(String[] args) {
        System.out.println(ReadFileNode.class.getProtectionDomain().getCodeSource().getLocation().getPath());
    }
}
