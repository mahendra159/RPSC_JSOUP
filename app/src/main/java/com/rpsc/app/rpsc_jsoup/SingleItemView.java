package com.rpsc.app.rpsc_jsoup;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
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
 * Created by Mahendra on 11/27/2015.
 */
public class SingleItemView extends ActionBarActivity {
    // Declare Variables
    String sno;
    String date;
    String examname;
    String papernotetitle;
    String flag;
    String fileurl;
    String position;
//    ImageLoader imageLoader = new ImageLoader(this);

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
        date = i.getStringExtra("date");
        // Get the result of population
        examname = i.getStringExtra("examname");
        // Get the result of paper note title
        papernotetitle = i.getStringExtra("papernotetitle");

        fileurl = i.getStringExtra("fileurl");
        // Get the result of flag
//        flag = i.getStringExtra("flag");

        // Locate the TextViews in singleitemview.xml
        TextView txtsno = (TextView) findViewById(R.id.sno);
        TextView txtdate = (TextView) findViewById(R.id.date);
        TextView txtexamname = (TextView) findViewById(R.id.examname);
        TextView txtpapernotetitle = (TextView) findViewById(R.id.papernotetitle);

        // Locate the ImageView in singleitemview.xml
  //      ImageView imgflag = (ImageView) findViewById(R.id.flag);

        // Set results to the TextViews
        txtsno.setText(sno);
        txtdate.setText(date);
        txtexamname.setText(examname);
        txtpapernotetitle.setText(papernotetitle);

        // Capture position and set results to the ImageView
        // Passes flag images URL into ImageLoader.class
   //       imageLoader.DisplayImage(flag, imgflag);


        btn_download.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!cd.isConnectingToInternet()) {
                    // Internet Connection is not present
                    alert.showAlertDialog(SingleItemView.this, "Internet Connection Error",
                            getString(R.string.inet_conn_msg), false);
                    return ;
                }
                // starting new Async Task
                new DownloadFileFromURL().execute(fileurl);
            }
        });


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
                conection.setRequestProperty ("Authorization", userpass);
                conection.connect();


                /* code without username and password & with buffered stream
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();
                // this will be useful so that you can show a tipical 0-100% progress bar
                int lenghtOfFile = conection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                // Output stream
                OutputStream output = new FileOutputStream("/sdcard/downloadedfile.pdf");

                byte data[] = new byte[1024];

                long total = 0;
                */

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
                File direct = new File(Environment.getExternalStorageDirectory()+"/RPSC/Press_Notes");

                if(!direct.exists()) {
                    if(direct.mkdirs()); //directory is created;
                }

                // Output stream
                OutputStream output = new FileOutputStream("/sdcard/RPSC/Press_Notes/"+fname);

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

            File file = new File(Environment.getExternalStorageDirectory().toString() + "/RPSC/Press_Notes/"+file_name);

            if (file.exists()) {
                //Uri path = Uri.fromFile(file);
                Uri path = GenericFileProvider.getUriForFile(SingleItemView.this, getApplicationContext().getPackageName() + ".provider", file);
                //com.rpsc.app.rpsc_jsoup.com.rpsc.app.rpsc_jsoup.provider
                /*Uri photoURI = FileProvider.getUriForFile(MainActivity.this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        createImageFile());*/
                final Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(path, "application/pdf");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                if (haveStoragePermission()){

                try {

                    new AlertDialog.Builder(SingleItemView.this).setIcon(android.R.drawable.ic_dialog_info)
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
                    //startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    //Toast.makeText(SingleItemView.this, "No Application Available to View PDF",
                    //        Toast.LENGTH_SHORT).show();
                    new AlertDialog.Builder(SingleItemView.this).setIcon(android.R.drawable.ic_dialog_info)
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


        public  boolean haveStoragePermission() {
            if (Build.VERSION.SDK_INT >= 23) {
                if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    Log.e("Permission error","You have permission");
                    return true;
                } else {

                    Log.e("Permission error","You have asked for permission");
                    ActivityCompat.requestPermissions(SingleItemView.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    return false;
                }
            }
            else { //you dont need to worry about these stuff below api level 23
                Log.e("Permission error","You already have the permission");
                return true;
            }
        }

    }



}
