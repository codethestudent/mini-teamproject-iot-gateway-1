import java.io.FileReader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.nhnacademy.node.MqttInNode;
import com.nhnacademy.node.MqttOutNode;
import com.nhnacademy.node.SwitchNode;
import com.nhnacademy.wire.Wire;

public class SwitchNodeTest {
    public static void main(String[] args) {
        String filePath = "src/main/resources/flows.json";
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonFile = (JSONObject) jsonParser.parse(new FileReader(filePath));

        MqttInNode mqttInNode = new MqttInNode(args);
        SwitchNode switchNode = new SwitchNode(1,1, jsonFile);
        MqttOutNode mqttOutNode = new MqttOutNode();
        Wire intToSwitch = new Wire();
        Wire switchToOut = new Wire();
        mqttInNode.connectOutputWire(0, intToSwitch);
        switchNode.connectInputWire(0, intToSwitch);
        switchNode.connectOutputWire(0, switchToOut);
        mqttOutNode.connectInputWire(0, switchToOut);
        mqttInNode.start();
        switchNode.start();
        mqttOutNode.start();

    }
}
