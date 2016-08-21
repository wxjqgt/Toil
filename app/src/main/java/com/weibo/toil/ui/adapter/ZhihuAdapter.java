package com.weibo.toil.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.weibo.toil.utils.Constant;
import com.weibo.toil.utils.MainApplication;
import com.weibo.toil.R;
import com.weibo.toil.bean.zhihu.ZhihuDailyItem;
import com.weibo.toil.ui.activity.ZhihuStoryActivity;
import com.weibo.toil.ui.view.recyclerView.CommonAdapter;
import com.weibo.toil.ui.view.recyclerView.ViewHolder;
import com.weibo.toil.utils.DBUtils;
import com.weibo.toil.utils.ScreenUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class ZhihuAdapter extends CommonAdapter<ZhihuDailyItem> {

    public ZhihuAdapter(Context context, int LayoutId, List<ZhihuDailyItem> datas) {
        super(context, LayoutId, datas);
    }

    class ZhihuViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_zhihu_daily)
        ImageView ivZhihuDaily;
        @BindView(R.id.tv_zhihu_daily)
        TextView tvZhihuDaily;
        @BindView(R.id.cv_zhihu)
        CardView cvZhihu;
        @BindView(R.id.tv_time)
        TextView tvTime;
        @BindView(R.id.btn_zhihu)
        ImageView btnZhihu;

        ZhihuViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
    @Override
    public void convert(final Context mContext, final ViewHolder holder, final ZhihuDailyItem zhihuDailyItem, int position) {

        Subscription subscription = Observable.just(zhihuDailyItem.getId())
                .map(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String s) {
                        return DBUtils.getDB().isRead(Constant.ZHIHU, s, 1);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (aBoolean) {
                            holder.setTextColor(R.id.tv_zhihu_daily,Color.GRAY);
                        } else{
                            holder.setTextColor(R.id.tv_zhihu_daily,Color.BLACK);
                        }
                    }
                });
        MainApplication.addToList(subscription);

        holder.setText(R.id.tv_zhihu_daily,zhihuDailyItem.getTitle());
        holder.setText(R.id.tv_time,zhihuDailyItem.getDate());
        holder.setOnClickListener(R.id.cv_zhihu,new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.setTextColor(R.id.tv_zhihu_daily,Color.GRAY);
                Intent intent = new Intent(mContext, ZhihuStoryActivity.class);
                intent.putExtra("type", ZhihuStoryActivity.TYPE_ZHIHU);
                intent.putExtra("id", zhihuDailyItem.getId());
                intent.putExtra("title", zhihuDailyItem.getTitle());
                mContext.startActivity(intent);
                DBUtils.getDB().insertHasRead(Constant.ZHIHU, zhihuDailyItem.getId(), 1);
            }
        });
        holder.setOnClickListener(R.id.btn_zhihu,new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(mContext, holder.getView(R.id.btn_zhihu));
                popupMenu.getMenuInflater().inflate(R.menu.pop_menu, popupMenu.getMenu());
                popupMenu.getMenu().removeItem(R.id.pop_share);
                popupMenu.getMenu().removeItem(R.id.pop_fav);
                final boolean isRead = DBUtils.getDB().isRead(Constant.ZHIHU, zhihuDailyItem.getId(), 1);
                if (!isRead)
                    popupMenu.getMenu().findItem(R.id.pop_unread).setTitle(R.string.common_set_read);
                else
                    popupMenu.getMenu().findItem(R.id.pop_unread).setTitle(R.string.common_set_unread);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.pop_unread:
                                if (isRead) {
                                    DBUtils.getDB().insertHasRead(Constant.ZHIHU, zhihuDailyItem.getId(), 0);
                                    holder.setTextColor(R.id.tv_zhihu_daily,Color.BLACK);
                                } else {
                                    DBUtils.getDB().insertHasRead(Constant.ZHIHU, zhihuDailyItem.getId(), 1);
                                    holder.setTextColor(R.id.tv_zhihu_daily,Color.GRAY);
                                }
                                break;
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
        runEnterAnimation(mContext,holder.itemView);
        if (zhihuDailyItem.getImages() != null)
            holder.setImageSrc(R.id.iv_zhihu_daily,zhihuDailyItem.getImages()[0]);
    }

    private void runEnterAnimation(Context mContext,View view) {
        view.setTranslationX(ScreenUtil.getScreenWidth(mContext));
        view.animate()
                .translationX(0)
                .setStartDelay(100)
                .setInterpolator(new DecelerateInterpolator(3.f))
                .setDuration(700)
                .start();
    }

}
