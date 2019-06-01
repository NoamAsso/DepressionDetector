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
    Utils utils;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=  inflater.inflate(R.layout.fragment_recordings, container, false);

        db = Utils.getDB();
        utils = new Utils(getActivity());
        Cursor mCursor = db.getAllRowsRecordings();
        mDataSet = new ArrayList<RecordingProfile>();
        for(mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
            // The Cursor is now set to the right position
            RecordingProfile rectemp = new RecordingProfile();
            rectemp.set_recId(mCursor.getInt(mCursor.getColumnIndex("_id")));
            rectemp.set__userId(mCursor.getInt(mCursor.getColumnIndex("user_id")));
            rectemp.set_recordName(mCursor.getString(mCursor.getColumnIndex("recording_name")));
            rectemp.set_prediction(mCursor.getInt(mCursor.getColumnIndex("prediction")));
            rectemp.set_time(mCursor.getString(mCursor.getColumnIndex("time_added")));
            rectemp.set_length(mCursor.getInt(mCursor.getColumnIndex("length")));
            rectemp.set_path(mCursor.getString(mCursor.getColumnIndex("file_path")));
            mDataSet.add(rectemp);
        }

        mRecyclerView = (RecyclerView) v.findViewById(R.id.my_recycler_view_rec);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        /*
        mDataSet = new ArrayList<RecordingProfile>();
        RecordingProfile rec = new RecordingProfile(60, "record check1", "not relevant", 23, "6.5.2019 at 19:46PM", 5.30);
        RecordingProfile rec2 = new RecordingProfile(80, "record check2", "not relevant", 43, "6.5.2019 at 20:24PM", 66.30);
        mDataSet.add(rec);
        mDataSet.add(rec2);*/
        adapter = new SwipeRecyclerViewAdapterRec(getActivity(), mDataSet);
        ((SwipeRecyclerViewAdapterRec) adapter).setMode(Attributes.Mode.Single);

        mRecyclerView.setAdapter(adapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.e("RecyclerView", "onScrollStateChanged");
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        return v;
    }


}
