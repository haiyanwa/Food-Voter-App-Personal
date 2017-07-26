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

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    private static final String TAG = UsersAdapter.class.getSimpleName();

    private List<User> users = new ArrayList<>();
    private UserOnClickHandler userOnClickHandler;

    public UsersAdapter(UserOnClickHandler userOnClickHandler) {
        this.userOnClickHandler = userOnClickHandler;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        int itemResId = R.layout.user_item_list;
        boolean attachToParent = false;

        View view = inflater.inflate(itemResId, parent, attachToParent);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void add(User user) {
        users.add(user);
        notifyDataSetChanged();
    }

    public void clear() {
        users.clear();
        notifyDataSetChanged();
    }

    /* Update is use primarily to update the online status of the users.
    * It is the only field that can change */
    public void update(User user) {
        for (User entry : users) {
            if (entry.equals(user)) {
                entry.setOnline(user.isOnline());
                notifyDataSetChanged();
                break;
            }
        }
    }
    public interface UserOnClickHandler {
        void onImageButtonClick(User clickedUser);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView usernameTextView;
        private ImageButton addUserToFriendButton;
        private ImageView userPresenceImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            usernameTextView = (TextView) itemView.findViewById(R.id.tv_username);
            userPresenceImageView = (ImageView) itemView.findViewById(R.id.iv_user_presence);
            addUserToFriendButton = (ImageButton) itemView.findViewById(R.id.btn_user_to_friend);
            addUserToFriendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    User clickedUser = users.get(getAdapterPosition());
                    userOnClickHandler.onImageButtonClick(clickedUser);
                }
            });
        }

        public void bind(int position) {
            User user = users.get(position);
            Log.d(TAG, "binding user: " + user.toString());

            usernameTextView.setText(user.getUsername());

            if (user.isOnline()) {
                userPresenceImageView.setImageResource(android.R.drawable.presence_online);
            } else {
                userPresenceImageView.setImageResource(android.R.drawable.presence_offline);
            }
        }
    }
}
