package com.android.summer.csula.foodvoter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.summer.csula.foodvoter.database.FoodVoterFirebaseDb;
import com.android.summer.csula.foodvoter.models.User;
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

    private FirebaseUser firebaseUser;

    private TextView usernameTextView;
    private Button startPollBtn;

    private FoodVoterFirebaseDb database;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        /* Setup firebase database */
        connectedDatabaseReference = FirebaseDatabase.getInstance().getReference(".info/connected");

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = setupAuthStateListener();

        usernameTextView = (TextView) findViewById(R.id.tv_username);

        startPollBtn = (Button) findViewById(R.id.btn_start_poll);
        startPollBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(PollActivity.newIntent(HomeActivity.this));
            }
        });
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
                    Drawable img = getResources().getDrawable(android.R.drawable.presence_online);
                    usernameTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null);
                    setUserOnlineStatusTo(ONLINE);

                } else {
                    Drawable img = getResources().getDrawable(android.R.drawable.presence_offline);
                    usernameTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null);
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
        detachDatabaseReadListener();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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

