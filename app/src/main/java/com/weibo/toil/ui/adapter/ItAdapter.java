package com.weibo.toil.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.View;

import com.weibo.toil.utils.Constant;
import com.weibo.toil.utils.MainApplication;
import com.weibo.toil.R;
import com.weibo.toil.bean.itHome.ItHomeItem;
import com.weibo.toil.ui.activity.ItHomeActivity;
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

public class ItAdapter extends CommonAdapter<ItHomeItem> {

    //解决item状态混乱问题
    private SparseBooleanArray mSparseBooleanArray = new SparseBooleanArray();

    public ItAdapter(Context context, int LayoutId, List<ItHomeItem> datas) {
        super(context, LayoutId, datas);
    }
    @Override
    public void convert(final Context mContext, final ViewHolder holder, final ItHomeItem itHomeItem, int position) {
        Subscription subscription = Observable.just(itHomeItem.getNewsid())
                .map(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String s) {
                        return DBUtils.getDB().isRead(Constant.IT,s , 1);
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

        holder.setText(R.id.tv_title,itHomeItem.getTitle());
        holder.setText(R.id.tv_time,itHomeItem.getPostdate());
        holder.setText(R.id.tv_description,itHomeItem.getDescription());
        holder.setImageSrc(R.id.iv_ithome,itHomeItem.getImage());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.setTextColor(R.id.tv_title,Color.GRAY);
                mContext.startActivity(new Intent(mContext, ItHomeActivity.class).putExtra("item", itHomeItem));
                DBUtils.getDB().insertHasRead(Constant.IT, itHomeItem.getNewsid(), 1);
            }
        });
        if (mSparseBooleanArray.get(Integer.parseInt(itHomeItem.getNewsid()))){
            holder.setImageSrc(R.id.btn_detail,R.drawable.ic_expand_less_black_24px);
            holder.getView(R.id.tv_description).setVisibility(View.VISIBLE);
        }else{
            holder.setImageSrc(R.id.btn_detail,R.drawable.ic_expand_more_black_24px);
            holder.getView(R.id.tv_description).setVisibility(View.GONE);
        }
        holder.setOnClickListener(R.id.tv_description,new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.getView(R.id.tv_description).getVisibility() == View.GONE) {
                    holder.setImageSrc(R.id.btn_detail,R.drawable.ic_expand_less_black_24px);
                    holder.getView(R.id.tv_description).setVisibility(View.VISIBLE);
                    mSparseBooleanArray.put(Integer.parseInt(itHomeItem.getNewsid()),true);
                } else {
                    holder.setImageSrc(R.id.btn_detail,R.drawable.ic_expand_more_black_24px);
                    holder.getView(R.id.tv_description).setVisibility(View.GONE);
                    mSparseBooleanArray.put(Integer.parseInt(itHomeItem.getNewsid()),false);
                }
            }
        });
    }

}
