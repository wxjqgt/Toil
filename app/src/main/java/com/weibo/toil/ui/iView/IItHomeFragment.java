package com.weibo.toil.ui.iView;


import com.weibo.toil.bean.itHome.ItHomeItem;

import java.util.ArrayList;

public interface IItHomeFragment extends IBaseFragment {
    void updateList(ArrayList<ItHomeItem> itHomeItems);
}
