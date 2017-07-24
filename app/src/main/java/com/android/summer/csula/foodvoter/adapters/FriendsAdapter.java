package com.android.summer.csula.foodvoter.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.summer.csula.foodvoter.R;
import com.android.summer.csula.foodvoter.models.User;

import java.util.ArrayList;
import java.util.List;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {

    private static final String TAG =  FriendsAdapter.class.getSimpleName();

    private List<User> friends = new ArrayList<>();
    private FriendsAdapterListener listener;

    public FriendsAdapter(FriendsAdapterListener listener) {
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean attachToParent = false;
        int friendItemResId = R.layout.friends_item_list;

        View view = inflater.inflate(friendItemResId, parent, attachToParent);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return friends == null ? 0 : friends.size();
    }

    public void clear() {
        friends.clear();
        notifyDataSetChanged();
    }

    public void setFriends(List<User> friends) {
        this.friends = friends;
        notifyDataSetChanged();
    }

   public void addFriend(User friend) {
       friends.add(friend);
       notifyDataSetChanged();
   }

    /* Check if the input user is a "friend" and it is update its online status */
    public void updateFriend(User updatedUser) {
        for (User friend : friends) {
            if (friend.equals(updatedUser)) {
                friend.setOnline(updatedUser.isOnline());
                notifyDataSetChanged();
                break;
            }
        }
    }

    public void removeFriend(int position) {
        friends.remove(position);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView friendUsernameTextView;
        private ImageView friendPresenceImageView;
        private ImageButton unfriendButton;

        public ViewHolder(View itemView) {
            super(itemView);
            friendUsernameTextView = (TextView) itemView.findViewById(R.id.tv_friend_username);
            friendPresenceImageView = (ImageView) itemView.findViewById(R.id.iv_friend_presence);
            unfriendButton = (ImageButton) itemView.findViewById(R.id.btn_un_friend);
            unfriendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int index = getAdapterPosition();
                    User friend = friends.get(index);
                    Log.d(TAG, "unfriend: " + friend.toString());

                    listener.unfriend(friend);
                    removeFriend(index);
                }
            });
        }

        public void bind(int position) {
            User friend = friends.get(position);
            friendUsernameTextView.setText(friend.getUsername());

            if (friend.isOnline()) {
                friendPresenceImageView.setImageResource(android.R.drawable.presence_online);
            } else {
                friendPresenceImageView.setImageResource(android.R.drawable.presence_offline);
            }
        }
    }

    public interface FriendsAdapterListener {
       void unfriend(User user);
    }
}
