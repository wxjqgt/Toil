package com.weibo.toil.utils;

import com.weibo.toil.R;

public interface Constant {

    String KUAIDI = "http://www.kuaidi100.com/";

    String TX_APP_KEY = "1ae28fc9dd5afadc696ad94cd59426d8";

    String DB__IS_READ_NAME = "IsRead";
    String WEIXIN = "weixin";
    String GUOKR = "guokr";
    String ZHIHU = "zhihu";
    String VIDEO = "video";
    String IT = "it";

    enum Channel {
        WEIXIN( R.string.fragment_wexin_title, R.drawable.icon_weixin),
        GUOKR(R.string.fragment_guokr_title, R.drawable.icon_guokr),
        ZHIHU(R.string.fragment_zhihu_title, R.drawable.icon_zhihu),
        VIDEO(R.string.fragment_video_title, R.drawable.icon_video),
        IT( R.string.fragment_it_title, R.drawable.icon_it),
        OTHER( R.string.fragment_other_title, R.drawable.search_for_black_24dp),
        MUSIC( R.string.fragment_music_title, R.drawable.musci_icon);

        private int title;
        private int icon;

        Channel(int title, int icon) {
            this.title = title;
            this.icon = icon;
        }

        public int getTitle() {
            return title;
        }

        public void setTitle(int title) {
            this.title = title;
        }

        public int getIcon() {
            return icon;
        }

        public void setIcon(int icon) {
            this.icon = icon;
        }
    }
}
