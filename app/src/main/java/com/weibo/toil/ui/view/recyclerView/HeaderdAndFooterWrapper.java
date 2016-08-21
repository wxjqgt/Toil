package com.weibo.toil.ui.view.recyclerView;

import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Administrator on 2016/7/9.
 */
public class HeaderdAndFooterWrapper extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private SparseArrayCompat<View> headerViews = new SparseArrayCompat<>();
    private SparseArrayCompat<View> footerViews = new SparseArrayCompat<>();

    private static final int BASE_ITEM_TYPE_HEARDER = 100000;
    private static final int BASE_ITEM_TYPE_FOOTER = 100000;

    private RecyclerView.Adapter adapter;

    public HeaderdAndFooterWrapper(RecyclerView.Adapter adapter) {
        this.adapter = adapter;
    }

    public void addHearderView(View view) {
        headerViews.put(getHeadersCount() + BASE_ITEM_TYPE_HEARDER, view);
    }

    public void addFooterView(View view) {
        footerViews.put(BASE_ITEM_TYPE_FOOTER + getFootersCount(), view);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (headerViews.get(viewType) != null){
            view = headerViews.get(viewType);
        }else if (footerViews.get(viewType) != null){
            view = footerViews.get(viewType);
        }
        if (view != null) {
            RecyclerView.ViewHolder holder = ViewHolder.createViewHolder(parent.getContext(), view);
            return holder;
        }
        return adapter.onCreateViewHolder(parent,viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (isHeaderViewPos(position) || isFootersViewPos(position)){
            return;
        }
        adapter.onBindViewHolder(holder,position - getHeadersCount());
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        adapter.onViewAttachedToWindow(holder);
        int position = holder.getLayoutPosition();
        if (isFootersViewPos(position) || isHeaderViewPos(position)){
            ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
            if (layoutParams != null && layoutParams instanceof StaggeredGridLayoutManager.LayoutParams){
                StaggeredGridLayoutManager.LayoutParams lp = (StaggeredGridLayoutManager.LayoutParams) layoutParams;
                lp.setFullSpan(true);
            }
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        adapter.onAttachedToRecyclerView(recyclerView);
        final RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager){
            final GridLayoutManager gridLayoutManager = (GridLayoutManager) manager;
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int viewType = getItemViewType(position);
                    if (
                            headerViews.get(viewType) != null ||
                                    footerViews.get(viewType) != null)
                    {
                        int count = ((GridLayoutManager) manager).getSpanCount();
                        return count;
                    }
                    return 1;
                }
            });
            gridLayoutManager.setSpanCount(gridLayoutManager.getSpanCount());
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeaderViewPos(position)){
            return headerViews.keyAt(position);
        }else if (isFootersViewPos(position)) {
            return footerViews.keyAt(position - getHeadersCount() - getRealItemCount());
        }
        return adapter.getItemViewType(position - getHeadersCount());
    }

    @Override
    public int getItemCount() {
        return getHeadersCount() + getRealItemCount() + getFootersCount();
    }

    private boolean isHeaderViewPos(int position) {
        return position < getHeadersCount();
    }

    private boolean isFootersViewPos(int position) {
        return position >= getHeadersCount() + getRealItemCount();
    }

    private int getRealItemCount(){
        return adapter.getItemCount();
    }

    private int getHeadersCount() {
        return headerViews.size();
    }

    private int getFootersCount() {
        return footerViews.size();
    }

}
