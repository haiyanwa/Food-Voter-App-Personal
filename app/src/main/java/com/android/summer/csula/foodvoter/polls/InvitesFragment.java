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
import com.android.summer.csula.foodvoter.adapters.FriendsInvitedAdapter;
import com.android.summer.csula.foodvoter.database.FoodVoterFirebaseDb;
import com.android.summer.csula.foodvoter.models.Invitee;
import com.android.summer.csula.foodvoter.models.User;
import com.android.summer.csula.foodvoter.polls.models.Poll;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class InvitesFragment extends Fragment implements FoodVoterFirebaseDb.Listener, FriendsInvitedAdapter.OnInviteListener {

    private static final String TAG = InvitesFragment.class.getSimpleName();
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_POLL_ID = "pollId";

    private String userId;
    private String pollId;
    private FoodVoterFirebaseDb database;
    private RecyclerView friendsRecyclerView;
    private FriendsInvitedAdapter friendsAdapter;
    private DatabaseReference pollRef;
    private List<User> invitedUsers = new ArrayList<>();
    private Poll poll;

    public static InvitesFragment newInstance(Poll poll) {
        Bundle args = new Bundle();
        args.putString(KEY_USER_ID, poll.getAuthorId());
        args.putString(KEY_POLL_ID, poll.getPollId());
        InvitesFragment fragment = new InvitesFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userId = getArguments().getString(KEY_USER_ID);
        pollId = getArguments().getString(KEY_POLL_ID);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        database.detachReadListener();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_invites, container, false);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        friendsAdapter = new FriendsInvitedAdapter(this);

        friendsRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_friends_list);
        friendsRecyclerView.setLayoutManager(layoutManager);
        friendsRecyclerView.setAdapter(friendsAdapter);

        database = new FoodVoterFirebaseDb(this, userId);
        database.attachReadListener();

        // TODO: Refactor
        String pollId = getArguments().getString(KEY_POLL_ID);
        Log.d(TAG, "Poll Id: " + pollId);
        pollRef = FirebaseDatabase.getInstance().getReference().child("polls").child(pollId);
        pollRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                poll = dataSnapshot.getValue(Poll.class);
                if (poll != null) {
                    Log.d(TAG, poll.toString());
                    for (User voter : poll.getVoters()) {
                        invitedUsers.add(voter);
                        friendsAdapter.updateInvitedUser(voter);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
        return view;
    }

    @Override
    public void onUserAdded(User user) { }

    @Override
    public void onUserChanged(User user) { }

    @Override
    public void onFriendAdded(User user) {
        Log.d(TAG, "friends added: " + user.toString());
        friendsAdapter.addFriend(user);

        // TODO: Refactor
        for (User inv : invitedUsers) {
            if (inv.getId().equals(user.getId())) {
                friendsAdapter.updateInvitedUser(inv);
            }
        }
    }

    @Override
    public void onInvite(Invitee user) {
        poll.addVoters(user.getUser());
        pollRef.setValue(poll);
    }

    @Override
    public void onUninvite(Invitee user) {

    }
}
