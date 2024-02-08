package com.example.research.gachi;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class Message extends BroadcastReceiver  {


    private NotificationManager notiManager;
    //진동을 줘보자 진동객체
    Vibrator vibrator;
    //알림 식별값
    final static int MyNoti=0;
    MyAdapter mMyAdapter = new MyAdapter();
     double longitude;
     double latitude;
    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("wwwwwwwwwwwwww");
        try
        {
            LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, // 등록할 위치제공자
                    100, // 통지사이의 최소 시간간격 (miliSecond)
                    1, // 통지사이의 최소 변경거리 (m)
                    mLocationListener);
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 등록할 위치제공자
                    100, // 통지사이의 최소 시간간격 (miliSecond)
                    1, // 통지사이의 최소 변경거리 (m)
                    mLocationListener);
        }catch(SecurityException ex)
        {
            Log.e("GACHI", "Location Manager Error");
        }

        JSONObject obj = new JSONObject();
        String url = "http://114.70.63.54:8000/androidserver/postalram/";
        try {
            System.out.println("jjjjjj"+latitude);
            obj.put("la", latitude);
            obj.put("lo", longitude);
            obj.put("range",30);
            obj.put("min",20);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String data = obj.toString();
        ContentValues cv = new ContentValues();
        cv.put("data", data);
        String result=null;
        try {
            NetworkTask networkTask = new NetworkTask(url, cv);
            result=networkTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        notiManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification.Builder mBuilder = new Notification.Builder(context);
        mBuilder.setSmallIcon(R.drawable.icon);
        mBuilder.setTicker("message");
        mBuilder.setContentTitle("alarm receieved");
        Intent intent2 = new Intent(context, AlarmList.class);
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent2, 0);
        mBuilder.setContentIntent(pi);

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Data>>() {
        }.getType();
        String text;
        if(result.length()==14)
        {
            text="없음";
        }else {
            ArrayList<Data> list = (ArrayList<Data>) gson.fromJson(result, type);
            text=("새로운 글이 도착하였습니다:"+list.get(0).getTitle());
        }
        mBuilder.setContentText(text);

        mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
        //mBuilder.setContentIntent(pendingIntent);
        mBuilder.setAutoCancel(true);

        notiManager.notify(111, mBuilder.build());


    }
    private final LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            //여기서 위치값이 갱신되면 이벤트가 발생한다.

            longitude = location.getLongitude(); //경도
            latitude = location.getLatitude();   //위도


        }
        public void onProviderDisabled(String provider) {

        }

        public void onProviderEnabled(String provider) {

        }

        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    };
}

