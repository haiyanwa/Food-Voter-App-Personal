package com.android.summer.csula.foodvoter.database;


import android.util.Log;

import com.android.summer.csula.foodvoter.models.User;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserDatabase {

    private static final String TAG = UserDatabase.class.getSimpleName();
    private static final String USERS = "users";
    private static final String ONLINE_KEY = "online";

    private DatabaseReference usersRef;
    private ChildEventListener childEventListener;

    private UserDatabaseListener listener;


    public UserDatabase(UserDatabaseListener listener) {
        this.listener = listener;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        usersRef = database.getReference().child(USERS);
    }

    public void attachReadListener() {
        if (childEventListener == null) {

            childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    User user = dataSnapshot.getValue(User.class);
                    Log.d(TAG, "onUserAdded: " + user.toString());
                    listener.onUserAdded(user);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    User user = dataSnapshot.getValue(User.class);
                    Log.d(TAG, "onChildChanged: " + user.toString());
                    listener.onUserChanged(user);
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) { }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) { }

                @Override
                public void onCancelled(DatabaseError databaseError) { }
            };
            usersRef.addChildEventListener(childEventListener);
        }
    }

    public void detachReadListener() {
        if (childEventListener != null) {
            usersRef.removeEventListener(childEventListener);
            childEventListener = null;
        }
    }

    public void addNewUserToDatabase(final FirebaseUser firebaseUser) {
        usersRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(TAG, "Current user id: " + firebaseUser.getUid());
                        if (dataSnapshot.hasChild(firebaseUser.getUid())) {
                            Log.d(TAG, "existing user has log in");
                        } else {
                            Log.d(TAG, "new user has log in; adding user to the database");
                            usersRef.child(firebaseUser.getUid()).setValue(
                                    new User(firebaseUser.getDisplayName(), firebaseUser.getUid()));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    public void setUserOnlineStatus(final FirebaseUser firebaseUser, final boolean isOnline) {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                usersRef.child(firebaseUser.getUid()).child(ONLINE_KEY).setValue(isOnline);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public interface UserDatabaseListener {
        void onUserAdded(User user);

        void onUserChanged(User user);
    }
}
