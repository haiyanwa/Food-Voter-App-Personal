package com.android.summer.csula.foodvoter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class PollActivity extends AppCompatActivity {

    private Spinner priceSpinner;

    public static Intent newIntent(Context context) {
        return new Intent(context, PollActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll);

        initSpinner();
    }

    private void initSpinner() {
        priceSpinner = (Spinner) findViewById(R.id.spinners_price);

        /* From Android Spinner documentation:
         * https://developer.android.com/guide/topics/ui/controls/spinner.html */

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
            this, R.array.price_array, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        priceSpinner.setAdapter(adapter);
    }
}
