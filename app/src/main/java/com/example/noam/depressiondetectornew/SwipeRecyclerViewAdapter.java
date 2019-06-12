package com.example.noam.depressiondetectornew;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.example.jean.jcplayer.model.JcAudio;

import java.util.ArrayList;

public class SwipeRecyclerViewAdapter extends RecyclerSwipeAdapter<SwipeRecyclerViewAdapter.SimpleViewHolder> {

    private Context mContext;
    private ArrayList<UserProfile> studentList;
    MyDBmanager db;
    SimpleViewHolder prev;
    boolean flagOpen = false;
    UserProfile itemPrev;
    Utils utils;
    public SwipeRecyclerViewAdapter(Context context, ArrayList<UserProfile> objects) {
        this.mContext = context;
        this.studentList = objects;
    }


    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.swipe_layout, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder viewHolder, final int position) {
        final UserProfile item = studentList.get(position);
        db = utils.getDB();
        viewHolder.Name.setText(item.get_firstName()+" "+item.get_lastName());
        //viewHolder.LastName.setText(item.get_lastName());
        viewHolder.Phone.setText("Phone Number: " + item.get_phoneNumber());
        if(item.getRecordings().size() == 0)
            viewHolder.Status.setText("No predictions yet");
        else{
            long idx = item.getRecordings().get(item.getRecordings().size()-1);
            RecordingProfile rec = db.getRecordingAt(idx);
            viewHolder.Status.setText("Last prediction: " + String.format("%.2f", rec.get_prediction())+"%");
            //viewHolder.ID.setText("UserId: " + item.get_userId() + " - Row Position " + position);
            viewHolder.Date.setText("At: " + rec.get_time());
        }
        if (item.get_gender() != null) {
            if(item.get_gender().matches("Female")){
                viewHolder.image.setImageResource(R.drawable.ic_circle_icon_woman);
            }
        }

        viewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);

        //dari kiri
        viewHolder.swipeLayout.addDrag(SwipeLayout.DragEdge.Left, viewHolder.swipeLayout.findViewById(R.id.bottom_wrapper1));

        //dari kanan
        viewHolder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, viewHolder.swipeLayout.findViewById(R.id.bottom_wraper));

        viewHolder.details.setVisibility(View.GONE);

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
                //Toast.makeText(mContext, " Click : " + item.get_userId() + " \n" + item.get_phoneNumber(), Toast.LENGTH_SHORT).show();

                if(item.isClicked()){

                    viewHolder.details.setVisibility(View.GONE);

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
                        prev.details.setVisibility(View.GONE);
                        itemPrev.setClicked(false);
                        prev = viewHolder;
                        itemPrev = item;
                    }
                    viewHolder.details.setVisibility(View.VISIBLE);
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


        viewHolder.details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                item.setcurrentUserId(item.get_userId());
                Intent intent = new Intent(view.getContext(),UserPageActivity.class);
                view.getContext().startActivity(intent);
            }
        });

        viewHolder.Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemManger.removeShownLayouts(viewHolder.swipeLayout);
                db = Utils.getDB();
                db.removeUserWithId(item.get_userId());
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

    public static class SimpleViewHolder extends RecyclerView.ViewHolder{
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
        public Button details;
        public ImageView image;
        public ImageButton btnLocation;
        public SimpleViewHolder(View itemView) {
            super(itemView);

            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            Name = (TextView) itemView.findViewById(R.id.Name);
            //LastName = (TextView) itemView.findViewById(R.id.LastName);
            Phone = (TextView) itemView.findViewById(R.id.phone);
            Status = (TextView) itemView.findViewById(R.id.status);
            Date = (TextView) itemView.findViewById(R.id.date);
            image = (ImageView) itemView.findViewById(R.id.gender_image);
            details = (Button) itemView.findViewById(R.id.user_details);
            //ID = (TextView) itemView.findViewById(R.id.id_and_more);
            //EmailId = (TextView) itemView.findViewById(R.id.EmailId);

            Delete = (TextView) itemView.findViewById(R.id.Delete);
            btnLocation = (ImageButton) itemView.findViewById(R.id.btnLocation);
        }
    }
}
