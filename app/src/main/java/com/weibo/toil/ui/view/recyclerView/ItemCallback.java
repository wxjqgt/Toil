package com.weibo.toil.ui.view.recyclerView;

import android.content.Context;
import android.graphics.Color;
import android.os.Vibrator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.weibo.toil.utils.MainApplication;

import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2016/7/15.
 * 因为关闭页面之后再打开，会丧失之前所以拖拽后的样子，所以需要在clerview方法中
 * 对位置进行保存，然后在初始化数据时读取
 */
public class ItemCallback<T> extends ItemTouchHelper.Callback {

    private List<T> datas;

    /**
     *从外面传进来的adapter不能在执行notify移除，拖拽动作后重新刷新，所以不能从外部
     * 传，可以从onMove方法或getMovementFlags方法中获得，可以定义全局变量，再赋值方便onSwipe
     * 滑动方法中使用
     */
    private RecyclerView.Adapter adapter;

    public ItemCallback(List<T> datas) {
        this.datas = datas;
    }

    /**
     * 表示是不是允许长按然后开始拖拽item，默认为true
     * 如果需要控制一些item不允许拖拽，可以在这返回false，然后给recyclerview设置监听，在
     * 长按点击事件中用ItemTouchHelper,startDrag(ViewHolder)进行过滤
     */
    @Override
    public boolean isLongPressDragEnabled() {
        return super.isLongPressDragEnabled();
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            viewHolder.itemView.setBackgroundColor(Color.LTGRAY);
            Vibrator vibrator = (Vibrator) MainApplication.getContext()
                    .getSystemService(Context.VIBRATOR_SERVICE);
            //震动100毫秒
            vibrator.vibrate(100);
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        viewHolder.itemView.setBackgroundColor(0);
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        setAdapter(recyclerView.getAdapter());
        int dragFlags;
        int swipeFlags;
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            dragFlags =
                    ItemTouchHelper.UP | ItemTouchHelper.DOWN
                            | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT
            ;
            swipeFlags = 0;
        } else {
            dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        }
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        setAdapter(recyclerView.getAdapter());
        int from = viewHolder.getAdapterPosition();
        int to = target.getAdapterPosition();
        if (from < to) {
            //由于adpterPosition是从1开始的，而List集合是从0开始的，所以左右以下i都要减一
            for (int i = from; i < to; i++) {
                Collections.swap(datas, i - 1, i);
            }
        } else {
            //由于adpterPosition是从1开始的，而List集合是从0开始的，所以左右以下i都要减一
            for (int i = from; i > to; i--) {
                Collections.swap(datas, i + 1, i);
            }
        }
        adapter.notifyItemMoved(from, to);
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        adapter.notifyItemRemoved(position);
        //由于adpterPosition是从1开始的，而List集合是从0开始的，所以左右以下position都要减一
        datas.remove(position - 1);
    }

    private void setAdapter(RecyclerView.Adapter adapter){
        if (this.adapter == null) {
            this.adapter = adapter;
        }
    }

}
