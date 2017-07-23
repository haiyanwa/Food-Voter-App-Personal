package com.android.summer.csula.foodvoter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Haiyan on 7/19/17.
 */

public class RVoteAdapter extends RecyclerView.Adapter<RVoteAdapter.ViewHolder>{

    final private ListItemClickListener mOnClickListener;

    private static int viewHolderCount;

    private int mNumberItems;

    private List<Restaurant> mChoiceData;

    public interface ListItemClickListener{
        void onListItemClick(int clickedItemIndex);
    }


    public RVoteAdapter(List<Restaurant> restaurants, ListItemClickListener listener) {
        mChoiceData = restaurants;
        mOnClickListener = listener;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.list_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Restaurant restaurant = mChoiceData.get(position);
        holder.choiceItemView.setText(restaurant.getName());

        Switch vSwitch = holder.voteSwitch;
        vSwitch.setTextOn("Yes");
        vSwitch.setTextOff("No");
        vSwitch.setChecked(false);

    }

    @Override
    public int getItemCount() {
        if (null == mChoiceData) return 0;
        return mChoiceData.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView choiceItemView;
        public Switch voteSwitch;

        public ViewHolder(View view){
            super(view);
            choiceItemView = (TextView) view.findViewById(R.id.rv_choice_item);
            voteSwitch = (Switch) view.findViewById(R.id.rv_vote_switch);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }

}

