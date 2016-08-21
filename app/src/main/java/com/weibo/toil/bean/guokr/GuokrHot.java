package com.weibo.toil.bean.guokr;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GuokrHot {
    @SerializedName("result")
    private ArrayList<GuokrHotItem> result = new ArrayList<>();

    public void setResult(ArrayList<GuokrHotItem> result) {
        this.result = result;
    }

    public ArrayList<GuokrHotItem> getResult() {
        return result;
    }

}
