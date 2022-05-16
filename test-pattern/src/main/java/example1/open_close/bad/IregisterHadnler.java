package example1.open_close.bad;

public interface IregisterHadnler {

    /**
     * 用户注册后的处理函数
     * @param userId 用户id
     * @param sourceId 渠道id
     */
    void postProcessorAfterRegister(int sourceId, long userId);
}
