package com.example.noam.depressiondetectornew;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class UserPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);

        android.support.v7.app.ActionBar myActionBar = getSupportActionBar();
        myActionBar.hide();

    }
}
