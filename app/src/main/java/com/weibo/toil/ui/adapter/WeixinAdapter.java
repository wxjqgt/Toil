package com.weibo.toil.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.weibo.toil.utils.Constant;
import com.weibo.toil.utils.MainApplication;
import com.weibo.toil.R;
import com.weibo.toil.bean.weixin.WeixinNews;
import com.weibo.toil.ui.activity.WeixinNewsActivity;
import com.weibo.toil.ui.view.recyclerView.CommonAdapter;
import com.weibo.toil.ui.view.recyclerView.ViewHolder;
import com.weibo.toil.utils.DBUtils;
import com.weibo.toil.utils.ScreenUtil;
import com.weibo.toil.utils.SharePreferenceUtil;

import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class WeixinAdapter extends CommonAdapter<WeixinNews> {

    public WeixinAdapter(Context context, int LayoutId, List<WeixinNews> datas) {
        super(context, LayoutId, datas);
    }

    @Override
    public void convert(final Context mContext, final ViewHolder holder, final WeixinNews weixinNews, int position) {
        Subscription subscription = Observable.just(weixinNews.getUrl())
                .map(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String s) {
                        return DBUtils.getDB().isRead(Constant.WEIXIN, s, 1);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (aBoolean) {
                            holder.setTextColor(R.id.tv_title,Color.GRAY);
                        } else{
                            holder.setTextColor(R.id.tv_title,Color.BLACK);
                        }
                    }
                });
        MainApplication.addToList(subscription);

        holder.setText(R.id.tv_description,weixinNews.getDescription());
        holder.setText(R.id.tv_title,weixinNews.getTitle());
        holder.setText(R.id.tv_time,weixinNews.getHottime());

        if (!TextUtils.isEmpty(weixinNews.getPicUrl())) {
            holder.setImageSrc(R.id.iv_weixin,weixinNews.getPicUrl());
        } else {
            holder.setImageSrc(R.id.iv_weixin,R.drawable.bg);
        }
        holder.setOnClickListener(R.id.btn_weixin,new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(mContext, holder.getView(R.id.btn_weixin));
                popupMenu.getMenuInflater().inflate(R.menu.pop_menu, popupMenu.getMenu());
                popupMenu.getMenu().removeItem(R.id.pop_fav);
                final boolean isRead = DBUtils.getDB().isRead(Constant.WEIXIN, weixinNews.getUrl(), 1);
                if (!isRead){
                    popupMenu.getMenu().findItem(R.id.pop_unread).setTitle(R.string.common_set_read);
                } else{
                    popupMenu.getMenu().findItem(R.id.pop_unread).setTitle(R.string.common_set_unread);
                }
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.pop_unread:
                                if (isRead) {
                                    DBUtils.getDB().insertHasRead(Constant.WEIXIN, weixinNews.getUrl(), 0);
                                    holder.setTextColor(R.id.tv_title,Color.BLACK);
                                } else {
                                    DBUtils.getDB().insertHasRead(Constant.WEIXIN, weixinNews.getUrl(), 1);
                                    holder.setTextColor(R.id.tv_title,Color.GRAY);
                                }
                                break;
                            case R.id.pop_share:
                                Intent shareIntent = new Intent();
                                shareIntent.setAction(Intent.ACTION_SEND);
                                shareIntent.putExtra(Intent.EXTRA_TEXT, weixinNews.getTitle() + " " + weixinNews.getUrl() + mContext.getString(R.string.share_tail));
                                shareIntent.setType("text/plain");
                                //设置分享列表的标题，并且每次都显示分享列表
                                mContext.startActivity(Intent.createChooser(shareIntent, mContext.getString(R.string.share)));
                                break;
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
        runEnterAnimation(mContext,holder.itemView, position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBUtils.getDB().insertHasRead(Constant.WEIXIN, weixinNews.getUrl(), 1);
                holder.setTextColor(R.id.tv_title,Color.GRAY);
                if (SharePreferenceUtil.isUseLocalBrowser(mContext)) {
                    mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(weixinNews.getUrl())));
                } else {
                    Intent intent = new Intent(mContext, WeixinNewsActivity.class);
                    intent.putExtra("url", weixinNews.getUrl());
                    intent.putExtra("title", weixinNews.getTitle());
                    mContext.startActivity(intent);
                }
            }
        });
    }

    private void runEnterAnimation(Context mContext,View view, int position) {
        view.setTranslationY(ScreenUtil.getScreenHight(mContext));
        view.animate().translationY(0)
                .setStartDelay(100 * (position % 5))
                .setInterpolator(new DecelerateInterpolator(3.f))
                .setDuration(700)
                .start();
    }


}
