package com.weibo.toil.bean.guokr;

import com.google.gson.annotations.SerializedName;

public class GuokrArticleResult {
    @SerializedName("small_image")
    private String mSmallImage;
    @SerializedName("title")
    private String title;
    @SerializedName("url")
    private String url;
    @SerializedName("content")
    private String content;

    public String getSmallImage() {
        return mSmallImage;
    }

    public void setSmallImage(String smallImage) {
        this.mSmallImage = smallImage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
