package com.clouiotech.pda.demo.Fragment;

import android.app.SearchManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.clouiotech.pda.demo.Activity.MainActivity;
import com.clouiotech.pda.demo.Activity.RecyclerViewActivity;
import com.clouiotech.pda.demo.Adapter.EpcScanAdapter;
import com.clouiotech.pda.demo.BaseObject.EpcObject;
import com.clouiotech.pda.demo.BaseObject.SearchQueryAggregator;
import com.clouiotech.pda.demo.Sqlite.DataOrder;
import com.clouiotech.pda.demo.Sqlite.DataTemp;
import com.clouiotech.pda.demo.Sqlite.MyDBHandler;
import com.clouiotech.pda.demo.uhf.StokReadEPCActivity;
import com.clouiotech.pda.demo.utils.Utils;
import com.clouiotech.pda.demoExample.R;
import com.clouiotech.pda.rfid.EPCModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller;

/**
 * Created by roka on 28/07/16.
 */
public class StockScanFragment extends Fragment {
    private EpcScanAdapter mAdapter;
    private List<EpcObject> mListData = new ArrayList<>();
    private List<EpcObject> mListDataHelper = new ArrayList<>();
    private LinearLayoutManager mManager;

    private RelativeLayout mLlSearchNotFound = null;
    private RecyclerView mRecyclerView;
    private VerticalRecyclerViewFastScroller mScroller = null;

    private MyDBHandler mMyDBHandler = null;
    private int scanId = 1;
    private volatile boolean isRefresh = false;
    private Handler mHandler = null;

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
            Toast.makeText(getActivity(), "size" + mListData.size() , Toast.LENGTH_SHORT).show();
            if(mListData != null && mListData.size() == 0) {
                mRecyclerView.setVisibility(View.GONE);
                mLlSearchNotFound.setVisibility(View.VISIBLE);
            } else {
                mRecyclerView.setVisibility(View.VISIBLE);
                mLlSearchNotFound.setVisibility(View.GONE);
            }
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

            isRefresh = false;
        }

        @Override
        public void onSuccess() {
            // For any general success database operation
            getAllData();
        }

        @Override
        public void onError() {

        }
    };

    private DatabaseResponseCallback saveEPCObjectCallback = new DatabaseResponseCallback() {
        @Override
        public void onData(List<EpcObject> listItem) {

        }

        @Override
        public void onSuccess() {
            isRefresh = true;
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

        mHandler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // do your stuff - don't create a new runnable here!
                if(isRefresh) {
                    getAllData();
                }
                Log.d("SCANNED", "" + scanId);
                mHandler.postDelayed(this,500);
            }
        };

        mHandler.post(runnable);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_recycler_view, null, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.rv_recycler);
        mLlSearchNotFound = (RelativeLayout) v.findViewById(R.id.ll_search_not_found);
        mScroller = (VerticalRecyclerViewFastScroller) v.findViewById(R.id.rvfs_fast_scroller);

        mRecyclerView.setLayoutManager(mManager);
        mRecyclerView.setAdapter(mAdapter);

        mScroller.setRecyclerView(mRecyclerView);
        mRecyclerView.setOnScrollListener(mScroller.getOnScrollListener());
        getAllData();
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

    public void filterByItemName(String text) {
        mMyDBHandler.getAllItemByItemName(callback, text);

    }


    public static interface DatabaseResponseCallback extends MainActivity.Callbacks {
        void onData(List<EpcObject> listItem);
    };

    public void saveScanItemToDatabaseInFragment(int id, String epcRawCode) {
        mMyDBHandler.addToEpcRawDatabase(id,epcRawCode, epcSaveCallback);
    }

    public void getEpcObjectFilter(final int operation, final int minimum, final int maximum) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Log.d("ASDASD", "OPERATION " + operation );
                mMyDBHandler.getAllItemByItemQuantityStatus(callback, operation, minimum, maximum);
            }
        };

        new Thread(runnable).start();
    }

    public void doProcessEPCScanResult(EPCModel epcObj) {
        incrementScanId();
        {
            try {
                String dataSticker = (epcObj._EPC + epcObj._TID);
                if (dataSticker.length() == 48) {
                    String pemisah = dataSticker.substring(38, 40);
                    if (pemisah.trim().equals("3B")) {
                        String kodeSaja = dataSticker.substring(0, 38);//Ex:4d593130313532362d394d0000000000000000
                        String kodeCounter = dataSticker.substring(39, 48);//Ex:0000000A⁠⁠⁠⁠
                        kodeSaja = kodeSaja.replaceAll("00", "");//Ex:4d593130313532362d394d
                        String cek = Utils.HexToStringConverter(kodeSaja);//Ex:MY101526-9M
                        String fixKodeCounter = cek + ";" + kodeCounter;
                        Log.d("INSERTEPC", "process " + cek);
                        insertEPCModelToDatabase(cek);
                    } else {
                        insertEPCModelToDatabase(dataSticker);
                    }
                } else {
                    insertEPCModelToDatabase(dataSticker);
                }
            } catch (Exception e) {

            }
        }
    }

    public void getAllData() {
        Runnable databaseRunnable = new Runnable() {
            @Override
            public void run() {
                mMyDBHandler.getDataAsli(callback);
            }
        };

        new Thread(databaseRunnable).start();
    }

    private void insertEPCModelToDatabase(final String itemId) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Log.d("INSERTEPC", "insert to database " + itemId);
                mMyDBHandler.insertEPCScanResult(saveEPCObjectCallback, itemId);
            }
        };

        new Thread(runnable).start();
    }

    public void sortStockScan(final int mSortOperation, final int ascending) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mMyDBHandler.getDataAsli(callback, mSortOperation, ascending);
            }
        };

        new Thread(runnable).start();
    }
}
