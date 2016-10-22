package com.clouiotech.pda.demo.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.clouiotech.pda.demo.Adapter.EpcScanAdapter;
import com.clouiotech.pda.demo.BaseObject.EpcObject;
import com.clouiotech.pda.demoExample.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by roka on 28/07/16.
 */
public class StockScanFragment extends Fragment {
    private EpcScanAdapter mAdapter;
    private List<EpcObject> mListData = new ArrayList<>();
    private LinearLayoutManager mManager;
    private RecyclerView mRecyclerView;

    public static StockScanFragment newInstance(){
        StockScanFragment fragment = new StockScanFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mAdapter = new EpcScanAdapter(mListData, getActivity());

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_recycler_view, null, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.rv_recycler);

        Random r = new Random();
        for (int i = 0; i < 300; i++) {
            int quantity = r.nextInt(300);
            int physic = r.nextInt(300);
            String id = "EPC " + i;
            String desc = "EPC Desc" + i;
            mListData.add(new EpcObject(id, desc, quantity, physic));
        }

        mRecyclerView.setLayoutManager(mManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.notifyDataSetChanged();

        return v;
    }
}