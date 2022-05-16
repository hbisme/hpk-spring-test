package example1.open_close.bad;

public class TestMain {
    public static void main(String[] args) {
        RegisterHandler registerHandler = new RegisterHandler();
        registerHandler.postProcessorAfterRegister(0, 10001);
        registerHandler.postProcessorAfterRegister(1, 10002);

    }
}
