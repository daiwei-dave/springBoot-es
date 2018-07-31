package com.mkyong.sdk;

/**
 * @author daiwei
 * @date 2018/7/31 10:11
 */
public class JsonCastException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public JsonCastException() {
    }

    public JsonCastException(Throwable t) {
        super(t);
    }

    public JsonCastException(String msg) {
        super(msg);
    }

    public JsonCastException(String msg, Throwable t) {
        super(msg, t);
    }
}