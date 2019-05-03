package com.example.noam.depressiondetectornew;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daimajia.swipe.util.Attributes;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link  interface
 * to handle interaction events.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private ArrayList<UserProfile> mDataSet;
    SwipeRecyclerViewAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        mDataSet = new ArrayList<>();
        mDataSet.add(new UserProfile(1,"noam"));
        mDataSet.add(new UserProfile(2,"dvir"));
        mDataSet.add(new UserProfile(3,"sagy"));
        mDataSet.add(new UserProfile(4,"elie"));
        mDataSet.add(new UserProfile(5,"itay"));
        mDataSet.add(new UserProfile(6,"yakov"));
        mDataSet.add(new UserProfile(7,"itzik"));
        mDataSet.add(new UserProfile(8,"israel"));
        mDataSet.add(new UserProfile(9,"metushelah"));
        mDataSet.add(new UserProfile(10,"daniel"));
        mDataSet.add(new UserProfile(11,"yaniv"));
        mDataSet.add(new UserProfile(12,"ben"));

        mRecyclerView = (RecyclerView) v.findViewById(R.id.my_recycler_view);
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
        return v;
    }


}
