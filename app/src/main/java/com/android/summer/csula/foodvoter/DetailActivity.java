package com.android.summer.csula.foodvoter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.android.summer.csula.foodvoter.models.Details;
import com.android.summer.csula.foodvoter.data.RestaurantDataTest_DetailsActivity;


import com.android.summer.csula.foodvoter.R;


public class DetailActivity extends AppCompatActivity {

//    Button btnSetText;
//    TextView tvText;
    TextView name;
    TextView phone;
    TextView address;
    Details details;
    String phoneNumber = "323-233-1444";
    String restAddress = "4351 S Central Ave, Los Angeles, CA 90011";
    String restName = "Pizza Hut";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        viewModel();

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


//        tvText = (TextView) findViewById(R.id.tvText);
//        details.setMessage("View updated by model");
//        tvText.setText(details.getMessage());
    }




}
