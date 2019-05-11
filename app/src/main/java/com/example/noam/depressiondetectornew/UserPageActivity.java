package com.example.noam.depressiondetectornew;

import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.daimajia.swipe.util.Attributes;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;

public class UserPageActivity extends AppCompatActivity {

    public static final String TAG = "UserPageActivity";
    private RecyclerView mRecyclerView;
    private ArrayList<RecordingProfile> mDataSet;
    SwipeRecyclerViewAdapterRec adapter;
    MyDBmanager db;
    Utils utils;
    TextView Name;
    TextView LastName;
    TextView Phone;
    TextView Status;
    TextView Date;
    TextView ID;
    LineChart mChart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);
        db = Utils.getDB();
        android.support.v7.app.ActionBar myActionBar = getSupportActionBar();
        myActionBar.hide();
        Name = (TextView) findViewById(R.id.name_and_last_name);
        Phone = (TextView) findViewById(R.id.phone_num_user);
        mChart = (LineChart) findViewById(R.id.line_chart);
        //mChart.setOnChartGestureListener(UserPageActivity.this);
        //mChart.setOnChartValueSelectedListener(UserPageActivity.this);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(false);

        ArrayList<Entry> yValues = new ArrayList<>();

        yValues.add(new Entry(0,60f));
        yValues.add(new Entry(1,50f));
        yValues.add(new Entry(2,70f));
        yValues.add(new Entry(3,30f));
        yValues.add(new Entry(4,50f));
        yValues.add(new Entry(5,60f));
        yValues.add(new Entry(6,65f));

        LineDataSet set1 = new LineDataSet(yValues,"data set 1");

        set1.setFillAlpha(110);
        set1.setColor(Color.BLUE);
        set1.setCircleColor(Color.YELLOW);
        set1.setCircleRadius(6f);
        set1.setLineWidth(3f);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        LineData data = new LineData(dataSets);

        mChart.setData(data);
        mChart.animateY(2000);
        long currentUserIdtemp = UserProfile.getcurrentUserId();
        UserProfile currentUser = db.getUserAt(currentUserIdtemp);

        Name.setText(currentUser.get_firstName() + " " + currentUser.get_lastName());
        Phone.setText(currentUser.get_phoneNumber());

        db = Utils.getDB();
        utils = new Utils(this);

        mDataSet = new ArrayList<RecordingProfile>();
        for(int i = 0; i < currentUser.getRecordings().size(); i++){
            mDataSet.add(db.getRecordingAt(currentUser.getRecordings().get(i)));
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view_user_page);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        /*
        mDataSet = new ArrayList<RecordingProfile>();
        RecordingProfile rec = new RecordingProfile(60, "record check1", "not relevant", 23, "6.5.2019 at 19:46PM", 5.30);
        RecordingProfile rec2 = new RecordingProfile(80, "record check2", "not relevant", 43, "6.5.2019 at 20:24PM", 66.30);
        mDataSet.add(rec);
        mDataSet.add(rec2);*/
        adapter = new SwipeRecyclerViewAdapterRec(this, mDataSet);
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

    }
}
