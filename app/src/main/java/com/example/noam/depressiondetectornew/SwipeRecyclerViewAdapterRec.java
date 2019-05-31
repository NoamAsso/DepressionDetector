package com.example.noam.depressiondetectornew;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
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
    ViewGroup parent1;
    SimpleViewHolderRec prev;
    RecordingProfile itemPrev;
    boolean flagOpen = false;
    public SwipeRecyclerViewAdapterRec(Context context, ArrayList<RecordingProfile> objects) {
        this.mContext = context;
        this.studentList = objects;
    }


    @Override
    public SimpleViewHolderRec onCreateViewHolder(ViewGroup parent, int viewType) {
        parent1 = parent;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.swipe_layout_rec, parent, false);
        return new SimpleViewHolderRec(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolderRec viewHolder, final int position) {
        final RecordingProfile item = studentList.get(position);

        viewHolder.Name.setText(item.get_recordName());
        viewHolder.Status.setText("Status: " + Double.toString(item.get_prediction()));
        viewHolder.ID.setText("Rec ID: " + item.get_recId());
        viewHolder.Date.setText(item.get_time());
        viewHolder.UserBelong.setText("User ID: " + item.get__userId());
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
                //Toast.makeText(mContext, " Click : " + item.get__userId() + " \n" + item.get_recordName(), Toast.LENGTH_SHORT).show();



                if(item.isClicked()){

                    viewHolder.jcplayerView.setVisibility(View.GONE);

                    viewHolder.jcplayerView.pause();
                    viewHolder.jcplayerView.kill();
                    prev = viewHolder;
                    item.setClicked(false);
                    flagOpen = false;
                }
                else{
                    if(prev == null){
                        prev = viewHolder;
                        itemPrev = item;
                    }
                    else{
                        prev.jcplayerView.setVisibility(View.GONE);
                        prev.jcplayerView.pause();
                        prev.jcplayerView.kill();
                        itemPrev.setClicked(false);
                        prev = viewHolder;
                        itemPrev = item;
                    }
                    viewHolder.jcplayerView.setVisibility(View.VISIBLE);
                    ArrayList<JcAudio> jcAudios = new ArrayList<>();
                    JcAudio temp = JcAudio.createFromFilePath("Asset audio",item.get_path());
                    jcAudios.add(temp);
                    viewHolder.jcplayerView.initAnonPlaylist(jcAudios);
                    item.setClicked(true);
                    flagOpen = true;
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
                db = Utils.getDB();
                db.UpdateDelGson(item.get__userId(),item.get_recId());
                db.removeRecWithId(studentList.get(position).get_recId());
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
        public TextView UserBelong;
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
            UserBelong = (TextView) itemView.findViewById(R.id.user_belong);
            jcplayerView = (JcPlayerView) itemView.findViewById(R.id.jcplayer);

            Delete = (TextView) itemView.findViewById(R.id.Delete);
            Edit = (TextView) itemView.findViewById(R.id.Edit);
            Share = (TextView) itemView.findViewById(R.id.Share);
            btnLocation = (ImageButton) itemView.findViewById(R.id.btnLocation);
        }
    }
}
