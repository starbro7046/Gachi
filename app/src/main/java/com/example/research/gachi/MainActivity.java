package com.example.research.gachi;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import static android.content.Context.MODE_PRIVATE;
import static com.example.research.gachi.R.id.textView;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class MainActivity extends AppCompatActivity {

    int lastId;
    private ListView mListView;
    public int page=1;
    MyAdapter mMyAdapter = new MyAdapter();

    private List<HashMap<String, String>> mAndroidMapList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = getSharedPreferences("setting", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("range",30);
        editor.putInt("min",30);
        editor.commit();

        Button zoo=(Button) findViewById(R.id.zoo);
        zoo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent al=new Intent(MainActivity.this,AlarmList.class);
                startActivity(al);
            }
        });


        Logger.addLogAdapter(new AndroidLogAdapter());
        final TextView tv=(TextView) findViewById(R.id.textView2);

        ImageButton alarm=(ImageButton) findViewById(R.id.alarm);
        alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent alramIntent=new Intent(MainActivity.this,AlarmManager2.class);
                startActivity(alramIntent);
            }
        });


        Button serc=(Button) findViewById(R.id.serc);
        serc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TextView search=(TextView) findViewById(R.id.search);

                if(search.getText().length()==0)
                {
                    Toast.makeText(getApplicationContext(), "검색내용을 입력하세요", Toast.LENGTH_LONG).show();
                }else {
                    boolean c = Pattern.matches("^[0-9]*$", search.getText().toString());

                    Intent intent = new Intent(MainActivity.this, result.class);
                    intent.putExtra("boolean",c);
                    intent.putExtra("number",search.getText().toString());
                    startActivity(intent);
                }
            }
        });

        Button wrt = (Button) findViewById(R.id.wrt);
        wrt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, write.class);
                startActivity(intent);
            }
        });

        ImageButton re=(ImageButton) findViewById(R.id.re);
        re.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    page++;
                    tv.setText("페이지: 1~"+page);
                    mMyAdapter.notifyDataSetChanged();
                    dataSetting();
            }
        });


        /* 위젯과 멤버변수 참조 획득 */

        mListView = (ListView)findViewById(R.id.listView);

        /* 아이템 추가 및 어댑터 등록 */
        dataSetting();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent it=new Intent(MainActivity.this,read.class);
                System.out.println("aaaaaa"+(lastId- position+(page-1)*7));

                it.putExtra("id", (lastId-position+(page-1)*7));
                startActivity(it);
            }
        });
    }
    private void dataSetting(){
        String url2="http://114.70.63.54:8000/androidserver/postlist/";
        ContentValues cv2 = new ContentValues();
        JSONObject obj2 = new JSONObject();
        try {
            obj2.put("page", page);
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
        ArrayList<Data> list = (ArrayList<Data>) gson.fromJson(result,type);
        lastId=list.get(0).getId();

        for (int i=0; i<7; i++) {

            mMyAdapter.addItem("제목: " + list.get(i).getTitle(), "" + list.get(i).getDate().substring(0,16),"위치:" +getAddress(this,list.get(i).getLatitude(),list.get(i).getLongitude()),list.get(i).getId()  );
        }

        /* 리스트뷰에 어댑터 등록 */
        mListView.setAdapter(mMyAdapter);
    }
    public static String getAddress(Context mContext,double lat, double lng) {
        String nowAddress ="전국";
        Geocoder geocoder = new Geocoder(mContext, Locale.KOREA);
        List <Address> address;
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