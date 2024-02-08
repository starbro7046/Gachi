package com.example.research.gachi;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ProgressDialog;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

import static android.R.attr.data;
import static com.example.research.gachi.R.layout.activity_write;


public class write extends AppCompatActivity {

     double longitude2;
     double latitude2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_write);

        Button wrtB = (Button) findViewById(R.id.wrtB);
        wrtB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText title = (EditText) findViewById(R.id.title);
                EditText content = (EditText) findViewById(R.id.content);
                EditText reason = (EditText) findViewById(R.id.reason);
                String pTitle = title.getText().toString();
                String pContent = content.getText().toString();
                String pReason = reason.getText().toString();

                if (pTitle.length() == 0) {
                    Toast.makeText(getApplicationContext(), "제목을 입력해 주세요", Toast.LENGTH_LONG).show();
                } else if (pContent.length() == 0) {
                    Toast.makeText(getApplicationContext(), "내용을 입력해 주세요", Toast.LENGTH_LONG).show();
                } else if (pReason.length() == 0) {
                    Toast.makeText(getApplicationContext(), "출처를 입력해 주세요", Toast.LENGTH_LONG).show();
                } else {

                    try {
                        String myNumber = null;
                        TelephonyManager mgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

                        // Android API 6.0이상부터는 사용자에게 직접 위치,전화번호 취득을위한 권한을 얻어야 합니다
                        myNumber = mgr.getLine1Number();
                        myNumber = myNumber.replace("+82", "0");

                        // LocationManager 객체를 얻어온다
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
                        System.out.println("jjjjjj"+longitude2);
                        JSONObject obj = new JSONObject();
                        System.out.println("ssssss"+latitude2);
                        String url = "http://114.70.63.54:8000/androidserver/postregister/";
                        obj.put("number", myNumber);
                        obj.put("la", latitude2);
                        obj.put("lo", longitude2);
                        obj.put("content", pContent);
                        obj.put("title", pTitle);
                        obj.put("url", pReason);

                        String data = obj.toString();
                        ContentValues cv = new ContentValues();
                        cv.put("data", data);

                        NetworkTask networkTask = new NetworkTask(url, cv);
                        networkTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                        inte();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

    }
    void inte()
    {
        Intent intent2=new Intent(write.this,MainActivity.class);
        startActivity(intent2);
    }

    private final LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            //여기서 위치값이 갱신되면 이벤트가 발생한다.

            longitude2 = location.getLongitude(); //경도
            latitude2 = location.getLatitude();   //위도
            String msg = "New Latitude: " + location.getLatitude()
                    + "New Longitude: " + location.getLongitude();
            System.out.println("jjjjjj"+longitude2);

        }
        public void onProviderDisabled(String provider) {

        }

        public void onProviderEnabled(String provider) {

        }

        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    };
}

class NetworkTask extends AsyncTask<Void, Void, String> {

  private String url;
  private ContentValues values;
  private TextView tv;

  NetworkTask(String url, ContentValues values) {

    this.url = url;
    this.values = values;
  }

  @Override
  protected void onPreExecute() {

    super.onPreExecute();

  }
  @Override
  protected String doInBackground(Void... params) {
    // 요청 결과를 저 장할 변수.
    RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
    return requestHttpURLConnection.request(url, values); // 해당 URL로 부터 결과물을 얻어온다.
  }

  @Override
  protected void onPostExecute(String s) {
    super.onPostExecute(s);;

  }
}