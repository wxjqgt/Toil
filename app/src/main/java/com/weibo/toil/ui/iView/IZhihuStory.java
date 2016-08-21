package com.weibo.toil.ui.iView;

import com.weibo.toil.bean.guokr.GuokrArticle;
import com.weibo.toil.bean.zhihu.ZhihuStory;

public interface IZhihuStory {

    void showError(String error);

    void showZhihuStory(ZhihuStory zhihuStory);

    void showGuokrArticle(GuokrArticle guokrArticle);
}
