package com.weibo.toil.bean.weixin;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class TxWeixinResponse{
    @SerializedName("code")
    private int code;
    @SerializedName("msg")
    private String msg;
    @SerializedName("newslist")
    private ArrayList<WeixinNews> newslist;


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ArrayList<WeixinNews> getNewslist() {
        return newslist;
    }

    public void setNewslist(ArrayList<WeixinNews> newslist) {
        this.newslist = newslist;
    }
}
