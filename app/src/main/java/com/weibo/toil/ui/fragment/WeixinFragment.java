package com.weibo.toil.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.weibo.toil.R;
import com.weibo.toil.bean.weixin.WeixinNews;
import com.weibo.toil.presenter.IWeixinPresenter;
import com.weibo.toil.presenter.impl.WeiXinPresenterImpl;
import com.weibo.toil.ui.activity.MainActivity;
import com.weibo.toil.ui.adapter.WeixinAdapter;
import com.weibo.toil.ui.iView.IWeixinFragment;
import com.weibo.toil.ui.view.DividerItemDecoration;
import com.weibo.toil.utils.NetWorkUtil;
import com.weibo.toil.utils.SharePreferenceUtil;

import java.util.ArrayList;

import butterknife.BindView;

public class WeixinFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, IWeixinFragment {

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        initView();
    }

    private void initView() {
        showProgressDialog();
        swipeRefreshLayout.setOnRefreshListener(this);
        setSwipeRefreshLayoutColor(swipeRefreshLayout);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        swipeTarget.setLayoutManager(mLinearLayoutManager);
        swipeTarget.setHasFixedSize(true);
        swipeTarget.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
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
        weixinAdapter = new WeixinAdapter(mainActivity,R.layout.weixin_item,weixinNewses);
        swipeTarget.setAdapter(weixinAdapter);
        mWeixinPresenter.getWeixinNews(1);
        if (SharePreferenceUtil.isRefreshOnlyWifi(getActivity())) {
            if (NetWorkUtil.isWifiConnected(getActivity())) {
                onRefresh();
            } else {
                Toast.makeText(getActivity(), getString(R.string.toast_wifi_refresh_data), Toast.LENGTH_SHORT).show();
            }
        } else {
            onRefresh();
        }
    }

    private void initData() {
        mWeixinPresenter = new WeiXinPresenterImpl(this, getActivity());
    }

    @Override
    public void onRefresh() {
        currentPage = 1;
        weixinNewses.clear();
        //2016-04-05修复Inconsistency detected. Invalid view holder adapter positionViewHolder
        weixinAdapter.notifyDataSetChanged();
        mWeixinPresenter.getWeixinNews(currentPage);
    }

    public void onLoadMore() {
        mWeixinPresenter.getWeixinNews(currentPage);
    }

    @Override
    public void showProgressDialog() {
        if (progressBar != null)
            progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mWeixinPresenter.unsubcrible();
    }

    @Override
    public void hidProgressDialog() {
        if (swipeRefreshLayout != null) {//不加可能会崩溃
            swipeRefreshLayout.setRefreshing(false);
            loading = false;
        }
        if (progressBar != null)
            progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showError(String error) {
        if (swipeTarget != null) {
            mWeixinPresenter.getWeixinNewsFromCache(currentPage);
            Snackbar.make(swipeTarget, getString(R.string.common_loading_error) + error, Snackbar.LENGTH_INDEFINITE).setAction("重试", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mWeixinPresenter.getWeixinNews(currentPage);
                }
            }).show();
        }
    }

    @Override
    public void updateList(ArrayList<WeixinNews> weixinNewsesList) {
        currentPage++;
        weixinNewses.addAll(weixinNewsesList);
        weixinAdapter.notifyDataSetChanged();
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
    private WeixinAdapter weixinAdapter;
    private IWeixinPresenter mWeixinPresenter;
    private ArrayList<WeixinNews> weixinNewses = new ArrayList<>();
    private int currentPage = 1;
    private LinearLayoutManager mLinearLayoutManager;
    private boolean loading = false;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;

    public static WeixinFragment newInstance() {
        WeixinFragment fragment = new WeixinFragment();
        return fragment;
    }

    public WeixinFragment() {
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_common;
    }

}
