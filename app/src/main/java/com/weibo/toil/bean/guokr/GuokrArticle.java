package com.weibo.toil.bean.guokr;

import com.google.gson.annotations.SerializedName;

public class GuokrArticle {
    @SerializedName("result")
    private GuokrArticleResult result;

    public GuokrArticleResult getResult() {
        return result;
    }

    public void setResult(GuokrArticleResult result) {
        this.result = result;
    }
}
