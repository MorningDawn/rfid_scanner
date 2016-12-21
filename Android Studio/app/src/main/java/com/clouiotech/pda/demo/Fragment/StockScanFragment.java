package com.clouiotech.pda.demo.Fragment;

import android.app.SearchManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.clouiotech.pda.demo.Activity.MainActivity;
import com.clouiotech.pda.demo.Activity.RecyclerViewActivity;
import com.clouiotech.pda.demo.Adapter.EpcScanAdapter;
import com.clouiotech.pda.demo.BaseObject.EpcObject;
import com.clouiotech.pda.demo.BaseObject.SearchQueryAggregator;
import com.clouiotech.pda.demo.Sqlite.MyDBHandler;
import com.clouiotech.pda.demoExample.R;
import com.clouiotech.pda.rfid.EPCModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by roka on 28/07/16.
 */
public class StockScanFragment extends Fragment {
    private EpcScanAdapter mAdapter;
    private List<EpcObject> mListData = new ArrayList<>();
    private List<EpcObject> mListDataHelper = new ArrayList<>();
    private LinearLayoutManager mManager;
    private RecyclerView mRecyclerView;
    private MyDBHandler mMyDBHandler = null;
    private int scanId = 1;

    public static StockScanFragment newInstance(){
        StockScanFragment fragment = new StockScanFragment();
        return fragment;
    }

    public void incrementScanId() {
        scanId++;
    }


    private Runnable notifyOnUiThread = new Runnable() {
        @Override
        public void run() {
            mAdapter.notifyDataSetChanged();
        }
    };

    private void toastInCallback(final String toast) {
        Runnable showToast = new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), toast, Toast.LENGTH_SHORT).show();
            }
        };

        getActivity().runOnUiThread(showToast);
    }

    private DatabaseResponseCallback callback = new DatabaseResponseCallback() {
        @Override
        public void onData(List<EpcObject> listItem) {
            mListData.clear();
            mListData.addAll(listItem);
            getActivity().runOnUiThread(notifyOnUiThread);
        }

        @Override
        public void onSuccess() {

        }

        @Override
        public void onError() {

        }
    };

    private DatabaseResponseCallback epcSaveCallback = new DatabaseResponseCallback() {
        @Override
        public void onData(List<EpcObject> listItem) {

        }

        @Override
        public void onSuccess() {
            toastInCallback("Save to EPC Database Success");
        }

        @Override
        public void onError() {
            toastInCallback("Save to EPC Database Error");
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mAdapter = new EpcScanAdapter(mListData, getActivity());
        mMyDBHandler = new MyDBHandler(getActivity(), null, null, 1);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_recycler_view, null, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.rv_recycler);


        mRecyclerView.setLayoutManager(mManager);
        mRecyclerView.setAdapter(mAdapter);

        Runnable databaseRunnable = new Runnable() {
            @Override
            public void run() {
                mMyDBHandler.getDataAsli(callback);
            }
        };

        new Thread(databaseRunnable).start();
        return v;
    }

    public void refreshDataByTextQuery(String text) {
        Toast.makeText(getActivity(), "Text Submitted in fragment " + text, Toast.LENGTH_SHORT).show();
        Pattern pattern = Pattern.compile("[^a-zA-Z0-9 ]");
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            SearchQueryAggregator searchQueryAggregator = new SearchQueryAggregator(text);
            mMyDBHandler.getDataAsli(callback, searchQueryAggregator);
        } else {
            String[] arrayStringAggregator = text.split(" ");
            mMyDBHandler.getDataAsli(callback, arrayStringAggregator);
        }
    }


    public static interface DatabaseResponseCallback extends MainActivity.Callbacks {
        void onData(List<EpcObject> listItem);
    };

    public void saveScanItemToDatabaseInFragment(int id, String epcRawCode) {
        mMyDBHandler.addToEpcRawDatabase(id,epcRawCode, epcSaveCallback);
    }
}
