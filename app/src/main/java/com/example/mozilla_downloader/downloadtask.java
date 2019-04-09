package com.example.mozilla_downloader;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;

import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static android.content.Context.BATTERY_SERVICE;
import static android.support.v4.content.ContextCompat.getSystemService;

public class downloadtask extends AsyncTask<String,Integer,String>
{
    private Context context;
    ProgressBar progressBar;
    TextView status,title;
    OutputStream outputStream;
    InputStream inputStream;
    Integer flag = 0;
    long total = 0,length = 0;

    downloadtask(ProgressBar progressBar,TextView status,TextView title) {
        this.progressBar=progressBar;
        this.status=status;
        this.title=title;
    }
    public int batlevel()
    {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float batteryPct = level / (float) scale;
        Toast.makeText(context,"abhi",Toast.LENGTH_SHORT).show();
        return Math.round(batteryPct);
    }

    @Override
    protected void onPreExecute () {
        progressBar.setProgress(0);
        status.setText("DOWNLOADING...");
        title.setText("download1.pdf");
    }

    @Override
    protected void onProgressUpdate (Integer...values){
        progressBar.setProgress(values[0]);
    }
    @Override
    protected void onPostExecute (String result){
        status.setText(result);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected String doInBackground (String...params){
        String path = params[0];
        int length = 0;
        try {
            URL url = new URL(path);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();//connection opened
            File newF = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "mozilladownload");
            if (!newF.exists()) {
                newF.mkdir();
            }
//            title.setText("hello");
            File inp = new File(newF, "download1.pdf");
            if (inp.exists())//pre-existing file,checked for resuming download
            {
                long size = inp.length();
                urlConnection.setRequestProperty("Range", "bytes=" + size + "-");
                urlConnection.connect();
                length = urlConnection.getContentLength();
                progressBar.setProgress((int) size * 100 / length);
                total = size;
            } else//no pre-existing download
            {
//                title.setText("hello");
                urlConnection.connect();
                length = urlConnection.getContentLength();
            }
            inputStream = new BufferedInputStream(url.openStream(), 8192);
            byte[] data = new byte[1024];
            //total = 0;
            int count = 0;
            outputStream = new FileOutputStream(inp);
            while ((count = inputStream.read(data)) != -1) {
                int BatLevel = 30;
//                BatteryManager batteryManager
//                =(BatteryManager)getSystemService(BATTERY_SERVICE);
//                int batLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
                if (BatLevel < 20) //if battery level < 20, download is not possible
                {
//                        Toast.makeText(MainActivity.this, "There current battery level is " + Integer.toString(batLevel) + ". Please charge your phone and retry!", Toast.LENGTH_LONG).show();
                    return "Download Cancelled due to low battery";
                }
                if (isCancelled())//cancel trigger handling for download cancellation
                {
                    status.setText("CANCELLED...");
                    if (flag == 2)//deletion of incomplete download in case of cancelled download(flag = 2),flag=1->paused
                        inp.delete();
                    inputStream.close();
                    outputStream.close();
                    urlConnection.disconnect();
                    Toast.makeText(context, "DOWNLOAD CANCELLED", Toast.LENGTH_LONG).show();
//                        downloadTask1.cancel(true);
                    if (flag == 2)
                        return "Download Cancelled";
                    else if (flag == 1) return "Download Paused";
                }
                total += count;
                outputStream.write(data, 0, count);
                int progress = (int) total * 100 / length;
                publishProgress(progress);
            }
            inputStream.close();
            outputStream.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Download  Complete";

    }


}

