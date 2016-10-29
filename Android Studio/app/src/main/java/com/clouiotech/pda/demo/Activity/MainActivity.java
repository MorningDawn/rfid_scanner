package com.clouiotech.pda.demo.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.clouiotech.pda.demo.BaseObject.GlobalVariable;
import com.clouiotech.pda.demo.BaseObject.Item;
import com.clouiotech.pda.demo.BaseObject.ItemResponse;
import com.clouiotech.pda.demo.Sqlite.DataAsli;
import com.clouiotech.pda.demo.Sqlite.DataOrder;
import com.clouiotech.pda.demo.Sqlite.MyDBHandler;
import com.clouiotech.pda.demo.restclient.BaseRestClient;
import com.clouiotech.pda.demo.restinterface.BaseRestInterface;
import com.clouiotech.pda.demoExample.R;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by roka on 25/07/16.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView mIvScanUHF;
    private LinearLayout mLlDownload;
    private LinearLayout mLlScan;
    private LinearLayout mLlClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mIvScanUHF = (ImageView) findViewById(R.id.iv_uhf);
        mLlDownload = (LinearLayout) findViewById(R.id.ll_download);
        mLlScan = (LinearLayout) findViewById(R.id.ll_scan);
        mLlClear = (LinearLayout) findViewById(R.id.ll_clear);

        mIvScanUHF.setOnClickListener(this);
        mLlClear.setOnClickListener(this);
        mLlScan.setOnClickListener(this);
        mLlDownload.setOnClickListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menu) {
        switch (menu.getItemId()) {
            case R.id.menu_version :
                Toast.makeText(MainActivity.this, "Version clicked", Toast.LENGTH_LONG).show();
                break;
            case R.id.menu_admin_privillege :
                Toast.makeText(MainActivity.this, "Admin Mode On", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, AdminMainActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_uhf : {
                Toast.makeText(MainActivity.this, "Scan UHF set", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, RecyclerViewActivity.class);
                intent.putExtra(GlobalVariable.INTENT_EXTRA_PAGE, GlobalVariable.PAGE_TO_STOCK_SCAN_FRAGMENT);
                startActivity(intent);
            } break;

            case R.id.ll_download : {
                Toast.makeText(MainActivity.this, "Download Data", Toast.LENGTH_SHORT).show();
                downloadData();
            } break;

            case R.id.ll_scan : {
                Toast.makeText(MainActivity.this, "History Scan", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, RecyclerViewActivity.class);
                intent.putExtra(GlobalVariable.INTENT_EXTRA_PAGE, GlobalVariable.PAGE_TO_HISTORY_SCAN_FRAGMENT);
                startActivity(intent);
            } break;

            case R.id.ll_clear : {
                Toast.makeText(MainActivity.this, "Clear Data", Toast.LENGTH_SHORT).show();
            } break;

            default:
                break;
        }
    }

    private void downloadData() {
        BaseRestInterface restInterface = BaseRestClient.getRestClient().create(BaseRestInterface.class);

        Call<ItemResponse> call = restInterface.getTryJson();

        call.enqueue(new Callback<ItemResponse>() {
            @Override
            public void onResponse(Call<ItemResponse> call, Response<ItemResponse> response) {
                int statusCode = response.code();
                List<Item> listItem = response.body().getListItem();
                Toast.makeText(MainActivity.this, "sizenya " + listItem.size(), Toast.LENGTH_SHORT).show();
                addDataToDatabase(listItem);
//                for(int i=0; i<listItem.size(); i++) {
//                    Toast.makeText(MainActivity.this,
//                            "ini " + listItem.get(i).getItemId(),
//                            Toast.LENGTH_SHORT).show();
//                }
            }

            @Override
            public void onFailure(Call<ItemResponse> call, Throwable throwable) {
                String error = throwable.toString();
                Toast.makeText(MainActivity.this, "On Failure " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addDataToDatabase(final List<Item> listItem) {
        final Callbacks databaseCallback = new Callbacks() {
            @Override
            public void onSuccess() {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Database Write Success", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, RecyclerViewActivity.class);
                        intent.putExtra(GlobalVariable.INTENT_EXTRA_PAGE, GlobalVariable.PAGE_TO_STOCK_SCAN_FRAGMENT);
                        startActivity(intent);
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
                MyDBHandler databaseHandler = new MyDBHandler(MainActivity.this, null, null, 1);
                databaseHandler.addDataOrderAndAsli(listItem, databaseCallback);
            }
        };

        new Thread(databaseRunnable).start();
    }

    public static interface Callbacks {
        void onSuccess();
        void onError();
    }
}
