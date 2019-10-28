package com.example.noam.depressiondetectornew;

        import android.content.Context;
        import android.graphics.Color;
        import android.support.v7.widget.RecyclerView;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ImageButton;
        import android.widget.ImageView;
        import android.widget.ListView;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.daimajia.swipe.SwipeLayout;
        import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
        import com.example.jean.jcplayer.model.JcAudio;
        import com.example.jean.jcplayer.view.JcPlayerView;
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

public class GraphAdapter extends RecyclerSwipeAdapter<GraphAdapter.SimpleViewHolderGraph> {

    MyDBmanager db;
    Utils utils;
    private Context mContext; //context
    ViewGroup parent1;
    private ArrayList<UserProfile> items; //data source of the list adapter
    public GraphAdapter(Context context, ArrayList<UserProfile> objects) {
        this.mContext = context;
        this.items = objects;
    }


    @Override
    public SimpleViewHolderGraph onCreateViewHolder(ViewGroup parent, int viewType) {
        parent1 = parent;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.graphs_layout, parent, false);
        return new SimpleViewHolderGraph(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolderGraph viewHolder, final int position) {
        final UserProfile currentItem = items.get(position);




        db = Utils.getDB();
        LineChart mChart;
        Long reference_timestamp = 0l;
        viewHolder.userName.setText(currentItem.get_firstName());
        YAxis yAxis;
        ArrayList<RecordingProfile> mDataSet = new ArrayList<RecordingProfile>();
        for (int i = 0; i < currentItem.getRecordings().size(); i++) {
            mDataSet.add(db.getRecordingAt(currentItem.getRecordings().get(i)));
        }


        viewHolder.mChart.setDragEnabled(true);
        viewHolder.mChart.setScaleEnabled(false);

        ArrayList<Entry> yValues = new ArrayList<>();

        if (mDataSet.size() > 0) {
            long Xnew[] = new long[mDataSet.size()];
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

                Xnew[i] = date1.getTime() - reference_timestamp;
                yValues.add(new Entry((float) i, (float) mDataSet.get(i).get_prediction()));

            }
            MyMarkerView.setit(Xnew,0);
            ValueFormatter xAxisFormatter = new FooFormatter(reference_timestamp);
            XAxis xAxis = viewHolder.mChart.getXAxis();
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
            MyMarkerView myMarkerView = new MyMarkerView(mContext, R.layout.custom_marker_view, reference_timestamp);
            yAxis = viewHolder.mChart.getAxisLeft();
            yAxis.setAxisMaximum(100f);
            yAxis.setAxisMinimum(0f);
            viewHolder.mChart.setMarker(myMarkerView);
            viewHolder.mChart.setData(data);
            //viewHolder.mChart.animateY(2000);
        } else{
            yAxis = viewHolder.mChart.getAxisLeft();
            yAxis.setAxisMaximum(100f);
            yAxis.setAxisMinimum(0f);
            LineData data = new LineData(); // use your valid x-values array here
            viewHolder.mChart.setData(data);
            viewHolder.mChart.invalidate();
            //mChart.setAutofillHints("No predictions was made");
            //mchart.set
        }










        viewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);

        //dari kiri
        viewHolder.swipeLayout.addDrag(SwipeLayout.DragEdge.Left, viewHolder.swipeLayout.findViewById(R.id.bottom_wrapper1));

        //dari kanan
        viewHolder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, viewHolder.swipeLayout.findViewById(R.id.bottom_wraper));



        viewHolder.swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onStartOpen(SwipeLayout layout) {

            }

            @Override
            public void onOpen(SwipeLayout layout) {

            }

            @Override
            public void onStartClose(SwipeLayout layout) {

            }

            @Override
            public void onClose(SwipeLayout layout) {

            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {

            }

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {

            }
        });







        viewHolder.swipeLayout.getSurfaceView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(mContext, " Click : " + item.get__userId() + " \n" + item.get_recordName(), Toast.LENGTH_SHORT).show();

            }
        });

        viewHolder.btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(v.getContext(), "Clicked on Information " + viewHolder.Name.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });


        viewHolder.Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //Toast.makeText(v.getContext(), "Deleted " + viewHolder.Name.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });

        mItemManger.bindView(viewHolder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    public static class SimpleViewHolderGraph extends RecyclerView.ViewHolder{
        public SwipeLayout swipeLayout;
        public LineChart mChart;
        public TextView userName;
        public ImageButton btnLocation;
        public TextView Delete;

        public SimpleViewHolderGraph(View itemView) {
            super(itemView);
            mChart = (LineChart) itemView.findViewById(R.id.line_chart_stat);
            userName = itemView.findViewById(R.id.text_view_item_name);
            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            Delete = (TextView) itemView.findViewById(R.id.Delete);
            btnLocation = (ImageButton) itemView.findViewById(R.id.btnLocation);
        }
    }
}
