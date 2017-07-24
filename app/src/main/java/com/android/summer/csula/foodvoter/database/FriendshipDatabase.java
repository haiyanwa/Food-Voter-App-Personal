package com.android.summer.csula.foodvoter.database;


import android.util.Log;

import com.android.summer.csula.foodvoter.models.FriendList;
import com.android.summer.csula.foodvoter.models.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FriendshipDatabase {

    public interface OnGetDataListener {
        void onChildAdded(FriendList friendList );
    }

    private static OnGetDataListener onGetDataListener;
    private static final String FRIENDSHIP_CHILD = "friendship";
    private static final String TAG = FriendshipDatabase.class.getSimpleName();
    private static FriendshipDatabase friendshipDatabase;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference friendshipReference;
    private ChildEventListener childEventListener;


    public static FriendshipDatabase get(OnGetDataListener onGetDataListener) {
        if (friendshipDatabase == null) {
            friendshipDatabase = new FriendshipDatabase();
        }

        FriendshipDatabase.onGetDataListener = onGetDataListener;
        return friendshipDatabase;
    }

    public FriendshipDatabase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        friendshipReference = firebaseDatabase.getReference().child(FRIENDSHIP_CHILD);
    }


    public void attachReadListener() {
        if (childEventListener == null) {
            childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Log.d(TAG, "Friend child added");
                    FriendList friendList = new FriendList(dataSnapshot.getKey());

                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        User u = child.getValue(User.class);
                        friendList.addFriend(u);
                    }

                    onGetDataListener.onChildAdded(friendList);
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
                    friendshipReference.child(hostId).child(friend.getId()).setValue(friend);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }
}

