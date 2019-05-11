package com.example.noam.depressiondetectornew;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daimajia.swipe.util.Attributes;

import java.util.ArrayList;



public class HomeFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private ArrayList<UserProfile> mDataSet;
    SwipeRecyclerViewAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        TextView appName = v.findViewById(R.id.appName2);
        Typeface custom_font = Typeface.createFromAsset(getActivity().getAssets(),  "fonts/Highest.ttf");
        appName.setTypeface(custom_font);
        return v;
    }


}
