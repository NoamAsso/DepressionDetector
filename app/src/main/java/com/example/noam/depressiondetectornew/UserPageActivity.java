package com.example.noam.depressiondetectornew;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.swipe.util.Attributes;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.MPPointF;

import java.security.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

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
    TextView uId;
    TextView status;
    ImageView statusImage;
    TextView Date;
    TextView ID;
    ImageView image;
    LineChart mChart;
    YAxis yAxis;
    Long reference_timestamp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);
        db = Utils.getDB();
        android.support.v7.app.ActionBar myActionBar = getSupportActionBar();
        myActionBar.hide();
        Name = (TextView) findViewById(R.id.name_and_last_name);
        Phone = (TextView) findViewById(R.id.phone_num_user);
        image = (ImageView) findViewById(R.id.gender_image);
        status = (TextView) findViewById(R.id.status_user);
        statusImage = (ImageView) findViewById(R.id.status_icon);
        mChart = (LineChart) findViewById(R.id.line_chart);
        uId = (TextView) findViewById(R.id.user_id);

        //mChart.setOnChartGestureListener(UserPageActivity.this);
        //mChart.setOnChartValueSelectedListener(UserPageActivity.this);

        long currentUserIdtemp = UserProfile.getcurrentUserId();
        UserProfile currentUser = db.getUserAt(currentUserIdtemp);

        Name.setText(currentUser.get_firstName() + " " + currentUser.get_lastName());
        Phone.setText(currentUser.get_phoneNumber());
        uId.setText("Patient id: "+ String.format("%d", currentUser.get_userId()));


        if(currentUser.get_gender() == "Female"){
            image.setBackgroundResource(R.drawable.ic_circle_icon_woman);
        }
        db = Utils.getDB();
        utils = new Utils(this);

        mDataSet = new ArrayList<RecordingProfile>();
        for (int i = 0; i < currentUser.getRecordings().size(); i++) {
            mDataSet.add(db.getRecordingAt(currentUser.getRecordings().get(i)));
        }

        if(mDataSet.isEmpty()){
            statusImage.setBackgroundResource(R.drawable.ic_information);
            status.setText( "No predictions detected");
        }
        else {
            double pred = mDataSet.get(mDataSet.size() - 1).get_prediction();
            status.setText(String.format("%f", pred) + " depressed");
            if(pred > 66f)
                statusImage.setBackgroundResource(R.drawable.ic_happy);
            else if(pred < 33f)
                statusImage.setBackgroundResource(R.drawable.ic_sad);
            else
                statusImage.setBackgroundResource(R.drawable.ic_confused);
        }

        ///////////////////////////////////////

        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(false);

        ArrayList<Entry> yValues = new ArrayList<>();

        if (mDataSet.size() > 0) {
            for (int i = 0; i < mDataSet.size(); i++) {
                String date = mDataSet.get(i).get_time();
                java.util.Date date1 = null;
                //SimpleDateFormat formatter1=new SimpleDateFormat("dd/MM/yyyy 'at' h:mm a");
                try {
                    SimpleDateFormat formatter1 = new SimpleDateFormat("dd/MM/yyyy 'at' h:mm a");
                    date1 = formatter1.parse(date);
                } catch (java.text.ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                if (i == 0)
                    reference_timestamp = date1.getTime();

                long Xnew = date1.getTime() - reference_timestamp;
                yValues.add(new Entry((float) Xnew, (float) mDataSet.get(i).get_prediction()));

            }
            ValueFormatter xAxisFormatter = new HourAxisValueFormatter(reference_timestamp);
            XAxis xAxis = mChart.getXAxis();
            xAxis.setValueFormatter(xAxisFormatter);

            LineDataSet set1 = new LineDataSet(yValues, "data set 1");

            set1.setFillAlpha(110);
            set1.setColor(Color.BLUE);
            set1.setCircleColor(Color.YELLOW);
            set1.setCircleRadius(6f);
            set1.setLineWidth(3f);
            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            LineData data = new LineData(dataSets);
            MyMarkerView myMarkerView = new MyMarkerView(getApplicationContext(), R.layout.custom_marker_view, reference_timestamp);
            yAxis = mChart.getAxisLeft();
            yAxis.setAxisMaximum(100f);
            yAxis.setAxisMinimum(0f);
            mChart.setMarkerView(myMarkerView);
            mChart.setData(data);
            mChart.animateY(2000);
        } else{
            yAxis = mChart.getAxisLeft();
            yAxis.setAxisMaximum(100f);
            yAxis.setAxisMinimum(0f);
            LineData data = new LineData(); // use your valid x-values array here
            mChart.setData(data);
            mChart.invalidate();
            //mChart.setAutofillHints("No predictions was made");
            //mchart.set
        }

        ///////////////////////////////////////////////
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

class HourAxisValueFormatter extends ValueFormatter {

    private long referenceTimestamp;
    private DateFormat mDataFormat;
    private Date mDate;

    /**
     * Constructor of the formatter.
     *
     * @param referenceTimestamp minimum timestamp of the data set (in seconds).
     */
    public HourAxisValueFormatter(long referenceTimestamp) {
        this.referenceTimestamp = referenceTimestamp;
        this.mDataFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        this.mDate = new Date();
    }

    /**
     * Called when a value from an axis is to be formatted before being drawn.
     *
     * @param value the value to be formatted.
     * @param axis  the axis the value belongs to.
     * @return timestamp formatted.
     */
    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        // relativeTimestamp = originalTimestamp - referenceTimestamp
        long relativeTimestamp = (long) value;
        // Retrieve absolute timestamp (referenceTimestamp + relativeTimestamp)
        long originalTimestamp = referenceTimestamp + relativeTimestamp;
        // Convert timestamp to hour:minute
        return getHour(originalTimestamp);
    }

    /**
     * Get formatted hour from a timestamp.
     *
     * @param timestamp timestamp to format.
     * @return formatted hour.
     */
    private String getHour(long timestamp) {
        mDate.setTime(timestamp * 1000); // Convert from seconds to milliseconds
        return mDataFormat.format(mDate);
    }
}
class MyMarkerView extends MarkerView {

    private TextView tvContent;
    private long referenceTimestamp;  // minimum timestamp in your data set
    private DateFormat mDataFormat;
    private Date mDate;
    private MPPointF mOffset = new MPPointF();

    public MyMarkerView (Context context, int layoutResource, long referenceTimestamp) {
        super(context, layoutResource);
        // this markerview only displays a textview
        tvContent = (TextView) findViewById(R.id.tvContent);
        this.referenceTimestamp = referenceTimestamp;
        this.mDataFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        this.mDate = new Date();
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        long currentTimestamp = (int)e.getX() + referenceTimestamp;

        tvContent.setText(e.getY() + "% at " + getTimedate(currentTimestamp)); // set the entry-value as the display text
    }

    @Override
    public MPPointF getOffset() {
        mOffset = new MPPointF();
        mOffset.x = -(getWidth() / 2);
        mOffset.y = -getHeight();
        return mOffset;
    }


    private String getTimedate(long timestamp){

        try{
            mDate.setTime(timestamp*1000);
            return mDataFormat.format(mDate);
        }
        catch(Exception ex){
            return "xx";
        }
    }
}