package com.android.summer.csula.foodvoter.poll;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.android.summer.csula.foodvoter.R;

public class SettingFragment extends Fragment {

    private Spinner priceSpinner;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.setting_fragment, container, false);
        initSpinner(view);

        return view;
    }

    private void initSpinner(View view) {
        priceSpinner = (Spinner) view.findViewById(R.id.spinners_price);

        /* From Android Spinner documentation:
         * https://developer.android.com/guide/topics/ui/controls/spinner.html */

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                view.getContext(), R.array.price_array, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        priceSpinner.setAdapter(adapter);
    }
}
