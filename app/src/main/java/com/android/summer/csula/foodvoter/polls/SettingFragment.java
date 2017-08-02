package com.android.summer.csula.foodvoter.polls;


import android.Manifest;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.summer.csula.foodvoter.R;
import com.android.summer.csula.foodvoter.polls.models.Poll;
import com.android.summer.csula.foodvoter.yelpApi.models.Coordinate;
import com.android.summer.csula.foodvoter.yelpApi.models.YelpPriceLevel;

import java.util.Arrays;

public class SettingFragment extends Fragment {

    private static final String TAG = SettingFragment.class.getSimpleName();

    private static final String KEY_POLL_ID = "pollKey";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_TITLE = "title";
    private static final String KEY_AUTHOR_ID = "authorId";
    private static final String KEY_ZIP_CODE = "zipCode";
    private static final String KEY_PRICE = "price";
    private static final String KEY_OPEN = "openNow";

    private static final int REQUEST_CODE_LOCATION = 123;

    private Spinner priceSpinner;
    private EditText title;
    private EditText description;
    private EditText zipCode;
    private CheckBox openNow;
    private RadioGroup locationRadioGroup;
    private RadioButton currentLocationRadioButton;
    private RadioButton zipCodeRadioButton;

    private OnPollListener onPollListener;
    private View view;

    private Coordinate coordinate = new Coordinate();


    public static SettingFragment newInstance(Poll poll) {
        SettingFragment fragment = new SettingFragment();
        fragment.setArguments(getPollBundle(poll));
        return fragment;
    }

    private static Bundle getPollBundle(Poll poll) {
        Bundle args = new Bundle();

        args.putString(KEY_AUTHOR_ID, poll.getAuthorId());
        args.putString(KEY_POLL_ID, poll.getPollId());
        args.putString(KEY_TITLE, poll.getTitle());
        args.putString(KEY_DESCRIPTION, poll.getDescription());
        args.putString(KEY_ZIP_CODE, poll.getZipCode());
        args.putString(KEY_PRICE, poll.getPrice());
        args.putBoolean(KEY_OPEN, poll.isOpenNow());

        return args;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.setting_fragment, container, false);
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION);

        initializeUI();
        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode != REQUEST_CODE_LOCATION) {
            return;
        }

        Log.d(TAG, "requestCode: " + requestCode +
                ", permission: " + Arrays.toString(permissions) +
                ", grantedResults: " + Arrays.toString(grantResults));

        try {
            LocationManager lm = (LocationManager) view.getContext().getSystemService(Context.LOCATION_SERVICE);
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener());

            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (location == null) {
                currentLocationRadioButton.setEnabled(false);
                zipCodeRadioButton.setChecked(true);
                Toast.makeText(view.getContext(), "I can't  access your location. Please enter it manually", Toast.LENGTH_LONG).show();
            }

        } catch (SecurityException e) {
            Log.e(TAG, "Location Failed!", e);
            currentLocationRadioButton.setEnabled(false);
        }
    }


    private void initializeUI() {
        setupTitle();
        setupDescription();
        setupZipCode();
        setupCheckbox();
        setupSpinner();
        setupRadioGroups();
        setupRadioButtons();
    }

    private void setupRadioButtons() {
        currentLocationRadioButton = (RadioButton) view.findViewById(R.id.radio_button_current_location);
        zipCodeRadioButton = (RadioButton) view.findViewById(R.id.radio_button_use_zip_code);
    }

    private void setupCheckbox() {
        openNow = (CheckBox) view.findViewById(R.id.checkbox_open_now);
        openNow.setOnCheckedChangeListener(checkBoxChangeListener());

        boolean pollOpenNow = getArguments().getBoolean(KEY_OPEN, false);
        openNow.setChecked(pollOpenNow);
    }

    private void setupZipCode() {
        zipCode = (EditText) view.findViewById(R.id.edit_text_zip_code);
        zipCode.addTextChangedListener(zipCodeTextWatcher());

        String pollZipCode = getArguments().getString(KEY_ZIP_CODE);
        zipCode.setText(pollZipCode);
    }

    private void setupDescription() {
        description = (EditText) view.findViewById(R.id.edit_text_description);
        description.addTextChangedListener(descriptionTextWatcher());

        String pollDescription = getArguments().getString(KEY_DESCRIPTION);
        description.setText(pollDescription);
    }

    private void setupTitle() {
        title = (EditText) view.findViewById(R.id.edit_text_title);
        title.addTextChangedListener(titleTextWatcher());

        String pollTitle = getArguments().getString(KEY_TITLE);
        title.setText(pollTitle);
    }

    private void setupRadioGroups() {
        locationRadioGroup = (RadioGroup) view.findViewById(R.id.radioGroup);
        locationRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if (R.id.radio_button_use_zip_code == i) {
                    zipCode.setEnabled(true);
                } else if (R.id.radio_button_current_location == i) {
                    zipCode.getText().clear();
                    zipCode.setEnabled(false);
                }
            }
        });
    }

    private TextWatcher zipCodeTextWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                onPollListener.onZipCodeChange(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        };
    }


    private void setupSpinner() {
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

        priceSpinner.setOnItemSelectedListener(priceSpinnerListener());

        String pollPrice = getArguments().getString(KEY_PRICE);
        String yelpPollPrice = YelpPriceLevel.toYelpString(pollPrice);
        int yelpPollPricePosition = getSpinnerPosition(yelpPollPrice);

        priceSpinner.setSelection(yelpPollPricePosition);

    }

    private int getSpinnerPosition(String yelpPriceLevel) {
        for (int i = 0; i < priceSpinner.getCount(); i++) {
            String item = (String) priceSpinner.getItemAtPosition(i);
            if (yelpPriceLevel != null && yelpPriceLevel.equals(item)) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onPollListener = (OnPollListener) context;
    }

    private TextWatcher titleTextWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                onPollListener.onTitleChange(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        };
    }

    private TextWatcher descriptionTextWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                onPollListener.onDescriptionChange(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        };
    }


    private CompoundButton.OnCheckedChangeListener checkBoxChangeListener() {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                onPollListener.onOpenNowChange(b);
            }
        };
    }

    private AdapterView.OnItemSelectedListener priceSpinnerListener() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String value = adapterView.getItemAtPosition(i).toString();
                onPollListener.onPriceChange(value);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        };
    }

    private LocationListener locationListener() {
        return new LocationListener() {
            public void onLocationChanged(Location location) {
                Log.d(TAG, "Coordinate Changed: " + coordinate.toString());

                coordinate.setLatitude(location.getLongitude());
                coordinate.setLongitude(location.getLongitude());

                onPollListener.onCoordinateChange(coordinate);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) { }

            @Override
            public void onProviderEnabled(String s) { }

            @Override
            public void onProviderDisabled(String s) { }

        };
    }

    public interface OnPollListener {
        void onTitleChange(String title);

        void onDescriptionChange(String description);

        void onOpenNowChange(boolean openNow);

        void onPriceChange(String price);

        void onZipCodeChange(String zipCode);

        void onCoordinateChange(Coordinate coordinate);
    }
}
