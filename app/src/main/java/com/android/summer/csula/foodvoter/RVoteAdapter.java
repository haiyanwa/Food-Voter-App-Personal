package com.android.summer.csula.foodvoter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Haiyan on 7/19/17.
 */

public class RVoteAdapter extends RecyclerView.Adapter<RVoteAdapter.ViewHolder>{

    final private ListItemClickListener mOnClickListener;

    private SwitchListener switchListener;

    private static int viewHolderCount;

    private int mNumberItems;

    private List<Restaurant> mChoiceData;

    private final int ListItem = 0;
    private final int EndOfList= 1;

    private String TAG = "RVoteAdapter";

    public interface ListItemClickListener{
        void onListItemClick(int clickedItemIndex);
    }

    public interface SwitchListener{
        void onSwitchSwiped(int swipedItemIndex, boolean swiped);
    }


    public RVoteAdapter(List<Restaurant> restaurants, ListItemClickListener listener, SwitchListener swListener) {
        mChoiceData = restaurants;
        mOnClickListener = listener;
        switchListener = swListener;

    }

    @Override
    public int getItemViewType(int position) {
        return (position == mChoiceData.size()) ? EndOfList : ListItem;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        if(viewType == EndOfList){
            view = inflater.inflate(R.layout.vote_submit_btn, parent, false);
        }else{
            view = inflater.inflate(R.layout.list_item, parent, false);
        }
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(holder, position);
    }

    @Override
    public int getItemCount() {
        if (null == mChoiceData) return 0;
        return mChoiceData.size() + 1;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView choiceItemView;
        public Switch voteSwitch;
        public Button voteButton;
        int index;

        public ViewHolder(View view){
            super(view);
            choiceItemView = (TextView) view.findViewById(R.id.rv_choice_item);
            voteSwitch = (Switch) view.findViewById(R.id.rv_vote_switch);
            voteButton = (Button) view.findViewById(R.id.rv_vote_btn);
            view.setOnClickListener(this);
        }

        public void bind(ViewHolder holder, int position){
            if(position < mChoiceData.size()){
                Restaurant restaurant = mChoiceData.get(position);
                choiceItemView.setText(restaurant.getName());

                voteSwitch.setTextOn("Yes");
                voteSwitch.setTextOff("No");
                voteSwitch.setChecked(false);

                index = position;

                holder.voteSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean swiped) {
                        switchListener.onSwitchSwiped(index, swiped);
                    }
                });
            }
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            mOnClickListener.onListItemClick(pos);
        }
    }

}

