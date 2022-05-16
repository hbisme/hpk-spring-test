package example1.open_close.better;

import java.util.ArrayList;
import java.util.List;

public class RegisterServiceImpl implements IRegisterService {

    private static List<AbstractRegisterHandler> registerHandlerList = new ArrayList<>();

    // 如果有新的渠道要处理逻辑,则只要在这里添加就可以了
    static {
        registerHandlerList.add(new GZHRegisterHandler());
        registerHandlerList.add(new AppRegisterHandler());
    }


    @Override
    public void postProcessorAfterRegister(RegisterInputParam registerInputParam) {
        for (AbstractRegisterHandler abstractRegisterHandler : registerHandlerList) {
            if (abstractRegisterHandler.getSource() == registerInputParam.getSource()) {
                abstractRegisterHandler.doPostProcessorAfterRegister(registerInputParam);
                return ;
            }
        }

        throw new RuntimeException("未知注册渠道");
    }
}
