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
    private static final String KEY_FRIENDSHIP = "friendship";

    private FoodVoterFirebaseDb.Listener listener;
    private DatabaseReference friendshipRef;
    private ChildEventListener userChildEventListener;
    private ChildEventListener friendsChildEventListener;


    public FoodVoterFirebaseDb(Listener listener, String userId) {
        this.listener = listener;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        friendshipRef = database.getReference().child(KEY_FRIENDSHIP).child(userId);
    }

    public void attachReadListener() {
        if (userChildEventListener == null) {
            attachUserChildEventListener();
        }

        if (friendsChildEventListener == null) {
            attachFriendsChildEventListener();
        }
    }

    private void attachUserChildEventListener() {
        userChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User user = dataSnapshot.getValue(User.class);
                listener.onUserAdded(user);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                User user = dataSnapshot.getValue(User.class);
                listener.onUserChanged(user);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) { }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) { }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        };
        UserUpdater.USERS_REFERENCE.addChildEventListener(userChildEventListener);
    }

    private void attachFriendsChildEventListener() {
        friendsChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User user = dataSnapshot.getValue(User.class);
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

    public void detachReadListener() {
        if (userChildEventListener != null) {
            UserUpdater.USERS_REFERENCE.removeEventListener(userChildEventListener);
            userChildEventListener = null;
        }

        if (friendsChildEventListener != null) {
            friendshipRef.removeEventListener(friendsChildEventListener);
            friendsChildEventListener = null;
        }
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

    public void unfriendUser(final User friend) {
        friendshipRef.child(friend.getId()).removeValue();
    }

    public interface Listener {
        void onUserAdded(User user);

        void onUserChanged(User user);

        void onFriendAdded(User user);
    }
}
