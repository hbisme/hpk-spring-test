package priv.hb.sample.eventListener.listener.netty.listener;

// import com.yangt.hirac.protocol.RpcResponse;
// import com.yangt.hirac.protocol.RpcWebResponse;

/**
 * @author: <a href="mailto:lingxiao@2dfire.com">凌霄</a>
 * @time: Created in 14:48 2018/1/10
 * @desc web请求在netty中的handler处理响应监听
 */
public abstract class ResponseListener {

    /**
     * 自动调度层消息response
     *
     * @param response
     */
    public abstract void onResponse(String response);

    /**
     * 页面任务执行请求response
     *
     * @param webResponse
     */
    public abstract void onWebResponse(String webResponse);
}