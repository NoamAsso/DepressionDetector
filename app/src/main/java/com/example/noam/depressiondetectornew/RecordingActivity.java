package com.example.noam.depressiondetectornew;

import android.content.DialogInterface;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

public class RecordingActivity extends AppCompatActivity {
    long difference = 0;
    public static MyDBmanager db;
    InputStream is = null;
    RecordWavMaster recordWavMaster;
    MediaPlayer mediaPlayer;
    private String filesDirPath;
    File latestRecFile;
    int recID=0;

    private long pauseOffset;
    private Chronometer chronometer; //timer
    private boolean running;

    private static final int RECORDING_NOW = 4;
    private static final int NOT_RECORDING_NOW = 5;
    private static int recState;
    private static boolean firstTimeFlag = true;
    private double precentage;
    long startTime;
    static int TEN_SECONDS = 10000;
    TextView resultsText;
    TextView titleText;
    Button btnSave, btnDelete;

    Utils utils;
    String testTrimmedPath = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording2);

        filesDirPath = Utils.getFilesDirPath(this);
        db = Utils.getDB();
        utils = new Utils(this);

        chronometer = findViewById(R.id.chronometer);
        chronometer.setFormat("%s");
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.setVisibility(View.INVISIBLE);
        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        recState = NOT_RECORDING_NOW;
        //mediaPlayer = new MediaPlayer();
        findViewById(R.id.btnSave).setVisibility(View.INVISIBLE);
        findViewById(R.id.btnDelete).setVisibility(View.INVISIBLE);

        startTime = System.currentTimeMillis();
        setFilesDirPath();
        setButtonHandlers();
        enableButtons(true);
        findViewById(R.id.resultsText).setVisibility(View.INVISIBLE);
        resultsText = findViewById(R.id.results);
        titleText = findViewById(R.id.txtTitle);

        // custom_font = Typeface.createFromAsset(getAssets(),  "fonts/EncodeSans-Bold.ttf");
        btnSave = findViewById(R.id.btnSave);
        btnDelete =  findViewById(R.id.btnDelete);

        btnSave.setOnClickListener(btnClick);
        btnDelete.setOnClickListener(btnClick);

        resultsText.setVisibility(View.INVISIBLE);

        Log.d("STAM", "JUST TO CHECKyouvalisidiot");
    }


    private void setFilesDirPath() {
        filesDirPath = getApplicationContext().getFilesDir().toString();
    }

    /*This method creates and shows a Rename-Save-Popup
     * @params: _id - the id that the adapter works with
     *          a cursor
     * */
    void makePopup(){
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        final View myDialogView = inflater.inflate(R.layout.rename_save_dialogue, null);
        final String time = Utils.getTime();
        final String defaultName = "Default name - ";
        //Get Audio duration time
        final int duration = utils.getDuration(latestRecFile);


        final EditText recordName = myDialogView.findViewById(R.id.recordName);

        //Build the dialog
        final AlertDialog.Builder dialog = new AlertDialog.Builder(
                RecordingActivity.this,
                R.style.MyDialogTheme
        );
        dialog.setTitle("Please name the recording:");
        dialog.setView(myDialogView);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                recID++;
                if(recordName.getText().toString().equals(""))
                {
                    recordName.setText(defaultName + recID);
                }
                RecordingProfile voice_record = new RecordingProfile(1,recordName.getText().toString(),latestRecFile.toString(),duration,time,precentage);
                utils.saveRecord(voice_record);
                dialog.dismiss();
            }
        });

        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                latestRecFile.delete();
                dialog.dismiss();
            }
        });
        //   dialog.setNegativeButton("Cancel", null);
        dialog.create();
        dialog.show();
    }



    private void setButtonHandlers() {

        findViewById(R.id.btnRecord).setOnClickListener(btnClick);

    }

    private void enableButton(int id, boolean isEnable) {
        if (!isEnable){
            findViewById(id).setAlpha(.5f);
        }
        else {
            findViewById(id).setAlpha(1f);
        }
        findViewById(id).setEnabled(isEnable);
    }


    private void enableButtons(boolean isRecording) {

        enableButton(R.id.btnRecord, isRecording);
        enableButton(R.id.btnDelete,!isRecording);
        enableButton(R.id.btnSave,!isRecording);
    }

    private View.OnClickListener btnClick = new View.OnClickListener() {
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.btnRecord: {
                    if (recState == NOT_RECORDING_NOW) {
                        recState = RECORDING_NOW;

                        //   titleText.setVisibility(View.INVISIBLE);
                        //   titleText.setText("Press the MIC to stop and see results");

                        chronometer.setVisibility(View.VISIBLE);
                        startChronometer(v);
                        enableButtons(true);

                        v.setBackgroundResource(R.drawable.icons8_pause_button_96);




                        resultsText.setText("Recording...");
                        resultsText.setVisibility(View.VISIBLE);

                        recordWavMaster = new RecordWavMaster();
                        recordWavMaster.startRecording();
                        break;
                    } else {          //Now recording, needs to stop
                        recState = NOT_RECORDING_NOW;
                        chronometer.setVisibility(View.INVISIBLE);
                        resetChronometer(v);
                        pauseChronometer(v);

                        titleText.setText("Record for at least 10 second for prediction");
                        v.setBackgroundResource(R.drawable.icons8_record_96);

                        recordWavMaster.stopRecording();

                        latestRecFile = recordWavMaster.latestRecFile;
                        recordWavMaster.releaseRecord();
                        if(utils.getDuration(latestRecFile)<TEN_SECONDS)
                        {
                            Toast.makeText(RecordingActivity.this, "Record was too short, no prediction was made",
                                    Toast.LENGTH_LONG).show();
                            latestRecFile.delete();
                            returnBeginning();
                            return;
                        }
                        resultsText.setText("Calculating Prediction...");
                        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
                        btnSave.setVisibility(View.INVISIBLE);
                        btnDelete.setVisibility(View.INVISIBLE);

                        VoiceAnalysisAsyncTask runner = new VoiceAnalysisAsyncTask();
                        runner.execute(latestRecFile);

                        break;
                    }
                }
                case R.id.btnDelete:{
                    latestRecFile.delete();
                    returnBeginning();
                    break;
                }
                case R.id.btnSave:{
                    makePopup(); //opens the UI name input and saves the record to the Database
                    findViewById(R.id.txtTitle).setVisibility(View.INVISIBLE);
                    returnBeginning();
                    break;
                }
            }
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        if (mediaPlayer != null) {
            //mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
    private void saveOrDeleteRec(){

        enableButtons(false);

        btnSave.setVisibility(View.VISIBLE);
        btnDelete.setVisibility(View.VISIBLE);

        //   findViewById(R.id.btnCancel).setOnClickListener(btnClick);
        //   findViewById(R.id.btnSave).setOnClickListener(btnClick);

    }
    private void returnBeginning(){
        findViewById(R.id.btnSave).setVisibility(View.INVISIBLE);
        findViewById(R.id.btnDelete).setVisibility(View.INVISIBLE);
        findViewById(R.id.resultsText).setVisibility(View.INVISIBLE);
        findViewById(R.id.results).setVisibility(View.INVISIBLE);
        findViewById(R.id.txtTitle).setVisibility(View.VISIBLE);
        enableButtons(true);
    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    private class VoiceAnalysisAsyncTask extends AsyncTask<File, Void, Double> {

        private String resp;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Double doInBackground(File... wavFile) {
            //Run openSmile and predict depression
            int openSmileExitValue=0;
            Utils utils = new Utils(RecordingActivity.this);
            openSmileExitValue = utils.runOpenSmile(wavFile[0]);
            if (openSmileExitValue != 0) {
                Log.e("Opensmile", "openSMILE failed with error code " + openSmileExitValue);
            }
            return utils.predictDepression();
        }

        @Override
        protected void onPostExecute(Double prediction) {
            DecimalFormat df2 = new DecimalFormat("#.##");
            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
            precentage = prediction;
            resultsText.setText(" " + df2.format(prediction) + "%");
            resultsText.append(" Depressed");
            findViewById(R.id.resultsText).setVisibility(View.VISIBLE);

            //Render the save and delete buttons
            saveOrDeleteRec();
        }

        @Override
        protected void onProgressUpdate(Void... param) {

        }
    }

    public void startChronometer(View v) {
        if (!running) {
            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            chronometer.start();
            running = true;
        }
    }
    public void resetChronometer(View v) {
        chronometer.setBase(SystemClock.elapsedRealtime());
        pauseOffset = 0;
    }
    public void pauseChronometer(View v) {
        if (running) {
            chronometer.stop();
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
            running = false;
        }
    }

}
