package com.android.summer.csula.foodvoter.yelpApi.tasks;


import android.net.Uri;
import android.support.annotation.Nullable;

import com.android.summer.csula.foodvoter.yelpApi.models.Yelp;
import com.android.summer.csula.foodvoter.yelpApi.models.YelpAccessToken;
import com.android.summer.csula.foodvoter.yelpApi.utils.NetworkUtils;
import com.android.summer.csula.foodvoter.yelpApi.utils.YelpJsonUtil;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

public class RequestYelpSearchTask {

    public static Yelp execute(URL searchUrl) {
        Yelp yelp = null;

        try {
            YelpAccessToken token = RequestAccessTokenTask.execute();
            String jsonResponse = NetworkUtils.getJsonResponseFromHttpUrl(
                searchUrl, NetworkUtils.GET_REQUEST, token.authorizationHttpHeader());
            yelp = YelpJsonUtil.parse(jsonResponse);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return yelp;
    }

    public static class SearchBuilder {

        private static final String YELP_SEARCH_BASE_URL = "https://api.yelp.com/v3/businesses/search";

        private static final String LOCATION_PARAM = "location";
        private static final String LATITUDE_PARAM = "latitude";
        private static final String LONGITUDE_PARAM = "longitude";

        private static final String LIMIT_PARAM = "limit";
        private static final String TERM_PARAM = "term";


        private static final int MAX_LIMIT = 50;

        /* required if lat and long not provided */
        private String location;

        /* required if location is not provided */
        private String latitude;
        private String longitude;
        private int limit = MAX_LIMIT;
        private String term = "food";


        public URL build() throws Exception {
            Uri.Builder builder = Uri.parse(YELP_SEARCH_BASE_URL).buildUpon();
            builder.appendQueryParameter(LIMIT_PARAM, Integer.toString(limit));
            builder.appendQueryParameter(TERM_PARAM, term);

            if (location != null) {
                builder.appendQueryParameter(LOCATION_PARAM, location);
            } else if (latitude != null & longitude != null) {
                builder.appendQueryParameter(LATITUDE_PARAM, latitude);
                builder.appendQueryParameter(LONGITUDE_PARAM, longitude);
            } else {
                throw new Exception("Location(zipCode) or Longitude and Latitude is required!");
            }

            Uri uri = builder.build();
            return new URL(uri.toString());
        }

        public SearchBuilder location(String zipCodeLocation) {
            location = zipCodeLocation;
            return this;
        }

        public SearchBuilder latitude(String latitude) {
            this.latitude = latitude;
            return this;
        }

        public SearchBuilder longitude(String longitude) {
            this.longitude = longitude;
            return this;
        }

    }

}
