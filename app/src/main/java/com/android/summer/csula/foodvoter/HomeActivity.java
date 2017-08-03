package com.android.summer.csula.foodvoter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.summer.csula.foodvoter.database.FoodVoterFirebaseDb;
import com.android.summer.csula.foodvoter.models.User;
import com.android.summer.csula.foodvoter.polls.AllPollsFragment;
import com.android.summer.csula.foodvoter.polls.InvitedPollsFragment;
import com.android.summer.csula.foodvoter.polls.PollActivity;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;

public class HomeActivity extends AppCompatActivity implements FoodVoterFirebaseDb.Listener {

    private static final String TAG = HomeActivity.class.getSimpleName();
    private static final int REQUEST_CODE_SIGN_IN = 1;
    private static final boolean ONLINE = true;
    private static final boolean OFFLINE = false;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DatabaseReference connectedDatabaseReference;
    private ValueEventListener connectedValueListener;
    private FoodVoterFirebaseDb database;

    private FirebaseUser firebaseUser;

    private TextView usernameTextView;
    private ImageView userPresenceImage;
    private TabLayout tabLayout;


    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        /* Setup firebase database */
        connectedDatabaseReference = FirebaseDatabase.getInstance().getReference(".info/connected");

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = setupAuthStateListener();

        usernameTextView = (TextView) findViewById(R.id.tv_username);
        userPresenceImage = (ImageView) findViewById(R.id.image_view_my_presence);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout_home_polls);

        FloatingActionButton addPollButton = (FloatingActionButton) findViewById(R.id.button_add_poll);
        addPollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User user = new User(firebaseUser.getDisplayName(), firebaseUser.getUid(), true);
                startActivity(PollActivity.newIntent(HomeActivity.this, user));
            }
        });
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume()");
        super.onResume();
        firebaseAuth.addAuthStateListener(authStateListener);
        // Since the fragment data could change during onPause, we need to (re) initialized the tab
        // layout during onResume();
        initializeTabLayout();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause()");
        super.onPause();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
        detachDatabaseReadListener();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult()");
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Signed in as " + getSignedInUsername(), Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                finish();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int selectedItemId = item.getItemId();

        switch (selectedItemId) {
            case R.id.friends_menu:
                launchFriendsActivity();
                return true;
            case R.id.sign_out_menu:
                setUserOnlineStatusTo(OFFLINE);
                AuthUI.getInstance().signOut(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private FirebaseAuth.AuthStateListener setupAuthStateListener() {
        return new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                firebaseUser = firebaseAuth.getCurrentUser();

                if (firebaseUser != null) {
                    onSignedInInitialized();
                    setUserOnlineStatusTo(ONLINE);
                } else {
                    onSignedOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(
                                            Arrays.asList(
                                                    new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                                    new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build())
                                    ).build(),
                            REQUEST_CODE_SIGN_IN);
                }
            }
        };
    }

    private void onSignedInInitialized() {
        database = new FoodVoterFirebaseDb(this, firebaseUser.getUid());
        usernameTextView.setText(firebaseUser.getDisplayName());
        addNewUserToDatabase();
        attachDatabaseReadListener();
        attachConnectedValueListener();
    }

    private void initializeTabLayout() {
        tabLayout = (TabLayout) findViewById(R.id.tab_layout_home_polls);
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

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container_poll_list, AllPollsFragment.newInstance())
                .commit();
    }

    private void swapFragment(TabLayout.Tab tab) {
        String selectedTab = tab.getText().toString();
        String allTabs = getResources().getString(R.string.tab_item_all_polls);
        String invitedTab = getResources().getString(R.string.tab_item_invited_polls);

        if (selectedTab.equals(allTabs)) {
            replaceFragment(AllPollsFragment.newInstance());
        } else if (selectedTab.equals(invitedTab)) {
            replaceFragment(InvitedPollsFragment.newInstance());
        }
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container_poll_list, fragment)
                .commit();
    }

    private void addNewUserToDatabase() {
        database.addNewUserToDatabase(firebaseUser);
    }

    private void attachDatabaseReadListener() {
        database.attachReadListener();
    }

    private void attachConnectedValueListener() {
        connectedValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = dataSnapshot.getValue(Boolean.class);
                if (connected) {
                    userPresenceImage.setImageResource(android.R.drawable.presence_online);
                    setUserOnlineStatusTo(ONLINE);

                } else {
                    userPresenceImage.setImageResource(android.R.drawable.presence_offline);
                    setUserOnlineStatusTo(OFFLINE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        connectedDatabaseReference.addValueEventListener(connectedValueListener);
    }

    private void onSignedOutCleanup() {
        usernameTextView.setText("");
        detachDatabaseReadListener();
    }

    private void detachDatabaseReadListener() {
        if (database != null) {
            database.detachReadListener();
        }

        if (connectedDatabaseReference != null) {
            connectedDatabaseReference.removeEventListener(connectedValueListener);
        }
    }

    private void launchFriendsActivity() {
        String userId = firebaseUser.getUid();
        Context context = this;
        Intent friendsIntent = FriendsActivity.newIntent(context, userId);
        startActivity(friendsIntent);
    }

    private String getSignedInUsername() {
        return firebaseAuth.getCurrentUser().getDisplayName();
    }

    private void setUserOnlineStatusTo(final boolean isOnline) {
        database.setUserOnlineStatus(firebaseUser, isOnline);
    }


    @Override
    public void onUserAdded(User user) { } // Left intentionally blank

    @Override
    public void onUserChanged(User user) { } // Left intentionally blank

    @Override
    public void onFriendAdded(User user) { } // Left intentionally blank
}
