package com.example.noam.depressiondetectornew;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.media.AudioFormat;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.audiofx.NoiseSuppressor;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.gauravk.audiovisualizer.visualizer.BarVisualizer;
import com.gauravk.audiovisualizer.visualizer.CircleLineVisualizer;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;

import me.bogerchan.niervisualizer.NierVisualizerManager;
import me.bogerchan.niervisualizer.renderer.IRenderer;
import me.bogerchan.niervisualizer.renderer.circle.CircleBarRenderer;
import me.bogerchan.niervisualizer.renderer.circle.CircleWaveRenderer;
import me.bogerchan.niervisualizer.renderer.columnar.ColumnarType1Renderer;
import me.bogerchan.niervisualizer.renderer.columnar.ColumnarType4Renderer;
import me.bogerchan.niervisualizer.renderer.line.LineRenderer;
import me.bogerchan.niervisualizer.renderer.other.ArcStaticRenderer;
import omrecorder.AudioChunk;
import omrecorder.AudioRecordConfig;
import omrecorder.OmRecorder;
import omrecorder.PullTransport;
import omrecorder.PullableSource;
import omrecorder.Recorder;

import static com.example.noam.depressiondetectornew.RecordWavMaster.getInstanceInit;

//import static com.example.noam.depressiondetectornew.RecordWavMaster.getBytes;
//import static com.example.noam.depressiondetectornew.RecordWavMaster.getInstanceInit;

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
    static int TEN_SECONDS = 100000;
    TextView resultsText;
    TextView titleText;
    ImageButton like;
    ImageButton dislike;
    Button btnSave, btnDelete;
    SurfaceView surface;
    RecordingProfile voice_record;
    NierVisualizerManager visualizerManager;
    CircleLineVisualizer circleVisual;
    Utils utils;
    String testTrimmedPath = "";



    File lastestFile;
    String audioFilePath;
    private String RECORD_WAV_PATH = Environment.getExternalStorageDirectory() + File.separator + "AudioRecord";
    Recorder recorder;
    ImageView recordButton;
    View general;
    private AVLoadingIndicatorView avi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording2);


        //circleVisual = (CircleLineVisualizer) findViewById(R.id.blast) ;
        //surface = (SurfaceView) findViewById(R.id.sv_wave);
        /*
        surface.setZOrderOnTop(true);    // necessary
        SurfaceHolder sfhTrackHolder = surface.getHolder();
        sfhTrackHolder.setFormat(PixelFormat.TRANSPARENT);
        */
        //pass the bytes to visualizer



        //setupRecorder();
        recordButton = (ImageButton) findViewById( R.id.btnRecord);
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                recorder.startRecording();
            }
        });




        //filesDirPath = Utils.getFilesDirPath(this);
        db = Utils.getDB();
        utils = new Utils(this);

        chronometer = findViewById(R.id.chronometer);
        chronometer.setFormat("%s");
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.setVisibility(View.GONE);

        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        recState = NOT_RECORDING_NOW;
        //mediaPlayer = new MediaPlayer();
        findViewById(R.id.btnSave).setVisibility(View.INVISIBLE);
        findViewById(R.id.btnDelete).setVisibility(View.INVISIBLE);
        avi= (AVLoadingIndicatorView) findViewById(R.id.avi);
        startTime = System.currentTimeMillis();
        setFilesDirPath();
        setButtonHandlers();
        enableButtons(true);
        findViewById(R.id.resultsText).setVisibility(View.GONE);
        resultsText = findViewById(R.id.results);
        titleText = findViewById(R.id.txtTitle);
        // custom_font = Typeface.createFromAsset(getAssets(),  "fonts/EncodeSans-Bold.ttf");
        btnSave = findViewById(R.id.btnSave);
        btnDelete =  findViewById(R.id.btnDelete);

        voice_record = new RecordingProfile();
        btnSave.setOnClickListener(btnClick);
        btnDelete.setOnClickListener(btnClick);

        resultsText.setVisibility(View.GONE);

        Log.d("STAM", "JUST TO CHECK");
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
        final String time = Utils.getTimeSave();
        final String defaultName = "Default name - ";
        //Get Audio duration time
        final int duration = utils.getDuration(lastestFile);
        final EditText recordName = myDialogView.findViewById(R.id.recordName);
        UserProfile usertemp = db.getUserAt(voice_record.get__userId());
        int size = usertemp.getRecordings().size() +1;
        String name = usertemp.get_firstName();
        recordName.setText(name + " Rec " +Integer.toString(size), TextView.BufferType.EDITABLE );
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

                }
                //latestRecFile.renameTo()
                //recordName.getText().toString(),latestRecFile.toString(),duration,time,precentage;
                voice_record.set_recordName(recordName.getText().toString());
                voice_record.set_path(lastestFile.toString());
                voice_record.set_length(duration);
                voice_record.set_time(time);
                voice_record.set_prediction(precentage);
                makePopupFeedback();
                dialog.dismiss();
            }
        });

        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                lastestFile.delete();
                dialog.dismiss();
            }
        });
        //   dialog.setNegativeButton("Cancel", null);
        dialog.create();
        dialog.show();
    }


    void makePopupList(){
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(RecordingActivity.this);
        builderSingle.setIcon(R.drawable.baseline_person_add_black_18dp);
        builderSingle.setTitle("Select One Name:-");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(RecordingActivity.this, android.R.layout.select_dialog_singlechoice);
        Cursor mCursor = db.getAllRowsUser();
        final ArrayList<UserProfile> users = new ArrayList<UserProfile>();
        for(mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
            // The Cursor is now set to the right position
            UserProfile usertemp = new UserProfile();
            usertemp.set_userId(mCursor.getInt(mCursor.getColumnIndex("_id")));
            usertemp.set_firstName(mCursor.getString(mCursor.getColumnIndex("first_name")));
            usertemp.set_lastName(mCursor.getString(mCursor.getColumnIndex("last_name")));
            usertemp.set_phoneNumber(mCursor.getString(mCursor.getColumnIndex("phone_number")));
            usertemp.set_status(mCursor.getInt(mCursor.getColumnIndex("status")));
            usertemp.set_joinDate(mCursor.getString(mCursor.getColumnIndex("join_date")));
            users.add(usertemp);
            arrayAdapter.add(usertemp.get_firstName());
        }


        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);
                AlertDialog.Builder builderInner = new AlertDialog.Builder(RecordingActivity.this);
                builderInner.setTitle("Your Selected Item is");
                builderInner.setMessage(strName);
                for(int i = 0 ;i <users.size();i++){

                    String name = users.get(i).get_firstName();
                    if(strName.equals(name)){
                        voice_record.set__userId(users.get(i).get_userId());
                        break;
                    }
                }
                makePopup();
            }
        });

        builderSingle.show();
    }

    void makePopupFeedback(){
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        final View myDialogView = inflater.inflate(R.layout.rename_feedback_dialogue, null);
        final String time = Utils.getTimeSave();
        final String defaultName = "Default name - ";
        //Get Audio duration time
        final int duration = utils.getDuration(lastestFile);

        like = myDialogView.findViewById(R.id.like);
        dislike = myDialogView.findViewById((R.id.dislike));

        //Build the dialog
        final AlertDialog.Builder dialog = new AlertDialog.Builder(
                RecordingActivity.this,
                R.style.MyDialogTheme
        );
        dialog.setTitle("Prediction feedback");
        dialog.setView(myDialogView);
        like.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                like.setBackgroundResource(R.drawable.event_page_background3);
                dislike.setBackgroundResource(0);
                like.setPressed(true);
                dislike.setPressed(false);
                return true;
            }
        });
        dislike.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                dislike.setBackgroundResource(R.drawable.event_page_background3);
                like.setBackgroundResource(0);
                dislike.setPressed(true);
                like.setPressed(false);
                return true;
            }
        });
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if(dislike.isPressed())
                    voice_record.setPrediction_feedback(0);
                else if(like.isPressed())
                    voice_record.setPrediction_feedback(1);
                //latestRecFile.renameTo()
                //recordName.getText().toString(),latestRecFile.toString(),duration,time,precentage;
                long recid = utils.saveRecord(voice_record);
                db.UpdateGson(voice_record.get__userId(),recid);
                dialog.dismiss();
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });

        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                long recid = utils.saveRecord(voice_record);
                db.UpdateGson(voice_record.get__userId(),recid);
                lastestFile.delete();
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

                        if(db.getUserCount()==0){
                            Toast.makeText(RecordingActivity.this, "No users detected! create a user first.",
                                    Toast.LENGTH_LONG).show();
                        }
                        else{
                            recState = RECORDING_NOW;
                            general = v;
                            resultsText.setTypeface(resultsText.getTypeface(), Typeface.NORMAL);
                            //   titleText.setVisibility(View.INVISIBLE);
                            //   titleText.setText("Press the MIC to stop and see results");

                            chronometer.setVisibility(View.VISIBLE);
                            startChronometer(v);
                            enableButtons(true);

                            v.setBackgroundResource(R.drawable.ic_microphone_live);

                            Time time = new Time();
                            time.setToNow();
                            audioFilePath = time.format("%Y%m%d%H%M%S");
                            setupRecorder();
                            recorder.startRecording();
                            resultsText.setText("Recording...");
                            resultsText.setVisibility(View.VISIBLE);


                            //////////////////////////////////recordWavMaster = new RecordWavMaster();
                            ////////////////////////////////int sessionId = recordWavMaster.getSession();

                            //visualizerManager = new NierVisualizerManager();

                            //circleVisual.setRawAudioBytes(getBytes());



                            //visualizerManager = getInstanceInit();
                            //////////////////////////////////recordWavMaster.startRecording();
                            //visualizerManager.start(surface, new IRenderer[]{new ColumnarType1Renderer()});


                            //mVisualizer.setAudioSessionId(0);
                            //mVisualizer.setDrawLine(true);
                        }

                        break;
                    } else {          //Now recording, needs to stop
                        recState = NOT_RECORDING_NOW;
                        chronometer.setVisibility(View.GONE);
                        resetChronometer(v);
                        pauseChronometer(v);

                        titleText.setText("Record for at least 10 second for prediction");
                        v.setBackgroundResource(R.drawable.ic_microphone);
                        try {
                            recorder.stopRecording();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        v.post(new Runnable() {
                            @Override public void run() {
                                animateVoice(0);
                            }
                        });
                        ////////////////////////////////////////////recordWavMaster.stopRecording();
                        //circleVisual.hide();


                        //visualizerManager.stop();
                        //visualizerManager.release();


                       //////////////////////////////latestRecFile = recordWavMaster.latestRecFile;
                        //////////////////////////////recordWavMaster.releaseRecord();
                        if(utils.getDuration(lastestFile)<TEN_SECONDS)
                        {
                            Toast.makeText(RecordingActivity.this, "Record was too short, no prediction was made",
                                    Toast.LENGTH_LONG).show();

                            lastestFile.delete();
                            returnBeginning();
                            return;
                        }
                        resultsText.setText("Calculating Prediction...");
                        avi.show();
                        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
                        btnSave.setVisibility(View.INVISIBLE);
                        btnDelete.setVisibility(View.INVISIBLE);

                        VoiceAnalysisAsyncTask runner = new VoiceAnalysisAsyncTask();
                        runner.execute(lastestFile);

                        break;
                    }
                }
                case R.id.btnDelete:{
                    lastestFile.delete();
                    returnBeginning();
                    break;
                }
                case R.id.btnSave:{
                    makePopupList();


                    //opens the UI name input and saves the record to the Database
                    //findViewById(R.id.txtTitle).setVisibility(View.INVISIBLE);
                    //returnBeginning();
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
        findViewById(R.id.resultsText).setVisibility(View.GONE);
        findViewById(R.id.results).setVisibility(View.GONE);
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
            String csv = utils.MakeCSV();
            voice_record.set_csv(csv);

            return utils.predictDepression(csv);
        }

        @Override
        protected void onPostExecute(Double prediction) {
            DecimalFormat df2 = new DecimalFormat("#.##");
            avi.hide();
            avi.setVisibility(View.GONE);
            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
            precentage = prediction;
            resultsText.setTypeface(resultsText.getTypeface(), Typeface.BOLD);
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










    private void setupRecorder() {
        recorder = OmRecorder.wav(
                new PullTransport.Default(mic(), new PullTransport.OnAudioChunkPulledListener() {
                    @Override public void onAudioChunkPulled(AudioChunk audioChunk) {
                        animateVoice((float) (audioChunk.maxAmplitude() / 200.0));
                    }
                }), file());
    }
    private void animateVoice(final float maxPeak) {
        recordButton.animate().scaleX(1 + maxPeak).scaleY(1 + maxPeak).setDuration(10).start();
    }
    private PullableSource mic() {
        return new PullableSource.Default(
                new AudioRecordConfig.Default(
                        MediaRecorder.AudioSource.MIC, AudioFormat.ENCODING_PCM_16BIT,
                        AudioFormat.CHANNEL_IN_MONO, 44100
                )
        );
    }
    @NonNull
    private File file() {
        //String x = Environment.getExternalStorageDirectory() + File.separator + "AudioRecord";
        //String x = Environment.getExternalStorageDirectory();
        lastestFile = new File(RECORD_WAV_PATH, audioFilePath + ".wav");
        if(!lastestFile.exists()){
            try {

                // returns pathnames for files and directory
                File directory = new File(RECORD_WAV_PATH);

                // create directories
                boolean bool = directory.mkdirs();

                // print
                System.out.print("Directory created? "+bool);

            } catch(Exception e) {
                // if any error occurs
                Log.e("ERORRRRRRRRRRRR","dkdkdkkdkdkdkkdkdkdkdkdkddkdkdkddkdkdkdkdkd");
                e.printStackTrace();
            }
        }
        else
        {
            Log.i("all gssssood","file existsssssssssssssssssssss");
        }
        return lastestFile;
}
}
