package com.android.summer.csula.foodvoter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.android.summer.csula.foodvoter.adapters.UsersAdapter;
import com.android.summer.csula.foodvoter.database.UserDatabase;
import com.android.summer.csula.foodvoter.models.User;

import java.util.List;

public class AddFriendshipActivity extends AppCompatActivity {

    private static final String TAG = AddFriendshipActivity.class.getSimpleName();
    private static final String EXTRA_ID = "userId";

    private UserDatabase userDatabase;

    private RecyclerView userRecyclerView;
    private UsersAdapter usersAdapter;

    private String userId;

    public static Intent newIntent(Context context, String userId) {
        Intent intent = new Intent(context, AddFriendshipActivity.class);
        intent.putExtra(EXTRA_ID, userId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friendship);

        Intent intent = getIntent();
        userId = intent.getStringExtra(EXTRA_ID);

        userDatabase = UserDatabase.get();
        userRecyclerView = (RecyclerView) findViewById(R.id.rv_all_users);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        usersAdapter = new UsersAdapter();
        userRecyclerView.setLayoutManager(layoutManager);
        userRecyclerView.setAdapter(usersAdapter);

    }
}
