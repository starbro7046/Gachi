package com.example.research.gachi;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

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

public class result extends AppCompatActivity {
    ArrayList<Data> list2;

    int id;
    MyAdapter mMyAdapter2 = new MyAdapter();
    private ListView mListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        final Intent it = getIntent();   // 값을 받기 위한 Intent 생성
        boolean id = it.getBooleanExtra("boolean", true);
        String number = it.getStringExtra("number");
        if(id)
        {
            String url2="http://114.70.63.54:8000/androidserver/postsearchnumber/";
            ContentValues cv2 = new ContentValues();
            JSONObject obj2 = new JSONObject();
            try {
                obj2.put("number", number);
                String data2=obj2.toString();
                cv2.put("data", data2);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String result1=null;
            try {
                NetworkTask networkTask4 =new NetworkTask(url2, cv2);
                result1=networkTask4.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR).get();

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            mListView = (ListView)findViewById(R.id.listView);
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Data>>() {
            }.getType();
            list2 = (ArrayList<Data>) gson.fromJson(result1,type);
            mListView = (ListView)findViewById(R.id.listview2);

            try {
                for (int i = 0; i < 100; i++) {

                    mMyAdapter2.addItem("제목: " + list2.get(i).getTitle(), "" + list2.get(i).getDate().substring(0,16),"loc" +getAddress(this,list2.get(i).getLatitude(),list2.get(i).getLongitude()),list2.get(i).getId()  );
                }
            }catch (Exception e)
            {

            }
            mListView.setAdapter(mMyAdapter2);
        }else{
            String url2="http://114.70.63.54:8000/androidserver/postsearchtitle/";
            ContentValues cv2 = new ContentValues();
            JSONObject obj2 = new JSONObject();
            try {
                obj2.put("title", number);
                String data2=obj2.toString();
                cv2.put("data", data2);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String result1=null;
            try {
                NetworkTask networkTask4 =new NetworkTask(url2, cv2);
                result1=networkTask4.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR).get();

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            mListView = (ListView)findViewById(R.id.listView);
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Data>>() {
            }.getType();
            list2 = (ArrayList<Data>) gson.fromJson(result1,type);
            mListView = (ListView)findViewById(R.id.listview2);
            try {
                for (int i = 0; i < 100; i++) {

                    mMyAdapter2.addItem("제목: " + list2.get(i).getTitle(), "date" + list2.get(i).getDate(),"loc" +getAddress(this,list2.get(i).getLatitude(),list2.get(i).getLongitude()),list2.get(i).getId()  );
                }
            }catch (Exception e)
            {
            }
        /* 리스트뷰에 어댑터 등록 */
            mListView.setAdapter(mMyAdapter2);
        }

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent it=new Intent(result.this,read.class);
                int id2=list2.get(position).getId();
                System.out.println("ccccc"+id2);
                it.putExtra("id",id2 );
                startActivity(it);
            }
        });
    }
    public static String getAddress(Context mContext, double lat, double lng) {
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
}
