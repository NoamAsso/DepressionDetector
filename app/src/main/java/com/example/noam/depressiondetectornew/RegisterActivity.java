package com.example.noam.depressiondetectornew;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RegisterActivity extends AppCompatActivity {
    EditText firstnameWrapper;
    EditText lastnameWrapper;
    EditText phoneWrapper;
    EditText emailWrapper;
    MyDBmanager db;
    Utils utils;
    Button registerButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        android.support.v7.app.ActionBar myActionBar = getSupportActionBar();
        myActionBar.hide();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        db = Utils.getDB();
        utils = new Utils(this);
        firstnameWrapper = (EditText) findViewById(R.id.input_first_name);
        lastnameWrapper = (EditText) findViewById(R.id.input_last_name);
        phoneWrapper = (EditText) findViewById(R.id.input_phone_number);
        emailWrapper = (EditText) findViewById(R.id.input_email);
        registerButton = (Button) findViewById(R.id.btn_signup);

        registerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                boolean error = false;

                if(firstnameWrapper.getText().toString().matches("")){
                    error = true;
                    //firstnameWrapper.setBackgroundResource(R.drawable.rounded_edittext_red );
                }
                if(lastnameWrapper.getText().toString().matches("")){
                    error = true;
                    //userPassword.setBackgroundResource(R.drawable.rounded_edittext_red );
                }
                if(phoneWrapper.getText().toString().matches("")){
                    error = true;
                    //userEmail.setBackgroundResource(R.drawable.rounded_edittext_red );
                }
                if(emailWrapper.getText().toString().matches("")) {
                    error = true;
                    //userPhone.setBackgroundResource(R.drawable.rounded_edittext_red );
                }
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat format = new SimpleDateFormat("EEEE, MMMM d, yyyy 'at' h:mm a");
                String date = format.format(calendar.getTime());
                if(!error){
                    UserProfile user = new UserProfile(phoneWrapper.getText().toString(),firstnameWrapper.getText().toString(),
                            lastnameWrapper.getText().toString(),date);

                    utils.saveUser(user);


                    //Intent myIntent = new Intent(RegisterActivity.this, LoginMenu.class);
                    //myIntent.putExtra("key", value); //Optional parameters
                    //RegisterActivity.this.startActivity(myIntent);
                    finish();
                }
                else {
                    Toast.makeText(getApplicationContext(),"Some fields are missing",Toast.LENGTH_SHORT).show();
                }
            }
            //create button
        });


    }
}
