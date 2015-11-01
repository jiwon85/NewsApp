package com.inf385t.jiwon_85.newsapp;

import android.app.ProgressDialog;
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

import java.io.IOException;

public class CreateContentActivity extends ActionBarActivity implements View.OnClickListener {

    private Button postButton;
    private EditText articleLinkText;
    private ListView categoryView;
    private ListView placeView;
    private String urlTitle = "";

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
                new MyTask().execute(link);
                post.add("link", link);
                //Politics, Business, Tech, Entertainment, Sports, Science, Health, Other
                post.add("isPolitics", checkedCategories.valueAt(0));
                post.add("isBusiness", checkedCategories.valueAt(1));
                post.add("isTech", checkedCategories.valueAt(2));
                post.add("isEntertainment", checkedCategories.valueAt(3));
                post.add("isSports", checkedCategories.valueAt(4));
                post.add("isScience", checkedCategories.valueAt(5));
                post.add("isHealth", checkedCategories.valueAt(6));
                post.add("isOther", checkedCategories.valueAt(7));
                if(placeView.getCheckedItemCount() > 0) {
//                    post.add("locations", placeView.getCheckedItemPositions().get)
                    //TODO: how to handle location???
                }
                if(!urlTitle.isEmpty()) {
                    post.add("title", urlTitle);
                }

                post.add("user", ParseUser.getCurrentUser().getUsername());

                post.saveInBackground();
                finish();


            }
        }


    }
    private class MyTask extends AsyncTask<String, Void, String> {
        ProgressDialog prog;
        String title = "";

        @Override
        protected void onPreExecute() {
            prog = new ProgressDialog(CreateContentActivity.this);
            prog.setMessage("Loading....");
            prog.show();
        }
        @Override
        protected String doInBackground(String... params) {
            try {
                Document doc = Jsoup.connect(params[0]).get(); //TODO: import jsoup
                title = doc.title();
            } catch (IOException e) {
                e.printStackTrace();
                //TODO: toast error, ask to fix url
            }
            return title;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            prog.dismiss();
            urlTitle = result;
        }

    }


}
