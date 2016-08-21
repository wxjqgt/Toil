package com.weibo.toil.utils;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

/**
 * Created by Shannon on 2016/3/21.
 */
public class AnimationUtils {


    public static ScaleAnimation getSa(){


        ScaleAnimation sa = new ScaleAnimation(1.0f,1.5f , 1.0f , 1.5f);
        sa.setDuration(10000);
        sa.setRepeatMode(Animation.REVERSE);
        sa.setFillAfter(true);
        return sa;


    }

    public static AlphaAnimation getAa(){


        AlphaAnimation aa = new AlphaAnimation(0.0f , 1.0f);
        aa.setRepeatMode(Animation.REVERSE);
        aa.setDuration(1000);
        aa.setFillAfter(true);
        return aa;

    }

}
