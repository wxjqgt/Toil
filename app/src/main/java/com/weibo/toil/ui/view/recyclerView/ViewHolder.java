package com.weibo.toil.ui.view.recyclerView;

import android.content.Context;
import android.net.Uri;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.weibo.toil.utils.MainApplication;
import com.weibo.toil.utils.ImageLoader;

import java.io.File;

/**
 * Created by Administrator on 2016/7/9.
 */
public class ViewHolder extends RecyclerView.ViewHolder {

    public View itemView;
    private Context context;

    private SparseArrayCompat<View> views = new SparseArrayCompat<>();

    public ViewHolder(View itemView,Context context) {
        super(itemView);
        this.itemView = itemView;
        this.context = context;
    }

    public static ViewHolder createViewHolder(Context context,View itemView){
        ViewHolder holder = new ViewHolder(itemView,context);
        return holder;
    }

    public static ViewHolder createViewHolder(Context context, int LaoutId, ViewGroup parent){
        View view = LayoutInflater.from(context).inflate(LaoutId,parent,false);
        ViewHolder holder = new ViewHolder(view, context);
        return holder;
    }

    public <T extends View> T getView(int id){
        View view = views.get(id);
        if (view == null) {
            view = itemView.findViewById(id);
            views.put(id,view);
        }
        return (T) view;
    }

    public void setText(int id,String text){
        ((TextView)getView(id)).setText(text);
    }

    public void setTextColor(int id,int color){
        ((TextView)getView(id)).setTextColor(color);
    }

    public void setImageSrc(int id,Uri uri){
        ImageLoader.load(MainApplication.getContext(),uri, (ImageView) getView(id));
    }

    public void setImageSrc(int id,String url){
        ImageLoader.load(MainApplication.getContext(),url, (ImageView) getView(id));
    }

    public void setImageSrc(int id,File url){
        ImageLoader.load(MainApplication.getContext(),url, (ImageView) getView(id));
    }

    public void setImageSrc(int id,int res){
        ((ImageView) getView(id)).setImageResource(res);
    }

    public void setOnClickListener(int id, View.OnClickListener onClickListener){
        getView(id).setOnClickListener(onClickListener);
    }

}
