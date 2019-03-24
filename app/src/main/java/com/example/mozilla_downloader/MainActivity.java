package com.example.mozilla_downloader;

import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
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

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity {

    String url2 = "https://www.nord.com/cms/media/documents/bw/G1011_60Hz_US_1103.pdf";
    String url1 = "https://www.hrw.org/sites/default/files/reports/wr2010_0.pdf";
    Button p1;
    TextView title1,status1;
    DownloadTask downloadTask1;
    ProgressBar progressBar1;
    OutputStream outputStream;
    InputStream inputStream;
    Integer flag = 0;
    long total = 0,length = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar1 = (ProgressBar)findViewById(R.id.pbar1);
        title1 = (TextView)findViewById(R.id.title1);
        status1 = (TextView)findViewById(R.id.status1);
        p1 = (Button)findViewById(R.id.start1);
        downloadTask1 = new DownloadTask();
        downloadTask1.checkState();
    }
    class DownloadTask extends AsyncTask<String,Integer,String>
    {
        @Override
        protected void onPreExecute(){
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
        @Override
        protected String doInBackground(String... params) {
            String path = params[0];
            int length = 0;
            try {
                URL url = new URL(path);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                File newF = new File("sdcard/mozilladownload");
                if (!newF.exists()) {
                    newF.mkdir();
                }
                File inp = new File(newF, "download1.pdf");
                if(inp.exists())
                {
                    long size = inp.length();
                    urlConnection.setRequestProperty("Range", "bytes="+size+"-");
                    urlConnection.connect();
                    length = urlConnection.getContentLength();
                    progressBar1.setProgress((int)size*100/length);
                    total = size;
                }
                else
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
                    if(isCancelled())
                    {   status1.setText("CANCELLED...");
                        if(flag==2)
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

        public void checkState() {
            File newF = new File("sdcard/mozilladownload");
            File inp = new File(newF, "download1.pdf");
            if(inp.exists())
            {
                p1.setText("RESUME");
                status1.setText("DOWNLOAD PAUSED...");
                progressBar1.setVisibility(INVISIBLE);
            }
        }

        public void canceldelete(){
            File newF = new File("sdcard/mozilladownload");
            File inp = new File(newF, "download1.pdf");
            if(inp.delete())
            status1.setText("hrllo");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void start1(View view)
    {
        if(flag==0) {
            BatteryManager bm = (BatteryManager) getSystemService(BATTERY_SERVICE);
            int batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
            if (batLevel < 20) {
                Toast.makeText(MainActivity.this, "There current battery level is " + Integer.toString(batLevel) + ". Please charge your phone and retry!", Toast.LENGTH_LONG).show();
                return;
            }
            p1.setText("PAUSE");
            flag=1;
            progressBar1.setVisibility(VISIBLE);
            downloadTask1.execute(url1);
        }
        else
            if(flag==1)
            {
                downloadTask1.cancel(true);
            }
    }
    public void cancel1(View view)
    {
        Toast.makeText(MainActivity.this,"DOWNLOAD CANCELLED",Toast.LENGTH_LONG).show();
        flag=2;
        downloadTask1.cancel(true);

    }
    

}
