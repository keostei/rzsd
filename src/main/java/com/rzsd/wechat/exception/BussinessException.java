package com.rzsd.wechat.exception;

public class BussinessException extends RuntimeException {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 2232187822178337878L;

    public BussinessException(String message) {
        super(message);
    }
}
