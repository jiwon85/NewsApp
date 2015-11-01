package com.inf385t.jiwon_85.newsapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class MainActivity extends ActionBarActivity implements View.OnClickListener{

    private EditText usernameText;
    private EditText passwordText;
    private Button loginButton;
    private Button createAccountButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Parse.initialize(this, getString(R.string.parseappid), getString(R.string.parseclientkey));

        usernameText = (EditText) findViewById(R.id.usernameText);
        passwordText = (EditText) findViewById(R.id.passwordText);
        loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(this);
        loginButton.setClickable(false);
        createAccountButton = (Button) findViewById(R.id.createAccountButton);
        createAccountButton.setOnClickListener(this);
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(usernameText.getText().length()!= 0 && passwordText.getText().length()!= 0 ) {
                    loginButton.setClickable(true);
                } else {
                    loginButton.setClickable(false);
                }
            }
            @Override public void afterTextChanged(Editable s) {}
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        };
        usernameText.addTextChangedListener(textWatcher);
        passwordText.addTextChangedListener(textWatcher);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    public void onClick(View v) {
        //TODO: avoid user logging in every time the app is opened
        switch(v.getId()) {
            case R.id.loginButton:
                ParseUser.logInInBackground(usernameText.getText().toString(),
                        passwordText.getText().toString(), new LogInCallback() {
                    public void done(ParseUser user, ParseException e) {
                        if (user != null) {
                            Toast t = Toast.makeText(getApplicationContext(), "success!", Toast.LENGTH_SHORT);
                            t.show();
                            Intent i = new Intent(getApplicationContext(), NewsListActivity.class);
                            startActivity(i);
                        } else {
                            // Signup failed. Look at the ParseException to see what happened.
                            Toast t = Toast.makeText(getApplicationContext(), e.getMessage() , Toast.LENGTH_SHORT);
                            t.show();
                        }
                    }
                });
                break;
            case R.id.createAccountButton:
                Intent i = new Intent(this, CreateAccountActivity.class);
                i.putExtra("username", usernameText.getText().toString());
                startActivity(i);
                break;
        }
    }
}
