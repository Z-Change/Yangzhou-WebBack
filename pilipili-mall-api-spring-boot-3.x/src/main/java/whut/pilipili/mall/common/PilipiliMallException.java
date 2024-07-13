
package whut.pilipili.mall.common;

public class PilipiliMallException extends RuntimeException {

    public PilipiliMallException() {
    }

    public PilipiliMallException(String message) {
        super(message);
    }

    /**
     * 丢出一个异常
     *
     * @param message
     */
    public static void fail(String message) {
        throw new PilipiliMallException(message);
    }

}
