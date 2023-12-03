import com.nhnacademy.system.SystemOption;

public class SystemManagement {
    public static void main(String[] args) {
        SystemOption systemOption = new SystemOption(args);
        systemOption.createNode();
        systemOption.createFlow();
    }
}
