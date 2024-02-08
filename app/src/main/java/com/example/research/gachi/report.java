package com.example.research.gachi;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class report extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        final Intent it = getIntent();
        final Integer id = it.getIntExtra("id", 0);

        RadioButton option1 = (RadioButton) findViewById(R.id.op1);
        option1.setChecked(true);

        Button singo=(Button) findViewById(R.id.singo);
        singo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int type=1;
                RadioButton option1 = (RadioButton) findViewById(R.id.op1);
                RadioButton option2 = (RadioButton) findViewById(R.id.op2);
                RadioButton option3 = (RadioButton) findViewById(R.id.op3);
                if(option1.isChecked()==true)
                {
                    type=1;
                }
                else if(option2.isChecked()==true)
                {
                    type=2;
                }
                else if(option3.isChecked()==true)
                {
                    type=3;
                }
                else
                {
                    type=4;
                }
                EditText reason=(EditText) findViewById(R.id.reason);
                String reasonP=reason.getText().toString();
                String url="http://114.70.63.54:8000/androidserver/postreport/";
                ContentValues cv = new ContentValues();
                JSONObject obj = new JSONObject();

                String myNumber = null;
                TelephonyManager mgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

                // Android API 6.0이상부터는 사용자에게 직접 위치,전화번호 취득을위한 권한을 얻어야 합니다
                myNumber = mgr.getLine1Number();
                myNumber = myNumber.replace("+82", "0");

                try {
                    obj.put("id", id);
                    obj.put("number",myNumber );
                    obj.put("type", type);
                    obj.put("reason",reasonP );
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String data=obj.toString();
                cv.put("data", data);

                NetworkTask networkTask2 =new NetworkTask(url, cv);
                networkTask2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                Toast.makeText(getApplicationContext(), "신고가 접수되었습니다", Toast.LENGTH_LONG).show();

                Intent next=new Intent(report.this,read.class);
                next.putExtra("id", id);
                startActivity(next);
            }
        });
    }
}
