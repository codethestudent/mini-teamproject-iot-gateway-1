package com.nhnacademy.node;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONObject;
import com.nhnacademy.message.JsonMessage;
import com.nhnacademy.message.Message;
import com.nhnacademy.wire.Wire;

public class JsonTest2 extends InputOutputNode {

    String path = "src/test/java/logs.json";

    // Fix constructor name
    public JsonTest2(String name, int inCount, int outCount) {
        super(name, inCount, outCount);
        // TODO Auto-generated constructor stub
    }

    @Override
    void process() {
        sendToLogs();
    }


    public void sendToLogs() {
        try {
            Wire inputWire = getInputWire(0);
            if (inputWire != null) {
                while (inputWire.hasMessage()) {
                    Message message = inputWire.get();

                    if (message instanceof JsonMessage) {
                        JsonMessage jsonMessage = (JsonMessage) message;

                        // 파일이 없으면 생성
                        File file = new File(path);
                        if (!file.exists()) {
                            try {
                                file.createNewFile();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        // 기존 데이터 읽어오기
                        List<String> existingData = readExistingData(file);

                        // 새로운 데이터 추가
                        existingData.add(jsonMessage.toString());

                        // JSON 파일 쓰기
                        writeDataToFile(file, existingData);

                        output(jsonMessage);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<String> readExistingData(File file) throws IOException {
        List<String> existingData = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                existingData.add(line);
            }
        }

        return existingData;
    }

    private void writeDataToFile(File file, List<String> data) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(file))) {
            for (String line : data) {
                out.println(line);
            }
            System.out.println("Write success!");
        }
    }
}