package com.inf385t.jiwon_85.newsapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class NewsListActivity extends ActionBarActivity {

    private Button plusButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);
        plusButton = (Button) findViewById(R.id.plusButton);
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), CreateContentActivity.class);
                startActivity(i);
            }
        });
        ArrayList<PostResults> postResults = GetSearchResults();

        final ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(new MyCustomBaseAdapter(this, postResults));

//        lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
//                Object o = lv1.getItemAtPosition(position);
//                PostResults fullObject = (PostResults)o;
////                Toast.makeText(ListViewBlogPost.this, "You have chosen: " + " " + fullObject.getName(), Toast.LENGTH_LONG).show();
//            }
//        });
    }


    private ArrayList<PostResults> GetSearchResults(){
        ArrayList<PostResults> results = new ArrayList<PostResults>();
    //TODO: populate listview with parse query
        //placeholder
        PostResults placeholder = new PostResults();
        placeholder.setDate("date/////");
        placeholder.setTitle("title//////");
        results.add(placeholder);

        placeholder = new PostResults();
        placeholder.setDate("date/////2");
        placeholder.setTitle("title//////2");
        results.add(placeholder);

        placeholder = new PostResults();
        placeholder.setDate("date/////3");
        placeholder.setTitle("title//////3");
        results.add(placeholder);


        placeholder = new PostResults();
        placeholder.setDate("date/////4");
        placeholder.setTitle("title//////4");
        results.add(placeholder);

        placeholder = new PostResults();
        placeholder.setDate("date/////5");
        placeholder.setTitle("title//////5");
        results.add(placeholder);
//        SearchResults sr1 = new SearchResults();
//        sr1.setName("John Smith");
//        sr1.setCityState("Dallas, TX");
//        sr1.setPhone("214-555-1234");
//        results.add(sr1);
//
//        sr1 = new SearchResults();
//        sr1.setName("Jane Doe");
//        sr1.setCityState("Atlanta, GA");
//        sr1.setPhone("469-555-2587");
//        results.add(sr1);
//
//        sr1 = new SearchResults();
//        sr1.setName("Steve Young");
//        sr1.setCityState("Miami, FL");
//        sr1.setPhone("305-555-7895");
//        results.add(sr1);
//
//        sr1 = new SearchResults();
//        sr1.setName("Fred Jones");
//        sr1.setCityState("Las Vegas, NV");
//        sr1.setPhone("612-555-8214");
//        results.add(sr1);

        return results;
    }


}
