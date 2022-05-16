package example1.open_close.bad;

public class RegisterHandler implements IregisterHadnler {


    @Override
    public void postProcessorAfterRegister(int sourceId, long userId) {

        // 这里的业务处理逻辑可以抽取到独立的handler中
        if (sourceId == 0) {
            System.out.println("正在处理-公众号渠道");
        } else if (sourceId == 1) {
            System.out.println("正在处理-app渠道");
        } else {

        }
        notifyWorker(sourceId, userId);
    }

    private void notifyWorker(int sourceId, long userId) {
        System.out.println("userId: " + userId + "正在处理");
    }
}
