package com.example.noam.depressiondetectornew;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ProgressBar;
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
    String[] bankPathss;
    String[] names = {
        "Depressed girl",
        "Depressed girl 2",
        "Depressed man",
        "Not depressed girl",
        "Not depressed girl 2 ",
        "Not depressed man"};
    File currentFile;
    RecordingProfile voice_record;
    int number = 0;
    private ProgressBar pgsBar;
    TextView appName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        utils = new Utils(this);
        context = this;
        Activity thisActivity = this;
        RandomForestClassifier lgbm = new RandomForestClassifier();
        appName = findViewById(R.id.appName);

        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/Highest.ttf");
        //appName.setTypeface(custom_font);
        pgsBar = findViewById(R.id.pBar3);
        //Run One Time - Insert the openSmile necessary files to the phone:
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.getBoolean("firstTime", false)) {

            //putInDevice("SMILExtract", R.raw.smilextract);
            putInDevice("emobase.conf", R.raw.emobase);
            putInDevice("depressed_girl_from_youtube.wav",R.raw.depressed_girl_from_youtube);
            putInDevice("depressed_girl_from_youtube_2.wav",R.raw.depressed_girl_from_youtube_2);
            putInDevice("depressed_man_from_youtube.wav",R.raw.depressed_man_from_youtube);
            putInDevice("not_depressed_girl.wav",R.raw.not_depressed_girl);
            putInDevice("not_depressed_girl_2.wav",R.raw.not_depressed_girl_2);
            putInDevice("not_depressed_man.wav",R.raw.not_depressed_man);
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
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.MODIFY_AUDIO_SETTINGS,
                            Manifest.permission.READ_CONTACTS},
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
                    appName.setText("Initializing...");


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
                filesDirPath + "depressed_girl_from_youtube.wav",
                filesDirPath + "depressed_girl_from_youtube_2.wav",
                filesDirPath + "depressed_man_from_youtube.wav",
                filesDirPath + "not_depressed_girl.wav",
                filesDirPath + "not_depressed_girl_2.wav",
                filesDirPath + "not_depressed_man.wav",
        };
        bankPathss = bankPaths;
        String recordName;
        double prediction;
        //File currentFile;
        String tempName;
        UserProfile patient = new UserProfile();
        patient.set_gender("Male");
        patient.set_firstName("Patient Example");
        patient.set_phoneNumber("123-456-789");
        patient.set_lastName("25");
        patient.set_joinDate(Utils.getTime());
        utils.saveUser(patient);
        currentFile = new File(bankPaths[0]);
        voice_record = new RecordingProfile();
        VoiceAnalysisAsyncTask runner = new VoiceAnalysisAsyncTask();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            prediction = runner.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, currentFile).get();
        else
            prediction = runner.execute(currentFile).get();
        /*
        for (int i=0;i < 6;i++){

            //runInBackground(currentFile);
            tempName = bankPaths[i].substring(bankPaths[i].indexOf("/") + 1);
            for(int j=0;j<5;j++){
                tempName=tempName.substring(tempName.indexOf("/")+1);
            }
            recordName = tempName.substring(0,tempName.indexOf("."));

            RecordingProfile voice_record = new RecordingProfile();
            voice_record.set_path( bankPaths[i]);
            voice_record.set_recordName(recordName);
            voice_record.set_time(Utils.getTime());
            voice_record.set__userId(1);
            voice_record.set_length(utils.getDuration(currentFile));
            voice_record.set_prediction(prediction);
            utils.saveRecord(voice_record);
        }*/
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
            String csv = utils.MakeCSV();
            voice_record.set_csv(csv);
            return utils.predictDepression(csv);
        }


        @Override
        protected void onPostExecute(Double prediction) {
            if(number < 6){
                String recordName;
                //double prediction;
                MyDBmanager db;

                String tempName;
                //runInBackground(currentFile);
                tempName = bankPathss[number].substring(bankPathss[number].indexOf("/") + 1);
                for(int j=0;j<5;j++){
                    tempName=tempName.substring(tempName.indexOf("/")+1);
                }
                recordName = tempName.substring(0,tempName.indexOf("."));
                if(number == 1)
                    appName.setText("Initializing...");
                if(number == 3)
                    appName.setText("Just a moment...");
                if(number == 5)
                    appName.setText("Preparing data...");
                if(number != 0)
                    voice_record = new RecordingProfile();
                voice_record.set_path( bankPathss[number]);
                voice_record.set_recordName(names[number]);
                voice_record.set_time(Utils.getTime());
                voice_record.set__userId(1);
                voice_record.setPrediction_feedback(1);
                voice_record.set_length(utils.getDuration(currentFile));
                voice_record.set_prediction(prediction);
                //utils.saveRecord(voice_record);
                long recid = utils.saveRecord(voice_record);
                db = Utils.getDB();
                db.UpdateGson(1,recid);
                number++;
                if(number==6){
                    Intent intent = new Intent(getApplicationContext(),
                            MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    currentFile = new File(bankPathss[number]);
                    VoiceAnalysisAsyncTask runner = new VoiceAnalysisAsyncTask();
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                        runner.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, currentFile);
                    else
                        runner.execute(currentFile);
                }

            }
        }

        @Override
        protected void onProgressUpdate(Void... param) {

        }
    }


}
