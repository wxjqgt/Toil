package com.weibo.toil.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;

public class ImageLoader {

    private ImageLoader() {}

    public static void loadImage(Context context, String url, ImageView imageView) {
        if (Config.isNight) {
            imageView.setAlpha(0.2f);
            imageView.setBackgroundColor(Color.BLACK);
        }
        Glide.with(context).load(url).into(imageView);
    }


    public static void load(Context context, String url, ImageView imageView) {
        Glide.with(context).load(url).into(imageView);
    }

    public static void load(Context context, File file, ImageView imageView) {
        Glide.with(context).load(file).into(imageView);
    }

    public static void load(Context context, Uri uri,ImageView imageView) {
        Glide.with(context).load(uri).into(imageView);
    }

    public static void load(Activity activity, String url, int error, ImageView imageView) {
        Glide.with(activity).load(url).error(error).into(imageView);
    }

    public static void load(Fragment fragment, String url, ImageView imageView) {
        Glide.with(fragment).load(url).into(imageView);
    }

    public static void load(Fragment fragment, Uri url, ImageView imageView) {
        Glide.with(fragment).load(url).into(imageView);
    }

    public static void load(Fragment fragment, int res, int error, ImageView imageView) {
        Glide.with(fragment).load(res).error(error).into(imageView);
    }

    public static void load(Fragment fragment, File file,ImageView imageView) {
        Glide.with(fragment).load(file).into(imageView);
    }

    public static void load(Fragment fragment, Uri uri, int error, ImageView imageView) {
        Glide.with(fragment).load(uri).error(error).into(imageView);
    }

    public static void clearCache(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Glide.get(context).clearDiskCache();
            }
        }).start();
        Glide.get(context).clearMemory();
    }

}
