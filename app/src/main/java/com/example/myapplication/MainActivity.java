package com.example.myapplication;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView rcv;
    Button btNew;
    ArrayList<People> people = new ArrayList<>();

    Gson gson = new Gson();
    RCVAdapter rcvAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        rcv = findViewById(R.id.rcv);
        btNew = findViewById(R.id.btNew);

        People a = new People("AAA", "0911-111111", "No 1, Nanking Rd. Taipei");
        People b = new People("BBB", "0922-222222", "No 2, Nanking Rd. Taipei");
        People c = new People("CCC", "0933-333333", "No 3, Nanking Rd. Taipei");
        people.add(a);
        people.add(b);
        people.add(c);

        rcv.setLayoutManager(new LinearLayoutManager(this));
        rcvAdapter = new RCVAdapter(this, people);
        rcv.setAdapter(rcvAdapter);

        btNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MainActivity.this, Show.class);
                it.putExtra("action",Action.NEW);
                String json = gson.toJson(a);
                it.putExtra("json",json);
                newLuncher.launch(it);
            }
        });

    }

    ActivityResultLauncher<Intent> newLuncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == RESULT_OK){
                        Intent it = result.getData();
                        String json = it.getStringExtra("json");
                        People p = gson.fromJson(json,People.class);
                        people.add(p);
                        rcvAdapter.notifyDataSetChanged();
                    }
                }
            }
    );


    class RCVAdapter extends RecyclerView.Adapter<RCVAdapter.RCVHolder> {

        Context context;
        ArrayList<People> pList;
        public RCVAdapter(Context c_, ArrayList<People> p_) {
            context = c_;
            pList = p_;
        }


        @NonNull
        @Override
        public RCVAdapter.RCVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.people_item, parent, false);
            return new RCVHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RCVAdapter.RCVHolder holder, int position) {
            holder.tv_name.setText(pList.get(position).name);
            holder.tv_tel.setText(pList.get(position).tel);
            holder.tv_addr.setText(pList.get(position).addr);
        }

        @Override
        public int getItemCount() {
            return pList.size();
        }

        public class RCVHolder extends RecyclerView.ViewHolder {
            TextView tv_name, tv_tel, tv_addr;
            public RCVHolder(@NonNull View itemView) {
                super(itemView);
                tv_name = itemView.findViewById(R.id.tv_name);
                tv_tel = itemView.findViewById(R.id.tv_tel);
                tv_addr = itemView.findViewById(R.id.tv_addr);
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Delete Message");
                        int position = getAdapterPosition();
                        builder.setMessage("Delete?"+people.get(position).name);
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                int position = getAdapterPosition();
                                people.remove(position);
                                rcvAdapter.notifyDataSetChanged();
                                dialogInterface.dismiss();
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        builder.show();
                        return true;
                    }
                });
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        People p = pList.get(position);
                        String json = gson.toJson(p);
                        Intent it = new Intent(MainActivity.this,Show.class);
                        it.putExtra("json",json);
                        it.putExtra("position",position);
                        it.putExtra("action",Action.EDIT);
                        editLauncher.launch(it);
                    }
                });
            }
        }
        ActivityResultLauncher<Intent> editLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == RESULT_OK){
                            Intent data = result.getData();
                            String json = data.getStringExtra("json");
                            int positiotn = data.getIntExtra("position",-1);
                            Log.v("CCC","Position: " + positiotn);
                            people.remove(positiotn);
                            people.add(positiotn, gson.fromJson(json, People.class));
                            rcvAdapter.notifyDataSetChanged();
                        }
                    }
                }
        );
    }

}