package com.example.noam.depressiondetectornew;

import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daimajia.swipe.util.Attributes;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import static com.example.noam.depressiondetectornew.MyDBmanager.MyDBManagerItem.COLUMN_RECORDINGS_GSON;


public class HomeFragment extends Fragment {

    SearchView search;
    private RecyclerView mRecyclerView;
    private ArrayList<UserProfile> mDataSet;
    SwipeRecyclerViewAdapter adapter;
    private RecyclerView mRecyclerViewRec;
    private ArrayList<RecordingProfile> mDataSetRec;
    SwipeRecyclerViewAdapterRec adapterRec;
    MyDBmanager db;
    Utils utils;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_home, container, false);

        v.findViewById(R.id.patients_list_layout).setVisibility(View.GONE);
        v.findViewById(R.id.recoding_list_layout).setVisibility(View.GONE);
        search = (SearchView) v.findViewById(R.id.search);
        db = Utils.getDB();
        utils = new Utils(getActivity());
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search.setIconified(false);
            }
        });
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(s.matches("")|| s.matches(" ")) {
                    v.findViewById(R.id.patients_list_layout).setVisibility(View.GONE);
                    v.findViewById(R.id.recoding_list_layout).setVisibility(View.GONE);
                    return false;
                }
                Cursor mCursor = db.SearchUserDb(s);
                mDataSet = new ArrayList<UserProfile>();
                boolean flag = false;
                for(mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
                    // The Cursor is now set to the right position
                    flag= true;
                    UserProfile usertemp = new UserProfile();
                    Gson gson = new Gson();
                    usertemp.set_userId(mCursor.getInt(mCursor.getColumnIndex("_id")));
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
                if(flag)
                    v.findViewById(R.id.patients_list_layout).setVisibility(View.VISIBLE);
                mRecyclerView = (RecyclerView) v.findViewById(R.id.my_recycler_view_search);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));

                adapter = new SwipeRecyclerViewAdapter(getActivity(), mDataSet);
                ((SwipeRecyclerViewAdapter) adapter).setMode(Attributes.Mode.Single);

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

//////////////////////////////////////////////////////////////
                flag = false;
                Cursor mCursor2 = db.SearchRecDb(s);
                mDataSetRec = new ArrayList<RecordingProfile>();
                for(mCursor2.moveToFirst(); !mCursor2.isAfterLast(); mCursor2.moveToNext()) {
                    flag = true;
                    // The Cursor is now set to the right position
                    RecordingProfile rectemp = new RecordingProfile();
                    rectemp.set_recId(mCursor2.getInt(mCursor2.getColumnIndex("_id")));
                    rectemp.set__userId(mCursor2.getInt(mCursor2.getColumnIndex("user_id")));
                    rectemp.set_recordName(mCursor2.getString(mCursor2.getColumnIndex("recording_name")));
                    rectemp.set_prediction(mCursor2.getDouble(mCursor2.getColumnIndex("prediction")));
                    rectemp.set_time(mCursor2.getString(mCursor2.getColumnIndex("time_added")));
                    rectemp.set_length(mCursor2.getInt(mCursor2.getColumnIndex("length")));
                    rectemp.set_path(mCursor2.getString(mCursor2.getColumnIndex("file_path")));
                    rectemp.setPrediction_feedback(mCursor2.getInt(mCursor2.getColumnIndex("prediction_feedback")));
                    mDataSetRec.add(rectemp);
                }
                if(flag)
                    v.findViewById(R.id.recoding_list_layout).setVisibility(View.VISIBLE);
                mRecyclerViewRec = (RecyclerView) v.findViewById(R.id.my_recycler_view_rec_search);
                mRecyclerViewRec.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));

                adapterRec = new SwipeRecyclerViewAdapterRec(getActivity(), mDataSetRec);
                ((SwipeRecyclerViewAdapterRec) adapterRec).setMode(Attributes.Mode.Single);

                mRecyclerViewRec.setAdapter(adapterRec);

                mRecyclerViewRec.addOnScrollListener(new RecyclerView.OnScrollListener() {
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



                return false;
            }
        });
/*

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(query);
        }



        TextView appName = v.findViewById(R.id.appName2);
        Typeface custom_font = Typeface.createFromAsset(getActivity().getAssets(),  "fonts/Highest.ttf");
        appName.setTypeface(custom_font);*/
        return v;
    }


}
