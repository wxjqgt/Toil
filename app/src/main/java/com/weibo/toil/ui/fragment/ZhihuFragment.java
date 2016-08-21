package com.weibo.toil.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.weibo.toil.R;
import com.weibo.toil.bean.zhihu.ZhihuDaily;
import com.weibo.toil.bean.zhihu.ZhihuDailyItem;
import com.weibo.toil.presenter.IZhihuPresenter;
import com.weibo.toil.presenter.impl.ZhihuPresenterImpl;
import com.weibo.toil.ui.activity.MainActivity;
import com.weibo.toil.ui.adapter.ZhihuAdapter;
import com.weibo.toil.ui.iView.IZhihuFragment;
import com.weibo.toil.utils.NetWorkUtil;
import com.weibo.toil.utils.SharePreferenceUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ZhihuFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, IZhihuFragment {

    private void initView() {
        swipeRefreshLayout.setOnRefreshListener(this);
        setSwipeRefreshLayoutColor(swipeRefreshLayout);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        swipeTarget.setLayoutManager(mLinearLayoutManager);
        swipeTarget.setHasFixedSize(true);
        zhihuAdapter = new ZhihuAdapter(mainActivity,R.layout.zhihu_daily_item, zhihuStories);
        swipeTarget.setAdapter(zhihuAdapter);
        swipeTarget.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //向下滚动
                {
                    visibleItemCount = mLinearLayoutManager.getChildCount();
                    totalItemCount = mLinearLayoutManager.getItemCount();
                    pastVisiblesItems = mLinearLayoutManager.findFirstVisibleItemPosition();

                    if (!loading && (visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                        loading = true;
                        onLoadMore();
                    }
                }
            }
        });
        mZhihuPresenter.getLastFromCache();
        if (SharePreferenceUtil.isRefreshOnlyWifi(getActivity())) {
            if (NetWorkUtil.isWifiConnected(getActivity())) {
                onRefresh();
            }
        } else {
            onRefresh();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }

    @BindView(R.id.swipe_target)
    RecyclerView swipeTarget;
    @BindView(R.id.swipeToLoadLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private MainActivity mainActivity;
    private String currentLoadedDate;
    private ZhihuAdapter zhihuAdapter;
    private IZhihuPresenter mZhihuPresenter;
    private ArrayList<ZhihuDailyItem> zhihuStories = new ArrayList<>();
    private LinearLayoutManager mLinearLayoutManager;
    private boolean loading = false;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;

    public static ZhihuFragment newInstance() {
        ZhihuFragment fragment = new ZhihuFragment();
        return fragment;
    }

    public ZhihuFragment() {
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_common;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        initView();
    }

    private void initData() {
        mZhihuPresenter = new ZhihuPresenterImpl(this, getActivity());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mZhihuPresenter.unsubcrible();
    }

    @Override
    public void onRefresh() {
        currentLoadedDate = "0";
        zhihuStories.clear();
        zhihuAdapter.notifyDataSetChanged();
        mZhihuPresenter.getLastZhihuNews();
    }

    private void onLoadMore() {
        mZhihuPresenter.getTheDaily(currentLoadedDate);
    }

    @Override
    public void showProgressDialog() {
        if (progressBar != null)
            progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hidProgressDialog() {
        if (progressBar != null)
            progressBar.setVisibility(View.INVISIBLE);
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
            loading = false;
        }
    }

    @Override
    public void showError(String error) {
        if (swipeTarget != null) {
            Snackbar.make(swipeTarget, getString(R.string.common_loading_error) + error, Snackbar.LENGTH_SHORT).setAction("重试", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentLoadedDate.equals("0")) {
                        mZhihuPresenter.getLastZhihuNews();
                    } else {
                        mZhihuPresenter.getTheDaily(currentLoadedDate);
                    }
                }
            }).show();
        }
    }

    @Override
    public void updateList(ZhihuDaily zhihuDaily) {
        currentLoadedDate = zhihuDaily.getDate();
        zhihuStories.addAll(zhihuDaily.getStories());
        zhihuAdapter.notifyDataSetChanged();
        //若未填满屏幕
        if (!swipeTarget.canScrollVertically(View.SCROLL_INDICATOR_BOTTOM)){
            onLoadMore();
        }
    }
}
