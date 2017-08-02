package com.android.summer.csula.foodvoter;

import android.util.Log;

import com.android.summer.csula.foodvoter.yelpApi.models.Yelp;
import com.android.summer.csula.foodvoter.yelpApi.tasks.RequestYelpSearchTask;

import java.net.URL;

/**
 * Created by Haiyan on 8/1/17.
 */

public class DataRetriever {
    private final static String TAG = "DataRetriever";


    public static Yelp getRestaurants(String latitude, String longtitude) throws Exception{

        URL url = null;
        try{
            url = new RequestYelpSearchTask.SearchBuilder().coordinates(latitude,longtitude).build();
            Log.d(TAG, "URL: " + url);

            Yelp yelp = RequestYelpSearchTask.execute(url);

            if(yelp.getTotal() > 0){

                //ArrayList<Business> businesses = (ArrayList) yelp.getBusinesses();
                Log.d(TAG, "Number of business data retrieved " + yelp.getTotal());
                return yelp;
            }else{
                Log.d(TAG, "Error: failed to retrieve business data");
                return null;
            }
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }

    }




}
