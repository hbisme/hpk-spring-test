package priv.hb.sample.exception;

/**
 * 自定义异常
 *
 * @author hubin
 * @date 2022年08月10日 19:20
 */
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}
