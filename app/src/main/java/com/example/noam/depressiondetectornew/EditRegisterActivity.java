package com.example.noam.depressiondetectornew;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class EditRegisterActivity extends AppCompatActivity {
    MyDBmanager db;
    Utils utils;
    EditText firstnameWrapper;
    EditText lastnameWrapper;
    EditText phoneWrapper;
    Button registerButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_register);


        android.support.v7.app.ActionBar myActionBar = getSupportActionBar();
        myActionBar.hide();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        db = Utils.getDB();
        utils = new Utils(this);
        firstnameWrapper = (EditText) findViewById(R.id.input_first_name);
        lastnameWrapper = (EditText) findViewById(R.id.input_last_name);
        phoneWrapper = (EditText) findViewById(R.id.input_phone_number);
        registerButton = (Button) findViewById(R.id.btn_signup);
        long uidtemp = UserProfile.getcurrentUserId();
        final UserProfile tempUser = db.getUserAt(uidtemp);

        firstnameWrapper.setText(tempUser.get_firstName());
        lastnameWrapper.setText(tempUser.get_lastName());
        phoneWrapper.setText(tempUser.get_phoneNumber());




        final Spinner spinnerGender = (Spinner) findViewById(R.id.spinner_gender);
        String[] items = new String[] {"Male", "Female"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(adapter);
        spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });
        if(tempUser.get_gender() == "Female")
            spinnerGender.setSelection(1);

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
                if(spinnerGender.getSelectedItem().toString().matches("")) {
                    error = true;
                    //userPhone.setBackgroundResource(R.drawable.rounded_edittext_red );
                }

                if(!error){
                    tempUser.set_firstName(firstnameWrapper.getText().toString());
                    tempUser.set_lastName(lastnameWrapper.getText().toString());
                    tempUser.set_phoneNumber(phoneWrapper.getText().toString());
                    tempUser.set_gender(spinnerGender.getSelectedItem().toString());
                    db.updateUser(tempUser);
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
