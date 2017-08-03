package com.android.summer.csula.foodvoter.polls;


import android.content.Context;
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
import com.android.summer.csula.foodvoter.models.User;
import com.android.summer.csula.foodvoter.polls.models.Poll;

import java.util.ArrayList;
import java.util.List;


public class InvitesFragment extends Fragment implements FoodVoterFirebaseDb.Listener {

    private static final String TAG = InvitesFragment.class.getSimpleName();
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_VOTER_ID_LIST = "voterIdList";


    private View view;

    private String userId;
    private FoodVoterFirebaseDb database;
    private FriendsInvitedAdapter friendsAdapter;
    private List<String> invitedUsers = new ArrayList<>();
    private OnPollInvitesListener invitesListener;

    public static InvitesFragment newInstance(Poll poll) {
        Bundle args = new Bundle();
        args.putString(KEY_USER_ID, poll.getAuthorId());
        args.putStringArrayList(KEY_VOTER_ID_LIST, (ArrayList<String>) poll.getVoters());
        InvitesFragment fragment = new InvitesFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userId = getArguments().getString(KEY_USER_ID);
        invitedUsers = getArguments().getStringArrayList(KEY_VOTER_ID_LIST);
    }


    @Override
    public void onPause() {
        super.onPause();
        database.detachReadListener();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach()");

        try {
            invitesListener = (OnPollInvitesListener) context;
        } catch (ClassCastException e) {
            Log.e(TAG, "Host Activity need to implement the interface: "
                + OnPollInvitesListener.class.toString(), e);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_invites, container, false);

        initializeUI();

        initializeDatabase();

        return view;
    }

    private void initializeUI() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        friendsAdapter = new FriendsInvitedAdapter(invitesListener);
        RecyclerView friendsRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_friends_list);
        friendsRecyclerView.setLayoutManager(layoutManager);
        friendsRecyclerView.setAdapter(friendsAdapter);
    }

    private void initializeDatabase() {
        database = new FoodVoterFirebaseDb(this, userId);
        database.attachReadListener();
    }

    @Override
    public void onUserAdded(User user) { }

    @Override
    public void onUserChanged(User user) { }


    @Override
    public void onFriendAdded(User user) {
        Log.d(TAG, "friends added: " + user.toString());

        friendsAdapter.addFriend(user);

        /* Check if the new friend is already invited, if so update the viewHolder*/
        for (String invitedUser : invitedUsers) {
            if (invitedUser.equals(user.getId())) {
                friendsAdapter.updateInvitedUser(user);
                return;
            }
        }

    }

    /**
     * This interface is used to inform the host activity that a user have been invited/univted to
     * a poll
     */
    public interface OnPollInvitesListener {

        void onUserInvited(String userId);

        void onUserUninvited(String userId);
    }
}
