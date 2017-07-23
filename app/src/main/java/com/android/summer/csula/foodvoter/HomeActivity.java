package com.android.summer.csula.foodvoter;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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

    private static final String ANONYMOUS = "anonymous";
    private static final int REQUEST_CODE_SIGN_IN = 1;
    private static final String TAG = HomeActivity.class.getSimpleName();

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference usersDatabaseReference;
    private ChildEventListener childEventListener;

    private FirebaseUser firebaseUser;
    private FloatingActionButton addFriendButton;

    private TextView usernameTextView;

    private String username;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        username = ANONYMOUS;


        usernameTextView = (TextView) findViewById(R.id.tv_username);

        firebaseDatabase = FirebaseDatabase.getInstance();
        usersDatabaseReference = firebaseDatabase.getReference().child("users");

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                firebaseUser = firebaseAuth.getCurrentUser();

                if (user != null) {
                    onSignedInInitialized(user.getDisplayName());
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

        addFriendButton = (FloatingActionButton) findViewById(R.id.btn_add_friend);
        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    private void onSignedInInitialized(String username) {
        this.username = username;
        usernameTextView.setText(username);
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
        username = ANONYMOUS;
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
                AuthUI.getInstance().signOut(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private String getSignedInUsername() {
        return firebaseAuth.getCurrentUser().getDisplayName();
    }
}


