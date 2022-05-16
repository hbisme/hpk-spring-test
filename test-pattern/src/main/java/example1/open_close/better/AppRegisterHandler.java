package example1.open_close.better;

public class AppRegisterHandler extends AbstractRegisterHandler{
    @Override
    public int getSource() {
        return RegisterConstants.RegisterEnum.APP_CHANNEL.getCode();
    }

    @Override
    public boolean doPostProcessorAfterRegister(RegisterInputParam registerInputParam) {
        System.out.println("正在处理-app渠道注册后的处理逻辑, userId: " + registerInputParam.getUserId());
        return true;
    }
}
