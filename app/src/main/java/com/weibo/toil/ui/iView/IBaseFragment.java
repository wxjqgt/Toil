package com.weibo.toil.ui.iView;

public interface IBaseFragment  {
    void showProgressDialog();

    void hidProgressDialog();

    void showError(String error);
}
