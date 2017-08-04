package com.android.summer.csula.foodvoter.polls;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.summer.csula.foodvoter.R;

public class InvitedPollsFragment extends Fragment {

    public static InvitedPollsFragment newInstance() {
        return new InvitedPollsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_invited_polls, container,false);

        return view;
    }
}
