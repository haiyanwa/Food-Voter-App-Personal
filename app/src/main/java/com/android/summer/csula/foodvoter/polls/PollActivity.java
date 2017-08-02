package com.android.summer.csula.foodvoter.polls;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.summer.csula.foodvoter.R;
import com.android.summer.csula.foodvoter.polls.models.Poll;
import com.android.summer.csula.foodvoter.yelpApi.models.Coordinate;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PollActivity extends AppCompatActivity implements SettingFragment.OnPollListener {


    private static final String EXTRA_USER_ID = "userId";
    private static final String TAG = PollActivity.class.getSimpleName();

    private TabLayout tabLayout;
    private Fragment settingFragment;
    private Fragment invitesFragment;

    private String userId;
    private Poll poll;

    private FirebaseDatabase database;
    private DatabaseReference pollRef;

    public PollActivity() {}

    public static Intent newIntent(Context context, String userId) {
        Intent intent = new Intent(context, PollActivity.class);
        intent.putExtra(EXTRA_USER_ID, userId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll);

        userId = getIntent().getStringExtra(EXTRA_USER_ID);

        database = FirebaseDatabase.getInstance();
        pollRef = database.getReference().child("polls").push();
        poll = new Poll(userId);
        pollRef.setValue(poll);

        initTabLayout();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, poll.toString());
        pollRef.setValue(null);
    }

    private void initTabLayout() {
        settingFragment = SettingFragment.newInstance(pollRef.getKey());

        invitesFragment = InvitesFragment.newInstance(userId);

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
                .add(R.id.poll_fragment_container, settingFragment)
                .commit();

    }


    private void swapFragment(TabLayout.Tab tab) {
        String selectedTab = tab.getText().toString();
        String settings = getResources().getString(R.string.tab_item_settings);
        String invites = getResources().getString(R.string.tab_item_invites);

        pollRef.setValue(poll);
        if (selectedTab.equals(settings)) {
            replaceFragment(settingFragment);
        } else if (selectedTab.equals(invites)) {
            replaceFragment(invitesFragment);
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
        poll.setPrice(price);
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
