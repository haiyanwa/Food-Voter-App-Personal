package com.android.summer.csula.foodvoter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.android.summer.csula.foodvoter.ListActivity.getExtraPollId;


public class PostVoteActivity extends AppCompatActivity {
    private TextView voteView;
    private DatabaseReference userPollRef;
    private String votedBusinessId;
    private final String TAG = "PostVoteActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_vote);

        voteView = (TextView)findViewById(R.id.user_vote);

        String pollId = getExtraPollId(getIntent());
        userPollRef = getPollRef(pollId);
        userPollRef.addListenerForSingleValueEvent(getValueEventListenerForVote());

        Log.d(TAG, "Poll Id: " + pollId);
        //voteView.append(votedBusinessId);
        voteView.setText("Your vote is " + votedBusinessId);
    }

    private DatabaseReference getPollRef(String pollId) {
        return FirebaseDatabase.getInstance()
                .getReference()
                .child(ListActivity.POLLS)
                .child(pollId)
                .child(ListActivity.VOTES)
                .child(getCurrentUserId());
    }

    private String getCurrentUserId(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String userId = user.getUid();

        return userId;
    }

    private ValueEventListener getValueEventListenerForVote() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                votedBusinessId = dataSnapshot.getValue().toString();
                Log.d(TAG, "Business Id: " + votedBusinessId);
                voteView.setText("Your vote is " + votedBusinessId);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        };
    }

}
