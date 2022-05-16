package example1.open_close.better;

/**
 * 处理用户注册后操作的服务
 */
public interface IRegisterService {

    /**
     * 用户注册之后处理函数
     * @param registerInputParam
     */
    void postProcessorAfterRegister(RegisterInputParam registerInputParam);


}
