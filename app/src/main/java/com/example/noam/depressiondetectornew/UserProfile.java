package com.example.noam.depressiondetectornew;



public class UserProfile {

    private String _phoneNumber;
    private String _firstName;
    private String _lastName;
    private String _joinDate;
    private String _image;

    public UserProfile(String phoneNumber, String firstName, String lastName, String joinDate, String image){
    _phoneNumber= phoneNumber;
    _firstName= firstName;
    _lastName= lastName;
    _joinDate=  joinDate;
    _image= image;
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
}
