package com.zcxd.pda.exception;

public interface ErrorType {
    /**
     * 返回code
     *
     * @return
     */
    int getCode();

    /**
     * 返回mesg
     *
     * @return
     */
    String getMesg();
}
