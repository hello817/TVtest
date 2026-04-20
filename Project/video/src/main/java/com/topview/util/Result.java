package com.topview.util;
//统一响应包装


public class Result {
    private int code;
    private String msg;
    private Object data;

    public Result() {}

    public Result(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static Result success(Object data) {
        return new Result(200, "success", data);
    }

    public static Result success(String msg) {
        return new Result(200, msg, null);
    }

    public static Result error(int code, String msg) {
        return new Result(code, msg, null);
    }
    //getter+setter
    public int getCode(){return this.code;}
    public void setCode(int code){this.code = code;}

    public String getMsg(){return this.msg;}
    public void setMsg(String msg){this.msg = msg;}

    public Object getData(){return this.data;}
    public void setData(Object data){this.data = data;}

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
