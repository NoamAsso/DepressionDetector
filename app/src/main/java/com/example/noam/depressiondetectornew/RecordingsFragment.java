package com.example.noam.depressiondetectornew;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daimajia.swipe.util.Attributes;
import com.example.jean.jcplayer.model.JcAudio;
import com.example.jean.jcplayer.view.JcPlayerView;
import java.util.ArrayList;



public class RecordingsFragment extends Fragment {
    JcPlayerView jcplayerView;
    private RecyclerView mRecyclerView;
    private ArrayList<RecordingProfile> mDataSet;
    SwipeRecyclerViewAdapterRec adapter;
    MyDBmanager db;
    int dbSize;
    int currentFrom;
    private EndlessRecyclerViewScrollListener scrollListener;
    Utils utils;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=  inflater.inflate(R.layout.fragment_recordings, container, false);

        db = Utils.getDB();
        utils = new Utils(getActivity());
        dbSize = (int)db.getRecCount();

        //Cursor mCursor = db.getXrecs(1,7);
        Cursor mCursor = db.getAllRowsRecordings();
        currentFrom = 7;
        mDataSet = new ArrayList<RecordingProfile>();
        for(mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
            // The Cursor is now set to the right position
            RecordingProfile rectemp = new RecordingProfile();
            rectemp.set_recId(mCursor.getInt(mCursor.getColumnIndex("_id")));
            rectemp.set__userId(mCursor.getInt(mCursor.getColumnIndex("user_id")));
            rectemp.set_recordName(mCursor.getString(mCursor.getColumnIndex("recording_name")));
            rectemp.set_prediction(mCursor.getDouble(mCursor.getColumnIndex("prediction")));
            rectemp.set_time(mCursor.getString(mCursor.getColumnIndex("time_added")));
            rectemp.set_length(mCursor.getInt(mCursor.getColumnIndex("length")));
            rectemp.set_csv(mCursor.getString(mCursor.getColumnIndex("csv")));
            rectemp.set_path(mCursor.getString(mCursor.getColumnIndex("file_path")));
            rectemp.setPrediction_feedback(mCursor.getInt(mCursor.getColumnIndex("prediction_feedback")));
            mDataSet.add(rectemp);
        }

        mRecyclerView = (RecyclerView) v.findViewById(R.id.my_recycler_view_rec);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        /*
        mDataSet = new ArrayList<RecordingProfile>();
        RecordingProfile rec = new RecordingProfile(60, "record check1", "not relevant", 23, "6.5.2019 at 19:46PM", 5.30);
        RecordingProfile rec2 = new RecordingProfile(80, "record check2", "not relevant", 43, "6.5.2019 at 20:24PM", 66.30);
        mDataSet.add(rec);
        mDataSet.add(rec2);*/
        adapter = new SwipeRecyclerViewAdapterRec(getActivity(), mDataSet);
        ((SwipeRecyclerViewAdapterRec) adapter).setMode(Attributes.Mode.Single);
        mRecyclerView.setHasFixedSize(true);
        //mRecyclerView.setAutoMeasureEnabled(false);
        mRecyclerView.setAdapter(adapter);


        /*
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadNextDataFromApi(page);
            }
        };
        mRecyclerView.addOnScrollListener(scrollListener);
        */


        return v;
    }
    public void loadNextDataFromApi(int offset) {

        if(offset < dbSize){
            Cursor mCursor = db.getXrecs(offset,offset+7);
            for(mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
                // The Cursor is now set to the right position
                RecordingProfile rectemp = new RecordingProfile();
                rectemp.set_recId(mCursor.getInt(mCursor.getColumnIndex("_id")));
                rectemp.set__userId(mCursor.getInt(mCursor.getColumnIndex("user_id")));
                rectemp.set_recordName(mCursor.getString(mCursor.getColumnIndex("recording_name")));
                rectemp.set_prediction(mCursor.getDouble(mCursor.getColumnIndex("prediction")));
                rectemp.set_time(mCursor.getString(mCursor.getColumnIndex("time_added")));
                rectemp.set_length(mCursor.getInt(mCursor.getColumnIndex("length")));
                rectemp.set_csv(mCursor.getString(mCursor.getColumnIndex("csv")));
                rectemp.set_path(mCursor.getString(mCursor.getColumnIndex("file_path")));
                rectemp.setPrediction_feedback(mCursor.getInt(mCursor.getColumnIndex("prediction_feedback")));
                mDataSet.add(rectemp);
            }
            adapter.notifyItemRangeInserted(offset,7);
        }

        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`
    }

}
