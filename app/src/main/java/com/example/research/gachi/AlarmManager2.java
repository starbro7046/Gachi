package com.example.research.gachi;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

public class AlarmManager2 extends Activity  {

    EditText et;
    int rateB;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_manager2);


        Button btn=(Button) findViewById(R.id.btn_set_alarm);
        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "setAlarm()", Toast.LENGTH_SHORT).show();
                EditText rangeE=(EditText) findViewById(R.id.range);
                String e=rangeE.getText().toString();
                EditText rangeM=(EditText) findViewById(R.id.time);
                String m=rangeM.getText().toString();
                int range=Integer.parseInt(e);
                int min=Integer.parseInt(m);
                SharedPreferences prefs = getSharedPreferences("setting", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("range",range);
                editor.putInt("min",min);
                editor.commit();
                setAlarm();
            }
        });
        Button btnr=(Button) findViewById(R.id.btn_release_alarm);
        btnr.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "releaseAlarm()", Toast.LENGTH_SHORT).show();
                releaseAlarm();
            }
        });

    }

    // 알람 등록
    private void setAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Intent intent = new Intent(AlarmManager2.this, Message.class);
        PendingIntent pIntent = PendingIntent.getBroadcast(AlarmManager2.this, 0, intent, 0);
        EditText et = (EditText) findViewById(R.id.rate);

        int rate;
        if (et.getText().length() == 0) {
            rate = 3600;
        } else {
            rateB = Integer.parseInt(et.getText().toString()); //정수값 가져오기
            rate = rateB; //정수값 가져오기
        }

        long startTime = SystemClock.elapsedRealtime() + rate * 1000;
        long cycleTime = rate * 1000;


        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, startTime, cycleTime, pIntent);
    }

    // 알람 해제
    private void releaseAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Intent intent = new Intent(AlarmManager2.this, Message.class);
        PendingIntent pIntent = PendingIntent.getBroadcast(AlarmManager2.this, 0, intent, 0);
        //알람객체의 cancel 메소드로 해당 알람을 종료시키게 됩니다.
        alarmManager.cancel(pIntent);

    }

}