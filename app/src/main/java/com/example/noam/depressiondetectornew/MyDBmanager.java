package com.example.noam.depressiondetectornew;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.example.noam.depressiondetectornew.OnDatabaseChangedListener;

import java.io.Serializable;

import static com.example.noam.depressiondetectornew.MyDBmanager.MyDBManagerItem.COLUMN_NAME_TIME_ADDED;
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
    public static final String DATABASE_NAME = "saved_recordings.db";
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

        public static final String COLUMN_NAME_PHONE_NUMBER = "phone_number";
        public static final String COLUMN_NAME_FIRST_NAME = "first_name";
        public static final String COLUMN_NAME_LAST_NAME = "last_name";
        public static final String COLUMN_NAME_STATUS = "status";
        public static final String COLUMN_NAME_JOIN_DATE = "join_date";


    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String RECORDING_TABLE =
            "CREATE TABLE " + TABLE_NAME_REC + " (" +
                    MyDBManagerItem._ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
                    MyDBManagerItem.USER_ID + " REAL " + COMMA_SEP +//////////////////
                    MyDBManagerItem.COLUMN_NAME_PREDICTION + " REAL " + COMMA_SEP +
                    MyDBManagerItem.COLUMN_NAME_RECORDING_NAME + TEXT_TYPE + COMMA_SEP +
                    MyDBManagerItem.COLUMN_NAME_RECORDING_FILE_PATH + TEXT_TYPE + COMMA_SEP +
                    MyDBManagerItem.COLUMN_NAME_RECORDING_LENGTH + " INTEGER " + COMMA_SEP +
                    MyDBManagerItem.COLUMN_NAME_TIME_ADDED + " INTEGER " + ")";
    private static final String USER_TABLE =
            "CREATE TABLE " + TABLE_NAME_USER + " (" +
                    MyDBManagerItem._ID + " INTEGER PRIMARY KEY" + COMMA_SEP +////////////////
                    MyDBManagerItem.COLUMN_NAME_PHONE_NUMBER + TEXT_TYPE + COMMA_SEP +
                    MyDBManagerItem.COLUMN_NAME_FIRST_NAME + TEXT_TYPE + COMMA_SEP +
                    MyDBManagerItem.COLUMN_NAME_LAST_NAME + TEXT_TYPE + COMMA_SEP +
                    MyDBManagerItem.COLUMN_NAME_STATUS + " INTEGER " + COMMA_SEP +
                    MyDBManagerItem.COLUMN_NAME_JOIN_DATE + " INTEGER " + ")";


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

    public RecordingProfile getItemAt(int position) {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                MyDBManagerItem._ID,
                MyDBManagerItem.COLUMN_NAME_RECORDING_NAME,
                MyDBManagerItem.COLUMN_NAME_RECORDING_FILE_PATH,
                MyDBManagerItem.COLUMN_NAME_RECORDING_LENGTH,
                MyDBManagerItem.COLUMN_NAME_TIME_ADDED,
                MyDBManagerItem.COLUMN_NAME_PREDICTION
        };
        Cursor c = db.query(RECORDING_TABLE, projection, null, null, null, null, null);
        if (c.moveToPosition(position)) {
            RecordingProfile item = new RecordingProfile();
            item.set_recordName(c.getString(c.getColumnIndex(MyDBManagerItem.COLUMN_NAME_RECORDING_NAME)));
            item.set_path(c.getString(c.getColumnIndex(MyDBManagerItem.COLUMN_NAME_RECORDING_FILE_PATH)));
            item.set_length(c.getInt(c.getColumnIndex(MyDBManagerItem.COLUMN_NAME_RECORDING_LENGTH)));
            item.set_time(c.getString(c.getColumnIndex(MyDBManagerItem.COLUMN_NAME_TIME_ADDED)));
            item.set_prediction(c.getDouble(c.getColumnIndex(MyDBManagerItem.COLUMN_NAME_PREDICTION)));
            c.close();
            return item;
        }
        return null;
    }

    public long addRecording(RecordingProfile voice_record) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(MyDBManagerItem.COLUMN_NAME_RECORDING_NAME, voice_record.get_recordName());
        cv.put(MyDBManagerItem.COLUMN_NAME_RECORDING_FILE_PATH, voice_record.get_path());
        cv.put(MyDBManagerItem.COLUMN_NAME_RECORDING_LENGTH, voice_record.get_length());
        cv.put(MyDBManagerItem.COLUMN_NAME_TIME_ADDED, voice_record.get_time());
        cv.put(MyDBManagerItem.COLUMN_NAME_PREDICTION, voice_record.get_prediction());

        long rowId = db.insert(RECORDING_TABLE, null, cv);

        if (mOnDatabaseChangedListener != null) {
            mOnDatabaseChangedListener.onNewDatabaseEntryAdded();
        }

        return rowId;
    }
    public static void setOnDatabaseChangedListener(OnDatabaseChangedListener listener) {
        mOnDatabaseChangedListener = listener;
    }
    public void removeItemWithId(int id) {
        SQLiteDatabase db = getWritableDatabase();
        String[] whereArgs = { String.valueOf(id) };
        db.delete(RECORDING_TABLE, "_ID=?", whereArgs);
    }
    public Cursor getAllRows(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + RECORDING_TABLE + " order by " + COLUMN_NAME_TIME_ADDED,null);
        return cursor;
    }


}
