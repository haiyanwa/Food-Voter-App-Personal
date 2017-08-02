package com.android.summer.csula.foodvoter.polls.models;


import com.android.summer.csula.foodvoter.models.User;
import com.android.summer.csula.foodvoter.yelpApi.models.Coordinate;

import java.util.ArrayList;
import java.util.List;

public class Poll {

    private static final String DEFAULT_PRICE = "1";

    private User author;
    private String pollId;
    private String title;
    private String description;
    private Coordinate coordinate;
    private String zipCode;
    private String price = DEFAULT_PRICE;
    private boolean openNow;

    private boolean completed;

    /**
     * List of User's id. A List of strings will keep Firebase database structure flat and will make
     * it easier to communicate between Fragments and Activities.
     */
    private List<String> voters = new ArrayList<>();

    /**
     * No argument constructor is for Firebase
     */
    public Poll() {}


    public Poll(User user, String pollId) {
        this.author = user;
        this.pollId = pollId;
        voters.add(author.getId());
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
        this.price = price;
    }

    public boolean isOpenNow() {
        return openNow;
    }

    public void setOpenNow(boolean openNow) {
        this.openNow = openNow;
    }

    public String getAuthorId() {
        return author.getId();
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public List<String> getVoters() {
        return voters;
    }

    public void addVoters(String voter) {
        if (!voters.contains(voter)) {
            voters.add(voter);
        }
    }

    public void removeVoters(String voter) {
        voters.remove(voter);
    }

    public String getPollId() {
        return pollId;
    }

    public void setPollId(String pollId) {
        this.pollId = pollId;
    }

    public User getAuthor() {
        return author;
    }

    @Override
    public String toString() {
        return "Poll{" +
                "authorId='" + author.getId() + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", coordinate=" + coordinate +
                ", zipCode='" + zipCode + '\'' +
                ", price='" + price + '\'' +
                ", openNow=" + openNow +
                ", completed=" + completed +
                ", voters=" + voters +
                '}';

    }
}
