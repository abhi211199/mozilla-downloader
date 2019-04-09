package com.example.mozilla_downloader;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.concurrent.Executor;

import javax.net.ssl.HttpsURLConnection;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;


public class MainActivity extends AppCompatActivity {
    String url1 = "https://www.hrw.org/sites/default/files/reports/wr2010_0.pdf";//download url
    String url2 = "http://kmmc.in/wp-content/uploads/2014/01/lesson2.pdf";//download url
    Button p1;
    TextView title1,status1;
    DownloadTask downloadTask1;
    ProgressBar progressBar1;
    OutputStream outputStream;
    InputStream inputStream;
    Integer flag = 0;
    long total = 0,length = 0;
    TimePicker timePicker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        timePicker = findViewById(R.id.ti1me);
        progressBar1 = (ProgressBar)findViewById(R.id.pbar1);
        title1 = (TextView)findViewById(R.id.title1);
        status1 = (TextView)findViewById(R.id.status1);
        p1 = (Button)findViewById(R.id.start1);
        downloadTask1 = new DownloadTask();
        downloadTask1.checkState();
    }

    class DownloadTask extends AsyncTask<String,Integer,String>//asynctask which handles HttpURLConnection
    {
        @Override
        protected void onPreExecute(){
            progressBar1.setProgress(0);
            status1.setText("DOWNLOADING...");
            title1.setText("download1.pdf");
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar1.setProgress(values[0]);
        }
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(MainActivity.this,"DownloadTask1" + result,Toast.LENGTH_LONG).show();
        }
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected String doInBackground(String... params) {
            String path = params[0];
            int length = 0;
            try {
                URL url = new URL(path);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();//connection opened
                File newF = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"mozilladownload");
                if (!newF.exists()) {
                    newF.mkdir();
                }
                File inp = new File(newF, "download1.pdf");
                if(inp.exists())//pre-existing file,checked for resuming download
                {
                    long size = inp.length();
                    urlConnection.setRequestProperty("Range", "bytes="+size+"-");
                    urlConnection.connect();
                    length = urlConnection.getContentLength();
                    progressBar1.setProgress((int)size*100/length);
                    total = size;
                }
                else//no pre-existing download
                {
                    urlConnection.connect();
                    length = urlConnection.getContentLength();
                }
                inputStream = new BufferedInputStream(url.openStream(), 8192);
                byte[] data = new byte[1024];
                //total = 0;
                int count = 0;
                 outputStream = new FileOutputStream(inp);
                while ((count = inputStream.read(data)) != -1)
                {
                    BatteryManager bm = (BatteryManager)getSystemService(BATTERY_SERVICE);
                    int batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
                    if (batLevel < 20) //if battery level < 20, download is not possible
                    {
//                        Toast.makeText(MainActivity.this, "There current battery level is " + Integer.toString(batLevel) + ". Please charge your phone and retry!", Toast.LENGTH_LONG).show();
                        return "Download Cancelled due to low battery";
                    }
                    if(isCancelled())//cancel trigger handling for download cancellation
                    {   status1.setText("CANCELLED...");
                        if(flag==2)//deletion of incomplete download in case of cancelled download(flag = 2),flag=1->paused
                        inp.delete();
                        inputStream.close();
                        outputStream.close();
                        urlConnection.disconnect();
                        Toast.makeText(MainActivity.this,"DOWNLOAD CANCELLED",Toast.LENGTH_LONG).show();
//                        downloadTask1.cancel(true);
                        if(flag==2)
                        return "Download Cancelled";
                        else if(flag==1) return "Download Paused";
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

        public void checkState()
        {
            File newF = new File("sdcard/mozilladownload");
            File inp = new File(newF, "download1.pdf");
            if(inp.exists())
            {
                p1.setText("RESUME");
                status1.setText("DOWNLOAD PAUSED...");
                progressBar1.setVisibility(INVISIBLE);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)//to check battery percentage
    public void start1(View view)
    {
        if(flag==0) {
            BatteryManager bm = (BatteryManager) getSystemService(BATTERY_SERVICE);
            int batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
            if (batLevel < 20) //if battery level < 20, download is not possible
            {
                Toast.makeText(MainActivity.this, "There current battery level is " + Integer.toString(batLevel) + ". Please charge your phone and retry!", Toast.LENGTH_LONG).show();
                return;
            }
            Intent intent = new Intent(this,MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
            Intent broad=new Intent(this,alarm.class);
            PendingIntent act=PendingIntent.getBroadcast(this, 0, broad,PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .addAction(R.mipmap.ic_launcher,"PAUSE/RESUME",act)
                    .addAction(R.mipmap.ic_launcher,"CANCEL",act);
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            builder.setContentTitle("Download Started");
            mNotificationManager.notify(001, builder.build());
            p1.setText("PAUSE");
            flag=1;
            progressBar1.setVisibility(VISIBLE);
            downloadTask1.execute(url1);
        }
        else
            if(flag==1)
            {
                Intent intent = new Intent(this,MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                builder.setContentTitle("Download Paused");
                mNotificationManager.notify(001, builder.build());
                downloadTask1.cancel(true);
            }
    }
    public void cancel1(View view)
    {
        Intent intent = new Intent(this,MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        builder.setContentTitle("Download Cancelled");
        Toast.makeText(MainActivity.this,"DOWNLOAD CANCELLED",Toast.LENGTH_LONG).show();
        flag=2;
        downloadTask1.cancel(true);

    }

    public void al(View view)
    {
        Calendar calendar=Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,timePicker.getHour());
        calendar.set(Calendar.MINUTE,timePicker.getMinute());
        calendar.set(Calendar.SECOND,0);
        Calendar calendar1=Calendar.getInstance();
        Long current = calendar1.getTimeInMillis();
        Long sett = calendar.getTimeInMillis();
        al1(((sett-current)));//calendar.getTimeInMillis() will time in form of epoch milliseconds.
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void al1(long timein)
    {
        Toast.makeText(MainActivity.this, Long.toString(timein),Toast.LENGTH_SHORT).show();
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(MainActivity.this, alarm.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0  ,intent,0);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+timein,pendingIntent);
    }

    public void stat12(View view)
    {
//        new DownloadTask().execute(url1);
//        new DownloadTask().execute(url2);
        new DownloadTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,url1);
    }

}


 