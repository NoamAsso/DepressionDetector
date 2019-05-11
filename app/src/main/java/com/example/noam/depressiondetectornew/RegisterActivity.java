package com.example.noam.depressiondetectornew;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
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
    String name;
    String phoneNo;
    String email;
    String id;
    MyDBmanager db;
    Utils utils;
    Button registerButton;
    Button contactsButton;
    private String TAG = "Contacts";
    private static final int RESULT_PICK_CONTACT = 2015;
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
        contactsButton = (Button) findViewById(R.id.btn_signup_contacts);

        contactsButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                selectSingleContact();
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat format = new SimpleDateFormat("EEEE, MMMM d, yyyy 'at' h:mm a");
                String date = format.format(calendar.getTime());
                UserProfile user = new UserProfile(phoneNo,name,
                        name,date);

                utils.saveUser(user);
                finish();
            }


            });

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
    private void selectSingleContact() {
        Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(pickContact, 2015);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
// check whether the result is ok
        Uri contactData = data.getData();
        Cursor c = getContentResolver().query(contactData, null, null, null, null);
        if (c.moveToFirst()) {
            int phoneIndex = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            String num = c.getString(phoneIndex);
            Toast.makeText(RegisterActivity.this, "Number=" + num, Toast.LENGTH_LONG).show();
        }
    }

    private void contactPicked(Intent data) {

        Uri uri = data.getData();
        Log.i(TAG, "contactPicked() uri " + uri.toString());
        Cursor cursor;
        ContentResolver cr = this.getContentResolver();

        try {
            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
            if (null != cur && cur.getCount() > 0) {
                cur.moveToFirst();
                for (String column : cur.getColumnNames()) {
                    Log.i(TAG, "contactPicked() Contacts column " + column + " : " + cur.getString(cur.getColumnIndex(column)));
                }
            }

            if (cur.getCount() > 0) {
                //Query the content uri
                cursor = this.getContentResolver().query(uri, null, null, null, null);

                if (null != cursor && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    for (String column : cursor.getColumnNames()) {
                        Log.i(TAG, "contactPicked() uri column " + column + " : " + cursor.getString(cursor.getColumnIndex(column)));
                    }
                }

                cursor.moveToFirst();
                id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                Log.i(TAG, "contactPicked() uri id " + id);
                String contact_id = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                Log.i(TAG, "contactPicked() uri contact id " + contact_id);
                // column index of the contact name
                name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                // column index of the phone number
                phoneNo = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                //get Email id of selected contact....
                Log.e("ContactsFragment", "::>> " + id + name + phoneNo);

                Cursor cur1 = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{contact_id}, null);

                if (null != cur1 && cur1.getCount() > 0) {
                    cur1.moveToFirst();
                    for (String column : cur1.getColumnNames()) {
                        Log.i(TAG, "contactPicked() Email column " + column + " : " + cur1.getString(cur1.getColumnIndex(column)));
                        email = cur1.getString(cur1.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    }

                    //HERE YOU GET name, phoneno & email of selected contact from contactlist.....
                    Log.e("setcontactDetails","::>>" + name+"\nPhoneno:" + phoneNo+"\nEmail: " + email);
                } else {
                    Log.e("setcontactDetails","::>>" + name+"\nPhoneno:" + phoneNo+"\nEmail: " + email);
                }
            }
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }
}
