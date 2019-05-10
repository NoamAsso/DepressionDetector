package com.example.noam.depressiondetectornew;

public class RecordingProfile {


    private long _recId;
    private long __userId;
    private String _recordName; // file name
    private String _path; //file path
    private int _length; // length of recording in seconds
    private String _time; // date/time of the recording
    private String _csv;//features x
    private int prediction_feedback;// prediction y
    private double _prediction; //the prediction
    private boolean isClicked;


    public RecordingProfile() {
    }

    public RecordingProfile(int __userId, String _recordName, String _path, int _length, String _time, double _prediction) {
        this._recId = _recId;
        this.__userId = __userId;
        this._recordName = _recordName;
        this._path = _path;
        this._length = _length;
        this._time = _time;
        this._prediction = _prediction;
        this.isClicked = false;
    }

    public boolean isClicked() {
        return isClicked;
    }

    public void setClicked(boolean clicked) {
        isClicked = clicked;
    }

    public String get_csv() {
        return _csv;
    }

    public void set_csv(String _csv) {
        this._csv = _csv;
    }

    public int getPrediction_feedback() {
        return prediction_feedback;
    }

    public void setPrediction_feedback(int prediction_feedback) {
        this.prediction_feedback = prediction_feedback;
    }

    public long get__userId() {
        return __userId;
    }

    public void set__userId(long __userId) {
        this.__userId = __userId;
    }

    public String get_recordName() {
        return _recordName;
    }

    public void set_recordName(String _recordName) {
        this._recordName = _recordName;
    }

    public String get_path() {
        return _path;
    }

    public void set_path(String _path) {
        this._path = _path;
    }

    public int get_length() {
        return _length;
    }

    public void set_length(int _length) {
        this._length = _length;
    }

    public String get_time() {
        return _time;
    }

    public void set_time(String _time) {
        this._time = _time;
    }

    public double get_prediction() {
        return _prediction;
    }

    public void set_prediction(double _prediction) {
        this._prediction = _prediction;
    }

    public long get_recId() {
        return _recId;
    }

    public void set_recId(long _recId) {
        this._recId = _recId;
    }
}
