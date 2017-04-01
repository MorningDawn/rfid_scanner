package com.clouiotech.pda.demo.Fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.clouiotech.pda.demo.Activity.MainActivity;
import com.clouiotech.pda.demo.Adapter.CustomQueryDownloadAdapter;
import com.clouiotech.pda.demo.BaseObject.CustomQueryResponse;
import com.clouiotech.pda.demo.BaseObject.GlobalVariable;
import com.clouiotech.pda.demo.BaseObject.Item;
import com.clouiotech.pda.demo.BaseObject.ItemResponse;
import com.clouiotech.pda.demo.Sqlite.MyDBHandler;
import com.clouiotech.pda.demo.interfaces.CustomQueryOnClickListener;
import com.clouiotech.pda.demo.restclient.BaseRestClient;
import com.clouiotech.pda.demo.restinterface.BaseRestInterface;
import com.clouiotech.pda.demoExample.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller;

/**
 * Created by roka on 17/03/17.
 */

public class DownloadFragment extends Fragment implements CustomQueryOnClickListener {
    private VerticalRecyclerViewFastScroller mScroller = null;
    private RecyclerView mRvRecycler = null;
    private RelativeLayout mLlNotFound = null;

    private LinearLayoutManager mManager = null;
    private List<CustomQueryResponse> mData = new ArrayList<>();
    private CustomQueryDownloadAdapter mAdapter = null;
    private ProgressDialog mDialog = null;

    public static DownloadFragment newInstance() {
        DownloadFragment fragment = new DownloadFragment();
        return  fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler_view, null, false);

        mScroller = (VerticalRecyclerViewFastScroller) view.findViewById(R.id.rvfs_fast_scroller);
        mRvRecycler = (RecyclerView) view.findViewById(R.id.rv_recycler);
        mLlNotFound = (RelativeLayout) view.findViewById(R.id.ll_search_not_found);

        mDialog = new ProgressDialog(getActivity());
        mDialog.setMessage("Downloading Query");
        mDialog.setIndeterminate(true);
        mDialog.show();

        mLlNotFound.setVisibility(View.GONE);
        mScroller.setVisibility(View.GONE);

        initRecyclerView();
        downloadCustomQuery();
        return view;
    }

    private void downloadCustomQuery() {
        String url = GlobalVariable.BASE_URL;
        BaseRestInterface restInterface = BaseRestClient.getRestClient(url).create(BaseRestInterface.class);

        Call<CustomQueryResponse> call = restInterface.getAllCustomQuery();
        call.enqueue(new Callback<CustomQueryResponse>() {
            @Override
            public void onResponse(Call<CustomQueryResponse> call, Response<CustomQueryResponse> response) {
                List<CustomQueryResponse> list = response.body().getListQuery();
                for(CustomQueryResponse item: list) {
                    Log.d("ASDASD", item.getId() + " " + item.getQueryAlias() + " "+ item.getQuery());
                }
                mRvRecycler.setVisibility(View.VISIBLE);
                mDialog.dismiss();

                mData.clear();
                mData.addAll(list);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<CustomQueryResponse> call, Throwable throwable) {

            }
        });
    }

    public void initRecyclerView() {
        mManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mAdapter = new CustomQueryDownloadAdapter(getActivity(), mData, this);

        mRvRecycler.setLayoutManager(mManager);
        mRvRecycler.setAdapter(mAdapter);
        mScroller.setRecyclerView(mRvRecycler);
        mRvRecycler.setOnScrollListener(mScroller.getOnScrollListener());
    }

    @Override
    public void onQueryClicked(int id) {
        downloadData(id);
    }

    public void downloadData(int i) {
        final String url = GlobalVariable.BASE_URL;
        BaseRestInterface restInterface = BaseRestClient.getRestClient(url).create(BaseRestInterface.class);

        Call<ItemResponse> call = restInterface.getTryJson(i);

        call.enqueue(new Callback<ItemResponse>() {
            @Override
            public void onResponse(Call<ItemResponse> call, Response<ItemResponse> response) {
                int statusCode = response.code();
                Log.d("ASDASD", response.code() + " " + response.raw());
                if (statusCode != 200) {
                    Toast.makeText(getActivity(), statusCode + " " + url + " Request cannot be completed, Check whether the server url is right", Toast.LENGTH_SHORT).show();

                    return;
                }
                List<Item> listItem = response.body().getListItem();
                Toast.makeText(getActivity(), "Download completed with " + listItem.size() + " rows", Toast.LENGTH_SHORT).show();
                addDataToDatabase(listItem);
            }

            @Override
            public void onFailure(Call<ItemResponse> call, Throwable throwable) {
                String error = throwable.toString();
                Toast.makeText(getActivity(), "On Failure " + error, Toast.LENGTH_SHORT).show();
            }
        });

        restInterface = null;
    }

    private void addDataToDatabase(final List<Item> listItem) {
        final MainActivity.Callbacks databaseCallback = new MainActivity.Callbacks() {
            @Override
            public void onSuccess() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "Database Write Success", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError() {
                //Toast.makeText(MainActivity.this, "Download and write success", Toast.LENGTH_SHORT).show();
            }
        };

        final MainActivity.Callbacks databaseDeleteCallback = new MainActivity.Callbacks() {
            @Override
            public void onSuccess() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "Database Delete Success", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError() {
                //Toast.makeText(MainActivity.this, "Download and write success", Toast.LENGTH_SHORT).show();
            }
        };

        Runnable databaseRunnable = new Runnable() {
            @Override
            public void run() {
                MyDBHandler databaseHandler = new MyDBHandler(getActivity(), null, null, 1);
                databaseHandler.deleteScanStokOpnameData(databaseDeleteCallback);
                databaseHandler.addDataOrderAndAsli(listItem, databaseCallback);
            }
        };

        new Thread(databaseRunnable).start();
    }
}
