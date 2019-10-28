package com.example.noam.depressiondetectornew;

import android.Manifest;
import android.app.Activity;
import android.arch.lifecycle.Lifecycle;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;
import com.luseen.spacenavigation.SpaceOnLongClickListener;

import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final int REC_ACTIVITY_REQUEST = 1;
    private static final int REGISTER_ACTIVITY_REQUEST = 2;

    private static final int IMPORT_WAV = 3;
    private double precentage;
    RecordingProfile voice_record;
    File wavFile;
    int flaging;
    ImageButton like;
    ImageButton dislike;
    File currentDB;
    File backupDB2;
    public static MyDBmanager db;
    public int flag = 0;
    //this.deleteDatabase(DATABASE_NAME);
    Utils utils;
    String filesDirPath;
    private SpaceNavigationView spaceNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //loading the default fragment
        loadFragment(new PeopleFragment());
        filesDirPath = Utils.getFilesDirPath(this);
        db= Utils.getDB();
        utils = new Utils(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Depression Detector");
        setSupportActionBar(toolbar);
        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),RecordingActivity.class);
                startActivity(intent);
            }
        });
        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(intent);
            }

        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        //navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        spaceNavigationView = (SpaceNavigationView) findViewById(R.id.space);
        spaceNavigationView.initWithSaveInstanceState(savedInstanceState);
        spaceNavigationView.addSpaceItem(new SpaceItem("Patients", R.drawable.ic_round_account_circle_24px));
        spaceNavigationView.addSpaceItem(new SpaceItem("Recordings", R.drawable.ic_baseline_record_voice_over_24px));
        spaceNavigationView.addSpaceItem(new SpaceItem("Search", R.drawable.ic_baseline_search_24px));
        spaceNavigationView.addSpaceItem(new SpaceItem("Statistics", R.drawable.ic_baseline_bar_chart_24px));
        //spaceNavigationView.shouldShowFullBadgeText(true);
        spaceNavigationView.setCentreButtonIconColorFilterEnabled(false);
        spaceNavigationView.showIconOnly();
        //spaceNavigationView.animate();
        spaceNavigationView.setFocusable(true);
        spaceNavigationView.setSpaceItemIconSizeInOnlyIconMode(70);

        spaceNavigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                Log.d("onCentreButtonClick ", "onCentreButtonClick");
                spaceNavigationView.shouldShowFullBadgeText(true);
                Intent i = new Intent(getApplicationContext(), RecordingActivity.class);
                startActivityForResult(i, REC_ACTIVITY_REQUEST);
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
                Log.d("onItemClick ", "" + itemIndex + " " + itemName);
                Fragment fragment = null;
                switch (itemIndex) {
                    case 0:
                        //toolbar.setTitle("Home");
                        //setSupportActionBar(toolbar);
                        fragment = new PeopleFragment();
                        break;

                    case 1:
                        //toolbar.setTitle("Patients");
                        //setSupportActionBar(toolbar);
                        fragment = new RecordingsFragment();
                        break;
                    case 2:
                        //toolbar.setTitle("Patients");
                        //setSupportActionBar(toolbar);
                        fragment = new HomeFragment();
                        break;
                    case 3:
                        //toolbar.setTitle("Patients");
                        //setSupportActionBar(toolbar);
                        fragment = new StatisticsFragment();
                        break;
                }
                loadFragment(fragment);
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {
                Log.d("onItemReselected ", "" + itemIndex + " " + itemName);
                Log.d("onItemClick ", "" + itemIndex + " " + itemName);
                Fragment fragment = null;
                switch (itemIndex) {
                    case 0:
                        //toolbar.setTitle("Home");
                        //setSupportActionBar(toolbar);
                        fragment = new PeopleFragment();
                        break;

                    case 1:
                        //toolbar.setTitle("Patients");
                        //setSupportActionBar(toolbar);
                        fragment = new RecordingsFragment();
                        break;
                }
                loadFragment(fragment);

            }

        });

        spaceNavigationView.setSpaceOnLongClickListener(new SpaceOnLongClickListener() {
            @Override
            public void onCentreButtonLongClick() {
                Toast.makeText(MainActivity.this, "Record", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(int itemIndex, String itemName) {
                Toast.makeText(MainActivity.this,itemName, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
            Toast.makeText(this,"Settings will be available soon.",Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            importAudioFile();

            // Handle the camera action
        } else if (id == R.id.nav_slideshow) {
            Intent i = new Intent(getApplicationContext(), AboutActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_share) {
            Intent i = new Intent(getApplicationContext(), FAQActivity.class);
            startActivity(i);
            //Toast.makeText(this,"FAQ will be available soon.",Toast.LENGTH_LONG).show();
        } else if (id == R.id.nav_send) {
            exportDB();
            sendEmail();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    //toolbar.setTitle("Home");
                    //setSupportActionBar(toolbar);
                    fragment = new HomeFragment();
                    break;

                case R.id.navigation_dashboard:
                    //toolbar.setTitle("Patients");
                    //setSupportActionBar(toolbar);
                    fragment = new PeopleFragment();
                    break;

                case R.id.navigation_notifications:
                    //toolbar.setTitle("Recordings");
                    //setSupportActionBar(toolbar);
                    fragment = new RecordingsFragment();
                    break;
            }

            return loadFragment(fragment);

        }
    };
    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
    private boolean reloadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commitAllowingStateLoss();
            return true;
        }
        return false;
    }
    @Override
    public void onResume(){
        super.onResume();
        Fragment fragRefresh = getSupportFragmentManager().getPrimaryNavigationFragment();
        if(flag == 1)
            reloadFragment(new RecordingsFragment());
        else if(flag == 2)
            reloadFragment(new PeopleFragment());
        else if(flag == 3)
            reloadFragment(new RecordingsFragment());
        else
            reloadFragment(fragRefresh);
        // put your code here...

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        //getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED);
        switch (requestCode){
            case REC_ACTIVITY_REQUEST:
                if (resultCode == RESULT_OK || resultCode == 0 ) {

                    flag = 1;
                }
                break;
            case REGISTER_ACTIVITY_REQUEST:
                if (resultCode == RESULT_OK || resultCode == 0) {
                    flag = 2;
                }
                break;
            case IMPORT_WAV:
                if (resultCode == RESULT_OK || resultCode == 0){
                    flag = 3;
                    if(data != null)
                        importwav(data);
                }

        }

    }
    private void importAudioFile() {
        new MaterialFilePicker()
                .withActivity(MainActivity.this)
                .withRequestCode(IMPORT_WAV)
                //     .withFilter(Pattern.compile(".*\\.wav$")) // Filtering files and directories by file name using regexp
                //      .withFilterDirectories(true) // Set directories filterable (false by default)
                .withHiddenFiles(true) // Show hidden files and folders
                .start();
    }


    private void importwav(Intent data){
        final String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
        //Path p = Paths.get(filePath);
        //final String fileName = data.getStringExtra(FilePickerActivity);
        String filenameArray[] = filePath.split("\\.");
        String extension = filenameArray[filenameArray.length-1];
        flaging = 0;

        if(extension.equals("wav")) { //User imported a wav file
            if(Utils.getDB().getUserCount()!=0){
                wavFile = new File(filePath);
                System.out.println(wavFile.getName());
                VoiceAnalysisAsyncTask runner = new VoiceAnalysisAsyncTask();
                runner.execute(wavFile);

                final String time = Utils.getTime();
                final int duration = utils.getDuration(wavFile);
                final String defaultName = "Default name - ";
                //while(flaging==0);
                voice_record = new RecordingProfile();

            }
            else{
                Toast.makeText(this,"No user detected! create a user before importing",Toast.LENGTH_LONG).show();
                return;
            }
        }
        else {
            Toast.makeText(this,"Wrong file type! please choose a WAV file",Toast.LENGTH_LONG).show();
            return;
        }


    }

    void makePopup(){
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        final View myDialogView = inflater.inflate(R.layout.rename_save_dialogue, null);
        final String time = Utils.getTimeSave();
        final String defaultName = "Default name - ";
        //Get Audio duration time
        final int duration = utils.getDuration(wavFile);
        final EditText recordName = myDialogView.findViewById(R.id.recordName);
        UserProfile usertemp = db.getUserAt(voice_record.get__userId());
        int size = usertemp.getRecordings().size() +1;
        String name = usertemp.get_firstName();
        recordName.setText(wavFile.getName() + " Rec " +Integer.toString(size), TextView.BufferType.EDITABLE );
        //Build the dialog
        final AlertDialog.Builder dialog = new AlertDialog.Builder(
                MainActivity.this,
                R.style.MyDialogTheme
        );
        dialog.setTitle("Please name the recording:");
        dialog.setView(myDialogView);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if(recordName.getText().toString().equals(""))
                {

                }
                //latestRecFile.renameTo()
                //recordName.getText().toString(),latestRecFile.toString(),duration,time,precentage;

                voice_record.set_recordName(recordName.getText().toString());
                voice_record.set_path(wavFile.toString());
                voice_record.set_length(duration);
                voice_record.set_time(time);
                voice_record.set_prediction(precentage);
                makePopupFeedback();
                dialog.dismiss();
            }
        });

        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                wavFile.delete();
                dialog.dismiss();
            }
        });
        //   dialog.setNegativeButton("Cancel", null);
        dialog.create();
        dialog.show();
    }


    void makePopupList(){
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(MainActivity.this);
        builderSingle.setIcon(R.drawable.baseline_person_add_black_18dp);
        builderSingle.setTitle("Attach recording to patient");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.select_dialog_singlechoice);
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
                AlertDialog.Builder builderInner = new AlertDialog.Builder(MainActivity.this);
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
        TextView predDialog = (TextView) myDialogView.findViewById(R.id.pred_dialog);
        predDialog.setText(String.format("%.2f", voice_record.get_prediction())+"% depressed");
        //Get Audio duration time
        final int duration = utils.getDuration(wavFile);

        like = myDialogView.findViewById(R.id.like);
        dislike = myDialogView.findViewById((R.id.dislike));

        //Build the dialog
        final AlertDialog.Builder dialog = new AlertDialog.Builder(
                MainActivity.this,
                R.style.MyDialogTheme
        );
        like.setPressed(true);
        dislike.setPressed(false);
        dialog.setTitle("Prediction feedback");
        like.setBackgroundResource(R.drawable.event_page_background3);
        dialog.setView(myDialogView);
        like.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                like.setBackgroundResource(R.drawable.event_page_background3);
                dislike.setBackgroundResource(0);
                dislike.setPressed(false);
                like.setPressed(true);
                return true;
            }
        });
        dislike.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                dislike.setBackgroundResource(R.drawable.event_page_background3);
                like.setBackgroundResource(0);
                like.setPressed(false);
                dislike.setPressed(true);
                return true;
            }
        });
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if(dislike.isPressed() == true || like.isPressed() == true){
                    if(dislike.isPressed())
                        voice_record.setPrediction_feedback(0);
                    else if(like.isPressed())
                        voice_record.setPrediction_feedback(1);
                    //latestRecFile.renameTo()
                    //recordName.getText().toString(),latestRecFile.toString(),duration,time,precentage;
                    long recid = utils.saveRecord(voice_record);
                    db.UpdateGson(voice_record.get__userId(),recid);
                    reloadFragment(new RecordingsFragment());
                    dialog.dismiss();
                    //Intent returnIntent = new Intent();
                    //setResult(Activity.RESULT_OK,returnIntent);
                }

            }
        });

        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                long recid = utils.saveRecord(voice_record);
                db.UpdateGson(voice_record.get__userId(),recid);
                wavFile.delete();
                dialog.dismiss();
                reloadFragment(new RecordingsFragment());

            }
        });
        //   dialog.setNegativeButton("Cancel", null);
        dialog.create();
        dialog.show();
        //dialog.getWindow().setBackgroundDrawableResource(android.R.color.background_dark);
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
            Utils utils = new Utils(MainActivity.this);
            openSmileExitValue = utils.runOpenSmile(wavFile[0]);
            if (openSmileExitValue != 0) {
                Log.e("Opensmile", "openSMILE failed with error code " + openSmileExitValue);
            }
            String csv = utils.MakeCSV();
            voice_record.set_csv(csv);
            flaging = 1;
            return utils.predictDepression(csv);

        }

        @Override
        protected void onPostExecute(Double prediction) {
            precentage = prediction;
            makePopupList();
        }

        @Override
        protected void onProgressUpdate(Void... param) {

        }
    }


    public void sendEmail()
    {
        try
        {
            //email = et_email.getText().toString();
            //subject = et_subject.getText().toString();
            //message = et_message.getText().toString();
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            Uri path = Uri.fromFile(backupDB2);
            final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
            emailIntent.setType("plain/text");
            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,new String[] { "assoulinenoam@gmail.com" });
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Depression Detection Database");
            if (backupDB2 != null) {
                emailIntent.putExtra(Intent.EXTRA_STREAM, path);
            }
            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "this is the database!");
            this.startActivity(Intent.createChooser(emailIntent,"Sending email..."));
        }
        catch (Throwable t)
        {
            Toast.makeText(this, "Request failed try again: " + t.toString(),Toast.LENGTH_LONG).show();
        }
    }
    private void exportDB(){
        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();
        FileChannel source=null;
        FileChannel destination=null;
        String currentDBPath = "/data/"+ "com.example.noam.depressiondetectornew" +"/databases/"+"users_and_recordings.db";
        String backupDBPath = "users_and_recordings.db";
        currentDB = new File(data, currentDBPath);
        backupDB2 = new File(sd, backupDBPath);

        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB2).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            Toast.makeText(this, "DB Exported!", Toast.LENGTH_LONG).show();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
