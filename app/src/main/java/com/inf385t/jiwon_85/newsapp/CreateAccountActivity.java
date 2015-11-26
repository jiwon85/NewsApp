package com.inf385t.jiwon_85.newsapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class CreateAccountActivity extends ActionBarActivity implements View.OnClickListener {


    private EditText createUsernameText;
    private EditText createPasswordText;
    private EditText confirmPasswordText;
    private EditText createEmailText;
    private Button createButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        createUsernameText = (EditText) findViewById(R.id.createUsernameText);

        createPasswordText = (EditText) findViewById(R.id.createPasswordText);
        confirmPasswordText = (EditText) findViewById(R.id.confirmPasswordText);
        createEmailText = (EditText) findViewById(R.id.createEmailText);
        createButton = (Button) findViewById(R.id.createButton);
        createButton.setOnClickListener(this);
        createButton.setClickable(false);
        Intent i = getIntent();
        String username = i.getStringExtra("username"); //nullable!
        if(username != null) {
            createUsernameText.setText(username);
        }

        TextWatcher textWatcher = new TextWatcher() {
            @Override public void afterTextChanged(Editable s) {
                if(createUsernameText.getText().length()!= 0
                        && createPasswordText.getText().length()!= 0
                        && confirmPasswordText.getText().length()!= 0
                        && createEmailText.getText().length() != 0) {
                    createButton.setClickable(true);
                }
                if(!createPasswordText.getText().toString()
                        .equals(confirmPasswordText.getText().toString())) {
                    //TODO: toast for passwords don't match

                    createButton.setClickable(false);
                }
                if(!createEmailText.getText().toString().contains("@") ||
                        !createEmailText.getText().toString().contains(".")){
                    //TODO: toast for not a valid email
                    createButton.setClickable(false);
                }
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        };

        createUsernameText.addTextChangedListener(textWatcher);
        createPasswordText.addTextChangedListener(textWatcher);
        confirmPasswordText.addTextChangedListener(textWatcher);
        createEmailText.addTextChangedListener(textWatcher);


    }

    public void onClick(View v) {
        if(v.getId() == R.id.createButton) {
            ParseUser user = new ParseUser();
            user.setUsername(createUsernameText.getText().toString());
            user.setPassword(createPasswordText.getText().toString());
            user.setEmail(createEmailText.getText().toString());
            user.signUpInBackground(new SignUpCallback() {
                public void done(ParseException e) {
                    if (e == null) {
                        // Hooray! Let them use the app now.
                        Toast t = Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_SHORT);
                        t.show();
                        Intent i = new Intent(getApplicationContext(), NewsListActivity.class);
                        startActivity(i);
                    } else {
                        Toast t = Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT);
                        t.show();

                    }
                }
            });
        }
    }
}
