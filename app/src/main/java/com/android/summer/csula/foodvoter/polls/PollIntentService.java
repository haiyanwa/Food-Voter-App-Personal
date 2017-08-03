package com.android.summer.csula.foodvoter.polls;


import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.android.summer.csula.foodvoter.HomeActivity;
import com.android.summer.csula.foodvoter.yelpApi.models.Business;
import com.android.summer.csula.foodvoter.yelpApi.models.Yelp;
import com.android.summer.csula.foodvoter.yelpApi.tasks.RequestYelpSearchTask;
import com.android.summer.csula.foodvoter.yelpApi.utils.BusinessUtilities;
import com.google.firebase.database.FirebaseDatabase;

import java.net.URL;
import java.util.List;


public class PollIntentService extends IntentService {

    private static final String TAG = PollIntentService.class.getSimpleName();
    private static final String BUSINESSES_NODE = "businesses";
    private static final String POLLS_NODE = "polls";

    public PollIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        URL url = (URL) intent.getSerializableExtra("url");
        String pollId = intent.getStringExtra("pollId");

        // Make the API call and get random results
        Yelp yelp = RequestYelpSearchTask.execute(url);
        List<Business> selections = BusinessUtilities.getRandoms(yelp.getBusinesses());

        // Write to firebase
        updateBusinessesToFirebase(pollId, selections);

        // Broadcast that you've finished
        Intent updateBusinessIntent = new Intent(
                HomeActivity.PollBroadcastReceiver.UPDATE_BUSINESS_FIREBASE_COMPLETED);
        LocalBroadcastManager.getInstance(this).sendBroadcast(updateBusinessIntent);
    }

    /**
     * From the "polls" tree, at the child node of "pollId", at the "business" child, insert
     * the list of business objects there.
     */
    private static void updateBusinessesToFirebase(String pollId, List<Business> businesses) {
        FirebaseDatabase.getInstance().getReference()
                .child(POLLS_NODE)
                .child(pollId)
                .child(BUSINESSES_NODE)
                .setValue(businesses);
    }
}
