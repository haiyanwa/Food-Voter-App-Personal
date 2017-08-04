package com.android.summer.csula.foodvoter.database;


import android.util.Log;

import com.android.summer.csula.foodvoter.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserUpdater {

    public static final DatabaseReference USERS_REFERENCE = getUserRef();

    private static final String TAG = UserUpdater.class.getSimpleName();


    public static void updateOnlineStatus(final String userId, final boolean isOnline) {
        USERS_REFERENCE.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // If you don't check that the user exist, you could insert a new user with
                // a single field, "online => false"
                if (dataSnapshot.hasChild(userId)) {
                    USERS_REFERENCE
                            .child(userId)
                            .child("online")
                            .setValue(isOnline);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }


    public static void logUserOnline(final User user) {
        USERS_REFERENCE.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild(user.getId())) {
                    Log.d(TAG, "existing_user: " + user.toString());
                } else {
                    Log.d(TAG, "new_user: " + user.toString());
                    USERS_REFERENCE
                            .child(user.getId())
                            .setValue(user);
                }

               updateOnlineStatus(user.getId(), true);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    private static DatabaseReference getUserRef() {
        return FirebaseDatabase.getInstance().getReference().child("users");
    }
}
