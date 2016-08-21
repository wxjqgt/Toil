package com.weibo.toil.ui.iView;


import com.weibo.toil.bean.itHome.ItHomeArticle;

public interface IItHomeArticle {
    void showError(String error);

    void showItHomeArticle(ItHomeArticle itHomeArticle);
}
