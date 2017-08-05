package com.android.summer.csula.foodvoter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.summer.csula.foodvoter.models.Details;


import com.android.summer.csula.foodvoter.yelpApi.models.Business;
import com.android.summer.csula.foodvoter.yelpApi.models.Yelp;
import com.android.summer.csula.foodvoter.yelpApi.tasks.RequestYelpSearchTask;
import com.android.volley.RequestQueue;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;


public class DetailActivity extends AppCompatActivity {
    TextView mName, mPhone, mAddress, mUrl;
    ImageView mImageURL;

    Details details;
    Business mBusiness;
    String phoneNumber = "(818) 753-4867";
    String restAddress = "1000 Universal Studios Blvd #114, Universal City, CA 91608";
    String restName = "Bubba Gump Shrimp Co.";


    //this is tempoary, will need to find a way to get data for long and lat
    public String longitude = "34.137022";
    public String lattitude = "-118.352266";
    String STATIC_MAP_API_ENDPOINT;
//    public String STATIC_MAP_API_ENDPOINT ="http://maps.google.com/maps/api/staticmap?center="+longitude+","+lattitude+"&zoom=15&size=2000x500&scale=2&sensor=false";
//    static final String STATIC_MAP_API_ENDPOINT = "http://maps.google.com/maps/api/staticmap?center=34.004507,-118.256703&zoom=13&markers=size:mid|color:red|label:E|34.004507,-118.256703&size=1500x300&sensor=false";


//
//    public static Intent newIntent(Context context, Business business) {
//        Intent intent = new Intent(context, DetailActivity.class);
//        intent.putExtra("name", business.getName());
//        intent.putExtra("display_phone", business.getName());
//        intent.putExtra("display_address", business.getName());
//        return intent;
//    }


//    Intent intent = getIntent();
//    Bundle bundle = intent.getExtras();
//    String filter = bundle.getString("filter");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
//        viewModel();
        mName = (TextView) findViewById(R.id.restaurantName);
        mPhone = (TextView) findViewById(R.id.phoneNumber);
        mUrl = (TextView) findViewById(R.id.url);
        mAddress = (TextView) findViewById(R.id.address);


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
//        if(bundle!=null){
            String rest_Name = bundle.getString("name");
            String phone_number = bundle.getString("display_phone");
            String image_url = bundle.getString("image_url");
            String url = bundle.getString("url");
            String address = bundle.getString("display_address");
            longitude = bundle.getString("lon");
            lattitude = bundle.getString("lat");
            STATIC_MAP_API_ENDPOINT ="http://maps.google.com/maps/api/staticmap?center="+longitude+","+lattitude+"&zoom=15&size=2000x500&scale=2&sensor=false";



            mName.setText(rest_Name);
            mPhone.setText(phone_number);
            mUrl.setText(url);
            Linkify.addLinks(mUrl, Linkify.WEB_URLS);
            mAddress.setText(address);








            ImageView mImgURL = (ImageView) findViewById(R.id.imgURL);
            Picasso.with(this)
                    .load(image_url)
                    .into(mImgURL);
            //setting Rest Name for collapsingToolbar
            CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
            collapsingToolbar.setTitle(rest_Name);
            collapsingToolbar.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
            collapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);

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


    }





//method for testing view
    public void viewModel(){
        details = new Details();

        mUrl = (TextView) findViewById(R.id.url);
        mUrl.setText("http://www.yelp.com/biz/yelp-san-francisco");
        Linkify.addLinks(mUrl, Linkify.WEB_URLS);


        mPhone = (TextView) findViewById(R.id.phoneNumber);
        details.setPhoneNumber(phoneNumber);
        mPhone.setText(details.getPhoneNumber());

        mAddress = (TextView) findViewById(R.id.address);
        details.setAddress("140 New Montgomery St Financial District San Francisco, CA 94105");
        mAddress.setText(details.getAddress());


        mName = (TextView) findViewById(R.id.restaurantName);
        details.setRestaurantName(restName);
        mName.setText(details.getRestaurantName());
        mName.setVisibility(View.INVISIBLE);

        mImageURL = (ImageView) findViewById(R.id.imgURL);
        details.setImgURL("http://s3-media3.fl.yelpcdn.com/bphoto/nQK-6_vZMt5n88zsAS94ew/o.jpg");
        Picasso.with(this)
                .load(details.getImgURL())
                .into(mImageURL);

        //setting Rest Name
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(details.getRestaurantName());


        collapsingToolbar.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
    }



}
