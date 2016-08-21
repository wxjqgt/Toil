package com.weibo.toil.bean.other;

/**
 * Created by Administrator on 2016/7/19.
 */
public class Kuaidi {

    /**
     * status : 400
     * message : 参数错误
     */

    private String status;
    private String message;

    @Override
    public String toString() {
        return "Kuaidi{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                '}';
    }

    public Kuaidi() {
    }

    public Kuaidi(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
