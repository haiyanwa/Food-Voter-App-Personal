package com.android.summer.csula.foodvoter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.summer.csula.foodvoter.models.Details;


import com.android.summer.csula.foodvoter.yelpApi.models.Business;
import com.android.summer.csula.foodvoter.yelpApi.models.Category;
import com.android.summer.csula.foodvoter.yelpApi.models.Coordinate;
import com.android.summer.csula.foodvoter.yelpApi.models.Location;
import com.android.summer.csula.foodvoter.yelpApi.models.Yelp;
import com.android.summer.csula.foodvoter.yelpApi.tasks.RequestYelpSearchTask;
import com.android.volley.RequestQueue;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;


public class DetailActivity extends AppCompatActivity {

    private static final String TAG = "DetailActivity";
    //this is tempoary, will need to find a way to get data for long and lat
    static final String longitude = "34.137022";
    static final String lattitude = "-118.352266";
    static final String STATIC_MAP_API_ENDPOINT ="http://maps.google.com/maps/api/staticmap?center="+longitude+","+lattitude+"&zoom=15&size=2000x500&scale=2&sensor=false";
//    static final String STATIC_MAP_API_ENDPOINT = "http://maps.google.com/maps/api/staticmap?center=34.004507,-118.256703&zoom=13&markers=size:mid|color:red|label:E|34.004507,-118.256703&size=1500x300&sensor=false";

    TextView mName;
//    TextView phone;
//    TextView address;
//    Details details;
//    String phoneNumber = "(818) 753-4867";
//    String restAddress = "1000 Universal Studios Blvd #114, Universal City, CA 91608";
    String restName = "Bubba Gump Shrimp Co.";



//---------------------------------------------------------------------------------------------------

    String businessImage;
    String businessName;
    List<String> address;
    List<Category> categories;
    Double rating;
//---------------------------------------------------------------------------------------------------

    public Business mBusiness;
    public TextView mBusinessName;

//-------------------------------------------------------------------------------------------------

