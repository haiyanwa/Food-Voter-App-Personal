package com.android.summer.csula.foodvoter.database;


import android.util.Log;

import com.android.summer.csula.foodvoter.models.User;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendshipDatabase {

    private static final String FRIENDSHIP_CHILD = "friendship";
    private static final String TAG = FriendshipDatabase.class.getSimpleName();
    private static FriendshipDatabase friendshipDatabase;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference friendshipReference;
    private Map<String, List<User>> friendshipMap;


    public static FriendshipDatabase get() {
        if (friendshipDatabase == null) {
            friendshipDatabase = new FriendshipDatabase();
        }

        return friendshipDatabase;
    }

    public FriendshipDatabase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        friendshipReference = firebaseDatabase.getReference().child(FRIENDSHIP_CHILD);
        friendshipMap = new HashMap<>();
    }

    public void addNewUserToDatabase(final FirebaseUser firebaseUser) {
        friendshipReference.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(TAG, "Current user id: " + firebaseUser.getUid());
                        if (dataSnapshot.hasChild(firebaseUser.getUid())) {
                            Log.d(TAG, "user already exist in friendship database");
                        } else {
                            Log.d(TAG, "adding user to the friendship database");
                            friendshipReference.child(firebaseUser.getUid()).setValue(false);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    public void readFriendship() {
        friendshipReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, dataSnapshot.getChildrenCount() + "");

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Log.d(TAG, ds.getKey());

                    if (!friendshipMap.containsKey(ds.getKey())) {
                        friendshipMap.put(ds.getKey(), new ArrayList<User>());

                    }

                    if (ds.hasChildren()) {
                        for (DataSnapshot child: ds.getChildren()) {
                            User user = child.getValue(User.class);
                            friendshipMap.get(ds.getKey()).add(user);
                        }
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    public List<User> getFriends(FirebaseUser firebaseUser) {
        return friendshipMap.get(firebaseUser.getUid());
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
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}

