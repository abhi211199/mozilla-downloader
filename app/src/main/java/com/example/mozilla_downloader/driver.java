package com.example.mozilla_downloader;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class driver extends AppCompatActivity{

    private int mHour,mMinute;
    Button start1,start2,cancel1,cancel2;
    TextView title1,title2,status1,status2;
    ProgressBar p1,p2;
    downloadtask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);
        start1=(Button)findViewById(R.id.start1);
        start2=(Button)findViewById(R.id.start2);
        cancel1=(Button)findViewById(R.id.cancel1);
        cancel2=(Button)findViewById(R.id.cancel2);
        status1=(TextView)findViewById(R.id.status1);
        status2=(TextView)findViewById(R.id.status2);
        title1=(TextView)findViewById(R.id.title1);
        title2=(TextView)findViewById(R.id.title2);
        p1=(ProgressBar)findViewById(R.id.p1);
        p2=(ProgressBar)findViewById(R.id.p2);
    }



    public void timepick2(View view)
    {
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

//                        tim2.setText(hourOfDay + ":" + minute);
//                        tt2=tim1.getText().toString();
                        Calendar calendar=Calendar.getInstance();
                        calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                        calendar.set(Calendar.MINUTE,minute);
                        calendar.set(Calendar.SECOND,0);
                        Calendar calendar1=Calendar.getInstance();
                        Long current = calendar1.getTimeInMillis();
                        Long sett = calendar.getTimeInMillis();
//                        al1(((sett-current)));//calendar.getTimeInMillis() will time in form of epoch milliseconds.
                        Toast.makeText(driver.this,Long.toString(sett-current), Toast.LENGTH_SHORT).show();
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void start(View view)
    {
        new downloadtask(p1,status1,title1).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"https://www.hrw.org/sites/default/files/reports/wr2010_0.pdf");
    }



}
