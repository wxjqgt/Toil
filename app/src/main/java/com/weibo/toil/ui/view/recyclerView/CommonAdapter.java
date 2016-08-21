package com.weibo.toil.ui.view.recyclerView;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Administrator on 2016/7/9.
 */
public abstract class CommonAdapter<T> extends RecyclerView.Adapter<ViewHolder>{

    private List<T> datas;
    private Context context;
    private int LayoutId;

    public CommonAdapter(Context context,int LayoutId,List<T> datas) {
        this.context = context;
        this.datas = datas;
        this.LayoutId = LayoutId;
    }

    public void addDatas(List<T> data){
        datas.addAll(data);
        this.notifyDataSetChanged();
    }

    public void setDatas(List<T> data){
        this.datas = data;
        this.notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(LayoutId,parent,false);
        return ViewHolder.createViewHolder(context, itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        convert(context,holder,datas.get(position),position);
    }

    public abstract void convert(Context context,ViewHolder holder,T t,int position);

    @Override
    public int getItemCount() {
        return datas.size();
    }

}
