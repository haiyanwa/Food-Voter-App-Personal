package com.android.summer.csula.foodvoter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class PollActivity extends AppCompatActivity {

    public static Intent newIntent(Context context) {
        return new Intent(context, PollActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll);
    }
}
