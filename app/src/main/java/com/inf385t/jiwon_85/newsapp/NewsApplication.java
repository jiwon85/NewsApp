package com.inf385t.jiwon_85.newsapp;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by jiwon_85 on 11/17/15.
 */
public class NewsApplication extends Application {
        @Override
        public void onCreate() {
            super.onCreate();
            Parse.initialize(this, getString(R.string.parseappid), getString(R.string.parseclientkey));
        }
}
