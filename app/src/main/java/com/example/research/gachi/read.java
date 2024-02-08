package com.example.research.gachi;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static java.sql.DriverManager.println;


public class read extends AppCompatActivity {

    private ListView mListView2;
    MyAdapter mMyAdapter2 = new MyAdapter();
    Integer id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);

        Button com=(Button) findViewById(R.id.comment);
        com.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv=(TextView) findViewById(R.id.tv);
                String comment=tv.getText().toString();
                if(comment.length()==0)
                {
                    Toast.makeText(getApplicationContext(), "내용을 입력해 주세요", Toast.LENGTH_LONG).show();
                }
                else{

                    String myNumber = null;
                    TelephonyManager mgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

                    // Android API 6.0이상부터는 사용자에게 직접 위치,전화번호 취득을위한 권한을 얻어야 합니다
                    myNumber = mgr.getLine1Number();
                    myNumber = myNumber.replace("+82", "0");

                    ToggleButton tb=(ToggleButton) findViewById(R.id.toggleButton);
                    boolean agree=true;
                    if(tb.isChecked()) {
                        agree=true;
                    }
                    else
                    {
                        agree=false;
                    }
                    JSONObject obj = new JSONObject();
                    String url = "http://114.70.63.54:8000/androidserver/postcomment/";
                    try {
                        obj.put("id", id);
                        obj.put("number", myNumber);
                        obj.put("agree",agree );
                        obj.put("content", comment);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    String data = obj.toString();
                    ContentValues cv = new ContentValues();
                    cv.put("data", data);

                    NetworkTask networkTask = new NetworkTask(url, cv);
                    networkTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }
        });
        final Intent it = getIntent();   // 값을 받기 위한 Intent 생성
         id = it.getIntExtra("id",1);
        System.out.println("ddaaaadddd"+id);

        String url3="http://114.70.63.54:8000/androidserver/postdetail/";
        String result=null;
        ContentValues cv3 = new ContentValues();
        JSONObject obj3 = new JSONObject();
        try {
            obj3.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String data3=obj3.toString();
        cv3.put("data", data3);

        NetworkTask networkTask3 =new NetworkTask(url3, cv3);
        try {
            result=networkTask3.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        System.out.println("dddddd"+result);

        Gson gson = new Gson();
        System.out.println("aaaaaaa"+gson.fromJson(result, Data.class));
        Data data= gson.fromJson(result, Data.class);

        TextView title=(TextView) findViewById(R.id.title);
        title.setText(""+data.getTitle());
        TextView content=(TextView) findViewById(R.id.content);
        content.setText(""+data.getContent());
        TextView reason=(TextView) findViewById(R.id.reason);
        reason.setText(""+data.getReason());

        dataSetting2();

        Button singo=(Button) findViewById(R.id.singo);
        singo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(read.this, report.class);
                intent.putExtra("id",id);
                startActivity(intent);

            }
        });


    }
    private void dataSetting2(){
        String url2="http://114.70.63.54:8000/androidserver/postcommentlist/";
        ContentValues cv2 = new ContentValues();
        JSONObject obj2 = new JSONObject();
        try {
            obj2.put("id", id);
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
        System.out.println("qqqqqq"+result1);
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Data>>() {
        }.getType();
        ArrayList<Data> list2 = (ArrayList<Data>) gson.fromJson(result1,type);
        mListView2 = (ListView)findViewById(R.id.listview2);



       // System.out.println("test"+list2.get(1).getComment().toString());
        try {
            for (int i = 0; i < 100; i++) {

                mMyAdapter2.addItem("근거: " + list2.get(i).getComment(), "" + list2.get(i).getAgreeS(), "" + list2.get(i).getDate().substring(0,16), list2.get(i).getId());
            }
        }catch (Exception e)
        {

        }

        /* 리스트뷰에 어댑터 등록 */
        mListView2.setAdapter(mMyAdapter2);

    }
}
