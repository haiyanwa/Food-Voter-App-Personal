package com.android.summer.csula.foodvoter.poll;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.summer.csula.foodvoter.R;

public class PollActivity extends AppCompatActivity {


    private static final String EXTRA_USER_ID = "userId";
    private static final String TAG = PollActivity.class.getSimpleName();

    private TabLayout tabLayout;
    private Fragment settingFragment;
    private Fragment invitesFragment;

    private String userId;

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
        initTabLayout();
    }

    private void initTabLayout() {
        settingFragment = new SettingFragment();
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
}
