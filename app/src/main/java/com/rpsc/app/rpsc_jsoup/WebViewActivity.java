package com.rpsc.app.rpsc_jsoup;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

/**
 * Created by Mahendra on 1/10/2016.
 */
public class WebViewActivity extends AppCompatActivity {

    ConnectionDetector cd;

    AlertDialogManager alert = new AlertDialogManager();

    private WebView wv1;

    ProgressBar progressBar;

    String fileurl;

//    private ProgressBar progress;

    InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        cd = new ConnectionDetector(getApplicationContext());

        // setting interstitial ad
        mInterstitialAd = new InterstitialAd(this);
        // set the ad unit ID
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));

        AdRequest adRequest = new AdRequest.Builder().build();
        // Load ads into Interstitial Ads
        mInterstitialAd.loadAd(adRequest);

        //temporary blocked this part due to google warning...show interstitial ads//
        /*
        mInterstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded() {
                showInterstitial();
            }
        });
        */
        //temporary blocked this part due to google warning...show interstitial ads//

        // Dashboard answer keys button
        Button btn_back = (Button) findViewById(R.id.back_button);

        wv1=(WebView)findViewById(R.id.webview);
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        wv1.setWebViewClient(new MyBrowser());

        wv1.getSettings().setLoadsImagesAutomatically(true);
        wv1.getSettings().setJavaScriptEnabled(true);
        wv1.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        Intent i = getIntent();
        // Get the result of rank
        fileurl = i.getStringExtra("fileurl");

        if (fileurl==null) {
            wv1.loadUrl("https://rpsc.rajasthan.gov.in/");
            //WebViewActivity.this.progress.setProgress(0);
        }
        else {
            wv1.loadUrl(fileurl);
            Log.d("Results URL",fileurl);
        }


        // Listening Recruitment button click
        btn_back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                
                if (!cd.isConnectingToInternet()) {
                    // Internet Connection is not present
                    alert.showAlertDialog(WebViewActivity.this, "Internet Connection Error",
                            getString(R.string.inet_conn_msg), false);
                    return;
                }


                if (fileurl==null){
                    // Launching DashBoard Screen
                    Intent i = new Intent(getApplicationContext(), DashBoardActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.animation_enter_l_r, R.anim.animation_leave_l_r);
                    Log.d("FileURL","NUll");
                    finish();
                }
                else {
                    // Going back to previous activity
                    finish();
                }



            }
        });

    }

    private void showInterstitial() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }


    private class MyBrowser extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            progressBar.setVisibility(View.VISIBLE);
            view.loadUrl(url);
            return true;
        }


        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);

            //progress.setVisibility(View.GONE);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_others, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();



        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (wv1 != null && (keyCode == KeyEvent.KEYCODE_BACK)
                && wv1.canGoBack() )
        {

            wv1.goBack();
        }
        else{
            super.onBackPressed();
            overridePendingTransition(R.anim.animation_enter_l_r, R.anim.animation_leave_l_r);
        }

        return true;
    }



}
