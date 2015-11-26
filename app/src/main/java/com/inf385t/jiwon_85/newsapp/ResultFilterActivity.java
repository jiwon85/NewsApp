package com.inf385t.jiwon_85.newsapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

public class ResultFilterActivity extends ActionBarActivity implements GoogleApiClient.OnConnectionFailedListener {


    protected GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteAdapter mAdapter;

    private AutoCompleteTextView mAutocompleteView;

    private static final String TAG = "ResultFiltersActivity";

    private static final LatLngBounds BOUNDS = null;

    private static final int[] filters = {Place.TYPE_LOCALITY, Place.TYPE_ADMINISTRATIVE_AREA_LEVEL_3};

    private Boolean isCity = false;

    private CountDownLatch latch = new CountDownLatch(1);

    private Button businessButton;
    private Button politicsButton;
    private Button techButton;
    private Button entertainmentButton;
    private Button sportsButton;
    private Button healthButton;
    private Button scienceButton;
    private Button otherButton;

    private String cityName;
    private String cityId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_filter);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .build();

        mAutocompleteView = (AutoCompleteTextView)
                findViewById(R.id.autocomplete_places);

        // Register a listener that receives callbacks when a suggestion has been selected
        mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);

        // Set up the adapter that will retrieve suggestions from the Places Geo Data API that cover
        // the entire world.
        Integer[] filters = {Place.TYPE_GEOCODE}; //not supported for just cities.
        mAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient, BOUNDS,
                AutocompleteFilter.create(Arrays.asList(filters)));
        mAutocompleteView.setAdapter(mAdapter);

        // Set up the 'clear text' button that clears the text in the autocomplete view
        Button clearButton = (Button) findViewById(R.id.button_clear);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAutocompleteView.setText("");
            }
        });

        politicsButton = (Button) findViewById(R.id.politicsButton);
        politicsButton.setOnClickListener(onClickListener);
        businessButton = (Button) findViewById(R.id.businessButton);
        businessButton.setOnClickListener(onClickListener);
        techButton = (Button) findViewById(R.id.techButton);
        techButton.setOnClickListener(onClickListener);
        healthButton = (Button) findViewById(R.id.healthButton);
        healthButton.setOnClickListener(onClickListener);
        entertainmentButton = (Button) findViewById(R.id.entertainmentButton);
        entertainmentButton.setOnClickListener(onClickListener);
        scienceButton = (Button) findViewById(R.id.scienceButton);
        scienceButton.setOnClickListener(onClickListener);
        sportsButton = (Button) findViewById(R.id.sportsButton);
        sportsButton.setOnClickListener(onClickListener);
        otherButton = (Button) findViewById(R.id.otherButton);
        otherButton.setOnClickListener(onClickListener);

        cityName = "";
        cityId = "";

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.e(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());

        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(this,
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
            final AutocompletePrediction item = mAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);

            Log.i(TAG, "Autocomplete item selected: " + primaryText);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              */
            Thread thread = new Thread(new Runnable(){
                @Override
                public void run(){

                    PlaceBuffer places = Places.GeoDataApi
                            .getPlaceById(mGoogleApiClient, placeId).await();
                    isCity = false;
                    if (!places.getStatus().isSuccess()) {
                        // Request did not complete successfully
                        Log.e(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                        places.release();
                        return;
                    }
                    // Get the Place object from the buffer.
                    final Place place = places.get(0);
                    if(place.getPlaceTypes().contains(filters[0]) || place.getPlaceTypes().contains(filters[1])) {
                        //is a city
                        isCity = true;
                        Log.i(TAG, "isCity is TRUE");
                        cityName = place.getName().toString();
                        cityId = place.getId();


                    } else {
                        cityName = "";
                        cityId = "";
                    }
                    latch.countDown();
                    places.release();

                }
            });
            thread.start();
            try {
                latch.await();
            } catch(InterruptedException e) {
                Toast.makeText(getApplicationContext(),
                        "Interruption Error!", Toast.LENGTH_SHORT).show();
            }
            latch = new CountDownLatch(1);
            Log.i(TAG, "Called getPlaceById to get Place details for " + placeId);
            if(isCity && !cityId.isEmpty()) {
                //TODO: find geotagged results
                Intent i = new Intent(getApplicationContext(), NewsListActivity.class);
                i.putExtra("city", cityId);
                i.putExtra("cityName", cityName);
                startActivity(i);


            } else {
                mAutocompleteView.setText("");
                Toast.makeText(getApplicationContext(),
                        "Error: Selection not a city.", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String category = "";
            switch(v.getId()) {
                case R.id.businessButton:
                    category = "isBusiness";
                    break;
                case R.id.politicsButton:
                    category = "isCategory";
                    break;
                case R.id.techButton:
                    category = "isTech";
                    break;
                case R.id.sportsButton:
                    category = "isSports";
                    break;
                case R.id.scienceButton:
                    category = "isScience";
                    break;
                case R.id.healthButton:
                    category = "isHealth";
                    break;
                case R.id.entertainmentButton:
                    category = "isEntertainment";
                    break;
                case R.id.otherButton:
                    category = "isOther";
                    break;
            }
            if(!category.isEmpty()) {
                Intent i = new Intent(getApplicationContext(), NewsListActivity.class);
                i.putExtra("category", category);
                startActivity(i);
            }
        }
    };

}
