package com.android.summer.csula.foodvoter.polls;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.summer.csula.foodvoter.R;
import com.android.summer.csula.foodvoter.models.User;
import com.android.summer.csula.foodvoter.polls.models.Poll;
import com.android.summer.csula.foodvoter.yelpApi.models.Coordinate;
import com.android.summer.csula.foodvoter.yelpApi.models.YelpPriceLevel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PollActivity extends AppCompatActivity implements SettingFragment.OnPollListener {


    private static final String TAG = PollActivity.class.getSimpleName();
    private static final String EXTRA_USER_ID = "userId";
    private static final String EXTRA_ONLINE = "online";
    private static final String EXTRA_USERNAME = "username";

    private TabLayout tabLayout;

    private String userId;
    private Poll poll;

    private FirebaseDatabase database;
    private DatabaseReference pollRef;
    private Button submit;

    boolean savePoll;

    public PollActivity() {}

    public static Intent newIntent(Context context, User user) {
        Intent intent = new Intent(context, PollActivity.class);
        intent.putExtra(EXTRA_USER_ID, user.getId());
        intent.putExtra(EXTRA_ONLINE, user.isOnline());
        intent.putExtra(EXTRA_USERNAME, user.getUsername());
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll);

        userId = getIntent().getStringExtra(EXTRA_USER_ID);
        String userName = getIntent().getStringExtra(EXTRA_USERNAME);
        boolean isOnline = getIntent().getBooleanExtra(EXTRA_ONLINE, true);
        User user = new User(userName, userId, isOnline);

        database = FirebaseDatabase.getInstance();
        pollRef = database.getReference().child("polls").push();
        poll = new Poll(user, pollRef.getKey());

        pollRef.setValue(poll);

        // TODO: Refactor
        pollRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Poll newPoll = dataSnapshot.getValue(Poll.class);
                if (newPoll != null) {
                    poll = newPoll;
                    Log.d(TAG, "POLL CHANGED: " + newPoll.toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        initTabLayout();

        // TODO: Refactor
        submit = (Button) findViewById(R.id.button_submit_poll);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePoll = true;
                onBackPressed();
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (savePoll) {
            pollRef.setValue(poll);
        } else {
            pollRef.setValue(null);
        }
    }

    private void initTabLayout() {
        tabLayout = (TabLayout) findViewById(R.id.tab_layout_poll);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                swapFragment(tab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });


        /* Default fragment to settings */
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.poll_fragment_container, SettingFragment.newInstance(poll))
                .commit();

    }


    private void swapFragment(TabLayout.Tab tab) {
        String selectedTab = tab.getText().toString();
        String settings = getResources().getString(R.string.tab_item_settings);
        String invites = getResources().getString(R.string.tab_item_invites);

        pollRef.setValue(poll);
        if (selectedTab.equals(settings)) {
            replaceFragment(SettingFragment.newInstance(poll));
        } else if (selectedTab.equals(invites)) {
            replaceFragment(InvitesFragment.newInstance(poll));
        }
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.poll_fragment_container, fragment)
                .commit();

    }

    @Override
    public void onTitleChange(String title) {
        poll.setTitle(title);
    }

    @Override
    public void onDescriptionChange(String description) {
        poll.setDescription(description);
    }

    @Override
    public void onOpenNowChange(boolean openNow) {
        poll.setOpenNow(openNow);
    }

    @Override
    public void onPriceChange(String price) {
        poll.setPrice(YelpPriceLevel.fromYelpString(price));
    }

    @Override
    public void onZipCodeChange(String zipCode) {
        poll.setCoordinate(null);
        poll.setZipCode(zipCode);
    }

    @Override
    public void onCoordinateChange(Coordinate coordinate) {
        poll.setZipCode(null);
        poll.setCoordinate(coordinate);
    }

}
