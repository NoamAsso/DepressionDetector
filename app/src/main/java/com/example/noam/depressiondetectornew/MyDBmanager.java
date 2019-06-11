package com.example.noam.depressiondetectornew;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.example.noam.depressiondetectornew.OnDatabaseChangedListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.provider.BaseColumns._ID;
import static com.example.noam.depressiondetectornew.MyDBmanager.MyDBManagerItem.COLUMN_NAME_JOIN_DATE;
import static com.example.noam.depressiondetectornew.MyDBmanager.MyDBManagerItem.COLUMN_NAME_TIME_ADDED;
import static com.example.noam.depressiondetectornew.MyDBmanager.MyDBManagerItem.COLUMN_RECORDINGS_GSON;
import static com.example.noam.depressiondetectornew.MyDBmanager.MyDBManagerItem.TABLE_NAME_REC;
import static com.example.noam.depressiondetectornew.MyDBmanager.MyDBManagerItem.TABLE_NAME_USER;

/**
 * This class is responsible for everything we wanna do in the DataBase
 * We implement the following actions: Delete, Play, Stop, Predict and Add a record.
 */
public class MyDBmanager extends SQLiteOpenHelper implements Serializable {

    private Context mContext;
    private static final String LOG_TAG = "MyDBmanager";
    private static OnDatabaseChangedListener mOnDatabaseChangedListener;
    public static final String DATABASE_NAME = "users_and_recordings.db";
    private static final int DATABASE_VERSION = 1;

    public static abstract class MyDBManagerItem implements BaseColumns {
        public static final String TABLE_NAME_REC = "saved_recordings";
        public static final String TABLE_NAME_USER = "saved_users";
        public static final String USER_ID = "user_id";
        public static final String COLUMN_NAME_RECORDING_NAME = "recording_name";
        public static final String COLUMN_NAME_RECORDING_FILE_PATH = "file_path";
        public static final String COLUMN_NAME_RECORDING_LENGTH = "length";
        public static final String COLUMN_NAME_TIME_ADDED = "time_added";
        public static final String COLUMN_NAME_PREDICTION = "prediction";
        public static final String COLUMN_NAME_CSV = "csv";
        public static final String COLUMN_NAME_FEEDBACK = "prediction_feedback";

        public static final String COLUMN_NAME_PHONE_NUMBER = "phone_number";
        public static final String COLUMN_NAME_FIRST_NAME = "first_name";
        public static final String COLUMN_NAME_LAST_NAME = "last_name";
        public static final String COLUMN_NAME_GENDER = "gender";
        public static final String COLUMN_RECORDINGS_GSON = "recordings_gson";
        public static final String COLUMN_NAME_STATUS = "status";
        public static final String COLUMN_NAME_JOIN_DATE = "join_date";


    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String RECORDING_TABLE =
            "CREATE TABLE " + TABLE_NAME_REC + " (" +
                    MyDBManagerItem._ID + " INTEGER PRIMARY KEY " + COMMA_SEP +
                    MyDBManagerItem.USER_ID + " REAL " + COMMA_SEP +//////////////////
                    MyDBManagerItem.COLUMN_NAME_PREDICTION + " REAL " + COMMA_SEP +
                    MyDBManagerItem.COLUMN_NAME_RECORDING_NAME + TEXT_TYPE + COMMA_SEP +
                    MyDBManagerItem.COLUMN_NAME_RECORDING_FILE_PATH + TEXT_TYPE + COMMA_SEP +
                    MyDBManagerItem.COLUMN_NAME_RECORDING_LENGTH + " INTEGER " + COMMA_SEP +
                    MyDBManagerItem.COLUMN_NAME_CSV + TEXT_TYPE + COMMA_SEP +
                    MyDBManagerItem.COLUMN_NAME_FEEDBACK + " INTEGER " + COMMA_SEP +
                    MyDBManagerItem.COLUMN_NAME_TIME_ADDED + " INTEGER " + ")";
    private static final String USER_TABLE =
            "CREATE TABLE " + TABLE_NAME_USER + " (" +
                    MyDBManagerItem._ID + " INTEGER PRIMARY KEY" + COMMA_SEP +////////////////
                    MyDBManagerItem.COLUMN_NAME_PHONE_NUMBER + TEXT_TYPE + COMMA_SEP +
                    MyDBManagerItem.COLUMN_NAME_FIRST_NAME + TEXT_TYPE + COMMA_SEP +
                    MyDBManagerItem.COLUMN_NAME_LAST_NAME + TEXT_TYPE + COMMA_SEP +
                    MyDBManagerItem.COLUMN_NAME_GENDER + TEXT_TYPE + COMMA_SEP +
                    MyDBManagerItem.COLUMN_NAME_STATUS + " INTEGER " + COMMA_SEP +
                    COLUMN_RECORDINGS_GSON + TEXT_TYPE + COMMA_SEP +
                    COLUMN_NAME_JOIN_DATE + TEXT_TYPE + ")";
    @SuppressWarnings("unused")
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + RECORDING_TABLE;

