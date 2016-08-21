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
import com.weibo.toil.bean.guokr.GuokrHotItem;
import com.weibo.toil.presenter.IGuokrPresenter;
import com.weibo.toil.presenter.impl.GuokrPresenterImpl;
import com.weibo.toil.ui.activity.MainActivity;
import com.weibo.toil.ui.adapter.GuokrAdapter;
import com.weibo.toil.ui.iView.IGuokrFragment;
import com.weibo.toil.ui.view.DividerItemDecoration;
import com.weibo.toil.utils.NetWorkUtil;
import com.weibo.toil.utils.SharePreferenceUtil;

import java.util.ArrayList;

import butterknife.BindView;

public class GuokrFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, IGuokrFragment {

    private void initData() {
        mGuokrPresenter = new GuokrPresenterImpl(this, getActivity());
    }

    private void initView() {
        mLinearLayoutManager = new LinearLayoutManager(mainActivity);
        swipeTarget.setLayoutManager(mLinearLayoutManager);
        swipeTarget.setHasFixedSize(true);
        swipeTarget.addItemDecoration(new DividerItemDecoration(mainActivity, DividerItemDecoration.VERTICAL_LIST));
        guokrAdapter = new GuokrAdapter(mainActivity,R.layout.ithome_item,guokrHotItems);
        swipeTarget.setAdapter(guokrAdapter);
        mGuokrPresenter.getGuokrHotFromCache(0);
        swipeRefreshLayout.setOnRefreshListener(this);
        setSwipeRefreshLayoutColor(swipeRefreshLayout);
        addListener();
    }

    private void addListener(){
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

        if (SharePreferenceUtil.isRefreshOnlyWifi(getActivity())) {
            if (NetWorkUtil.isWifiConnected(MainApplication.getContext())) {
                onRefresh();
            } else {
                Toast.makeText(getActivity(), R.string.toast_wifi_refresh_data, Toast.LENGTH_SHORT).show();
            }
        } else {
            onRefresh();
        }
    }

    @Override
    public void onRefresh() {
        currentOffset = 0;
        guokrHotItems.clear();
        //2016-04-05修复Inconsistency detected. Invalid view holder adapter positionViewHolder
        guokrAdapter.notifyDataSetChanged();
        mGuokrPresenter.getGuokrHot(currentOffset);
    }

    public void onLoadMore() {
        mGuokrPresenter.getGuokrHot(currentOffset);
    }

    @Override
    public void showProgressDialog() {
        if (progressBar != null){
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hidProgressDialog() {
        if (swipeRefreshLayout != null) {//不加可能会崩溃
            swipeRefreshLayout.setRefreshing(false);
            loading = false;
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void showError(String error) {
        mGuokrPresenter.getGuokrHotFromCache(currentOffset);
        Snackbar.make(swipeTarget, getString(R.string.common_loading_error) + error, Snackbar.LENGTH_SHORT).setAction(getString(R.string.comon_retry), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGuokrPresenter.getGuokrHot(currentOffset);
            }
        }).show();
    }

    @Override
    public void updateList(ArrayList<GuokrHotItem> guokrHotItems) {
        currentOffset++;
        this.guokrHotItems.addAll(guokrHotItems);
        guokrAdapter.notifyDataSetChanged();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        initView();
    }

    @BindView(R.id.swipe_target)
    RecyclerView swipeTarget;
    @BindView(R.id.swipeToLoadLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private ArrayList<GuokrHotItem> guokrHotItems = new ArrayList<>();
    private GuokrAdapter guokrAdapter;
    private IGuokrPresenter mGuokrPresenter;
    private int currentOffset;
    private LinearLayoutManager mLinearLayoutManager;
    private boolean loading = false;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    private MainActivity mainActivity;

    public static GuokrFragment newInstance() {
        GuokrFragment fragment = new GuokrFragment();
        return fragment;
    }
    public GuokrFragment() {
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_common;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mGuokrPresenter.unsubcrible();
    }

}
