package com.android.summer.csula.foodvoter;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;

import com.android.summer.csula.foodvoter.adapters.FriendsAdapter;
import com.android.summer.csula.foodvoter.database.FriendshipDatabase;
import com.android.summer.csula.foodvoter.database.UserDatabase;
import com.android.summer.csula.foodvoter.models.User;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import static android.support.v7.widget.RecyclerView.*;

public class FriendsActivity extends AppCompatActivity implements
        FriendshipDatabase.OnGetDataListener,
        FriendsAdapter.FriendsAdapterListener,
        UserDatabase.UserDatabaseListener {

    private static final String EXTRA_USER_ID = "user_id";
    private static final String TAG = FriendsActivity.class.getSimpleName();

    private RecyclerView friendsRecyclerView;
    private FriendsAdapter friendsAdapter;
    private FriendshipDatabase friendshipDatabase;
    private FloatingActionButton addFriendButton;
    private UserDatabase userDatabase;

    private String userId;

    public static Intent newIntent(Context context, String userId) {
        Intent friendsIntent = new Intent(context, FriendsActivity.class);
        friendsIntent.putExtra(EXTRA_USER_ID, userId);
        return friendsIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        initialize();
    }

    private void initialize() {
        initUserId();
        initViews();
    }

    private void initUserId() {
        Intent currentIntent = getIntent();
        userId = currentIntent.getStringExtra(EXTRA_USER_ID);
    }

    private void initDatabase() {
        userDatabase = new UserDatabase(this);
        friendshipDatabase = new FriendshipDatabase(this, userId);
        friendshipDatabase.attachReadListener();
    }

    private void initViews() {
        LayoutManager layoutManager = new LinearLayoutManager(this);

        friendsAdapter = new FriendsAdapter(this);

        friendsRecyclerView = (RecyclerView) findViewById(R.id.rv_friend_list);
        friendsRecyclerView.setLayoutManager(layoutManager);
        friendsRecyclerView.setAdapter(friendsAdapter);

        addFriendButton = (FloatingActionButton) findViewById(R.id.btn_add_friend);
        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentAddFriendsActivity = AddFriendshipActivity.newIntent(
                        FriendsActivity.this, userId);
                startActivity(intentAddFriendsActivity);
            }
        });


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                User friend = (User) viewHolder.itemView.getTag();
                Log.d(TAG, "swipe friend: " + friend.toString());
                friendshipDatabase.unfriendUser(userId, friend);
                friendsAdapter.removeFriend(friend);

            }
        }).attachToRecyclerView(friendsRecyclerView);
    }

    @Override
    public void onFriendAdded(User friend) {
        Log.d(TAG, "onFriendAdded() - " + friend.toString());
        friendsAdapter.addFriend(friend);
    }

    @Override
    public void unfriend(User user) {
        friendshipDatabase.unfriendUser(userId, user);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initDatabase();
    }

    @Override
    protected void onPause() {
        super.onPause();
        friendshipDatabase.detachReadListener();
        friendsAdapter.clear();
    }

    @Override
    public void onUserAdded(User user) { } // Do nothing

    @Override
    public void onUserChanged(User user) {
        Log.d(TAG, "onUserChanged: " + user.toString());
        friendsAdapter.updateFriend(user);
    }
}
