package com.android.summer.csula.foodvoter.polls.models;


import com.android.summer.csula.foodvoter.models.User;
import com.android.summer.csula.foodvoter.yelpApi.models.Coordinate;
import com.android.summer.csula.foodvoter.yelpApi.models.YelpPriceLevel;

import java.util.ArrayList;
import java.util.List;

public class Poll {


    private static final String DEFAULT_PRICE = "1";

    private String authorId;
    private String pollId;

    private String title;
    private String description;
    private Coordinate coordinate;
    private String zipCode;
    private String price = DEFAULT_PRICE;
    private boolean openNow;

    private boolean started;
    private boolean completed;
    private List<User> voters = new ArrayList<>();

    /**
     * No argument constructor is for Firebase
     */
    public Poll() {}


    public Poll(String authorId, String pollId) {
        this.authorId = authorId;
        this.pollId = pollId;
        voters.add(new User("DEMO", "FAKE"));
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = YelpPriceLevel.fromYelpString(price);
    }

    public boolean isOpenNow() {
        return openNow;
    }

    public void setOpenNow(boolean openNow) {
        this.openNow = openNow;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public List<User> getVoters() {
        return voters;
    }

    public void setVoters(List<User> voters) {
        this.voters = voters;
    }

    public void addVoters(User voter) {
        if (!voters.contains(voter)) {
            voters.add(voter);
        }
    }

    public void removeVoters(User voter) {
        voters.remove(voter);
    }

    public String getPollId() {
        return pollId;
    }

    public void setPollId(String pollId) {
        this.pollId = pollId;
    }

    @Override
    public String toString() {
        return "Poll{" +
                "authorId='" + authorId + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", coordinate=" + coordinate +
                ", zipCode='" + zipCode + '\'' +
                ", price='" + price + '\'' +
                ", openNow=" + openNow +
                ", started=" + started +
                ", completed=" + completed +
                ", voters=" + voters +
                '}';
    }
}
