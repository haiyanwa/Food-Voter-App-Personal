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


public class InvitesFragment extends Fragment implements FoodVoterFirebaseDb.Listener, FriendsInvitedAdapter.OnInviteListener {

    private static final String KEY_USER_ID = "userId";

    private static final String TAG = InvitesFragment.class.getSimpleName();

    private String userId;
    private FoodVoterFirebaseDb database;
    private RecyclerView friendsRecyclerView;
    private FriendsInvitedAdapter friendsAdapter;

    public static InvitesFragment newInstance(String userId) {
        Bundle args = new Bundle();
        args.putString(KEY_USER_ID, userId);
        InvitesFragment fragment = new InvitesFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userId = getArguments().getString(KEY_USER_ID);
        Log.d(TAG, "userId: " + userId);

    }

    @Override
    public void onResume() {
        super.onResume();
        database.attachReadListener();
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
    }

    @Override
    public void onInvite(Invitee user) {

    }

    @Override
    public void onUninvite(Invitee user) {

    }
}
