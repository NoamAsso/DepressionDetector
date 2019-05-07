package com.example.noam.depressiondetectornew;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.example.jean.jcplayer.model.JcAudio;
import com.example.jean.jcplayer.view.JcPlayerView;

import java.util.ArrayList;

public class SwipeRecyclerViewAdapterRec extends RecyclerSwipeAdapter<SwipeRecyclerViewAdapterRec.SimpleViewHolderRec> {

    private Context mContext;
    private ArrayList<RecordingProfile> studentList;
    MyDBmanager db;
    Utils utils;
    public SwipeRecyclerViewAdapterRec(Context context, ArrayList<RecordingProfile> objects) {
        this.mContext = context;
        this.studentList = objects;
    }


    @Override
    public SimpleViewHolderRec onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.swipe_layout_rec, parent, false);
        return new SimpleViewHolderRec(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolderRec viewHolder, final int position) {
        final RecordingProfile item = studentList.get(position);

        viewHolder.Name.setText("Name: " + item.get_recordName());
        viewHolder.Status.setText("Status: " + Double.toString(item.get_prediction()));
        viewHolder.ID.setText("UserId: " + item.get_recId() + " - Row Position " + position);
        viewHolder.Date.setText("Join: " + item.get_time());
        viewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);

        viewHolder.jcplayerView.setVisibility(View.GONE);

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
                Toast.makeText(mContext, " Click : " + item.get__userId() + " \n" + item.get_recordName(), Toast.LENGTH_SHORT).show();
                if(item.isClicked()){

                    viewHolder.jcplayerView.setVisibility(View.GONE);
                    viewHolder.jcplayerView.pause();
                    item.setClicked(false);
                }
                else{
                    viewHolder.jcplayerView.setVisibility(View.VISIBLE);
                    ArrayList<JcAudio> jcAudios = new ArrayList<>();
                    JcAudio temp = JcAudio.createFromFilePath("Asset audio","/storage/emulated/0/Documents/AudioRecord/20190506142017.wav");
                    viewHolder.jcplayerView.playAudio(temp);
                    item.setClicked(true);
                }



            }
        });

        viewHolder.btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Clicked on Information " + viewHolder.Name.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });

        viewHolder.Share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(view.getContext(), "Clicked on Share " + viewHolder.Name.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });

        viewHolder.Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(view.getContext(), "Clicked on Edit  " + viewHolder.Name.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });

        viewHolder.Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemManger.removeShownLayouts(viewHolder.swipeLayout);
                //db = Utils.getDB();
                //db.removeUserWithId(studentList.get(position).get_userId());
                studentList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, studentList.size());
                mItemManger.closeAllItems();
                Toast.makeText(v.getContext(), "Deleted " + viewHolder.Name.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });

        mItemManger.bindView(viewHolder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    public static class SimpleViewHolderRec extends RecyclerView.ViewHolder{
        public SwipeLayout swipeLayout;
        public TextView Name;
        public TextView LastName;
        public TextView Phone;
        public TextView Status;
        public TextView Date;
        public TextView ID;
        public TextView EmailId;
        public TextView Delete;
        public TextView Edit;
        public TextView Share;
        public JcPlayerView jcplayerView;
        public ImageButton btnLocation;
        public SimpleViewHolderRec(View itemView) {
            super(itemView);

            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            Name = (TextView) itemView.findViewById(R.id.Name_rec);
            Status = (TextView) itemView.findViewById(R.id.status_rec);
            Date = (TextView) itemView.findViewById(R.id.date_rec);
            ID = (TextView) itemView.findViewById(R.id.id_and_more_rec);
            jcplayerView = (JcPlayerView) itemView.findViewById(R.id.jcplayer);
            //EmailId = (TextView) itemView.findViewById(R.id.EmailId);

            Delete = (TextView) itemView.findViewById(R.id.Delete);
            Edit = (TextView) itemView.findViewById(R.id.Edit);
            Share = (TextView) itemView.findViewById(R.id.Share);
            btnLocation = (ImageButton) itemView.findViewById(R.id.btnLocation);
        }
    }
}
