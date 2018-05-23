package com.rpsc.app.rpsc_jsoup;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.rpsc.app.rpsc_jsoup.R;

/**
 * Created by Mahendra on 11/28/2015.
 */
public class ResultItemView extends AppCompatActivity {

    // Declare Variables
    String sno;
    String resdate;
    String examname;
    String resulttitle;
    String flag;
    String fileurl;
    String position;
    //ImageLoader imageLoader = new ImageLoader(this);

    // button to show progress dialog
    Button btn_download;

    // Progress Dialog
    private ProgressDialog pDialog;
    ImageView my_image;
    // Progress dialog type (0 - for Horizontal progress bar)
    public static final int progress_bar_type = 0;

    ConnectionDetector cd;

    AlertDialogManager alert = new AlertDialogManager();

    InterstitialAd mInterstitialAd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from singleitemview.xml
        setContentView(R.layout.singleitemview);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        android.support.v7.app.ActionBar menu = getSupportActionBar();
        menu.setDisplayShowHomeEnabled(true);
        menu.setLogo(R.mipmap.ic_launcher);
        menu.setDisplayUseLogoEnabled(true);

        cd = new ConnectionDetector(getApplicationContext());

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

        AdView adView = (AdView) this.findViewById(R.id.adView);
        AdRequest adBannerRequest = new AdRequest.Builder()

                // Add a test device to show Test Ads
                //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                //.addTestDevice("014AEC120D7289943577FA761B3CBB61")
                .build();
        adView.loadAd(adBannerRequest);

        // show progress bar button
        btn_download = (Button) findViewById(R.id.btndwnload);

        Intent i = getIntent();
        // Get the result of rank
        sno = i.getStringExtra("sno");
        // Get the result of country
        resdate = i.getStringExtra("resdate");
        // Get the result of population
        examname = i.getStringExtra("examname");
        // Get the result of paper note title
        resulttitle = i.getStringExtra("resulttitle");

        fileurl = i.getStringExtra("fileurl");

        String fileExtenstionPre = MimeTypeMap.getFileExtensionFromUrl(fileurl);

        // Get the result of flag
      //  flag = i.getStringExtra("flag");

        // Locate the TextViews in singleitemview.xml
        TextView txtsno = (TextView) findViewById(R.id.sno);
        TextView txtresdate = (TextView) findViewById(R.id.date);
        TextView txtexamname = (TextView) findViewById(R.id.examname);
        TextView txtresulttitle = (TextView) findViewById(R.id.papernotetitle);

        // setting labels acc to this activity
        TextView txtreslabel = (TextView) findViewById(R.id.papernotelabel);
        txtreslabel.setText("Title : ");

        // Locate the ImageView in singleitemview.xml
        //ImageView imgflag = (ImageView) findViewById(R.id.flag);

        // Set results to the TextViews
        txtsno.setText(sno);
        txtresdate.setText(resdate);
        txtexamname.setText(examname);
        txtresulttitle.setText(resulttitle);


        // Capture position and set results to the ImageView
        // Passes flag images URL into ImageLoader.class
       // imageLoader.DisplayImage(flag, imgflag);

        if (fileExtenstionPre.equalsIgnoreCase("pdf")){
        btn_download.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!cd.isConnectingToInternet()) {
                    // Internet Connection is not present
                    alert.showAlertDialog(ResultItemView.this, "Internet Connection Error",
                            getString(R.string.inet_conn_msg), false);
                    return ;
                }
                // starting new Async Task
                new DownloadFileFromURL().execute(fileurl);
            }
        });
        }
        else {
            btn_download.setText("View Result");
            btn_download.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (!cd.isConnectingToInternet()) {
                        // Internet Connection is not present
                        alert.showAlertDialog(ResultItemView.this, "Internet Connection Error",
                                getString(R.string.inet_conn_msg), false);
                        return ;
                    }
                    // starting new Async Task
                    /*
                    new AlertDialog.Builder(ResultItemView.this).setIcon(android.R.drawable.ic_dialog_info)
                            .setMessage("This will open another window. \n\nOpen ?")
                            .setCancelable(false)
                            .setTitle("View Result")
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.cancel();
                                }
                            })
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    //startActivity(intent);
                                    Intent intent = new Intent(ResultItemView.this, WebViewActivity.class);
                                    // Pass all data sno
                                    intent.putExtra("fileurl",fileurl);
                                    // Pass all data flag
//                intent.putExtra("flag", resultp.get(ResultActivity.FLAG));
                                    // Start SingleItemView Class
                                    startActivity(intent);
                                }
                            }).show();
                    */
                    //startActivity(intent);
                    Intent intent = new Intent(ResultItemView.this, WebViewActivity.class);
                    // Pass all data sno
                    intent.putExtra("fileurl",fileurl);
                    // Pass all data flag
//                intent.putExtra("flag", resultp.get(ResultActivity.FLAG));
                    // Start SingleItemView Class
                    startActivity(intent);

                }
            });
        }


    }

    private void showInterstitial() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }



    /**
     * Showing Dialog
     * */
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case progress_bar_type: // we set this to 0
                pDialog = new ProgressDialog(this);
                pDialog.setMessage("Downloading file. Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(false);
                pDialog.show();
                return pDialog;
            default:
                return null;
        }
    }


    /**
     * Background Async Task to download file
     * */
    class DownloadFileFromURL extends AsyncTask<String, String, String> {


        String file_name;

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(progress_bar_type);
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                String userpass = "anonymous" + ":" + "anonymous";
                conection.setRequestProperty("Authorization", userpass);
                conection.connect();
                // this will be useful so that you can show a tipical 0-100% progress bar
                //int lenghtOfFile = conection.getContentLength();

                // download the file
                //InputStream input = new BufferedInputStream(url.openStream(), 8192);
                InputStream input = conection.getInputStream();
                int lenghtOfFile = conection.getContentLength();

                // naming file
                String main_url = url.toString();
                String fileExtenstion = MimeTypeMap.getFileExtensionFromUrl(main_url);
                String fname = URLUtil.guessFileName(main_url, null, fileExtenstion);
                file_name=fname;

                //creating directory if not exists
                File direct = new File(Environment.getExternalStorageDirectory()+"/RPSC/Results");

                if(!direct.exists()) {
                    if(direct.mkdirs()); //directory is created;
                }

                // Output stream
                OutputStream output = new FileOutputStream("/sdcard/RPSC/Results/"+fname);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress(""+(int)((total*100)/lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task
         * Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            dismissDialog(progress_bar_type);

            File file = new File(Environment.getExternalStorageDirectory().toString() + "/RPSC/Results/"+file_name);

            if (file.exists()) {
                Uri path = Uri.fromFile(file);
                final Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(path, "application/pdf");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                try {
                    new AlertDialog.Builder(ResultItemView.this).setIcon(android.R.drawable.ic_dialog_info)
                            .setMessage("File downloaded & saved in RPSC folder successfully. \n\nOpen file ?")
                            .setCancelable(false)
                            .setTitle("File Download")
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.cancel();
                                }
                            })
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    startActivity(intent);
                                }
                            }).show();
                }
                catch (ActivityNotFoundException e) {
                    //Toast.makeText(ResultItemView.this, "No Application Available to View PDF",
                    //        Toast.LENGTH_SHORT).show();
                    new AlertDialog.Builder(ResultItemView.this).setIcon(android.R.drawable.ic_dialog_info)
                            .setMessage("No Application available to view pdf file.\n\nFile downloaded & saved in RPSC folder successfully.")
                            .setCancelable(false)
                            .setTitle("File Download")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.cancel();
                                }
                            }).show();
                }
            }

        }

    }

}
