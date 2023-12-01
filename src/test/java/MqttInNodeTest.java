import com.nhnacademy.node.MqttInNode;

public class MqttInNodeTest {
    public static void main(String[] args) {
        MqttInNode node = new MqttInNode(args);
        node.start();
    }
}
