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

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Mahendra on 8/16/2017.
 */

public class RssActivity extends AppCompatActivity {

    ListView listview;
    //ListViewAnswerKeyAdapter adapter;
    ListViewRssFeedsAdapter adapter;
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
    String url = "https://rpsc.rajasthan.gov.in/rssfeeds";
    String main_url= "https://rpsc.rajasthan.gov.in/";

    Button retrybtn;

    // Search EditText
    EditText inputSearch;

    public static TextView count_tv;

    ConnectionDetector cd;

    AlertDialogManager alert = new AlertDialogManager();

    InterstitialAd mInterstitialAd;

    ArrayList headlines, links, publishDate, description;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(false);

//        android.support.v7.app.ActionBar menu = getSupportActionBar();
        //      menu.setDisplayShowHomeEnabled(true);
        //    menu.setLogo(R.mipmap.ic_launcher);
        //  menu.setDisplayUseLogoEnabled(true);

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
                    alert.showAlertDialog(RssActivity.this, "Internet Connection Error",
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
                RssActivity.this.adapter.getFilter().filter(cs);
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

    public InputStream getInputStream(URL url) {
        try {
            return url.openConnection().getInputStream();
        } catch (IOException e) {
            return null;
        }
    }

    // Title AsyncTask
    private class JsoupListView extends AsyncTask<Void, String, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            pDialog = new ProgressDialog(RssActivity.this);
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

                URL url2 = new URL(url);

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();


                //xpp.setInput(getInputStream(url2), "UTF_8");
                xpp.setInput(url2.openConnection().getInputStream(), "UTF_8");

    /* We will parse the XML content looking for the "<title>" tag which appears inside the "<item>" tag.
             * However, we should take in consideration that the rss feed name also is enclosed in a "<title>" tag.
             * As we know, every feed begins with these lines: "<channel><title>Feed_Name</title>...."
             * so we should skip the "<title>" tag which is a child of "<channel>" tag,
             * and take in consideration only "<title>" tag which is a child of "<item>"
             *
             * In order to achieve this, we will make use of a boolean variable.
             */
                boolean insideItem = false;

                // Returns the type of current event: START_TAG, END_TAG, etc..
                int eventType = xpp.getEventType();

                headlines = new ArrayList();
                links = new ArrayList();
                publishDate = new ArrayList();
                description = new ArrayList();
                arraylist = new ArrayList<HashMap<String, String>>();
                //HashMap<String, String> map = new HashMap<String, String>();

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    String item1="";
                    String item2="";
                    String item3="";
                    String item4="";


                    String name=xpp.getName();
                    //Log.d("Attribute Count value :",String.valueOf(xpp.getAttributeCount()));
                    //Log.d("Depth value :",String.valueOf(xpp.getDepth()));
                    //Log.d("Text value :",String.valueOf(xpp.getText()));


                    //while (xpp.next()!= XmlPullParser.END_TAG) {
                      if (eventType == XmlPullParser.START_TAG) {   //parsing


                          String test_headline,test_link,test_publish_date,test_desc;
                          int rt;

                        if (xpp.getName().equalsIgnoreCase("item")) {
                            insideItem = true;
                        } else if (xpp.getName().equalsIgnoreCase("title")) {
                            if (insideItem)
                                //headlines.add(xpp.nextText()); //extract the headline
                                //Log.d("TEST VALUE OF XPP",xpp.nextText().toString());
                            //Log.d("TEST VALUE 2 OF XPP",xpp.getAttributeValue(1).toString());
                                //test_headline = xpp.nextText().toString();
                            //rt=78+90;
                            if (xpp.nextText().toString()!= "") {
                                map.put("title", xpp.nextText().toString());
                            }
                                //rt=rt+100;
                        } else if (xpp.getName().equalsIgnoreCase("link")) {
                            if (insideItem)
                                //links.add(xpp.nextText()); //extract the link of article
                            map.put("link", xpp.nextText().toString());
                        } else if (xpp.getName().equalsIgnoreCase("pubDate")){
                            //if (insideItem)
                              //  publishDate.add(xpp.nextText()); //extract the link of article
                            //map.put("publish_date", xpp.nextText().toString());
                        }else if (xpp.getName().equalsIgnoreCase("description")){
                            //if (insideItem)
                            //    description.add(xpp.nextText()); //extract the link of article
                            //map.put("desc", xpp.nextText().toString());
                        }

                    }else if(eventType==XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")){
                        insideItem=false;
                    }

                    if (eventType==2 && insideItem==true){
                        arraylist.add(map);
                        int rrt=0+89;
                    }

                    eventType = xpp.next(); //move to next element

                }


            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (XmlPullParserException e) {
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
                Toast.makeText(RssActivity.this, "You are probably on slow internet connection", Toast.LENGTH_LONG).show();
                retrybtn.setVisibility(View.VISIBLE);
                return;
            }
            listview = (ListView) findViewById(R.id.listview);
            // Pass the results into ListViewAdapter.java
            adapter = new ListViewRssFeedsAdapter(RssActivity.this, arraylist);
            listview.setAdapter(adapter);
            pDialog.dismiss();
        }
    }

    @Override public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.animation_enter_l_r, R.anim.animation_leave_l_r);
    }
}
