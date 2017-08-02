package com.android.summer.csula.foodvoter.polls;


import com.android.summer.csula.foodvoter.yelpApi.models.Coordinate;

/**
 * This interface is use to for Fragment To Activity communication. Anytime a poll value is
 * updated in a Fragment, the Activity would be notify and it would handle it
 */

public interface OnPollListener {
    void onTitleChange(String title);

    void onDescriptionChange(String description);

    void onOpenNowChange(boolean openNow);

    void onPriceChange(String price);

    void onZipCodeChange(String zipCode);

    void onCoordinateChange(Coordinate coordinate);
}
