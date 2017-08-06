package com.android.summer.csula.foodvoter.models;

import com.android.summer.csula.foodvoter.yelpApi.models.Business;

/**
 * Created by Haiyan on 8/5/17.
 */

public class UserVote {
    private String userId;
    private Business business;

    public UserVote(String id, Business business){
        this.userId = id;
        this.business = business;
    }

    public String getUserId() {
        return userId;
    }

    public Business getBusiness() {
        return business;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setBusiness(Business business) {
        this.business = business;
    }
}
