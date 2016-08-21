package com.weibo.toil.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.View;

import com.weibo.toil.utils.Constant;
import com.weibo.toil.utils.MainApplication;
import com.weibo.toil.R;
import com.weibo.toil.bean.guokr.GuokrHotItem;
import com.weibo.toil.ui.activity.ZhihuStoryActivity;
import com.weibo.toil.ui.view.recyclerView.CommonAdapter;
import com.weibo.toil.ui.view.recyclerView.ViewHolder;
import com.weibo.toil.utils.DBUtils;

import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class GuokrAdapter extends CommonAdapter<GuokrHotItem> {

    public GuokrAdapter(Context context, int LayoutId, List<GuokrHotItem> datas) {
        super(context, LayoutId, datas);
    }

    @Override
    public void convert(final Context mContext, final ViewHolder holder, final GuokrHotItem guokrHotItem, int position) {
        Subscription subscription = Observable.just(guokrHotItem.getId())
                .map(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String s) {
                        return DBUtils.getDB().isRead(Constant.GUOKR, s, 1);
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

        holder.setText(R.id.tv_title,guokrHotItem.getTitle());
        holder.setText(R.id.tv_description,guokrHotItem.getSummary());
        holder.setText(R.id.tv_time,guokrHotItem.getTime());
        holder.setImageSrc(R.id.iv_ithome,guokrHotItem.getSmallImage());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.setTextColor(R.id.tv_title,Color.GRAY);
                Intent intent = new Intent(mContext, ZhihuStoryActivity.class);
                intent.putExtra("type", ZhihuStoryActivity.TYPE_GUOKR);
                intent.putExtra("id", guokrHotItem.getId());
                intent.putExtra("title", guokrHotItem.getTitle());
                mContext.startActivity(intent);
                DBUtils.getDB().insertHasRead(Constant.GUOKR, guokrHotItem.getId(), 1);
            }
        });

        if (mSparseBooleanArray.get(Integer.parseInt(guokrHotItem.getId()))){
            holder.setImageSrc(R.id.btn_detail,R.drawable.ic_expand_less_black_24px);
            holder.getView(R.id.tv_description).setVisibility(View.VISIBLE);
        }else{
            holder.setImageSrc(R.id.btn_detail,R.drawable.ic_expand_more_black_24px);
            holder.getView(R.id.tv_description).setVisibility(View.GONE);
        }
        holder.setOnClickListener(R.id.btn_detail,new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.getView(R.id.btn_detail).getVisibility() == View.GONE) {
                    holder.setImageSrc(R.id.btn_detail,R.drawable.ic_expand_less_black_24px);
                    holder.getView(R.id.tv_description).setVisibility(View.VISIBLE);
                    mSparseBooleanArray.put(Integer.parseInt(guokrHotItem.getId()), true);
                } else {
                    holder.setImageSrc(R.id.btn_detail,R.drawable.ic_expand_more_black_24px);
                    holder.getView(R.id.tv_description).setVisibility(View.GONE);
                    mSparseBooleanArray.put(Integer.parseInt(guokrHotItem.getId()), false);
                }
            }
        });
    }
    //解决item状态混乱问题
    private SparseBooleanArray mSparseBooleanArray = new SparseBooleanArray();

}
