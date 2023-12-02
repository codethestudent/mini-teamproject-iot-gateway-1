import com.nhnacademy.node.FunctionNode;
import com.nhnacademy.node.MqttInNode;
import com.nhnacademy.node.MqttOutNode;
import com.nhnacademy.wire.Wire;

public class FunctionNodeTest {
    public static void main(String[] args) {
        MqttInNode mqttInNode = new MqttInNode(args);
        FunctionNode functionNode = new FunctionNode(1, 1);
        MqttOutNode mqttOutNode = new MqttOutNode("mqttOutNode", 1);
        Wire inTofun = new Wire();
        Wire funToout = new Wire();
        mqttInNode.connectOutputWire(0, inTofun);
        functionNode.connectInputWire(0, inTofun);
        functionNode.connectOutputWire(0, funToout);
        mqttOutNode.connectInputWire(0, funToout);
        mqttInNode.start();
        functionNode.start();
        mqttOutNode.start();
    }
}
