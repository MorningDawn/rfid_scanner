package com.clouiotech.pda.demo.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.clouiotech.pda.demo.Adapter.ScanHistoryAdapter;
import com.clouiotech.pda.demo.BaseObject.ScanHistoryObject;
import com.clouiotech.pda.demoExample.R;

import java.util.ArrayList;
import java.util.List;

import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller;

/**
 * Created by roka on 30/07/16.
 */
public class ScanHistoryFragment extends Fragment {
    private List<ScanHistoryObject> mListData;
    private ScanHistoryAdapter mAdapter;
    private LinearLayoutManager mManager;
    private RecyclerView mRvRecycler;
    private VerticalRecyclerViewFastScroller mScroller = null;

    public static ScanHistoryFragment newInstance() {
        ScanHistoryFragment fragment = new ScanHistoryFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListData = new ArrayList<>();
        mAdapter = new ScanHistoryAdapter(getActivity(), mListData);
        mManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_recycler_view, null, false);

        mRvRecycler = (RecyclerView) v.findViewById(R.id.rv_recycler);
        mScroller = (VerticalRecyclerViewFastScroller) v.findViewById(R.id.rvfs_fast_scroller);

        mRvRecycler.setLayoutManager(mManager);
        mRvRecycler.setAdapter(mAdapter);

        mScroller.setVisibility(View.GONE);
        mScroller.setRecyclerView(mRvRecycler);

        for (int i = 0; i < 300; i++) {
            ScanHistoryObject scanHistoryObject = new ScanHistoryObject("Scan Code "+i, "Scan Number "+i, "1 Januari 1990");
            mListData.add(scanHistoryObject);
        }

        mAdapter.notifyDataSetChanged();
        return v;
    }
}
