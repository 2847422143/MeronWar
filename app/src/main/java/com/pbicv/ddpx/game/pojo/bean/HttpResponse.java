package com.pbicv.ddpx.game.pojo.bean;

public class HttpResponse<T> {
    private int code;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    private T result;

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "DataResponse{" +
                "code=" + code +
                ", data=" + result +
                ", message='" + message + '\'' +
                '}';
    }
}