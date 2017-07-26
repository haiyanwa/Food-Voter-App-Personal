package com.android.summer.csula.foodvoter.models;


import java.util.ArrayList;
import java.util.List;

public class FriendList {

    private String hostId;
    private List<User> friends;


    public FriendList(String hostId){
        this.hostId = hostId;
        this.friends = new ArrayList<>();
    }

    public String getHostId() {
        return  hostId ;
    }

    public void setHost(String hostId) {
        this.hostId= hostId ;
    }

    public void setFriends (List<User> friends) {
        this.friends = friends;
    }

    public List<User> getFriends() {
        return friends;
    }

    public void addFriend(User user) {
        friends.add(user);
    }
}
