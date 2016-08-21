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

import com.weibo.toil.utils.MainApplication;
import com.weibo.toil.R;
import com.weibo.toil.bean.itHome.ItHomeItem;
import com.weibo.toil.presenter.IItHomePresenter;
import com.weibo.toil.presenter.impl.ItHomePresenterImpl;
import com.weibo.toil.ui.activity.MainActivity;
import com.weibo.toil.ui.adapter.ItAdapter;
import com.weibo.toil.ui.iView.IItHomeFragment;
import com.weibo.toil.ui.view.DividerItemDecoration;
import com.weibo.toil.utils.NetWorkUtil;
import com.weibo.toil.utils.SharePreferenceUtil;

import java.util.ArrayList;

import butterknife.BindView;

public class ItHomeFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, IItHomeFragment {

    private void initView() {
        swipeRefreshLayout.setOnRefreshListener(this);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        setSwipeRefreshLayoutColor(swipeRefreshLayout);
        swipeTarget.setLayoutManager(mLinearLayoutManager);
        swipeTarget.setHasFixedSize(true);
        swipeTarget.addItemDecoration(new DividerItemDecoration(mainActivity,DividerItemDecoration.VERTICAL_LIST));
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
        itAdapter = new ItAdapter(mainActivity,R.layout.ithome_item, itHomeItems);
        swipeTarget.setAdapter(itAdapter);
        mItHomePresenter.getNewsFromCache();
        if (SharePreferenceUtil.isRefreshOnlyWifi(getActivity())) {
            if (NetWorkUtil.isWifiConnected(MainApplication.getContext())) {
                onRefresh();
            } else {
                Toast.makeText(mainActivity, getString(R.string.toast_wifi_refresh_data), Toast.LENGTH_SHORT).show();
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

    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.swipe_target)
    RecyclerView swipeTarget;
    @BindView(R.id.swipeToLoadLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    private MainActivity mainActivity;
    private ArrayList<ItHomeItem> itHomeItems = new ArrayList<>();
    private ItAdapter itAdapter;
    private IItHomePresenter mItHomePresenter;
    private String currentNewsId = "0";
    private LinearLayoutManager mLinearLayoutManager;
    private boolean loading = false;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;

    public static ItHomeFragment newInstance() {
        ItHomeFragment fragment = new ItHomeFragment();
        return fragment;
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
        mItHomePresenter = new ItHomePresenterImpl(this, getActivity());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mItHomePresenter.unsubcrible();
    }

    @Override
    public void onRefresh() {
        currentNewsId = "0";
        itHomeItems.clear();
        //2016-04-05修复Inconsistency detected. Invalid view holder adapter positionViewHolder
        itAdapter.notifyDataSetChanged();
        mItHomePresenter.getNewItHomeNews();
    }

    public void onLoadMore() {
        mItHomePresenter.getMoreItHomeNews(currentNewsId);
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
        if (swipeRefreshLayout != null) {//不加可能会崩溃
            swipeRefreshLayout.setRefreshing(false);
            loading = false;
        }
    }

    @Override
    public void showError(String error) {
        Snackbar.make(swipeRefreshLayout, getString(R.string.common_loading_error) + error, Snackbar.LENGTH_SHORT).setAction(getString(R.string.comon_retry), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentNewsId.equals("0")) {
                    mItHomePresenter.getNewItHomeNews();
                } else {
                    mItHomePresenter.getMoreItHomeNews(currentNewsId);
                }
            }
        }).show();
    }

    @Override
    public void updateList(ArrayList<ItHomeItem> itHomeItems) {
        currentNewsId = itHomeItems.get(itHomeItems.size() - 1).getNewsid();
        this.itHomeItems.addAll(itHomeItems);
        itAdapter.notifyDataSetChanged();
    }
}
