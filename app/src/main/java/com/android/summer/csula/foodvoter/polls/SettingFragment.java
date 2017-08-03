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
    private EditText zipCode;
    private RadioButton currentLocationRadioButton;
    private RadioButton zipCodeRadioButton;
    private View view;

    private OnPollSettingsListener onPollSettingsListener;
    private Coordinate coordinate;

    public SettingFragment() {}


    public static SettingFragment newInstance(Poll poll) {
        SettingFragment fragment = new SettingFragment();
        fragment.setArguments(buildPollBundle(poll));
        return fragment;
    }

    private static Bundle buildPollBundle(Poll poll) {
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

        // We need permission to obtain the users current coordinates,
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


        // Check if we can obtain the user's current coordinate
        // if we don't have permission or if can't obtain permission,
        // we will ask the user to manually enter their location instead
        try {
            LocationManager lm = (LocationManager) view.getContext().getSystemService(Context.LOCATION_SERVICE);

            // We get the last know location, if the users moved, locationListener() will provided us
            // with the latest coordinates.
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener());
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (location == null) {
                currentLocationRadioButton.setEnabled(false);
                zipCodeRadioButton.setChecked(true);
                Toast.makeText(view.getContext(), "I can't  access your location. Please enter it manually", Toast.LENGTH_LONG).show();
                Log.d(TAG, "onRequestPermissionsResult() - Location Object is null.");
            } else {
                Log.d(TAG, "onRequestPermissionsResult() - " + location.getLatitude() + " " + location.getLongitude());
                coordinate = new Coordinate();
                coordinate.setLatitude(location.getLatitude());
                coordinate.setLongitude(location.getLongitude());

                onPollSettingsListener.onCoordinateChange(coordinate);
            }

        } catch (SecurityException e) {
            Log.e(TAG, "Location Failed!", e);
            currentLocationRadioButton.setEnabled(false);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // Activity MUST implement this interface because it is used to communicated between
        // this fragment and the host activity.
        try {
            onPollSettingsListener = (OnPollSettingsListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("context must implement OnPollListener");
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
        CheckBox openNow = (CheckBox) view.findViewById(R.id.checkbox_open_now);
        boolean pollOpenNow = getArguments().getBoolean(KEY_OPEN, false);
        openNow.setChecked(pollOpenNow);

        openNow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                onPollSettingsListener.onOpenNowChange(b);
            }
        });
    }

    private void setupZipCode() {
        zipCode = (EditText) view.findViewById(R.id.edit_text_zip_code);
        String pollZipCode = getArguments().getString(KEY_ZIP_CODE);
        zipCode.setText(pollZipCode);

        zipCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                onPollSettingsListener.onZipCodeChange(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });
    }

    private void setupDescription() {
        EditText description = (EditText) view.findViewById(R.id.edit_text_description);
        String pollDescription = getArguments().getString(KEY_DESCRIPTION);
        description.setText(pollDescription);

        description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                onPollSettingsListener.onDescriptionChange(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });
    }

    private void setupTitle() {
        EditText title = (EditText) view.findViewById(R.id.edit_text_title);
        String pollTitle = getArguments().getString(KEY_TITLE);
        title.setText(pollTitle);

        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                onPollSettingsListener.onTitleChange(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });
    }

    private void setupRadioGroups() {
        RadioGroup locationRadioGroup = (RadioGroup) view.findViewById(R.id.radioGroup);
        locationRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if (R.id.radio_button_use_zip_code == i) {
                    zipCode.setEnabled(true);
                } else if (R.id.radio_button_current_location == i) {
                    zipCode.getText().clear();
                    zipCode.setEnabled(false);
                    if (coordinate != null) {
                        onPollSettingsListener.onCoordinateChange(coordinate);
                    }
                }
            }
        });
    }

    private void setupSpinner() {
        priceSpinner = (Spinner) view.findViewById(R.id.spinners_price);

        /* From https://developer.android.com/guide/topics/ui/controls/spinner.html */

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
            view.getContext(), R.array.price_array, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        priceSpinner.setAdapter(adapter);

        // Update the spinner position
        setSpinnerPosition();

        priceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String value = adapterView.getItemAtPosition(i).toString();
                onPollSettingsListener.onPriceChange(value);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
    }

    private void setSpinnerPosition() {
        String pollPrice = getArguments().getString(KEY_PRICE);
        String yelpPollPrice = YelpPriceLevel.toYelpString(pollPrice);
        int position = getSpinnerPosition(yelpPollPrice);
        priceSpinner.setSelection(position);
    }

    /**
     * Return the spinner position of a yelp price level ($, $$, $$$, $$$$). Defaults to 0 ($)
     * if none are found (for some reason).
     */
    private int getSpinnerPosition(String yelpPriceLevel) {
        for (int i = 0; i < priceSpinner.getCount(); i++) {
            String item = (String) priceSpinner.getItemAtPosition(i);
            if (yelpPriceLevel != null && yelpPriceLevel.equals(item)) {
                return i;
            }
        }
        // 0 is the $ in the spinner, which is the default price in the Poll class
        return 0;
    }

    /**
     * Return a listener that detects when the coordinates location of the device change.
     * When a location changed, it will inform the Host Activity through the OnPollSettingInterface
     */
    private LocationListener locationListener() {
        return new LocationListener() {
            public void onLocationChanged(Location location) {
                Log.d(TAG, "Coordinate Changed: " + coordinate.toString());
                coordinate.setLatitude(location.getLongitude());
                coordinate.setLongitude(location.getLongitude());
                onPollSettingsListener.onCoordinateChange(coordinate);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) { }

            @Override
            public void onProviderEnabled(String s) { }

            @Override
            public void onProviderDisabled(String s) { }
        };
    }

    /**
     * This interface is use to let the host activity know when a setting field has change in the
     * setting tab
     */
    interface OnPollSettingsListener {
        void onTitleChange(String title);

        void onDescriptionChange(String description);

        void onOpenNowChange(boolean openNow);

        void onPriceChange(String price);

        void onZipCodeChange(String zipCode);

        void onCoordinateChange(Coordinate coordinate);
    }
}
