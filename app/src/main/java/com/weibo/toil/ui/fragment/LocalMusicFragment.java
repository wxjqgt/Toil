package com.weibo.toil.ui.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.weibo.toil.R;
import com.weibo.toil.bean.music.Mp3Info;
import com.weibo.toil.ui.activity.MainActivity;
import com.weibo.toil.ui.adapter.LocalMusicAdapter;
import com.weibo.toil.ui.event.MusicPosition;
import com.weibo.toil.ui.event.MusicProgress;
import com.weibo.toil.ui.event.MusicResume;
import com.weibo.toil.ui.view.CoverView;
import com.weibo.toil.ui.view.DividerItemDecoration;
import com.weibo.toil.ui.view.ProgressView;
import com.weibo.toil.ui.view.recyclerView.CommonAdapter;
import com.weibo.toil.ui.view.recyclerView.ItemCallback;
import com.weibo.toil.ui.view.recyclerView.OnRecyclerViewItemClickListener;
import com.weibo.toil.ui.view.recyclerView.ViewHolder;
import com.weibo.toil.utils.ImageLoader;
import com.weibo.toil.utils.MediaUtils;
import com.weibo.toil.utils.MusicService;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class LocalMusicFragment extends BaseFragmentWithEventBus implements SwipeRefreshLayout.OnRefreshListener {

    private void lodaMusicData() {

        refreshLayout.setOnRefreshListener(this);
        setSwipeRefreshLayoutColor(refreshLayout);

        linearLayoutManager = new LinearLayoutManager(mActivity);
        localMusic.setLayoutManager(linearLayoutManager);
        localMusic.setHasFixedSize(true);
        localMusic.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        loacalMusicAdapter = new LocalMusicAdapter(mActivity, R.layout.listview_item, musicService.mp3InfoList);
        localMusic.setAdapter(loacalMusicAdapter);
        addRecyclerViewListener();

    }

    private void addRecyclerViewListener(){

        localMusic.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //向下滚动
                {
                    visibleItemCount = linearLayoutManager.getChildCount();
                    totalItemCount = linearLayoutManager.getItemCount();
                    pastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();

                    if (!loading && (visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                        loading = true;
                        //可以在这做分页加载
                    }
                }
            }
        });

        localMusic.addOnItemTouchListener(new OnRecyclerViewItemClickListener(localMusic) {
            @Override
            public void OnItemClickLitener(RecyclerView.ViewHolder viewHolder) {
                musicService.playMusic(viewHolder.getAdapterPosition());
            }

            @Override
            public void OnItemLongClickLitener(RecyclerView.ViewHolder viewHolder) {
                Snackbar.make(refreshLayout, "文件操作！", Snackbar.LENGTH_LONG).show();
            }
        });

    }


    @Override
    protected int setContentView() {
        return R.layout.localmusicfragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lodaMusicData();
    }

    @Override
    public void onRefresh() {
        musicService.updateMusicList();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshLocalMusic(List<Mp3Info> data) {
        loacalMusicAdapter.setDatas(data);
        loacalMusicAdapter.notifyDataSetChanged();
        refreshLayout.setRefreshing(false);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mActivity = null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (MainActivity) context;
        musicService = mActivity.ms;
    }


    public LocalMusicFragment() {

    }

    public static LocalMusicFragment newInstance() {
        LocalMusicFragment fragment = new LocalMusicFragment();
        return fragment;
    }

    private MainActivity mActivity;
    private MusicService musicService;
    private LinearLayoutManager linearLayoutManager;
    private boolean loading = false;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private CommonAdapter<Mp3Info> loacalMusicAdapter;

    @BindView(R.id.localMusic)
    RecyclerView localMusic;
    @BindView(R.id.refresh)
    SwipeRefreshLayout refreshLayout;

}
