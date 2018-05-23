package com.rpsc.app.rpsc_jsoup;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.rpsc.app.rpsc_jsoup.R;

/**
 * Created by Mahendra on 11/28/2015.
 */
public class AnswerKeyActivity extends AppCompatActivity {



    ListView listview;
    ListViewAnswerKeyAdapter adapter;
    ArrayList<HashMap<String, String>> arraylist;
    static String SNO = "S.No.";
    static String DATE = "Date";
    static String EXAMNAME = "Exam Name";
    static String TITLE = "Title";
    static String FILEURL = "url";
    //static String FLAG = "flag";

    // Progress Dialog
    private ProgressDialog pDialog;

    // URL Address
    String url = "https://rpsc.rajasthan.gov.in/AnswerKeys.aspx";
    String main_url= "https://rpsc.rajasthan.gov.in/";

    Button retrybtn;

    // Search EditText
    EditText inputSearch;

    public static TextView count_tv;

    ConnectionDetector cd;

    AlertDialogManager alert = new AlertDialogManager();

    InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        android.support.v7.app.ActionBar menu = getSupportActionBar();
        menu.setDisplayShowHomeEnabled(true);
        menu.setLogo(R.mipmap.ic_launcher);
        menu.setDisplayUseLogoEnabled(true);

        cd = new ConnectionDetector(getApplicationContext());

        retrybtn = (Button)findViewById(R.id.btnretry);
        count_tv = (TextView) findViewById(R.id.textView_count);
        inputSearch = (EditText) findViewById(R.id.inputSearch);
        inputSearch.setVisibility(View.GONE);
        count_tv.setVisibility(View.GONE);

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

        AdView adView = (AdView) this.findViewById(R.id.adView);
        AdRequest adBannerRequest = new AdRequest.Builder()

                // Add a test device to show Test Ads
                //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                //.addTestDevice("014AEC120D7289943577FA761B3CBB61")
                .build();
        adView.loadAd(adBannerRequest);

        retrybtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!cd.isConnectingToInternet()) {
                    // Internet Connection is not present
                    alert.showAlertDialog(AnswerKeyActivity.this, "Internet Connection Error",
                            getString(R.string.inet_conn_msg), false);
                    return;
                }
                retrybtn.setVisibility(View.GONE);
                // starting new Async Task
                new JsoupListView().execute();
            }
        });

        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                AnswerKeyActivity.this.adapter.getFilter().filter(cs);
                Log.d("Text Watcher", "called");
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                String text = inputSearch.getText().toString().toLowerCase(Locale.getDefault());
                //adapter.filter(text);
            }
        });

        new JsoupListView().execute();
    }

    private void showInterstitial() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }

    public void updateCount(int count) {
        count_tv.setVisibility(View.VISIBLE);
        count_tv.setText(String.valueOf(count));
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


        switch(id){
            case R.id.menu_search:

                inputSearch.setVisibility(View.VISIBLE);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }


    // Title AsyncTask
    private class JsoupListView extends AsyncTask<Void, String, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            pDialog = new ProgressDialog(AnswerKeyActivity.this);
            //pDialog.setTitle("Retrieving Content");
            pDialog.setMessage(getString(R.string.Processing_Message));
            pDialog.setIndeterminate(false);
            pDialog.setMax(100);
            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            int count=0;
            // Create an array
            arraylist = new ArrayList<HashMap<String, String>>();

            try {
                // Connect to the Website URL
                Connection con = Jsoup.connect(url).timeout(3000);
                con.maxBodySize(0);
                //Document doc = Jsoup.connect(url).timeout(300).get();
                Document doc = con.get();

                // Identify Table
                for (Element table : doc.getElementsByClass("displayTable")) {

                    // Identify all the table row's(tr)
                    for (Element row : table.select("tr:gt(0)")) {
                        HashMap<String, String> map = new HashMap<String, String>();

                        // Identify all the table cell's(td)
                        Elements tds = row.select("td");

                        // Identify all img src's
      //                  Elements imgSrc = row.select("img[src]");
                        // Get only src from img src
      //                  String temp_imgSrcStr = imgSrc.attr("src");
      //                  String imgSrcStr = main_url.concat(temp_imgSrcStr);

                        //Identify all download file urls
                        Elements fileUrl = row.select("a[href]");
                        //Get only url from url link
                        String temp_fileurl = fileUrl.attr("href");
                        String fileUrlStr = main_url.concat(temp_fileurl);

                        // Retrive Jsoup Elements
                        // Get the first td
                        map.put("S.No.", tds.get(1).text());
                        // Get the second td
                        map.put("Date", tds.get(2).text());
                        // Get the third td
                        map.put("Exam Name", tds.get(3).text());
                        // Get the fourth td
                        map.put("Title", tds.get(4).text());
                        // Get the image src links
        //                map.put("flag", imgSrcStr);

                        map.put("url", fileUrlStr);
                        // Set all extracted Jsoup Elements into the array
                        arraylist.add(map);

                        publishProgress("" + (count += 1));
                    }
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }

        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }


        @Override
        protected void onPostExecute(Void result) {
            if (arraylist.size()==0){
                pDialog.dismiss();
                Toast.makeText(AnswerKeyActivity.this, "You are probably on slow internet connection", Toast.LENGTH_LONG).show();
                retrybtn.setVisibility(View.VISIBLE);
                return;
            }
            listview = (ListView) findViewById(R.id.listview);
            // Pass the results into ListViewAdapter.java
            adapter = new ListViewAnswerKeyAdapter(AnswerKeyActivity.this, arraylist);
            listview.setAdapter(adapter);
            pDialog.dismiss();
        }
    }

    @Override public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.animation_enter_l_r, R.anim.animation_leave_l_r);
    }
}
