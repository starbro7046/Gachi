package com.example.research.gachi;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class AlarmList extends AppCompatActivity {

    double longitude;
    double latitude;
    private ListView mListView;
    NotificationManager notiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_list);

        SharedPreferences prefs = getSharedPreferences("setting", MODE_PRIVATE);

        try
        {
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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
        String url2="http://114.70.63.54:8000/androidserver/postalram/";
        ContentValues cv2 = new ContentValues();
        JSONObject obj2 = new JSONObject();
        try {

            obj2.put("la", latitude);
            obj2.put("lo", longitude);
            obj2.put("range",prefs.getInt("range",0));
            obj2.put("min",prefs.getInt("min",0));
            String data2=obj2.toString();
            cv2.put("data", data2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String result=null;
        try {
            NetworkTask networkTask2 =new NetworkTask(url2, cv2);
            result=networkTask2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR).get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Data>>() {
        }.getType();
        if(result.length()==14)
        {

        }else {
            final ArrayList<Data> list = (ArrayList<Data>) gson.fromJson(result, type);
            final MyAdapter mMyAdapter = new MyAdapter();
            try {
                for (int i = 0; i < 100; i++) {
                    mMyAdapter.addItem("제목: " + list.get(i).getTitle(), "" +list.get(i).getDate().substring(0,16), "loc" + getAddress(this, list.get(i).getLatitude(), list.get(i).getLongitude()), list.get(i).getId());
                }
            } catch (Exception e) {

            }
            mListView = (ListView) findViewById(R.id.listview);
        /* 리스트뷰에 어댑터 등록 */
            mListView.setAdapter(mMyAdapter);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent it=new Intent(AlarmList.this,read.class);
                    System.out.println("aaaaaa"+(list.get(position).getId()));

                    it.putExtra("id", list.get(position).getId());
                    startActivity(it);
                }
            });
        }
    }
    public static String getAddress(Context mContext,double lat, double lng) {
        String nowAddress ="전국";
        Geocoder geocoder = new Geocoder(mContext, Locale.KOREA);
        List<Address> address;
        try {
            if (geocoder != null) {
                //세번째 파라미터는 좌표에 대해 주소를 리턴 받는 갯수로
                //한좌표에 대해 두개이상의 이름이 존재할수있기에 주소배열을 리턴받기 위해 최대갯수 설정
                address = geocoder.getFromLocation(lat, lng, 1);

                if (address != null && address.size() > 0) {
                    // 주소 받아오기
                    String currentLocationAddress = address.get(0).getAddressLine(0).toString();
                    nowAddress  = currentLocationAddress;

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return nowAddress;
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

