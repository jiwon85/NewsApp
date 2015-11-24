package com.inf385t.jiwon_85.newsapp;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.GeoDataApi;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;


public class NewsListActivity extends ActionBarActivity implements GoogleApiClient.OnConnectionFailedListener{

    private Button plusButton;
    private Button refreshButton;
    private Boolean refresh = false;
    private ArrayList<PostResults> postResults;
    private int skip = 0;
    private ListView listView;
    protected GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteAdapter mAdapter;

    private AutoCompleteTextView mAutocompleteView;

    private static final String TAG = "NewsListActivity";

    private static final LatLngBounds BOUNDS = null;

    private static final int[] filters = {Place.TYPE_LOCALITY, Place.TYPE_ADMINISTRATIVE_AREA_LEVEL_3};

    private Boolean isCity = false;

    private CountDownLatch latch = new CountDownLatch(1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);
        listView = (ListView) findViewById(R.id.listView);
        plusButton = (Button) findViewById(R.id.plusButton);
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), CreateContentActivity.class);
                startActivity(i);
            }
        });
        refreshButton = (Button) findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postResults = new ArrayList<PostResults>();
                refresh = true;
                getSearchResults();
            }
        });


        postResults = new ArrayList<PostResults>();
        getSearchResults();



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Object o = listView.getItemAtPosition(position);
                PostResults fullObject = (PostResults) o;
//                Toast.makeText(ListViewBlogPost.this, "You have chosen: " + " " + fullObject.getName(), Toast.LENGTH_LONG).show();
                int viewId = v.getId();
                Toast.makeText(getApplicationContext(), "voted", Toast.LENGTH_SHORT).show();
                if (viewId == R.id.upvote || viewId == R.id.downvote) {

                    ParseObject p = fullObject.getParseObject();
                    int increment = viewId == R.id.upvote ? 1 : -1;
                    p.increment("votes", increment);
                    p.saveInBackground();
                }
            }
        });

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .build();

        mAutocompleteView = (AutoCompleteTextView)
                findViewById(R.id.autocomplete_places);

        // Register a listener that receives callbacks when a suggestion has been selected
        mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);

        // Retrieve the TextViews that will display details and attributions of the selected place.
//        mPlaceDetailsText = (TextView) findViewById(R.id.place_details);
//        mPlaceDetailsAttribution = (TextView) findViewById(R.id.place_attribution);

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




    private void getSearchResults() {
//        Toast.makeText(getApplicationContext(), "in method", Toast.LENGTH_SHORT).show();
        //TODO: sort by mods
        ParseQuery<ParseObject> query = ParseQuery.getQuery("post");
        query.setLimit(30);
//        if(refresh) {
//            refresh = false;
//        } else {
//            query.setSkip(skip);
//            skip+=30;
//        }
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> postList, ParseException e) {
                if (e == null) {
//                    Toast.makeText(getApplicationContext(), postList.size(), Toast.LENGTH_SHORT).show();

                    for (int i = 0; i < postList.size(); i++) {
                        ParseObject p = postList.get(i);
                        PostResults postResult = new PostResults();
                        postResult.setParseObject(p);
                        postResults.add(postResult);
                    }
                    listView.setAdapter(new MyCustomBaseAdapter(getApplicationContext(), postResults));
                } else {
                    //error?
                    Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });

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

                    }


                    Log.i(TAG, "Place details received: " + place.getName());
                    latch.countDown();
                    places.release();

                }
            });
            thread.start();
            try {
                latch.await();
            } catch(InterruptedException e) {
                Toast.makeText(getApplicationContext(),
                        "interruption Error!", Toast.LENGTH_SHORT).show();
            }
            latch = new CountDownLatch(1);
            Toast.makeText(getApplicationContext(), "Clicked: " + primaryText + " " + isCity,
                    Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Called getPlaceById to get Place details for " + placeId);
            if(isCity) {
                //TODO: find geotagged results
            } else {
                mAutocompleteView.setText("");
                Toast.makeText(getApplicationContext(),
                        "Error: Selection not a city.", Toast.LENGTH_SHORT).show();
            }
        }
    };


}
