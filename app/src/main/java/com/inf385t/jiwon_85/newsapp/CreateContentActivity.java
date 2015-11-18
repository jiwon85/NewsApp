package com.inf385t.jiwon_85.newsapp;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class CreateContentActivity extends ActionBarActivity implements View.OnClickListener {

    private Button postButton;
    private EditText articleLinkText;
    private ListView categoryView;
    private ListView placeView;
    private String urlTitle = "";
    private CountDownLatch latch = new CountDownLatch(1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_content);
        postButton = (Button) findViewById(R.id.postButton);
        postButton.setOnClickListener(this);
        articleLinkText = (EditText) findViewById(R.id.articleLinkText);

        categoryView = (ListView) findViewById(R.id.categoryView);
        categoryView.setAdapter(new ArrayAdapter<String>(this, R.layout.text_layout,
                getResources().getStringArray(R.array.news_categories)));

        categoryView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        categoryView.setItemsCanFocus(false);

        placeView = (ListView) findViewById(R.id.placeView);
        String[] placeholder = {"Austin, Tx"};
        placeView.setAdapter(new ArrayAdapter<String>(this, R.layout.text_layout, placeholder));
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
                post.put("isPolitics", checkedCategories.valueAt(0));
                post.put("isBusiness", checkedCategories.valueAt(1));
                post.put("isTech", checkedCategories.valueAt(2));
                post.put("isEntertainment", checkedCategories.valueAt(3));
                post.put("isSports", checkedCategories.valueAt(4));
                post.put("isScience", checkedCategories.valueAt(5));
                post.put("isHealth", checkedCategories.valueAt(6));
                post.put("isOther", checkedCategories.valueAt(7));
                post.put("votes", 0);
                if(placeView.getCheckedItemCount() > 0) {
//                    post.put("locations", placeView.getCheckedItemPositions().get)
                    //TODO: how to handle location???
                }
                try {
                    latch.await();
                } catch(InterruptedException e) {
                    Toast t2 = Toast.makeText(getApplicationContext(),
                            "interruption Error!", Toast.LENGTH_SHORT);
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


}
