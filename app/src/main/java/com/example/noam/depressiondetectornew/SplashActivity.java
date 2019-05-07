package com.example.noam.depressiondetectornew;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

public class SplashActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_RECORD_AND_STORAGE = 1;
    private static final int PERMISSIONS_REQUEST_RECORD = 2;
    private static final int PERMISSIONS_REQUEST_STORAGE = 3;
    private final String SMILEXTRACT_PATH = "";
    private Utils utils;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        utils = new Utils(this);
        context = this;
        Activity thisActivity = this;

        TextView appName = findViewById(R.id.appName);
        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/EncodeSans-Black.ttf");
        appName.setTypeface(custom_font);

        //Run One Time - Insert the openSmile necessary files to the phone:
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.getBoolean("firstTime", false)) {

            //putInDevice("SMILExtract", R.raw.smilextract);
            //putInDevice("emobase.conf", R.raw.emobase);
            putInDevice("girl_notdep2.wav",R.raw.girl_notdep2);
            putInDevice("man_notdep1.wav",R.raw.man_notdep1);
            putInDevice("girl_dep.wav",R.raw.girl_dep);
            putInDevice("man_dep1.wav",R.raw.man_dep1);
            putInDevice("man_dep2.wav",R.raw.man_dep2);
            putInDevice("girl_notdep1.wav",R.raw.girl_notdep1);
            // mark first time has runned.

        }

        // Check and Request for app permissions
        if (ContextCompat.checkSelfPermission(thisActivity,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(thisActivity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
            // Permissions are not granted
            //request the permissions
            ActivityCompat.requestPermissions(thisActivity,
                    new String[]{Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_RECORD_AND_STORAGE);
              /*  ActivityCompat.requestPermissions(thisActivity,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSIONS_REQUEST_STORAGE);*/
        } else {
            Intent intent = new Intent(getApplicationContext(),
                    MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void putInDevice(String resource, int rawResource) {
        Context context = getApplicationContext();

        String assetsFilename = resource;
        File file = new File(context.getFilesDir(), resource);
        if (resource.equals("SMILExtract")) {
            file = new File(SMILEXTRACT_PATH, resource);
        }

        try {
            InputStream ins = context.getResources().openRawResource(rawResource);
            byte[] buffer = new byte[ins.available()];
            ins.read(buffer);
            ins.close();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(buffer);
            fos.close();
            file.setExecutable(true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_RECORD_AND_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // For Project-Day - insert recordings bank. TO BE DELETED!
                        try {
                            insertRecordingBankToDB();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        Intent intent = new Intent(getApplicationContext(),
                                MainActivity.class);
                        startActivity(intent);
                        finish();

                } else {
                    Toast.makeText(context, "Permission to record denied",
                            Toast.LENGTH_LONG).show();
                }
                return;
            }

            // Maybe add a case for the storage permission
        }
    }

    private void insertRecordingBankToDB() throws ExecutionException, InterruptedException {
        String filesDirPath = utils.getFilesDirPath(context) + "/";

        String[] bankPaths = {
                filesDirPath + "girl_dep.wav",
                filesDirPath + "man_dep1.wav",
                filesDirPath + "man_dep2.wav",
                filesDirPath + "girl_notdep1.wav",
                filesDirPath + "girl_notdep2.wav",
                filesDirPath + "man_notdep1.wav",
        };
        String recordName;
        double prediction;
        File currentFile;
        String tempName;

        for (int i=0;i < 6;i++){
            currentFile = new File(bankPaths[i]);
            VoiceAnalysisAsyncTask runner = new VoiceAnalysisAsyncTask();
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                prediction = runner.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, currentFile).get();
            else
                prediction = runner.execute(currentFile).get();

            //runInBackground(currentFile);
            tempName = bankPaths[i].substring(bankPaths[i].indexOf("/") + 1);
            for(int j=0;j<5;j++){
                tempName=tempName.substring(tempName.indexOf("/")+1);
            }
            recordName = tempName.substring(0,tempName.indexOf("."));

            /////BRING IT BACK!
            ////////VoiceRecord voice_record = new VoiceRecord(recordName, bankPaths[i], prediction, Utils.getTime(), utils.getDuration(currentFile));
            ////////utils.saveRecord(voice_record);
        }
    }

    private class VoiceAnalysisAsyncTask extends AsyncTask<File, Void, Double> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Double doInBackground(File... wavFile) {
            //Run openSmile and predict depression
            int openSmileExitValue=0;
            Utils utils = new Utils(context);
            openSmileExitValue = utils.runOpenSmile(wavFile[0]);
            if (openSmileExitValue != 0) {
                Log.e("Opensmile", "openSMILE failed with error code " + openSmileExitValue);
            }
            return utils.predictDepression();
        }


        @Override
        protected void onPostExecute(Double prediction) {

        }

        @Override
        protected void onProgressUpdate(Void... param) {

        }
    }


}
