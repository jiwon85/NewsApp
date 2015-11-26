package com.inf385t.jiwon_85.newsapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLngBounds;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

public class CreateContentActivity extends ActionBarActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private Button postButton;

    private EditText articleLinkText;
    private ListView categoryView;

    private String urlTitle = "";
    private CountDownLatch latch;

    protected GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteAdapter mAdapter;

    private AutoCompleteTextView mAutocompleteView;

    private static final String TAG = "ResultFiltersActivity";

    private static final LatLngBounds BOUNDS = null;

    private static final int[] filters = {Place.TYPE_LOCALITY, Place.TYPE_ADMINISTRATIVE_AREA_LEVEL_3};

    private Boolean isCity = false;

    private TextView placesText;

    private String placeholder;
    private String placeholderId;

    private ArrayList<String> places;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_content);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        places = new ArrayList<>();
        placeholder = "";
        placeholderId = "";
        placesText = (TextView) findViewById(R.id.placesText);
        postButton = (Button) findViewById(R.id.postButton);
        postButton.setOnClickListener(this);
        articleLinkText = (EditText) findViewById(R.id.articleLinkText);

        categoryView = (ListView) findViewById(R.id.categoryView);
        categoryView.setAdapter(new ArrayAdapter<String>(this, R.layout.text_layout,
                getResources().getStringArray(R.array.news_categories)));

        categoryView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        categoryView.setItemsCanFocus(false);



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
    }

    public void onClick(View v) {
        if (v.getId() == R.id.postButton) {
            if(articleLinkText.getText().length() == 0) {
                Toast t = Toast.makeText(getApplicationContext(), "Please enter a link.",
                        Toast.LENGTH_SHORT);
                t.show();
            } else if(categoryView.getCheckedItemCount() == 0) {
                Toast t = Toast.makeText(getApplicationContext(),
                        "Please select at least one category.", Toast.LENGTH_SHORT);
                t.show();
            } else {
                SparseBooleanArray checkedCategories = categoryView.getCheckedItemPositions();
                ParseObject post = new ParseObject("post");
                String link = articleLinkText.getText().toString();
//

                new MyTask().execute(link);
                post.put("link", link);
                //Politics, Business, Tech, Entertainment, Sports, Science, Health, Other

                post.put("isPolitics", checkedCategories.get(0));
                post.put("isBusiness", checkedCategories.get(1));
                post.put("isTech", checkedCategories.get(2));
                post.put("isEntertainment", checkedCategories.get(3));
                post.put("isSports", checkedCategories.get(4));
                post.put("isScience", checkedCategories.get(5));
                post.put("isHealth", checkedCategories.get(6));
                post.put("isOther", checkedCategories.get(7));
                post.put("votes", 0);

                if(places.size() > 0) {
                    for(String s: places)
                        post.add("locations", s);
                }
                latch = new CountDownLatch(1);
                try {
                    latch.await();
                } catch(InterruptedException e) {
                    Toast t2 = Toast.makeText(getApplicationContext(),
                            "Interruption Error!", Toast.LENGTH_SHORT);
                    t2.show();
                }
                if(!urlTitle.isEmpty()) {
                    post.put("title", urlTitle);
                }

                post.put("user", ParseUser.getCurrentUser().getUsername());

                post.saveInBackground();
                finish();


            }
        }


    }

    private class MyTask extends AsyncTask<String, Void, String> {
        String title = "";

        @Override
        protected String doInBackground(String... params) {
            try {
                Document doc = Jsoup.connect(params[0]).get();
                title = doc.title();
            } catch (IOException e) {
                e.printStackTrace();
                Toast t = Toast.makeText(getApplicationContext(),
                        "Invalid URL, Please fix.", Toast.LENGTH_SHORT);
                t.show();
            }
            urlTitle = title;
            latch.countDown();
            return title;
        }
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
//                        cityName = place.getName().toString();
                        placeholderId = place.getId();
                        placeholder = place.getName().toString();
                    } else {
                        placeholder = "";
                        placeholderId = "";
                    }
                    latch.countDown();
                    places.release();

                }
            });
            latch = new CountDownLatch(1);
            thread.start();
            try {
                latch.await();
            } catch(InterruptedException e) {
                Toast.makeText(getApplicationContext(),
                        "Interruption Error!", Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(getApplicationContext(), "Clicked: " + primaryText + " " + isCity,
                    Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Called getPlaceById to get Place details for " + placeId);
            if(isCity && !placeholderId.isEmpty()) {
                places.add(placeholderId);
                placesText.setText(placesText.getText().toString() + "\n" + placeholder);
            } else {
                mAutocompleteView.setText("");
                Toast.makeText(getApplicationContext(),
                        "Error: Selection not a city.", Toast.LENGTH_SHORT).show();
            }
        }
    };


}
