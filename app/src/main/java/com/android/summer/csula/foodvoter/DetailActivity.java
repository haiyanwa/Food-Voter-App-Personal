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
    TextView name, phone, address, url;
    Details details;

    String phoneNumber = "(818) 753-4867";
    String restAddress = "1000 Universal Studios Blvd #114, Universal City, CA 91608";
    String restName = "Bubba Gump Shrimp Co.";

    ImageView imgURL;

    //this is tempoary, will need to find a way to get data for long and lat
    static final String longitude = "34.137022";
    static final String lattitude = "-118.352266";
    static final String STATIC_MAP_API_ENDPOINT ="http://maps.google.com/maps/api/staticmap?center="+longitude+","+lattitude+"&zoom=15&size=2000x500&scale=2&sensor=false";
//    static final String STATIC_MAP_API_ENDPOINT = "http://maps.google.com/maps/api/staticmap?center=34.004507,-118.256703&zoom=13&markers=size:mid|color:red|label:E|34.004507,-118.256703&size=1500x300&sensor=false";



    public static Intent newIntent(Context context, Business business) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra("name", business.getName());
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        viewModel();

        String name = getIntent().getStringExtra("name");







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




    public void viewModel(){
        details = new Details();

        TextView myClickableUrl = (TextView) findViewById(R.id.url);
        myClickableUrl.setText("http://www.yelp.com/biz/yelp-san-francisco");
        Linkify.addLinks(myClickableUrl, Linkify.WEB_URLS);


        phone = (TextView) findViewById(R.id.phoneNumber);
        details.setPhoneNumber(phoneNumber);
        phone.setText(details.getPhoneNumber());

        address = (TextView) findViewById(R.id.address);
        details.setAddress("140 New Montgomery St Financial District San Francisco, CA 94105");
        address.setText(details.getAddress());


        name = (TextView) findViewById(R.id.restaurantName);
        details.setRestaurantName(restName);
        name.setText(details.getRestaurantName());
        name.setVisibility(View.INVISIBLE);

        imgURL = (ImageView) findViewById(R.id.imgURL);
        details.setImgURL("http://s3-media3.fl.yelpcdn.com/bphoto/nQK-6_vZMt5n88zsAS94ew/o.jpg");
        Picasso.with(this)
                .load(details.getImgURL())
                .into(imgURL);

        //setting Rest Name
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(details.getRestaurantName());


        collapsingToolbar.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
    }
}
