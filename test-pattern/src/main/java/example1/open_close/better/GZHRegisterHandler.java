package example1.open_close.better;

public class GZHRegisterHandler extends AbstractRegisterHandler{
    @Override
    public int getSource() {
        return RegisterConstants.RegisterEnum.GZH_CHANNEL.getCode();
    }

    @Override
    public boolean doPostProcessorAfterRegister(RegisterInputParam registerInputParam) {
        System.out.println("正在处理-公众号渠道注册后的处理逻辑, userId: " + registerInputParam.getUserId());
        return true;
    }
}
