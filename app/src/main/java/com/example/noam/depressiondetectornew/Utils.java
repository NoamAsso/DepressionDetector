package com.example.noam.depressiondetectornew;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Utils {
    private Context _context;
    private static MyDBmanager _db;

    public Utils(Context context){
        _context = context;
        if (_db == null) {
            _db = new MyDBmanager(context);
        }
    }

    public int getDuration(File wavfile) {

        String test = wavfile.toString();
        int len = (int)wavfile.length();
        Uri uri = Uri.parse(wavfile.toString());
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(getContext(),uri);
        String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        final int milliSeconds = Integer.parseInt(durationStr);
        return len;
    }
    public static MyDBmanager getDB(){
        return _db;
    }

    public Context getContext() {
        return _context;
    }

    public static String getFilesDirPath(Context context) {
        return Environment.getExternalStorageDirectory().toString();
    }

    public double predictDepression() {

        String workingDirectory = getFilesDirPath(getContext()); //this is the path for the CSV file!!!!!!
        String dataLine = "";
        double[] features = new double[989];
        double precentage;
        Scanner scanner = null;
        File file = new File( workingDirectory + File.separator + "AudioRecord"+"/demo_arff.csv" );
        try {
            scanner = new Scanner(file);
            //       scanner = new Scanner(getResources().openRawResource(R.raw.depman));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while (scanner.hasNextLine()) {
            dataLine = scanner.nextLine();
        }
        List<String> line = CSVUtils.parseLine(dataLine); //Get the second line in CSV
        //        features = new double[line.size()];
        int j = 0;
        for (int i = 1; i < 990; i++) {
            features[j] = Double.parseDouble(line.get(i));
            j++;
        }

        scanner.close();
        //most_important = [439,440,54,21,39,933,775,224,420,390,49,47,391]
        double most_important[] = {features[439],features[440],features[54],features[21],features[39],features[933],features[775]
                ,features[224],features[420],features[390],features[49],features[47],features[391]};
        //final int numOfDepTrees = pred1.score(most_important);
        //final int numOfNotDepTrees = 200-numOfDepTrees;
        //final int depression_thr = 139;
        /////////////////////RandomForestClassifier lgbm = new RandomForestClassifier();
        /////////////////////double ans = lgbm.score(most_important);
        //precentage = (double)((numOfDepTrees*100)/200);
        //Log.e("predMain","pred"+precentage);

        return 100;
        //return ans*100;
    }

    public int runOpenSmile(File wavFile) {
        try {
            Context context = getContext();
            String filesDirPath = getFilesDirPath(context);
            int returnValue;

            String smilePath = context.getApplicationInfo().nativeLibraryDir + "/libSMILExtract.so";
            String emobaseConfPath = filesDirPath + "/emobase.conf";
            String out_folder = filesDirPath + File.separator + "AudioRecord";
//            String wavFile = /*testTrimmedPath;*/wavFileInput.toString();

            String[] command = new String[]{smilePath, "-C", emobaseConfPath, "-I", wavFile.toString(), "-O", out_folder + "/demo_arff.csv"};

            final String ret;

            File workingDirectory = new File(filesDirPath);
            final Process process = new ProcessBuilder(command)
                    .directory(workingDirectory)
                    .redirectErrorStream(true)
                    .start();

            // Reads stdout.
            // NOTE: You can write to stdin of the command using
            //       process.getOutputStream().
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            int read;
            char[] buffer = new char[4096];
            StringBuffer output = new StringBuffer();

            while ((read = reader.read(buffer)) > 0) {
                output.append(buffer, 0, read);
            }
            reader.close();

            // Waits for the command to finish.
            process.waitFor();
            returnValue = process.exitValue();
            process.destroy();

            return returnValue;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public String saveRecord(RecordingProfile voice_record){

        long insertData = getDB().addRecording(voice_record);

        if(insertData==-1){
            return "PROBLEM: record was UN - Succesfully added yes!, Error trying to save the record!";
        }
        else{
            return "record was succesfully added";
        }
    }
    public String saveUser(UserProfile user){

        long insertData = getDB().addUser(user);

        if(insertData==-1){
            return "PROBLEM: record was UN - Succesfully added yes!, Error trying to save the record!";
        }
        else{
            return "record was succesfully added";
        }
    }
}
