package com.android.summer.csula.foodvoter.database;


import android.util.Log;

import com.android.summer.csula.foodvoter.models.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FriendshipDatabase {

    private static OnGetDataListener onGetDataListener;
    private static final String FRIENDSHIP_CHILD = "friendship";
    private static final String TAG = FriendshipDatabase.class.getSimpleName();

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference friendshipReference;
    private ChildEventListener childEventListener;

    private String userId;


    public FriendshipDatabase(OnGetDataListener onGetDataListener, String userId) {
        this.onGetDataListener = onGetDataListener;
        this.userId = userId;

        firebaseDatabase = FirebaseDatabase.getInstance();
        friendshipReference = firebaseDatabase.getReference().child(FRIENDSHIP_CHILD).child(userId);
    }


    public void attachReadListener() {
        if (childEventListener == null) {
            childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    User user = dataSnapshot.getValue(User.class);
                    Log.d(TAG, "Friend child added: " + user.toString());
                    onGetDataListener.onFriendAdded(user);
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
            friendshipReference.addChildEventListener(childEventListener);
        }
    }

    public void detachReadListener() {
        if (childEventListener != null) {
            friendshipReference.removeEventListener(childEventListener);
            childEventListener = null;
        }
    }

    public void befriendUser(final String hostId, final User friend) {
        friendshipReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.child(hostId).hasChild(friend.getId())) {
                    friendshipReference.child(friend.getId()).setValue(friend);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    public void unfriendUser(final String hostId, final User friend) {
       friendshipReference.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
                friendshipReference.child(friend.getId()).removeValue();
           }

           @Override
           public void onCancelled(DatabaseError databaseError) { }
       });
    }

    public interface OnGetDataListener {
        void onFriendAdded(User friend);
    }
}

