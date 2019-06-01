package com.example.noam.depressiondetectornew;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class GraphAdapter extends BaseAdapter {

        MyDBmanager db;
        Utils utils;
        private Context context; //context
        private ArrayList<UserProfile> items; //data source of the list adapter

        //public constructor
        public GraphAdapter(Context context, ArrayList<UserProfile> items) {
            this.context = context;
            this.items = items;
        }

        @Override
        public int getCount() {
            return items.size(); //returns total of items in the list
        }

        @Override
        public Object getItem(int position) {
            return items.get(position); //returns list item at the specified position
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // inflate the layout for each list row
            if (convertView == null) {
                convertView = LayoutInflater.from(context).
                        inflate(R.layout.graphs_layout, parent, false);
            }

            // get current item to be displayed
            UserProfile currentItem = (UserProfile) getItem(position);
            db = Utils.getDB();
            LineChart mChart;
            Long reference_timestamp = 0l;
            mChart = (LineChart) convertView.findViewById(R.id.line_chart_stat);
            TextView userName = convertView.findViewById(R.id.text_view_item_name);
            userName.setText(currentItem.get_firstName());
            YAxis yAxis;
            ArrayList<RecordingProfile> mDataSet = new ArrayList<RecordingProfile>();
            for (int i = 0; i < currentItem.getRecordings().size(); i++) {
                mDataSet.add(db.getRecordingAt(currentItem.getRecordings().get(i)));
            }


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
                ValueFormatter xAxisFormatter = new FooFormatter(reference_timestamp);
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
                MyMarkerView myMarkerView = new MyMarkerView(context, R.layout.custom_marker_view, reference_timestamp);
                yAxis = mChart.getAxisLeft();
                yAxis.setAxisMaximum(100f);
                yAxis.setAxisMinimum(0f);
                mChart.setMarker(myMarkerView);
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
            // returns the view for the current row
            return convertView;
        }

}
