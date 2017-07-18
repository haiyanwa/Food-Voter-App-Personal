package com.android.summer.csula.foodvoter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button shayBtn;
    private Button yosepBtn;
    private Button haiyanBtn;
    private Button kailaBtn;
    private Button samanBtn;

    private Intent launchActivityIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        shayBtn = (Button) findViewById(R.id.shay_btn);
        shayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchActivityIntent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(launchActivityIntent);
            }
        });


        yosepBtn = (Button) findViewById(R.id.yosep_btn);
        yosepBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchActivityIntent = new Intent(MainActivity.this, DetailActivity.class);
                startActivity(launchActivityIntent);
            }
        });

        haiyanBtn = (Button) findViewById(R.id.haiyan_btn);
        haiyanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchActivityIntent = new Intent(MainActivity.this, ListActivity.class);
                startActivity(launchActivityIntent);
            }
        });

        kailaBtn = (Button) findViewById(R.id.kaila_btn);
        kailaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchActivityIntent = new Intent(MainActivity.this, TableResultActivity.class);
                startActivity(launchActivityIntent);
            }
        });

        samanBtn = (Button) findViewById(R.id.saman_btn);
        samanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchActivityIntent = new Intent(MainActivity.this, GraphResultActivity.class);
                startActivity(launchActivityIntent);
            }
        });
    }
}