    class YelpSearch extends AsyncTask<String, String, String>{
        @Override
        protected String doInBackground(String... parrams){
            RequestYelpSearchTask.SearchBuilder searchBuilder = new RequestYelpSearchTask.SearchBuilder();
            searchBuilder.location("91208");
            try {
//                ArrayList<Business> businesses;
                Yelp yelp = RequestYelpSearchTask.execute(searchBuilder.build());
                businessImage = yelp.getBusinesses().get(0).getImageUrl();
                businessName = yelp.getBusinesses().get(0).getName();
                address = yelp.getBusinesses().get(0).getLocation().getDisplayAddress();
                categories = yelp.getBusinesses().get(0).getCategories();
                rating = yelp.getBusinesses().get(0).getRating();
//                name = (TextView) findViewById(R.id.restaurantName);
//        details.setRestaurantName(restName);
//        name.setText(details.getRestaurantName());

                Log.d("strings",  businessName + "");
                Log.d("strings",  address + "");
                Log.d("strings",  businessImage + "");
                Log.d("strings",  categories + "");
                Log.d("strings",  rating + "");
                Log.d("strings",  yelp.getBusinesses().size() + "");
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("xxx", "failled", e);
            }
            return null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Log.d(TAG, "onCreate in DetailActivity");
//        viewModel();
//        intentGetter();
//        referenceViews();
        new YelpSearch().execute();


        mBusinessName = (TextView) findViewById(R.id.name);

//        RequestYelpSearchTask.SearchBuilder searchBuilder = new RequestYelpSearchTask.SearchBuilder();
//        searchBuilder.location("91208");
//        try {
//            Yelp yelp = RequestYelpSearchTask.execute(searchBuilder.build());
//            Log.d("xxx",  yelp.getBusinesses().size() + "");
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.e("xxx", "failled", e);
//        }


        AsyncTask<Void, Void, Bitmap> setImageFromUrl = new AsyncTask<Void, Void, Bitmap>(){
            @Override
            protected Bitmap doInBackground(Void... params) {
                Bitmap bmp = null;
                HttpClient httpclient = new DefaultHttpClient();
                HttpGet request = new HttpGet(STATIC_MAP_API_ENDPOINT);
                InputStream in = null;
                try {
                    in = httpclient.execute(request).getEntity().getContent();
                    bmp = BitmapFactory.decodeStream(in);
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return bmp;
            }
            protected void onPostExecute(Bitmap bmp) {
                if (bmp!=null) {
                    final ImageView iv = (ImageView) findViewById(R.id.img);
                    iv.setImageBitmap(bmp);
                }
            }
        };
        setImageFromUrl.execute();


        System.out.println("business Name: ---" + businessName);


        bind(mBusiness);





        mName = (TextView) findViewById(R.id.name);
//        details.setRestaurantName(restName);
        mName.setText(restName);





    }

//    public void bindDataToView(Business business) {
//        mBusinessName.setText(business.getName());
//        mPricing.setText(business.getPrice());
//        mPhoneNumber.setText(business.getPhone());
//
//        Location location = business.getLocation();
//        mAddress.setText(location.getAddress1() + ", " + location.getCity());
//        mOpenOrClosed.setText((business.getIsClosed()) ? "Closed" : "Open");
//
//
//        // Picasso method to get images for each business
//        Picasso.with(this)
//                .load(mBusiness.getImageUrl())
//                .into(mBusinessImage);
//    }

    public void bind(Business business){
        mBusinessName.setText(business.getName());
    }


    public static Intent newIntent(Context context, Business business) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra("name", business.getName());
//        intent.putExtra("categories", business.Category.getCategories)

        Coordinate coordinates = business.getCoordinate();
        intent.putExtra("latitude", coordinates.getLatitude());
        intent.putExtra("longitutde", coordinates.getLongitude());

        intent.putExtra("display_phone", business.getDisplayPhone());
        intent.putExtra("id", business.getId());
        intent.putExtra("image_url", business.getImageUrl());

//        Location location =  business.getLocation();
//        intent.putExtra


        intent.putExtra("price", business.getPrice());             // NOT SURE YET
        intent.putExtra("rating", business.getRating());
        intent.putExtra("reviewCount", business.getReviewCount());
        intent.putExtra("url", business.getUrl());

        return intent;
    }

    public void intentGetter(){
        String name = getIntent().getStringExtra("name");
        TextView mName = (TextView) findViewById(R.id.name);
        mName.setText(name);
    }

    public void bindBusiness(Business business) {
        mBusinessName.setText(business.getName());
//        mPricing.setText(business.getPrice());
//        mPhoneNumber.setText(business.getPhone());
//
//        Location location = business.getLocation();
//        mAddress.setText(location.getAddress1() + ", " + location.getCity());
//        mOpenOrClosed.setText((business.getIsClosed()) ? "Closed" : "Open");
//
//        mapSetup();
//        reviewStars();
//
//        // Picasso method to get images for each business
//        Picasso.with(this)
//                .load(mBusiness.getImageUrl())
//                .into(mBusinessImage);
    }




//to test data

//    public void viewModel(){
//
//        details = new Details();
//        phone = (TextView) findViewById(R.id.phoneNumber);
//        details.setPhoneNumber(phoneNumber);
//        phone.setText(details.getPhoneNumber());
//
//        address = (TextView) findViewById(R.id.address);
//        details.setAddress(restAddress);
//        address.setText(details.getAddress());
//
//        name = (TextView) findViewById(R.id.restaurantName);
//        details.setRestaurantName(restName);
//        name.setText(details.getRestaurantName());
//
//        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
//        collapsingToolbar.setTitle(details.getRestaurantName());
//
//
//        collapsingToolbar.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
//
//        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
//
//    }




}
