package com.android.summer.csula.foodvoter.yelpApi.models;


/**
 * Yelp Price Values for Business
 */
public enum Price {

    $(1), $$(2), $$$(3), $$$$(4);

    public int value;

    Price(int value) {
        this.value = value;
    }
}
