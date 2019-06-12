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
import java.util.Calendar;
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
        return context.getFilesDir().toString();
    }
    public String MakeCSV() {

        String workingDirectory = getFilesDirPath(getContext()); //this is the path for the CSV file!!!!!!
        String dataLine = "";
        double[] features = new double[989];
        double precentage;
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(workingDirectory + "/demo_arff.csv"));
            //       scanner = new Scanner(getResources().openRawResource(R.raw.depman));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while (scanner.hasNextLine()) {
            dataLine = scanner.nextLine();
        }
        return dataLine;
    }
    public double predictDepression(String dataLine) {

        double[] features = new double[989];

        List<String> line = CSVUtils.parseLine(dataLine); //Get the second line in CSV
        //        features = new double[line.size()];
        int j = 0;
        for (int i = 1; i < 990; i++) {
            features[j] = Double.parseDouble(line.get(i));
            j++;
        }
        //most_important = [439,440,54,21,39,933,775,224,420,390,49,47,391]

        //most_important = [54,21,39,933,775,224,420,390,49,47,391,383,72]
        String x= "0,984,477,470,461,480,458,5,24,965,504,10,2,501,500,509,14,7,502,508,511,900,512,510,513,956,17,15,503,505,18,6,19,16,843,9,938,786,8,881,11,496,767,495,520,805,862,292,497,824,406,1,3,919,976,710,789,311,766,387,844,349,672,880,888,368,787,808,691,434,901,899,26,903,576,596,842,539,558,748,330,653,884,785,770,615,519,595,273,425,788,729,634,823,922,846,235,941,890,577,882,861,929,796,804,795,893,870,444,926,854,930,908,102,937,927,775,931,83,790";
        String[] parts = x.split(",");
        double most_important[] = new double[parts.length];
        for(int i = 0; i<parts.length;i++)
            most_important[i] = features[Integer.valueOf(parts[i])];
       ////////// double most_important[] = {features[54],features[21],features[39],features[933],features[775],features[224],features[420]
             ////////////////   ,features[390],features[49],features[47],features[391],features[383],features[72]};
        //final int numOfDepTrees = pred1.score(most_important);
        //final int numOfNotDepTrees = 200-numOfDepTrees;
        //final int depression_thr = 139;
        RandomForestClassifier lgbm = new RandomForestClassifier();
        double ans = lgbm.score(most_important);
        //precentage = (double)((numOfDepTrees*100)/200);
        //Log.e("predMain","pred"+precentage);

        //return 100;
        return ans*100;
    }

    public int runOpenSmile(File wavFile) {
        try {
            Context context = getContext();
            String filesDirPath = getFilesDirPath(context);
            int returnValue;

            String smilePath = context.getApplicationInfo().nativeLibraryDir + "/libSMILExtract.so";
            String emobaseConfPath = filesDirPath + "/emobase.conf";
//            String wavFile = /*testTrimmedPath;*/wavFileInput.toString();

            String[] command = new String[]{smilePath, "-C", emobaseConfPath, "-I", wavFile.toString(), "-O", filesDirPath + "/demo_arff.csv"};

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

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy 'at' h:mm a");
        Date date = new Date();
        return dateFormat.format(date);
    }
    public static String getTimeSave() {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy 'at' h:mm a");
        String date = format.format(calendar.getTime());
        return date;
    }

    public long saveRecord(RecordingProfile voice_record){

        long insertData = getDB().addRecording(voice_record);

        if(insertData==-1){
            return -1;
            //return "PROBLEM: record was UN - Succesfully added yes!, Error trying to save the record!";
        }
        else{
            return insertData;
            //return "record was succesfully added";
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
