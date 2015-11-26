package com.inf385t.jiwon_85.newsapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.webkit.WebView;

public class BrowserActivity extends ActionBarActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);
        Intent i = getIntent();
        String url = i.getStringExtra("url");
        webView = (WebView) findViewById(R.id.webView);
        openBrowser(url);
    }

    /** Open a browser on the URL specified in the text box */
    private void openBrowser(String url) {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url.trim());
    }
}
