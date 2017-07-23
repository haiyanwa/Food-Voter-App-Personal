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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserDatabase {

    private static UserDatabase userDatabase;
    private static final String USERS = "users";
    private static final String ONLINE_KEY = "online";

    private static final String TAG = UserDatabase.class.getSimpleName();

    private DatabaseReference usersRef;

    private ChildEventListener childEventListener;

    private Set<User> users;

    public static UserDatabase get() {
        if (userDatabase == null) {
            userDatabase = new UserDatabase();
        }

        return userDatabase;
    }

    private UserDatabase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        usersRef = database.getReference().child(USERS);
        users = new HashSet<>();
    }

    public List<User> getUsers() {
        return new ArrayList<>(users);
    }

    public void attachReadListener() {
        if (childEventListener == null) {
            users.clear();

            childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    User user = dataSnapshot.getValue(User.class);

                    if (user != null) {
                        Log.d(TAG, "user added: " + user.toString());
                        users.add(user);
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    User user = dataSnapshot.getValue(User.class);

                    if (user != null) {
                        Log.d(TAG, "user changed: " + user.toString());

                        // Update the user (set)  when the value change
                        for (User u : users) {
                            if (u.equals(user)) {
                                u.setOnline(user.isOnline());
                            }
                            Log.d(TAG, u.toString());
                        }
                    }
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

}
