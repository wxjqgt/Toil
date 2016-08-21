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
import com.weibo.toil.bean.weiboVideo.WeiboVideoBlog;
import com.weibo.toil.presenter.IVideoPresenter;
import com.weibo.toil.presenter.impl.VideoPresenterImpl;
import com.weibo.toil.ui.activity.MainActivity;
import com.weibo.toil.ui.adapter.VideoAdapter;
import com.weibo.toil.ui.iView.IVideoFragment;
import com.weibo.toil.utils.NetWorkUtil;
import com.weibo.toil.utils.SharePreferenceUtil;

import java.util.ArrayList;

import butterknife.BindView;

public class VideoFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, IVideoFragment {

    private void initView() {
        swipeRefreshLayout.setOnRefreshListener(this);
        setSwipeRefreshLayoutColor(swipeRefreshLayout);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        swipeTarget.setLayoutManager(mLinearLayoutManager);
        swipeTarget.setHasFixedSize(true);
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
        videoAdapter = new VideoAdapter(mainActivity,R.layout.video_item ,mWeiboVideoBlogs);
        swipeTarget.setAdapter(videoAdapter);
        mIVideoPresenter.getVideoFromCache(1);
        if (SharePreferenceUtil.isRefreshOnlyWifi(MainApplication.getContext())) {
            if (NetWorkUtil.isWifiConnected(getActivity())) {
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
    private LinearLayoutManager mLinearLayoutManager;
    private boolean loading = false;
    int pastVisiblesItems, visibleItemCount, totalItemCount;

    private MainActivity mainActivity;
    private ArrayList<WeiboVideoBlog> mWeiboVideoBlogs = new ArrayList<>();
    private int currentPage = 1;
    private IVideoPresenter mIVideoPresenter;
    private VideoAdapter videoAdapter;

    public static VideoFragment newInstance() {
        VideoFragment fragment = new VideoFragment();
        return fragment;
    }

    public VideoFragment() {
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
        mIVideoPresenter = new VideoPresenterImpl(this, getActivity());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mIVideoPresenter.unsubcrible();
    }

    @Override
    public void onRefresh() {
        currentPage = 1;
        mWeiboVideoBlogs.clear();
        videoAdapter.notifyDataSetChanged();
        mIVideoPresenter.getVideo(currentPage);
    }

    public void onLoadMore() {
        mIVideoPresenter.getVideo(currentPage);
    }

    @Override
    public void showProgressDialog() {
        if (progressBar != null)
            progressBar.setVisibility(View.VISIBLE);
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
            mIVideoPresenter.getVideoFromCache(currentPage);
            Snackbar.make(swipeTarget, getString(R.string.common_loading_error) + error, Snackbar.LENGTH_SHORT).setAction("重试", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mIVideoPresenter.getVideo(currentPage);
                }
            }).show();
        }
    }

    @Override
    public void updateList(ArrayList<WeiboVideoBlog> weiboVideoBlogs) {
        currentPage++;
        mWeiboVideoBlogs.addAll(weiboVideoBlogs);
        videoAdapter.notifyDataSetChanged();
    }
}
