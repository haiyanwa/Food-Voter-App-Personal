package com.android.summer.csula.foodvoter.polls;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.summer.csula.foodvoter.R;
import com.android.summer.csula.foodvoter.polls.models.Poll;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AllPollsFragment extends Fragment {

    private static final String FIREBASE_NODE_POLLS = "polls";
    private static final String TAG = AllPollsFragment.class.getSimpleName();

    private RecyclerView recyclerView;
    private PollsAdapter pollsAdapter;

    public static AllPollsFragment newInstance() {
        AllPollsFragment fragment = new AllPollsFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView()");

        View view = inflater.inflate(R.layout.fragment_all_polls, container, false);

        initializeRecyclerView(view);
        DatabaseReference pollsRef = FirebaseDatabase.getInstance().getReference().child(FIREBASE_NODE_POLLS);
        String key = pollsRef.getKey();
        Log.d(TAG, "pollsRef key : " + key);
        pollsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Poll poll = ds.getValue(Poll.class);
                    Log.d(TAG, poll.toString());
                    pollsAdapter.addPoll(poll);
                    Log.d(TAG,  pollsAdapter.getItemCount() + " ");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

        return view;
    }

    private void initializeRecyclerView(View view) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());

        pollsAdapter = new PollsAdapter();

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_all_polls);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(pollsAdapter);
    }
}
