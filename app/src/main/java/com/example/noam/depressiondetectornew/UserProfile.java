package com.example.noam.depressiondetectornew;


import android.graphics.Bitmap;

import java.util.ArrayList;

public class UserProfile {


    private int _userId;
    private String _phoneNumber;
    private String _firstName;
    private String _lastName;
    private String _status;
    private String _joinDate;//check how to save date
    private String _image;//check how to save image
    private ArrayList<Integer> recordings;

    public UserProfile(int userId, String phoneNumber){
        _userId = userId;
        _phoneNumber = phoneNumber;
    }

    public UserProfile(String phoneNumber, String firstName, String lastName, String joinDate, String image){
    _phoneNumber= phoneNumber;
    _firstName= firstName;
    _lastName= lastName;
    _joinDate=  joinDate;
    _image= image;
    this.recordings = new ArrayList<Integer>();

}

    public int get_userId() {
        return _userId;
    }

    public void set_userId(int _userId) {
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

    public String get_joinDate() {
        return _joinDate;
    }
    public String get_image() {
        return _image;
    }

    public void set_image(String _image) {
        this._image = _image;
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

    public ArrayList<Integer> getRecordings() {
        return recordings;
    }

    public void setRecordings(ArrayList<Integer> recordings) {
        this.recordings = recordings;
    }

    public void add_recording(int rec_id){
        recordings.add(rec_id);
    }
    public void remove_recording(int rec_id){
        recordings.remove(rec_id);
    }
}
