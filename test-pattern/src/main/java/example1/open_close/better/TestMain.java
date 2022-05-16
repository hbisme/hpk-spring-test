package example1.open_close.better;

public class TestMain {
    public static void main(String[] args) {
        // 有了参数类, 参数就比较清晰
        RegisterInputParam registerInputParam1 = new RegisterInputParam(0, 10012);

        // 业务逻辑的核心类
        RegisterServiceImpl registerService = new RegisterServiceImpl();
        registerService.postProcessorAfterRegister(registerInputParam1);


        RegisterInputParam registerInputParam2 = new RegisterInputParam(1, 10013);
        registerService.postProcessorAfterRegister(registerInputParam2);


    }
}
