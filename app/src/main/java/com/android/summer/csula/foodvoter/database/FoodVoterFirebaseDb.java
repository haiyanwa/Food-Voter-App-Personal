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

public class FoodVoterFirebaseDb {

    private static final String TAG = FoodVoterFirebaseDb.class.getSimpleName();
    private static final String KEY_USERS = "users";
    private static final String KEY_ONLINE = "online";
    private static final String KEY_FRIENDSHIP = "friendship";

    private FoodVoterFirebaseDb.Listener listener;
    private DatabaseReference usersRef;
    private DatabaseReference friendshipRef;
    private ChildEventListener userChildEventListener;
    private ChildEventListener friendsChildEventListener;


    public FoodVoterFirebaseDb(Listener listener, String userId) {
        this.listener = listener;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        usersRef = database.getReference().child(KEY_USERS);
        friendshipRef = database.getReference().child(KEY_FRIENDSHIP).child(userId);
    }

    public void attachReadListener() {
        if (userChildEventListener == null) {
            userChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    User user = dataSnapshot.getValue(User.class);
                    Log.d(TAG, "onChildAdded(): " + user.toString());
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
            usersRef.addChildEventListener(userChildEventListener);
        }


        if (friendsChildEventListener == null) {
            friendsChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    User user = dataSnapshot.getValue(User.class);
                    Log.d(TAG, "Friend child added: " + user.toString());
                    listener.onFriendAdded(user);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) { }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) { }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) { }

                @Override
                public void onCancelled(DatabaseError databaseError) { }
            };
            friendshipRef.addChildEventListener(friendsChildEventListener);
        }

    }

    public void detachReadListener() {
        if (userChildEventListener != null) {
            usersRef.removeEventListener(userChildEventListener);
            userChildEventListener = null;
        }

        if (friendsChildEventListener != null) {
            friendshipRef.removeEventListener(friendsChildEventListener);
            friendsChildEventListener = null;
        }
    }

    public void addNewUserToDatabase(final FirebaseUser firebaseUser) {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
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
            public void onCancelled(DatabaseError databaseError) { }
        });
    }


    public void befriendUser(final String hostId, final User friend) {
        friendshipRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.child(hostId).hasChild(friend.getId())) {
                    friendshipRef.child(friend.getId()).setValue(friend);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    public void setUserOnlineStatus(final FirebaseUser firebaseUser, final boolean isOnline) {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                usersRef.child(firebaseUser.getUid()).child(KEY_ONLINE).setValue(isOnline);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    public void unfriendUser(final String hostId, final User friend) {
        friendshipRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                friendshipRef.child(friend.getId()).removeValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    public interface Listener {
        void onUserAdded(User user);

        void onUserChanged(User user);

        void onFriendAdded(User user);
    }
}
