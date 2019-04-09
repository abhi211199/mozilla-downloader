package com.example.mozilla_downloader;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class alarm extends BroadcastReceiver {
    ProgressBar progressBar;
    TextView status,title;
    alarm(ProgressBar progressBar,TextView status,TextView title) {
        this.progressBar=progressBar;
        this.status=status;
        this.title=title;
    }
    @Override
    public void onReceive(Context context, Intent intent)
    {
//        MediaPlayer mediaPlayer = MediaPlayer.create(context, Settings.System.DEFAULT_RINGTONE_URI);
//        mediaPlayer.start();
        new downloadtask(progressBar,status,title).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"https://www.hrw.org/sites/default/files/reports/wr2010_0.pdf");
    }


}