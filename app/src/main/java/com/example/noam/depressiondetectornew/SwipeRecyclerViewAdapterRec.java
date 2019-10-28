package com.example.noam.depressiondetectornew;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.example.jean.jcplayer.model.JcAudio;
import com.example.jean.jcplayer.view.JcPlayerView;

import java.io.File;
import java.util.ArrayList;

public class SwipeRecyclerViewAdapterRec extends RecyclerSwipeAdapter<SwipeRecyclerViewAdapterRec.SimpleViewHolderRec> {

    private Context mContext;
    private ArrayList<RecordingProfile> studentList;
    MyDBmanager db;
    Utils utils;
    ViewGroup parent1;
    SimpleViewHolderRec prev;
    RecordingProfile itemPrev;
    RecordingProfile itemCurr;
    int positiontmp;
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
        viewHolder.Status.setText("Prediction: " + String.format("%.2f", item.get_prediction()) + "%");
        viewHolder.ID.setText(Utils.getDB().getUserAt(item.get__userId()).get_firstName());
        if (item.getPrediction_feedback() == 0)
            viewHolder.feedbackImage.setImageResource(R.drawable.ic_dislike);
        else
            viewHolder.feedbackImage.setImageResource(R.drawable.ic_like);
        viewHolder.Date.setText(item.get_time());
        //viewHolder.UserBelong.setText(" ");
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


                if (item.isClicked()) {

                    viewHolder.jcplayerView.setVisibility(View.GONE);
                    viewHolder.recImage.setImageResource(R.drawable.ic_record);
                    viewHolder.jcplayerView.pause();
                    viewHolder.jcplayerView.kill();
                    prev = viewHolder;
                    item.setClicked(false);
                    flagOpen = false;
                } else {
                    if (prev == null) {
                        prev = viewHolder;
                        itemPrev = item;
                    } else {
                        prev.jcplayerView.setVisibility(View.GONE);
                        prev.recImage.setImageResource(R.drawable.ic_record);
                        prev.jcplayerView.pause();
                        prev.jcplayerView.kill();
                        itemPrev.setClicked(false);
                        prev = viewHolder;
                        itemPrev = item;
                    }
                    viewHolder.jcplayerView.setVisibility(View.VISIBLE);
                    viewHolder.recImage.setImageResource(R.drawable.ic_record_pressed);
                    ArrayList<JcAudio> jcAudios = new ArrayList<>();
                    JcAudio temp = JcAudio.createFromFilePath("Asset audio", item.get_path());
                    jcAudios.add(temp);
                    viewHolder.jcplayerView.initWithTitlePlaylist(jcAudios, "Playing");
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


        viewHolder.Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                positiontmp = position;
                itemCurr = item;
                makePopup(viewHolder,v);
                /*
                mItemManger.removeShownLayouts(viewHolder.swipeLayout);
                db = Utils.getDB();
                db.UpdateDelGson(item.get__userId(),item.get_recId());
                db.removeRecWithId(studentList.get(position).get_recId());
                studentList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, studentList.size());
                mItemManger.closeAllItems();
                Toast.makeText(v.getContext(), "Deleted " + viewHolder.Name.getText().toString(), Toast.LENGTH_SHORT).show();*/
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

    public static class SimpleViewHolderRec extends RecyclerView.ViewHolder {
        public SwipeLayout swipeLayout;
        public TextView Name;
        public TextView LastName;
        public TextView Phone;
        public TextView Status;
        public TextView Date;
        public TextView ID;
        public TextView UserBelong;
        public TextView Delete;
        public ImageView recImage;
        public ImageView feedbackImage;
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
            //UserBelong = (TextView) itemView.findViewById(R.id.user_belong);
            jcplayerView = (JcPlayerView) itemView.findViewById(R.id.jcplayer);
            recImage = (ImageView) itemView.findViewById(R.id.image_rec);
            feedbackImage = (ImageView) itemView.findViewById(R.id.image_feedbackk);
            Delete = (TextView) itemView.findViewById(R.id.Delete);
            btnLocation = (ImageButton) itemView.findViewById(R.id.btnLocation);
        }
    }

    void makePopup(final SimpleViewHolderRec viewHolder,final View v) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        final View myDialogView = inflater.inflate(R.layout.delete_dialogue, null);
        final String time = Utils.getTimeSave();
        final String defaultName = "Delete recording";
        //Get Audio duration time
        //Build the dialog
        final AlertDialog.Builder dialog = new AlertDialog.Builder(
                mContext,
                R.style.MyDialogTheme
        );
        dialog.setTitle("Delete recording");
        dialog.setView(myDialogView);
        final CheckBox chkAndroid = (CheckBox) myDialogView.findViewById(R.id.chkIos);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                mItemManger.removeShownLayouts(viewHolder.swipeLayout);
                db = Utils.getDB();

                db.UpdateDelGson(itemCurr.get__userId(), itemCurr.get_recId());
                db.removeRecWithId(studentList.get(positiontmp).get_recId());
                studentList.remove(positiontmp);
                notifyItemRemoved(positiontmp);
                notifyItemRangeChanged(positiontmp, studentList.size());
                mItemManger.closeAllItems();
                dialog.dismiss();
                if(chkAndroid.isChecked()){
                    File file = new File(itemCurr.get_path());
                    boolean deleted = file.delete();
                    Toast.makeText(v.getContext(),  viewHolder.Name.getText().toString() + " Deleted from storage", Toast.LENGTH_SHORT).show();

                }
                else{
                    Toast.makeText(v.getContext(),  viewHolder.Name.getText().toString() + " Successfully Deleted", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        //   dialog.setNegativeButton("Cancel", null);
        dialog.create();
        dialog.show();
    }
}