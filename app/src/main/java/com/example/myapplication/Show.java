package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

public class Show extends AppCompatActivity {
    String  json, action;
    EditText et_Name, et_Tel, et_Addr;

    Button bt_OK;

    Gson gson = new Gson();

    int position = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        bt_OK = findViewById(R.id.bt_ok);
        et_Name = findViewById(R.id.ed_Name);
        et_Tel = findViewById(R.id.ed_Tel);
        et_Addr = findViewById(R.id.ed_Addr);
        Intent it = getIntent();
        action = it.getStringExtra("action");
        //json = it.getStringExtra("json");
        //et_Name.setText(json);
        if(action.equals(Action.EDIT)){
            String json = it.getStringExtra("json");
            position = it.getIntExtra("position",  -1);
            People p = gson.fromJson(json, People.class);
            et_Name.setText(p.name);
            et_Tel.setText(p.tel);
            et_Addr.setText(p.addr);
        }
        bt_OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = et_Name.getText().toString();
                String tel = et_Tel.getText().toString();
                String addr = et_Addr.getText().toString();
                People p = new People(name, tel, addr);
                String json = gson.toJson(p);
                Intent it = new Intent();
                it.putExtra("json",json);
                if(action.equals(Action.EDIT)) it.putExtra("position",position);
                setResult(RESULT_OK,it);
                finish();
            }
        });
    }
}