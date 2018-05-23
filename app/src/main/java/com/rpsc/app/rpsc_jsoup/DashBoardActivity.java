package com.rpsc.app.rpsc_jsoup;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import com.google.android.gms.ads.InterstitialAd;
import com.rpsc.app.rpsc_jsoup.R;


/**
 * Created by Mahendra on 11/28/2015.
 */
public class DashBoardActivity extends AppCompatActivity {

    ConnectionDetector cd;

    AlertDialogManager alert = new AlertDialogManager();

    InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_layout);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        android.support.v7.app.ActionBar menu = getSupportActionBar();
        menu.setDisplayShowHomeEnabled(true);
        menu.setLogo(R.mipmap.ic_launcher);
        menu.setDisplayUseLogoEnabled(true);

        cd = new ConnectionDetector(getApplicationContext());

        /**
         * Creating all buttons instances
         * */
        // Dashboard Press Notes button
        Button btn_pressnotes = (Button) findViewById(R.id.btn_press_notes);

        // Dashboard recruitment button
        Button btn_recruitment = (Button) findViewById(R.id.btn_recruitment);

        // Dashboard results button
        Button btn_results = (Button) findViewById(R.id.btn_results);

        // Dashboard old papers button
        Button btn_oldpapers = (Button) findViewById(R.id.btn_oldpapers);

        // Dashboard answer keys button
        Button btn_anskeys = (Button) findViewById(R.id.btn_anskeys);

        //Web View key button
        Button btn_webview = (Button) findViewById(R.id.btn_webview);

        //Rss Feeds key button
        //Button btn_rssFeeds = (Button) findViewById(R.id.btn_rssFeeds);


        /**
         * Handling all button click events
         * */

        // Listening to Press Notes button click
        btn_pressnotes.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (!cd.isConnectingToInternet()) {
                    // Internet Connection is not present
                    alert.showAlertDialog(DashBoardActivity.this, "Internet Connection Error",
                            getString(R.string.inet_conn_msg), false);
                    return ;
                }
                // Launching Press Notes Screen
                Intent i = new Intent(getApplicationContext(), PressNoteActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.animation_enter_r_l, R.anim.animation_leave_r_l);
                //                                   100 -> 0                   0 -> -100

            }
        });

        // Listening Recruitment button click
        btn_recruitment.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!cd.isConnectingToInternet()) {
                    // Internet Connection is not present
                    alert.showAlertDialog(DashBoardActivity.this, "Internet Connection Error",
                            getString(R.string.inet_conn_msg), false);
                    return ;
                }
                // Launching Recruitment Screen
                Intent i = new Intent(getApplicationContext(), RecruitmentActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.animation_enter_r_l, R.anim.animation_leave_r_l);
            }
        });


        // Listening Results button click
        btn_results.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!cd.isConnectingToInternet()) {
                    // Internet Connection is not present
                    alert.showAlertDialog(DashBoardActivity.this, "Internet Connection Error",
                            getString(R.string.inet_conn_msg), false);
                    return ;
                }
                // Launching Results Screen
                Intent i = new Intent(getApplicationContext(), ResultActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.animation_enter_r_l, R.anim.animation_leave_r_l);
            }
        });

        // Listening to Old Papers button click
        btn_oldpapers.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!cd.isConnectingToInternet()) {
                    // Internet Connection is not present
                    alert.showAlertDialog(DashBoardActivity.this, "Internet Connection Error",
                            getString(R.string.inet_conn_msg), false);
                    return ;
                }
                // Launching Old Papers Screen
                Intent i = new Intent(getApplicationContext(), OldPaperActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.animation_enter_r_l, R.anim.animation_leave_r_l);
            }
        });

        // Listening to Answer Keys button click
        btn_anskeys.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!cd.isConnectingToInternet()) {
                    // Internet Connection is not present
                    alert.showAlertDialog(DashBoardActivity.this, "Internet Connection Error",
                            getString(R.string.inet_conn_msg), false);
                    return ;
                }
                // Launching Answer Keys Screen
                Intent i = new Intent(getApplicationContext(), AnswerKeyActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.animation_enter_r_l, R.anim.animation_leave_r_l);
            }
        });

        // Listening to Web View button click
        btn_webview.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!cd.isConnectingToInternet()) {
                    // Internet Connection is not present
                    alert.showAlertDialog(DashBoardActivity.this, "Internet Connection Error",
                            getString(R.string.inet_conn_msg), false);
                    return ;
                }
                // Launching Answer Keys Screen
                Intent i = new Intent(getApplicationContext(), WebViewActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.animation_enter_r_l, R.anim.animation_leave_r_l);
            }
        });

        /*
        // Listening to Web View button click
        btn_rssFeeds.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!cd.isConnectingToInternet()) {
                    // Internet Connection is not present
                    alert.showAlertDialog(DashBoardActivity.this, "Internet Connection Error",
                            getString(R.string.inet_conn_msg), false);
                    return ;
                }
                // Launching Answer Keys Screen
                Intent i = new Intent(getApplicationContext(), RssActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.animation_enter_r_l, R.anim.animation_leave_r_l);
            }
        });
        */

        // setting interstitial ad
        mInterstitialAd = new InterstitialAd(this);
        // set the ad unit ID
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));

        AdRequest adRequest = new AdRequest.Builder().build();
        // Load ads into Interstitial Ads
        mInterstitialAd.loadAd(adRequest);

        mInterstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded() {
                showInterstitial();
            }
        });

        //Locate the Banner Ad in activity_main.xml
        AdView adView = (AdView) this.findViewById(R.id.adView);
        // Load ads into Banner Ads



        // Request for Ads
        AdRequest adBannerRequest = new AdRequest.Builder()

                // Add a test device to show Test Ads
                //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                //.addTestDevice("014AEC120D7289943577FA761B3CBB61")
                .build();

        // Load ads into Banner Ads
        adView.loadAd(adBannerRequest);

    }

    private void showInterstitial() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id){
            case R.id.menu_share:

                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType("text/plain");
                        i.putExtra(Intent.EXTRA_SUBJECT, "RPSC. No more website hassle now.");
                        String sAux = "\n\nRPSC APP  \n\nNo more website hassle.\n\n";
                        sAux = sAux + "https://play.google.com/store/apps/details?id=com.rpsc.app.rpsc_jsoup \n\n";
                        i.putExtra(Intent.EXTRA_TEXT, sAux);
                        startActivity(Intent.createChooser(i, "Choose One"));

            return true;

            case R.id.contact_us:

                String[] TO = {"feedback.rpsc@rajasthan.gov.in"};
                //String[] CC = {""};
                Intent emailIntent = new Intent(Intent.ACTION_SEND);

                emailIntent.setData(Uri.parse("mailto:"));
                emailIntent.setType("message/rfc822");
                //emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
                //emailIntent.putExtra(Intent.EXTRA_CC, CC);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "");

                try {
                    startActivity(Intent.createChooser(emailIntent, "Choose an Email client..."));
                }
                catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(DashBoardActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
                }

                return true;



            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Exit")
                .setMessage("Are you sure?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                }).setNegativeButton("No", null).show();
    }
}
