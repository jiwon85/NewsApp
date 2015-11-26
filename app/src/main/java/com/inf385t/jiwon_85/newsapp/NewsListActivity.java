package com.inf385t.jiwon_85.newsapp;

import android.content.Intent;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;



public class NewsListActivity extends ActionBarActivity{

    private Button plusButton;
    private Button refreshButton;
    private ArrayList<PostResults> postResults;
    private int skip = 0;
    private ListView listView;

    private Button filterButton;

    private String city;
    private String category;

    private TextView titleText;

    private ToggleButton recentToggle;
    private ToggleButton popularToggle;

    private String sortBy = "createdAt";

    private Button logoutButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);

        logoutButton = (Button) findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser parseUser = ParseUser.getCurrentUser();
                parseUser.logOut();
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);


            }
        });


        recentToggle = (ToggleButton) findViewById(R.id.recentToggle);
        recentToggle.setChecked(true);
        recentToggle.setClickable(false);
        recentToggle.setOnClickListener(toggleListener);
        popularToggle = (ToggleButton) findViewById(R.id.popularToggle);
        popularToggle.setOnClickListener(toggleListener);

        Intent i = getIntent();
        city = i.getStringExtra("city") == null ? "" : i.getStringExtra("city");
        category = i.getStringExtra("category") == null ? "" : i.getStringExtra("category");


        titleText = (TextView) findViewById(R.id.titleText);
        if(!city.isEmpty()) {
            titleText.setText(i.getStringExtra("cityName") == null ? "" : i.getStringExtra("cityName"));
        } else if(!category.isEmpty()) {
            titleText.setText(category.substring(2));
        }

        listView = (ListView) findViewById(R.id.listView);
        plusButton = (Button) findViewById(R.id.plusButton);
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), CreateContentActivity.class);
                startActivity(i);
            }
        });
        refreshButton = (Button) findViewById(R.id.refreshButton); //will be home instead
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postResults = new ArrayList<PostResults>();
                city = "";
                category = "";
                titleText.setText("All News");
                getSearchResults();
            }
        });

        getSearchResults();



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Object o = listView.getItemAtPosition(position);
                PostResults fullObject = (PostResults) o;
                Intent launchactivity = new Intent(getApplicationContext(),BrowserActivity.class);
                launchactivity.putExtra("url", fullObject.getParseObject().getString("link"));
                startActivity(launchactivity);
            }
        });


        filterButton = (Button) findViewById(R.id.filterButton);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ResultFilterActivity.class);
                startActivity(i);
            }
        });




    }




    private void getSearchResults() {
        postResults = new ArrayList<PostResults>();
        //TODO: allow loading more on scroll
        ParseQuery<ParseObject> query = ParseQuery.getQuery("post");
        query.setLimit(30);
        if(!category.isEmpty()) {
            query.whereEqualTo(category, true);
        } else if(!city.isEmpty()) {
            query.whereEqualTo("locations", city);
        }
        query.orderByDescending(sortBy);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> postList, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < postList.size(); i++) {
                        ParseObject p = postList.get(i);
                        PostResults postResult = new PostResults();
                        postResult.setParseObject(p);
                        postResults.add(postResult);
                    }
                    listView.setAdapter(new MyCustomBaseAdapter(getApplicationContext(), postResults));
                } else {
                    //error
                    Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private View.OnClickListener toggleListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.popularToggle) {
                sortBy = "votes";
                recentToggle.setClickable(true);
                recentToggle.setChecked(false);
                popularToggle.setClickable(false);
            } else {
                sortBy = "createAt";
                popularToggle.setClickable(true);
                popularToggle.setChecked(false);
                recentToggle.setClickable(false);
            }
            getSearchResults();

        }
    };

}
