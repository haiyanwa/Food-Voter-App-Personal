package com.android.summer.csula.foodvoter.adapters;


import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.summer.csula.foodvoter.R;
import com.android.summer.csula.foodvoter.models.Invitee;
import com.android.summer.csula.foodvoter.models.User;

import java.util.ArrayList;
import java.util.List;

public class FriendsInvitedAdapter extends RecyclerView.Adapter<FriendsInvitedAdapter.ViewHolder> {

    public interface OnInviteListener {
        void onInvite(Invitee user);

        void onUninvite(Invitee user);
    }

    private List<Invitee> friends = new ArrayList<>();
    private OnInviteListener listener;

    public FriendsInvitedAdapter(OnInviteListener listener) {
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean attachToParent = false;
        int itemFriendsInvitesResId = R.layout.item_friends_invites_list;

        View view = inflater.inflate(itemFriendsInvitesResId, parent, attachToParent);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public void clear() {
        friends.clear();
        notifyDataSetChanged();
    }

    public void addFriend(User user) {
        friends.add(new Invitee(user));
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView usernameText;
        private ImageView presenceImage;
        private ImageButton inviteButton;
        private ConstraintLayout constraintLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            usernameText = (TextView) itemView.findViewById(R.id.tv_friend_username);
            presenceImage = (ImageView) itemView.findViewById(R.id.iv_friend_presence);
            inviteButton = (ImageButton) itemView.findViewById(R.id.image_button_invite_friend);
            constraintLayout = (ConstraintLayout) itemView.findViewById(R.id.constraint_layout_item_friend_invitee_root);


            Log.d("xxx", usernameText.getCurrentTextColor() + "");

            inviteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    Invitee invitee = friends.get(position);
                    if (!invitee.isInvited() && invitee.getUser().isOnline()) {
                        constraintLayout.setBackgroundColor(view.getResources().getColor(R.color.colorAccent));
                        usernameText.setTextColor(view.getResources().getColor(android.R.color.primary_text_dark));
                        invitee.setInvited(true);
                        listener.onInvite(invitee);
                    } else if (invitee.isInvited()) {
                        constraintLayout.setBackgroundColor(view.getResources().getColor(R.color.transparent));
                        usernameText.setTextColor(view.getResources().getColor(android.R.color.primary_text_light));
                        invitee.setInvited(false);
                        listener.onUninvite(invitee);
                    } else if (!invitee.getUser().isOnline()) {
                        Toast.makeText(view.getContext(), "Can't invite User who are not log on!", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }


        public void bind(int position) {
            User friend = friends.get(position).getUser();
            usernameText.setText(friend.getUsername());

            if (friend.isOnline()) {
                presenceImage.setImageResource(android.R.drawable.presence_online);
            } else {
                presenceImage.setImageResource(android.R.drawable.presence_offline);
            }
        }
    }
}
