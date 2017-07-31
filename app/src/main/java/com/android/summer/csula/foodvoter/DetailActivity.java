package com.android.summer.csula.foodvoter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.android.summer.csula.foodvoter.models.Details;
import com.android.summer.csula.foodvoter.data.RestaurantDataTest_DetailsActivity;


import com.android.summer.csula.foodvoter.R;

import java.io.InputStream;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;




public class DetailActivity extends AppCompatActivity {

    //this is tempoary, will need to find a way to get data for long and lat
    static final String longitude = "34.137022";
    static final String lattitude = "-118.352266";
    static final String STATIC_MAP_API_ENDPOINT ="http://maps.google.com/maps/api/staticmap?center="+longitude+","+lattitude+"&zoom=15&size=2000x500&scale=2&sensor=false";
//    static final String STATIC_MAP_API_ENDPOINT = "http://maps.google.com/maps/api/staticmap?center=34.004507,-118.256703&zoom=13&markers=size:mid|color:red|label:E|34.004507,-118.256703&size=1500x300&sensor=false";

    
    TextView name;
    TextView phone;
    TextView address;
    Details details;
    String phoneNumber = "(818) 753-4867";
    String restAddress = "1000 Universal Studios Blvd #114, Universal City, CA 91608";
    String restName = "Bubba Gump Shrimp Co.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        viewModel();

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

        phone = (TextView) findViewById(R.id.phoneNumber);
        details.setPhoneNumber(phoneNumber);
        phone.setText(details.getPhoneNumber());

        address = (TextView) findViewById(R.id.address);
        details.setAddress(restAddress);
        address.setText(details.getAddress());

        name = (TextView) findViewById(R.id.restaurantName);
        details.setRestaurantName(restName);
        name.setText(details.getRestaurantName());

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(details.getRestaurantName());


        collapsingToolbar.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);

        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);


    }




}
