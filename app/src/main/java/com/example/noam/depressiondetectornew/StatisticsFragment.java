package com.example.noam.depressiondetectornew;


import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
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
    BarChart barChart;
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


        db = Utils.getDB();
        Cursor mCursor1 = db.getAllRowsRecordings();
        ArrayList<RecordingProfile> mDataSet1 = new ArrayList<RecordingProfile>();
        for(mCursor1.moveToFirst(); !mCursor1.isAfterLast(); mCursor1.moveToNext()) {
            // The Cursor is now set to the right position
            RecordingProfile rectemp = new RecordingProfile();
            rectemp.set_recId(mCursor1.getInt(mCursor1.getColumnIndex("_id")));
            rectemp.set__userId(mCursor1.getInt(mCursor1.getColumnIndex("user_id")));
            rectemp.set_recordName(mCursor1.getString(mCursor1.getColumnIndex("recording_name")));
            rectemp.set_prediction(mCursor1.getInt(mCursor1.getColumnIndex("prediction")));
            rectemp.set_time(mCursor1.getString(mCursor1.getColumnIndex("time_added")));
            rectemp.set_length(mCursor1.getInt(mCursor1.getColumnIndex("length")));
            rectemp.set_path(mCursor1.getString(mCursor1.getColumnIndex("file_path")));
            mDataSet1.add(rectemp);
        }
        int depressed = 0, middle = 0, happy = 0;
        for(int i=0; i < mDataSet1.size(); i++){
            if (mDataSet1.get(i).get_prediction()>66f)
                depressed++;
            else if(mDataSet1.get(i).get_prediction()<33f)
                happy++;
            else
                middle++;
        }

        pieChart = (PieChart) v.findViewById(R.id.piechart);

        //pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5,10,5,5);

        pieChart.setDragDecelerationFrictionCoef(0.95f);

        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setTransparentCircleRadius(61f);

        ArrayList<PieEntry> yValues = new ArrayList<>();

        yValues.add(new PieEntry(34,"Depressed"));
        yValues.add(new PieEntry(10,"Not depressed"));
        yValues.add(new PieEntry(10,"Middle area"));

        PieDataSet dataSet = new PieDataSet(yValues,"");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(18f);
        data.setValueTextColor(Color.YELLOW);

        pieChart.setData(data);
        pieChart.animateY(2000);
        pieChart.setCenterText("All\nrecords");
        //pieChart.setCenterTextColor(android.R.color.white);
        pieChart.setCenterTextSize(20f);


        barChart = (BarChart) v.findViewById(R.id.barchart);

        //pieChart.setUsePercentValues(true);
        barChart.getDescription().setEnabled(false);
        ArrayList<BarEntry> yValsBar = new ArrayList<>();
        yValsBar.add(new BarEntry(1,10));
        yValsBar.add(new BarEntry(2,20));
        BarDataSet set = new BarDataSet(yValsBar,"Feedback");
        set.setColors(ColorTemplate.MATERIAL_COLORS);
        set.setDrawValues(true);
        ArrayList<String> labels = new ArrayList<>();
        labels.add("Good");
        labels.add("Bad");
        BarData data2 = new BarData(set);
        barChart.setData(data2);
        barChart.invalidate();
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.animateXY(1000,2000);

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
