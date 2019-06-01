package com.example.noam.depressiondetectornew;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import static com.example.noam.depressiondetectornew.MyDBmanager.MyDBManagerItem.COLUMN_RECORDINGS_GSON;


/**
 * A simple {@link Fragment} subclass.
 */
public class StatisticsFragment extends Fragment {

    ListView list;
    MyDBmanager db;
    Utils utils;
    PieChart pieChart ;
    ArrayList<PieEntry> entries ;
    ArrayList<String> PieEntryLabels ;
    PieDataSet pieDataSet ;
    PieData pieData ;
    public StatisticsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_statistics, container, false);



        list = v.findViewById(R.id.statistics_list);
        db = Utils.getDB();
        utils = new Utils(getActivity());
        Cursor mCursor = db.getAllRowsUser();
        ArrayList<UserProfile> mDataSet = new ArrayList<UserProfile>();
        for(mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
            // The Cursor is now set to the right position
            UserProfile usertemp = new UserProfile();
            Gson gson = new Gson();
            usertemp.set_userId(mCursor.getInt(mCursor.getColumnIndex("_id")));
            usertemp.set_gender(mCursor.getString(mCursor.getColumnIndex("gender")));
            usertemp.set_firstName(mCursor.getString(mCursor.getColumnIndex("first_name")));
            usertemp.set_lastName(mCursor.getString(mCursor.getColumnIndex("last_name")));
            usertemp.set_phoneNumber(mCursor.getString(mCursor.getColumnIndex("phone_number")));
            usertemp.set_status(mCursor.getInt(mCursor.getColumnIndex("status")));
            usertemp.set_joinDate(mCursor.getString(mCursor.getColumnIndex("join_date")));
            String recordings = (mCursor.getString(mCursor.getColumnIndex(COLUMN_RECORDINGS_GSON)));
            ArrayList<Long> recordlist = gson.fromJson(recordings,new TypeToken<List<Long>>(){}.getType());
            usertemp.setRecordings(recordlist);
            mDataSet.add(usertemp);
        }


// instantiate the custom list adapter
        GraphAdapter adapter = new GraphAdapter(getActivity(), mDataSet);

// get the ListView and attach the adapter
        list.setAdapter(adapter);


        return v;
    }



}
