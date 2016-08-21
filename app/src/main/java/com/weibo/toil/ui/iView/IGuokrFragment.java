package com.weibo.toil.ui.iView;

import com.weibo.toil.bean.guokr.GuokrHotItem;

import java.util.ArrayList;

public interface IGuokrFragment extends IBaseFragment {
    void updateList(ArrayList<GuokrHotItem> guokrHotItems);
}
