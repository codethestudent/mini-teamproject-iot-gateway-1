package main.java.com.nhnacademy.node;

import java.io.PrintWriter;
import java.util.List;

import com.nhnacademy.message.JsonMessage;
import com.nhnacademy.node.InputOutputNode;
import com.nhnacademy.node.OutputNode;

public class LogsInputOutputNode extends InputOutputNode {

    String path = "src/test/java/test.json";

    // Fix constructor name
    public LogsInputOutputNode(String name, int inCount, int outCount) {
        super(name, inCount, outCount);
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
                        // JSONObject messageJsonObject = jsonMessage.getJsonObject();

                        // 파일이 없으면 생성
                        File file = new File(path);
                        if (!file.exists()) {
                            try {
                                file.createNewFile();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        // json 파일 쓰기
                        try (PrintWriter out = new PrintWriter(new FileWriter(path))) {
                            out.write(jsonMessage.toString());
                            System.out.println("print success!");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        output(jsonMessage); // Fix typo here (messageObject to messageJsonObject)
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}