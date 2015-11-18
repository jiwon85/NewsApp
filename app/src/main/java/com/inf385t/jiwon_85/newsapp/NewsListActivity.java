package com.inf385t.jiwon_85.newsapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;




public class NewsListActivity extends ActionBarActivity {

    private Button plusButton;
    private Button refreshButton;
    private Boolean refresh = false;
    private ArrayList<PostResults> postResults;
    private int skip = 0;
    private ListView listView;


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
                PostResults fullObject = (PostResults)o;
//                Toast.makeText(ListViewBlogPost.this, "You have chosen: " + " " + fullObject.getName(), Toast.LENGTH_LONG).show();
                    int viewId = v.getId();
                    if(viewId == R.id.upvote || viewId == R.id.downvote) {
                        Toast.makeText(getApplicationContext(), "voted", Toast.LENGTH_SHORT).show();
                        ParseObject p = fullObject.getParseObject();
                        int increment = viewId == R.id.upvote ? 1 : -1;
                        p.increment("votes", increment);
                        p.saveInBackground();
                    }
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


}
