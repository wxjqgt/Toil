package com.weibo.toil.ui.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.weibo.toil.utils.Constant;
import com.weibo.toil.utils.MainApplication;
import com.weibo.toil.R;
import com.weibo.toil.api.util.UtilRequest;
import com.weibo.toil.bean.weiboVideo.WeiboVideoBlog;
import com.weibo.toil.ui.activity.VideoActivity;
import com.weibo.toil.ui.activity.VideoWebViewActivity;
import com.weibo.toil.ui.view.recyclerView.CommonAdapter;
import com.weibo.toil.ui.view.recyclerView.ViewHolder;
import com.weibo.toil.utils.DBUtils;
import com.weibo.toil.utils.SharePreferenceUtil;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class VideoAdapter extends CommonAdapter<WeiboVideoBlog> {

    public VideoAdapter(Context context, int LayoutId, List<WeiboVideoBlog> datas) {
        super(context, LayoutId, datas);
    }

    @Override
    public void convert(final Context mContext, final ViewHolder holder, final WeiboVideoBlog weiboVideoBlog, int position) {
        final String title = weiboVideoBlog.getBlog().getText().replaceAll("&[a-zA-Z]{1,10};", "").replaceAll(
                "<[^>]*>", "");

        Subscription subscription = Observable.just(weiboVideoBlog.getBlog().getPageInfo().getVideoUrl())
                .map(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String s) {
                        return DBUtils.getDB().isRead(Constant.VIDEO,s , 1);
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

        holder.setImageSrc(R.id.iv_video,weiboVideoBlog.getBlog().getPageInfo().getVideoPic());
        holder.setText(R.id.tv_title,title);
        holder.setText(R.id.tv_time,weiboVideoBlog.getBlog().getCreateTime());
        holder.setOnClickListener(R.id.btn_video,new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(mContext, holder.getView(R.id.btn_video));
                popupMenu.getMenuInflater().inflate(R.menu.pop_menu, popupMenu.getMenu());
                popupMenu.getMenu().removeItem(R.id.pop_fav);
                final boolean isRead = DBUtils.getDB().isRead(Constant.VIDEO, weiboVideoBlog.getBlog().getPageInfo().getVideoUrl(), 1);
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
                                    DBUtils.getDB().insertHasRead(Constant.VIDEO, weiboVideoBlog.getBlog().getPageInfo().getVideoUrl(), 0);
                                    holder.setTextColor(R.id.tv_title,Color.BLACK);
                                } else {
                                    DBUtils.getDB().insertHasRead(Constant.VIDEO, weiboVideoBlog.getBlog().getPageInfo().getVideoUrl(), 1);
                                    holder.setTextColor(R.id.tv_title,Color.GRAY);
                                }
                                break;
                            case R.id.pop_share:
                                Intent shareIntent = new Intent();
                                shareIntent.setAction(Intent.ACTION_SEND);
                                shareIntent.putExtra(Intent.EXTRA_TEXT, title + " " + weiboVideoBlog.getBlog().getPageInfo().getVideoUrl() + mContext.getString(R.string.share_tail));
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
        holder.setOnClickListener(R.id.cv_video,new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBUtils.getDB().insertHasRead(Constant.VIDEO, weiboVideoBlog.getBlog().getPageInfo().getVideoUrl(), 1);
                holder.setTextColor(R.id.tv_title,Color.GRAY);
                VideoAdapter.this.getPlayUrl(mContext,weiboVideoBlog, title);
            }
        });
    }

    private void getPlayUrl(final Context mContext, final WeiboVideoBlog weiboVideoBlog, final String title) {
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(mContext.getString(R.string.fragment_video_get_url));
        progressDialog.show();
        Subscription subscription = UtilRequest.getUtilApi().getVideoUrl(weiboVideoBlog.getBlog().getPageInfo().getVideoUrl())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (progressDialog.isShowing()) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Toast.makeText(mContext, "视频解析失败！", Toast.LENGTH_SHORT).show();
                            mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(weiboVideoBlog.getBlog().getPageInfo().getVideoUrl())));
                        }
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        //防止停止后继续执行
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                            try {
                                String shareUrl;
                                Pattern pattern = Pattern.compile(".*?target=\"_blank\">(.*?)</a>.*?");
                                final Matcher matcher = pattern.matcher(responseBody.string());
                                shareUrl = weiboVideoBlog.getBlog().getPageInfo().getVideoUrl();
                                if (TextUtils.isEmpty(shareUrl)) {
                                    Toast.makeText(mContext, "播放地址为空", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                Log.i("TAG", shareUrl);
                                if (matcher.find() && matcher.group(1).endsWith(".mp4")) {
                                    mContext.startActivity(new Intent(mContext, VideoActivity.class)
                                            .putExtra("url", matcher.group(1))
                                            .putExtra("shareUrl", shareUrl)
                                            .putExtra("title", title));
                                } else {
                                    String url = shareUrl;
                                    if (matcher.find())
                                        url = matcher.group(1);
                                    Log.i("TAG", url);
                                    if (SharePreferenceUtil.isUseLocalBrowser(mContext))
                                        mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                                    else
                                        mContext.startActivity(new Intent(mContext, VideoWebViewActivity.class)
                                                .putExtra("url", url));
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
        MainApplication.addToList(subscription);
    }

}
