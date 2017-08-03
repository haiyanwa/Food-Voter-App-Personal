package com.android.summer.csula.foodvoter.polls;


import android.content.Intent;
import android.util.Log;

import com.android.summer.csula.foodvoter.polls.models.Poll;
import com.android.summer.csula.foodvoter.yelpApi.models.Coordinate;

import java.net.URL;

import static com.android.summer.csula.foodvoter.yelpApi.tasks.RequestYelpSearchTask.*;

public class PollUtilities {

    /**
     * Build the Yelp Search URL based on the input Poll object.
     */
    public static URL toURL(Poll poll) throws Exception {
        Coordinate coordinate = poll.getCoordinate();
        String zipCode = poll.getZipCode();

        SearchBuilder builder = new SearchBuilder()
                .openNow(poll.isOpenNow())
                .price(poll.getPrice());

        if (coordinate != null) {
            builder
                    .latitude(String.valueOf(coordinate.getLatitude()))
                    .longitude(String.valueOf(coordinate.getLongitude()));
        } else {
            builder.location(zipCode);
        }
        return builder.build();
    }
}
