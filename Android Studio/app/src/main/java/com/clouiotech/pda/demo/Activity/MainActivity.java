package com.clouiotech.pda.demo.Activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.clouiotech.pda.demo.BaseObject.GlobalVariable;
import com.clouiotech.pda.demo.BaseObject.Item;
import com.clouiotech.pda.demo.BaseObject.ItemResponse;
import com.clouiotech.pda.demo.Sqlite.MyDBHandler;
import com.clouiotech.pda.demo.restclient.BaseRestClient;
import com.clouiotech.pda.demo.restinterface.BaseRestInterface;
import com.clouiotech.pda.demoExample.R;

import java.util.List;

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
    private LinearLayout mLlExport;
    private SharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPref = getSharedPreferences(GlobalVariable.PREFERENCES_NAME, Context.MODE_PRIVATE);

        mIvScanUHF = (ImageView) findViewById(R.id.iv_uhf);
        mLlDownload = (LinearLayout) findViewById(R.id.ll_download);
        mLlScan = (LinearLayout) findViewById(R.id.ll_scan);
        mLlClear = (LinearLayout) findViewById(R.id.ll_clear);
        mLlExport = (LinearLayout) findViewById(R.id.ll_export);

        mIvScanUHF.setOnClickListener(this);
        mLlClear.setOnClickListener(this);
        mLlScan.setOnClickListener(this);
        mLlDownload.setOnClickListener(this);
        mLlExport.setOnClickListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Create Database if app is installed for first time
        new MyDBHandler(this, null, null, 0);

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
                Toast.makeText(MainActivity.this, getResources().getString(R.string.app_version), Toast.LENGTH_LONG).show();
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
                Intent intent = new Intent(MainActivity.this, RecyclerViewActivity.class);
                intent.putExtra(GlobalVariable.INTENT_EXTRA_PAGE, GlobalVariable.PAGE_TO_DOWNLOAD_FRAGMENT);
                startActivity(intent);
            } break;

            case R.id.ll_scan : {
                Toast.makeText(MainActivity.this, "History Scan", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, RecyclerViewActivity.class);
                intent.putExtra(GlobalVariable.INTENT_EXTRA_PAGE, GlobalVariable.PAGE_TO_HISTORY_SCAN_FRAGMENT);
                startActivity(intent);
            } break;

            case R.id.ll_clear : {
                deleteStockScanDataInDatabase();
            } break;

            case R.id.ll_export : {
                Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show();
            } break;

            default:
                break;
        }
    }

    public void downloadData(int i) {
        final String url = mPref.getString(GlobalVariable.PREF_DOWNLOAD_URL_KEY, GlobalVariable.BASE_URL);
        BaseRestInterface restInterface = BaseRestClient.getRestClient(url).create(BaseRestInterface.class);

        Call<ItemResponse> call = restInterface.getTryJson(i);

        call.enqueue(new Callback<ItemResponse>() {
            @Override
            public void onResponse(Call<ItemResponse> call, Response<ItemResponse> response) {
                int statusCode = response.code();
                Log.d("ASDASD", response.code() + " " + response.raw());
                if (statusCode != 200) {
                    Toast.makeText(MainActivity.this, statusCode + " " + url + " Request cannot be completed, Check whether the server url is right", Toast.LENGTH_SHORT).show();

                    return;
                }
                List<Item> listItem = response.body().getListItem();
                Toast.makeText(MainActivity.this, "Download completed with " + listItem.size() + " rows", Toast.LENGTH_SHORT).show();
                addDataToDatabase(listItem);
            }

            @Override
            public void onFailure(Call<ItemResponse> call, Throwable throwable) {
                String error = throwable.toString();
                Toast.makeText(MainActivity.this, "On Failure " + error, Toast.LENGTH_SHORT).show();
            }
        });

        restInterface = null;
    }

    private void addDataToDatabase(final List<Item> listItem) {
        final Callbacks databaseCallback = new Callbacks() {
            @Override
            public void onSuccess() {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Database Write Success", Toast.LENGTH_SHORT).show();
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

    private void deleteStockScanDataInDatabase() {
        final Callbacks databaseCallback = new Callbacks() {
            @Override
            public void onSuccess() {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Delete Data Success", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError() {

            }
        };

        Runnable deleteInDatabase = new Runnable() {
            @Override
            public void run() {
                MyDBHandler databaseHandler = new MyDBHandler(MainActivity.this, null, null, 1);
                databaseHandler.deleteScanStokOpnameData(databaseCallback);
            }
        };

        new Thread(deleteInDatabase).start();
    }

    private void showDownloadAlertDialog() {
        DownloadAlertDialogFragment dialog = new DownloadAlertDialogFragment();
        dialog.show(getSupportFragmentManager(), null);
    }

    private void showDownloadURLAlertDialog() {
        DownloadURLDialogFragment dialog = new DownloadURLDialogFragment();
        dialog.show(getSupportFragmentManager(), null);
    }

    public interface Callbacks {
        void onSuccess();
        void onError();
    }

    public class DownloadURLDialogFragment extends DialogFragment {
        public DownloadURLDialogFragment() {
            super();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            LayoutInflater inflater = MainActivity.this.getLayoutInflater();
            View downloadURLView = inflater.inflate(R.layout.dialog_download_url, null);
            final EditText etDownloadURL = (EditText) downloadURLView.findViewById(R.id.et_download_url);

            etDownloadURL.setText(mPref.getString(GlobalVariable.PREF_DOWNLOAD_URL_KEY, GlobalVariable.BASE_URL));



            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
            dialogBuilder.setView(downloadURLView);
            dialogBuilder.setPositiveButton("Download", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if(!(etDownloadURL.getText().toString().endsWith("/"))) {
                        Toast.makeText(MainActivity.this, "Make sure url ends with '/'", Toast.LENGTH_SHORT).show();
                    } else {
                        String downloadURL = etDownloadURL.getText().toString();
                        Log.d("ASDF", downloadURL);
                        showDownloadAlertDialog();
                        mPref.edit().putString(GlobalVariable.PREF_DOWNLOAD_URL_KEY, downloadURL).commit();
                    }
                }
            });
            dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });

            return dialogBuilder.create();
        }
    }

    public class DownloadAlertDialogFragment extends DialogFragment {
        public DownloadAlertDialogFragment() {
            super();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final String[] listOfDownloadFilter = {"Filter Gudang", "Filter Period", "Filter Gudang dan Period"};

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
            dialogBuilder.setTitle("Pick Mode");
            dialogBuilder.setItems(listOfDownloadFilter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(MainActivity.this, "Download with " + listOfDownloadFilter[i], Toast.LENGTH_SHORT).show();
                    downloadData(i);
                }
            });

            return dialogBuilder.create();
        }
    }
}