    public MyDBmanager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(RECORDING_TABLE);
        db.execSQL(USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //try
    //Get recording via id
    public RecordingProfile getRecordingAt(long position) {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                MyDBManagerItem._ID,
                MyDBManagerItem.USER_ID,
                MyDBManagerItem.COLUMN_NAME_RECORDING_NAME,
                MyDBManagerItem.COLUMN_NAME_RECORDING_FILE_PATH,
                MyDBManagerItem.COLUMN_NAME_RECORDING_LENGTH,
                MyDBManagerItem.COLUMN_NAME_CSV,
                MyDBManagerItem.COLUMN_NAME_FEEDBACK,
                MyDBManagerItem.COLUMN_NAME_TIME_ADDED,
                MyDBManagerItem.COLUMN_NAME_PREDICTION
        };
        Cursor c = db.query(TABLE_NAME_REC, projection, null, null, null, null, null);
        int pos = (int)position;
        if (c.moveToPosition(pos-1)) {
            RecordingProfile item = new RecordingProfile();
            item.set_recId(c.getInt(c.getColumnIndex(MyDBManagerItem._ID)));
            item.set__userId(c.getInt(c.getColumnIndex(MyDBManagerItem.USER_ID)));
            item.set_recordName(c.getString(c.getColumnIndex(MyDBManagerItem.COLUMN_NAME_RECORDING_NAME)));
            item.set_path(c.getString(c.getColumnIndex(MyDBManagerItem.COLUMN_NAME_RECORDING_FILE_PATH)));
            item.set_length(c.getInt(c.getColumnIndex(MyDBManagerItem.COLUMN_NAME_RECORDING_LENGTH)));
            item.set_time(c.getString(c.getColumnIndex(MyDBManagerItem.COLUMN_NAME_TIME_ADDED)));
            item.set_prediction(c.getDouble(c.getColumnIndex(MyDBManagerItem.COLUMN_NAME_PREDICTION)));
            item.set_csv(c.getString(c.getColumnIndex(MyDBManagerItem.COLUMN_NAME_CSV)));
            item.setPrediction_feedback(c.getInt(c.getColumnIndex(MyDBManagerItem.COLUMN_NAME_FEEDBACK)));
            c.close();
            return item;
        }
        return null;
    }
    //add recording to database
    public long addRecording(RecordingProfile voice_record) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(MyDBManagerItem.USER_ID,voice_record.get__userId());
        cv.put(MyDBManagerItem.COLUMN_NAME_CSV,voice_record.get_csv());
        cv.put(MyDBManagerItem.COLUMN_NAME_FEEDBACK,voice_record.getPrediction_feedback());
        cv.put(MyDBManagerItem.COLUMN_NAME_RECORDING_NAME, voice_record.get_recordName());
        cv.put(MyDBManagerItem.COLUMN_NAME_RECORDING_FILE_PATH, voice_record.get_path());
        cv.put(MyDBManagerItem.COLUMN_NAME_RECORDING_LENGTH, voice_record.get_length());
        cv.put(MyDBManagerItem.COLUMN_NAME_TIME_ADDED, voice_record.get_time());
        cv.put(MyDBManagerItem.COLUMN_NAME_PREDICTION, voice_record.get_prediction());

        long rowId = db.insert(TABLE_NAME_REC, null, cv);


        if (mOnDatabaseChangedListener != null) {
            mOnDatabaseChangedListener.onNewDatabaseEntryAdded();
        }

