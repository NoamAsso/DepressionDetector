package com.example.noam.depressiondetectornew;


import android.graphics.Bitmap;

import java.util.ArrayList;

public class UserProfile {


    private long _userId;
    private String _phoneNumber;
    private String _firstName;
    private String _lastName;
    private int _status;
    private ArrayList<Long> recordings;
    private String _joinDate;//check how to save date
    private String _gender;//check how to save image
    private static long currentUserId;
    private boolean isClicked;


    public boolean isClicked() {
        return isClicked;
    }

    public void setClicked(boolean clicked) {
        isClicked = clicked;
    }

    public static long getcurrentUserId() {
        return currentUserId;
    }

    public static void setcurrentUserId(long userid) {
        UserProfile.currentUserId = userid;
    }

    public UserProfile(){
    }


    public UserProfile(int userId, String phoneNumber){
        _userId = userId;
        _phoneNumber = phoneNumber;
    }

    public UserProfile(String phoneNumber, String firstName, String lastName, String joinDate){
    _phoneNumber= phoneNumber;
    _firstName= firstName;
    _lastName= lastName;
    _joinDate=  joinDate;
    _status = 0;
    //_image= image;
    this.recordings = new ArrayList<Long>();

}

    public long get_userId() {
        return _userId;
    }

    public void set_userId(long _userId) {
        this._userId = _userId;
    }

    public String get_phoneNumber() {
        return _phoneNumber;
    }

    public String get_firstName() {
        return _firstName;
    }

    public String get_lastName() {
        return _lastName;
    }

    public int get_status() {
        return _status;
    }

    public void set_status(int _status) {
        this._status = _status;
    }
    public String get_joinDate() {
        return _joinDate;
    }
    public String get_gender() {
        return _gender;
    }

    public void set_gender(String _image) {
        this._gender = _image;
    }
    public void set_phoneNumber(String _phoneNumber) {
        this._phoneNumber = _phoneNumber;
    }

    public void set_firstName(String _firstName) {
        this._firstName = _firstName;
    }

    public void set_lastName(String _lastName) {
        this._lastName = _lastName;
    }

    public void set_joinDate(String _joinDate) {
        this._joinDate = _joinDate;
    }

    public ArrayList<Long> getRecordings() {
        if(recordings == null)
            recordings = new ArrayList<Long>();
        return recordings;
    }

    public void setRecordings(ArrayList<Long> recordings) {
        this.recordings = recordings;
    }

    public void add_recording(long rec_id){
        recordings.add(rec_id);
    }
    public void remove_recording(long rec_id){
        recordings.remove(rec_id);
    }
}
