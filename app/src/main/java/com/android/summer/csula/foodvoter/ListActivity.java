package com.android.summer.csula.foodvoter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.summer.csula.foodvoter.models.UserVote;
import com.android.summer.csula.foodvoter.polls.models.Poll;
import com.android.summer.csula.foodvoter.yelpApi.models.Business;
import com.android.summer.csula.foodvoter.yelpApi.models.Coordinate;
import com.android.summer.csula.foodvoter.yelpApi.models.Location;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity implements RVoteAdapter.ListItemClickListener, RVoteAdapter.SwitchListener {

    private static final String EXTRA_POLL = "poll";
    private static final String POLLS_TREE = "polls";

    private RVoteAdapter rVoteAdapter;
    private RecyclerView rVoteRecyclerView;
    private List<Business> rChoiceData;
    private Toast mToast;
    public static final String ANONYMOUS = "anonymous";
    //private Button mSendVoteBtn;
    private final static String TAG = "ListActivity";

    private Business votedBusiness;
    private String mUsername = ANONYMOUS;

    private DatabaseReference pollRef;      // polls/{id}
    private Poll poll;                      // the object corresponding to polls/{id}


    public static Intent newIntent(Context context, Poll poll) {
        Intent intent = new Intent(context, ListActivity.class);
        intent.putExtra(EXTRA_POLL, poll.getPollId());
        return intent;
    }

    public static String getExtraPollId(Intent intent) {
        return intent.getStringExtra(EXTRA_POLL);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        //mSendVoteBtn = (Button) findViewById(R.id.rv_vote_btn);

        rVoteRecyclerView = (RecyclerView) findViewById(R.id.rv_vote_list);
        rChoiceData = new ArrayList<Business>();
        rVoteAdapter = new RVoteAdapter(this,rChoiceData,this,this);
        rVoteRecyclerView.setAdapter(rVoteAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rVoteRecyclerView.setLayoutManager(layoutManager);
    }


    @Override
    protected void onResume() {
        super.onResume();

        initializePollRef();
    }

    @Override
    public void onListItemClick(Business business) {

        /**if(mToast != null){
            mToast.cancel();
        }
        String toastMessage = "Item " + business.getName() + " has been clicked.";
        mToast = Toast.makeText(this, toastMessage, Toast.LENGTH_LONG);
        mToast.show();**/

        Intent intent = new Intent(this, DetailActivity.class);

        Log.d(TAG, "intent passed details " + business.getCoordinate().getLatitude() + " " + business.getCoordinate().getLongitude());

        Location location = new Location();
        Coordinate coordinate = new Coordinate();
        location = business.getLocation();
        coordinate = business.getCoordinate();
        String address = location.getDisplayAddress().toString().substring(1,(location.getDisplayAddress().toString().length())-1);
        String rating = String.valueOf(business.getRating());
        intent.putExtra("display_phone", business.getDisplayPhone());
        intent.putExtra("name", business.getName());
        intent.putExtra("image_url", business.getImageUrl());
        intent.putExtra("url", business.getUrl());
        intent.putExtra("display_address", address);
        intent.putExtra("ratings", rating);
        intent.putExtra("price", business.getPrice());
        intent.putExtra("latitude",String.valueOf(business.getCoordinate().getLongitude()));
        intent.putExtra("longitude",  String.valueOf(business.getCoordinate().getLatitude()));

//        Log.d("ppp",  String.valueOf(business.getCoordinate().getLongitude()));
//        Log.d("ppp",  String.valueOf(business.getCoordinate().getLatitude()));

        startActivity(intent);
    }

    private void initializePollRef() {
        String pollId = getExtraPollId(getIntent());
        pollRef = getPollRef(pollId);
        attachSingleValueListenerToPoll();
    }

    @Override
    public void onSwitchSwiped(Business business, boolean swiped) {
        if(mToast != null){
            mToast.cancel();
        }
        String toastMessage = "";
        if(swiped){
            toastMessage = "Voted for " + business.getName();
        }else{
            toastMessage = "Switched off for " + business.getName();
        }
        votedBusiness = business;
        Log.d(TAG, "Voted for: " + votedBusiness);

        mToast = Toast.makeText(this, toastMessage, Toast.LENGTH_LONG);
        mToast.show();
    }

    //For SendMyVote Button
    public void sendVote(View v){
        String message = "Send my vote for " + votedBusiness.getName();
        //mToast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        //mToast.show();
        DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null){
            String userid = user.getUid();
            UserVote userVote = new UserVote(userid, votedBusiness);
            dbReference.push().setValue(userVote);
        }else{
            mToast = Toast.makeText(this, "Cannot find user. Failed to vote...", Toast.LENGTH_LONG);
            mToast.show();
        }

    }

    /**
     * Returns a DatabaseReference to polls/{pollId}.
     */
    private DatabaseReference getPollRef(String pollId) {
        return FirebaseDatabase.getInstance()
                .getReference()
                .child(POLLS_TREE)
                .child(pollId);
    }

    /**
     * Return a value event listener that will retrieve and set a Poll.cass to  our "poll" class
     * variable.
     */
    private ValueEventListener getValueEventListenerForPoll() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                poll = dataSnapshot.getValue(Poll.class);

                if (poll != null) {
                    rVoteAdapter.setBusinesses(poll.getBusinesses());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        };
    }

    /**
     *  Attach a listener for a single event, meaning it will read perform its operation once, and
     *  stop. Thus you don't have to detach it.
     */
    private void attachSingleValueListenerToPoll() {
        ValueEventListener valueEventListener = getValueEventListenerForPoll();
        pollRef.addListenerForSingleValueEvent(valueEventListener);
    }
}
