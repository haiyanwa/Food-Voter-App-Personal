package com.android.summer.csula.foodvoter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.android.summer.csula.foodvoter.adapters.UsersAdapter;
import com.android.summer.csula.foodvoter.database.FoodVoterFirebaseDb;
import com.android.summer.csula.foodvoter.models.User;


public class AddFriendshipActivity extends AppCompatActivity implements
        UsersAdapter.UserOnClickHandler,
        FoodVoterFirebaseDb.Listener {

    private static final String TAG = AddFriendshipActivity.class.getSimpleName();
    private static final String EXTRA_ID = "userId";

    private FoodVoterFirebaseDb database;

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

        database = new FoodVoterFirebaseDb(this, userId);

        userRecyclerView = (RecyclerView) findViewById(R.id.rv_all_users);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        usersAdapter = new UsersAdapter(this);
        userRecyclerView.setLayoutManager(layoutManager);
        userRecyclerView.setAdapter(usersAdapter);

    }

    @Override
    public void onImageButtonClick(User clickedUser) {
        Log.d(TAG, "User that was clicked: " + clickedUser.toString());
        database.befriendUser(userId, clickedUser);
        Toast.makeText(this, clickedUser.getUsername() + " added to your friend list!",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        database.attachReadListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        database.detachReadListener();
    }

    @Override
    public void onFriendAdded(User friend) { }

    @Override
    public void onUserAdded(User user) {
        Log.d(TAG, "user added: " + user.toString());
        usersAdapter.add(user);
    }

    @Override
    public void onUserChanged(User user) {
        Log.d(TAG, "user changed: " + user.toString());
        usersAdapter.update(user);
    }
}
