package com.android.summer.csula.foodvoter;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.summer.csula.foodvoter.models.User;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;

public class HomeActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_SIGN_IN = 1;
    private static final String TAG = HomeActivity.class.getSimpleName();

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference usersDatabaseReference;
    private DatabaseReference friendshipDatabaseReference;
    private ChildEventListener childEventListener;

    private FirebaseUser firebaseUser;

    private TextView usernameTextView;
    private FloatingActionButton addFriendButton;
    private Button  startPollBtn;
    private Button  activePollBtn;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        /* Setup firebase database */
        firebaseDatabase = FirebaseDatabase.getInstance();
        usersDatabaseReference = firebaseDatabase.getReference().child("users");
        friendshipDatabaseReference = firebaseDatabase.getReference().child("friendship");

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = setupAuthStateListener();

        /* Set up views*/
        usernameTextView = (TextView) findViewById(R.id.tv_username);

        addFriendButton = (FloatingActionButton) findViewById(R.id.btn_add_friend);
        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        startPollBtn = (Button) findViewById(R.id.btn_start_poll);
        startPollBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        activePollBtn = (Button) findViewById(R.id.btn_active_poll);
        activePollBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = dataSnapshot.getValue(Boolean.class);
                if(connected) {
                    Drawable img = getResources().getDrawable(android.R.drawable.presence_online);
                    usernameTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null);
                    setUserOnlineStatusTo(true);

                } else {
                    Drawable img = getResources().getDrawable(android.R.drawable.presence_offline);
                    usernameTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null);
                    setUserOnlineStatusTo(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
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
                    setUserOnlineStatusTo(true);
                } else {
                    onSignedOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(
                                            Arrays.asList(new AuthUI.IdpConfig.Builder(
                                                            AuthUI.EMAIL_PROVIDER).build(),
                                                    new AuthUI.IdpConfig.Builder(
                                                            AuthUI.GOOGLE_PROVIDER).build()))
                                    .build(),
                            REQUEST_CODE_SIGN_IN);
                }
            }
        };
    }

    private void onSignedInInitialized() {
        usernameTextView.setText(firebaseUser.getDisplayName());
        addNewUserToDatabase();
        attachDatabaseReadListener();
    }

    private void addNewUserToDatabase() {
        usersDatabaseReference.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(TAG, "Current user id: " + firebaseUser.getUid());

                        if (dataSnapshot.hasChild(firebaseUser.getUid())) {
                            Log.d(TAG, "existing user has log in");
                        } else {
                            Log.d(TAG, "new user has log in; adding user to the database");
                            usersDatabaseReference.child(firebaseUser.getUid()).setValue(
                                    new User(firebaseUser.getDisplayName(), firebaseUser.getUid()));

                            friendshipDatabaseReference.setValue(firebaseUser.getUid());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void attachDatabaseReadListener() {
        if (childEventListener == null) {
            childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    User user = dataSnapshot.getValue(User.class);
                    Log.d(TAG, "name: " + user.getUsername() + ", Id: " + user.getId());
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            usersDatabaseReference.addChildEventListener(childEventListener);
        }
    }

    private void onSignedOutCleanup() {
        usernameTextView.setText("");
        detachDatabaseReadListener();
    }

    private void detachDatabaseReadListener() {
        if (childEventListener != null) {
            usersDatabaseReference.removeEventListener(childEventListener);
            childEventListener = null;
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
                Toast.makeText(this, "Signed in as " + getSignedInUsername(),
                        Toast.LENGTH_LONG).show();
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
            case R.id.sign_out_menu:
                setUserOnlineStatusTo(false);
                AuthUI.getInstance().signOut(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private String getSignedInUsername() {
        return firebaseAuth.getCurrentUser().getDisplayName();
    }

    private void setUserOnlineStatusTo(final boolean isOnline) {
        usersDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                usersDatabaseReference.child(firebaseUser.getUid()).child("online").setValue(isOnline);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }
}