        return rowId;
    }
    public long addUser(UserProfile new_user) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        Gson gson = new Gson();
        String recordings= gson.toJson(new_user.getRecordings());
        cv.put(MyDBManagerItem.COLUMN_NAME_PHONE_NUMBER,new_user.get_phoneNumber());
        cv.put(MyDBManagerItem.COLUMN_NAME_FIRST_NAME,new_user.get_firstName());
        cv.put(MyDBManagerItem.COLUMN_NAME_LAST_NAME,new_user.get_lastName());
        cv.put(MyDBManagerItem.COLUMN_NAME_GENDER,new_user.get_gender());
        cv.put(COLUMN_RECORDINGS_GSON, recordings);
        cv.put(MyDBManagerItem.COLUMN_NAME_STATUS, new_user.get_status());
        cv.put(COLUMN_NAME_JOIN_DATE, new_user.get_joinDate());

        long rowId = db.insert(TABLE_NAME_USER, null, cv);


        if (mOnDatabaseChangedListener != null) {
            mOnDatabaseChangedListener.onNewDatabaseEntryAdded();
        }

        return rowId;
    }

    //Get recording via id
    public UserProfile getUserAt(long position) {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                MyDBManagerItem._ID,
                MyDBManagerItem.COLUMN_NAME_PHONE_NUMBER,
                MyDBManagerItem.COLUMN_NAME_FIRST_NAME,
                MyDBManagerItem.COLUMN_NAME_LAST_NAME,
                MyDBManagerItem.COLUMN_NAME_GENDER,
                COLUMN_RECORDINGS_GSON,
                MyDBManagerItem.COLUMN_NAME_STATUS,
                COLUMN_NAME_JOIN_DATE,
        };
        Cursor c1= db.rawQuery("select * from " + TABLE_NAME_USER + " where " + _ID + "='" + position + "'" , null);
        Cursor c = db.query(TABLE_NAME_USER, projection, null, null, null, null, null);
        int pos = (int)position;
        if (c1.moveToFirst()) {
            UserProfile item = new UserProfile();
            Gson gson = new Gson();
            String recordings;
            item.set_userId(c1.getInt(c1.getColumnIndex(MyDBManagerItem._ID)));
            item.set_phoneNumber(c1.getString(c1.getColumnIndex(MyDBManagerItem.COLUMN_NAME_PHONE_NUMBER)));
            item.set_firstName(c1.getString(c1.getColumnIndex(MyDBManagerItem.COLUMN_NAME_FIRST_NAME)));
            item.set_lastName(c1.getString(c1.getColumnIndex(MyDBManagerItem.COLUMN_NAME_LAST_NAME)));
            item.set_gender(c1.getString(c1.getColumnIndex(MyDBManagerItem.COLUMN_NAME_GENDER)));
            recordings = (c1.getString(c1.getColumnIndex(COLUMN_RECORDINGS_GSON)));
            item.set_status(c1.getInt(c1.getColumnIndex(MyDBManagerItem.COLUMN_NAME_STATUS)));
            item.set_joinDate(c1.getString(c1.getColumnIndex(COLUMN_NAME_JOIN_DATE)));
            ArrayList<Long> recordlist = gson.fromJson(recordings,new TypeToken<List<Long>>(){}.getType());
            item.setRecordings(recordlist);
            c1.close();
            return item;
        }
        return null;
    }

    public static void setOnDatabaseChangedListener(OnDatabaseChangedListener listener) {
        mOnDatabaseChangedListener = listener;
    }
    public void removeRecWithId(long id) {
        SQLiteDatabase db = getWritableDatabase();
        String[] whereArgs = { String.valueOf(id) };
        db.delete(TABLE_NAME_REC, "_ID=?", whereArgs);
    }
    public void removeUserWithId(long id) {
        SQLiteDatabase db = getWritableDatabase();
        UserProfile temp = getUserAt(id);
        if(getUserAt(id).getRecordings()!=null){
            ArrayList<Long> rectemp = getUserAt(id).getRecordings();
            for(int i=0 ; i < rectemp.size(); i++  ){
                removeRecWithId(rectemp.get(i));
            }
        }

        String[] whereArgs = { String.valueOf(id) };
        db.delete(TABLE_NAME_USER, "_ID=?", whereArgs);
    }

    public Cursor getAllRowsRecordings(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME_REC + " order by " + COLUMN_NAME_TIME_ADDED,null);
        return cursor;
    }
    public Cursor getAllRowsUser(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME_USER + " order by " + COLUMN_NAME_JOIN_DATE,null);
        return cursor;
    }
    public Cursor UpdateGson(long userId, long recId){
        UserProfile userTemp = getUserAt(userId);
        SQLiteDatabase db = getReadableDatabase();
        Gson gson = new Gson();
        ArrayList<Long> newTemp = userTemp.getRecordings();
        newTemp.add(recId);
        String recordings= gson.toJson(newTemp);
        Cursor cursor = db.rawQuery("UPDATE " +TABLE_NAME_USER + " SET " + COLUMN_RECORDINGS_GSON + " = '" + recordings + "'" + " WHERE " + _ID + " = " + userId,null);
        cursor.moveToFirst();
        cursor.close();
        return cursor;
    }
    public Cursor UpdateDelGson(long userId, long recId){
        UserProfile userTemp = getUserAt(userId);
        SQLiteDatabase db = getReadableDatabase();
        Gson gson = new Gson();
        ArrayList<Long> newTemp = userTemp.getRecordings();
        newTemp.remove(recId);
        String recordings= gson.toJson(newTemp);
        Cursor cursor = db.rawQuery("UPDATE " +TABLE_NAME_USER + " SET " + COLUMN_RECORDINGS_GSON + " = '" + recordings + "'" + " WHERE " + _ID + " = " + userId,null);
        cursor.moveToFirst();
        cursor.close();
        return cursor;
    }

    public void updateUser(UserProfile user){
        SQLiteDatabase db = getReadableDatabase();
        long id = user.get_userId();
        Cursor cursor1 = db.rawQuery("UPDATE " +TABLE_NAME_USER + " SET " + MyDBManagerItem.COLUMN_NAME_FIRST_NAME + " = '" + user.get_firstName() + "'" + " WHERE " + _ID + " = " + id,null);
        cursor1.moveToFirst();
        cursor1.close();
        Cursor cursor2 = db.rawQuery("UPDATE " +TABLE_NAME_USER + " SET " + MyDBManagerItem.COLUMN_NAME_LAST_NAME + " = '" + user.get_lastName() + "'" + " WHERE " + _ID + " = " + id,null);
        cursor2.moveToFirst();
        cursor2.close();
        Cursor cursor3 = db.rawQuery("UPDATE " +TABLE_NAME_USER + " SET " + MyDBManagerItem.COLUMN_NAME_PHONE_NUMBER + " = '" + user.get_phoneNumber() + "'" + " WHERE " + _ID + " = " + id,null);
        cursor3.moveToFirst();
        cursor3.close();
        Cursor cursor4 = db.rawQuery("UPDATE " +TABLE_NAME_USER + " SET " + MyDBManagerItem.COLUMN_NAME_GENDER + " = '" + user.get_phoneNumber() + "'" + " WHERE " + _ID + " = " + id,null);
        cursor4.moveToFirst();
        cursor4.close();
        //return 0;
    }

    public long getUserCount(){
        SQLiteDatabase db = getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, TABLE_NAME_USER);
        db.close();
        return count;
    }
    public long getRecCount(){
        SQLiteDatabase db = getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, TABLE_NAME_REC);
        db.close();
        return count;
    }

    public Cursor SearchUserDb(String s){
        SQLiteDatabase db = getReadableDatabase();
        //Select id from sometable where name like '%omm%'
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME_USER + " where "
                + MyDBManagerItem.COLUMN_NAME_GENDER + " like " + "'%"+s+"%'"
                + " or " + MyDBManagerItem.COLUMN_NAME_GENDER + " like " + "'%"+s+"%'"
                + " or " + MyDBManagerItem.COLUMN_NAME_PHONE_NUMBER + " like " + "'%"+s+"%'"
                + " or " + MyDBManagerItem.COLUMN_NAME_FIRST_NAME + " like " + "'%"+s+"%'"
                + " or " + MyDBManagerItem.COLUMN_NAME_LAST_NAME + " like " + "'%"+s+"%'",null);
        //db.rawQuery("UPDATE " +TABLE_NAME_USER + " SET " + MyDBManagerItem.COLUMN_NAME_FIRST_NAME + " = '" + user.get_firstName() + "'" + " WHERE " + _ID + " = " + id,null);
        return cursor;
    }
    public Cursor SearchRecDb(String s){
        SQLiteDatabase db = getReadableDatabase();
        //Select id from sometable where name like '%omm%'
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME_REC + " where "
                + MyDBManagerItem.COLUMN_NAME_RECORDING_NAME + " like " + "'%"+s+"%'",null);
        //db.rawQuery("UPDATE " +TABLE_NAME_USER + " SET " + MyDBManagerItem.COLUMN_NAME_FIRST_NAME + " = '" + user.get_firstName() + "'" + " WHERE " + _ID + " = " + id,null);
        return cursor;
    }
}
