package com.clouiotech.pda.demo.Activity;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.InterpolatorRes;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.ActionBarActivity;

import com.clouiotech.pda.demo.BaseObject.GlobalVariable;
import com.clouiotech.pda.demo.Fragment.ScanHistoryFragment;
import com.clouiotech.pda.demo.Fragment.StockScanFragment;
import com.clouiotech.pda.demo.PublicData;
import com.clouiotech.pda.demo.Sqlite.MyDBHandler;
import com.clouiotech.pda.demo.interfaces.QrCodeDetectImplementation;
import com.clouiotech.pda.demo.interfaces.QrCodeDetectListener;
import com.clouiotech.pda.demo.uhf.StokReadEPCActivity;
import com.clouiotech.pda.demo.uhf.UHFBaseActivity;
import com.clouiotech.pda.demoExample.R;

import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.clouiotech.pda.demo.Activity.MainActivity.Callbacks;
import com.clouiotech.pda.rfid.EPCModel;
import com.clouiotech.pda.rfid.IAsynchronousMessage;
import com.clouiotech.port.Adapt;
import com.clouiotech.util.Helper.Helper_ThreadPool;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by roka on 28/07/16.
 */
public class RecyclerViewActivity extends UHFBaseActivity implements View.OnClickListener, IAsynchronousMessage{
    private Fragment mFragment = null;
    private FloatingActionButton mFabSwitch = null;
    private List<String> mListTopItemName = new ArrayList<>();
    private int mOperation =0;
    private static String _NowReadParam = _NowAntennaNo + "|1";

    private boolean usingBackBattery = false;
    private final int MSG_RESULT_READ = MSG_USER_BEG + 1;
    private final int MSG_FLUSH_READTIME = MSG_USER_BEG + 2;
    private final int MSG_UHF_POWERLOW = MSG_USER_BEG + 3;

    private boolean mIsScannerActivated = false;

    private Boolean IsFlushList = true;
    private Boolean isStartPingPong = false;
    private static boolean isPowerLowShow = false;
    private Object beep_Lock = new Object();
    ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_SYSTEM,
            ToneGenerator.MAX_VOLUME);

    private int numberOfScans = 0;
    private MyDBHandler mDBHandler = null;
    private QrCodeDetectListener mQrCodeListener = null;

    private final int REQ_CODE_QR_CODE = 1;
    private String searchString = "";
    private boolean isFromQrCode = false;
    private String actionBarTitle = "Stock Scan";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_uhf);

        mFabSwitch = (FloatingActionButton) findViewById(R.id.fab_switch);
        mFabSwitch.setOnClickListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int toFragment = getIntent().getIntExtra(GlobalVariable.INTENT_EXTRA_PAGE, -1);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fr = fm.beginTransaction();

        mDBHandler = new MyDBHandler(this, null, null, 1);
        mQrCodeListener = new QrCodeDetectImplementation();

        switch (toFragment) {
            case GlobalVariable.PAGE_TO_STOCK_SCAN_FRAGMENT :
                getSupportActionBar().setTitle(getResources().getString(R.string.fragment_stock_scan));
                StockScanFragment stockScanFragment = StockScanFragment.newInstance();
                mFragment = stockScanFragment;
                fr.replace(R.id.ll_activity, stockScanFragment);

                break;

            case GlobalVariable.PAGE_TO_HISTORY_SCAN_FRAGMENT :
                getSupportActionBar().setTitle(getResources().getString(R.string.fragment_scan_history));
                ScanHistoryFragment scanHistoryFragment = ScanHistoryFragment.newInstance();
                mFragment = scanHistoryFragment;
                fr.replace(R.id.ll_activity, scanHistoryFragment);
                break;

            default :
                break;
        }

        fr.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
        if (mFragment instanceof StockScanFragment) {
            if(!isFromQrCode) {
                ((StockScanFragment) mFragment).getAllData();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if(mDBHandler != null) {
            mDBHandler.close();
        }
        super.onDestroy();
        dispose();
    }

    public MyDBHandler getDBHandler() {
        if(mDBHandler == null) {
            mDBHandler = new MyDBHandler(this, null, null, 1);
        }
        return mDBHandler;
    }

    protected void init() {
        usingBackBattery = canUsingBackBattery();
        if (!UHF_Init(usingBackBattery, this)) {
//            ShowMsg(getString(R.string.uhf_low_power_info),
//                    new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface arg0, int arg1) {
//                            //RecyclerViewActivity.this.finish();
//                        }
//                    });
        } else {
            try {
                super.UHF_GetReaderProperty();
                _NowReadParam = _NowAntennaNo + "|1";
                Thread.sleep(20);
                CLReader.Stop(); // ָֹͣ��
                Thread.sleep(20);
                super.UHF_SetTagUpdateParam();
            } catch (Exception ee) {
            }
            IsFlushList = true;
            Helper_ThreadPool.ThreadPool_StartSingle(new Runnable() {
                @Override
                public void run() {
                    while (IsFlushList) {
                        try {
                            sendMessage(MSG_RESULT_READ, null);
                            Thread.sleep(1000); // һ����ˢ��һ��
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            Helper_ThreadPool.ThreadPool_StartSingle(new Runnable() { // ����������
                @Override
                public void run() {
                    while (IsFlushList) {
                        synchronized (beep_Lock) {
                            try {
                                beep_Lock.wait();
                            } catch (InterruptedException e) {
                            }
                        }
                        if (IsFlushList) {
                            toneGenerator
                                    .startTone(ToneGenerator.TONE_PROP_BEEP);
                        }

                    }
                }
            });
            //


             return;
        }
    }

    protected void dispose() {
        isStartPingPong = false;
        IsFlushList = false;
        synchronized (beep_Lock) {
            beep_Lock.notifyAll();
        }
        UHF_Dispose();
    }
    public void UHF_Dispose() {
        if (_UHFSTATE == true) {
            CLReader.CloseConnect();
            _UHFSTATE = false;
        }
    }

    private Boolean canUsingBackBattery() {
        if (Adapt.getPowermanagerInstance().getBackupPowerSOC() < low_power_soc) {
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case android.R.id.home : {
                    finish();
            } break;

            case R.id.fab_switch : {
                if(mIsScannerActivated) {
                    pingpongStop();
                    mFabSwitch.setImageResource(R.drawable.ic_search_white_36dp);
                    Toast.makeText(view.getContext(), "Scanner stopped", Toast.LENGTH_SHORT).show();
                } else {
                    pingpongRead();
                    mFabSwitch.setImageResource(R.drawable.ic_stop_white_36dp);
                    Toast.makeText(view.getContext(), "Scanner started", Toast.LENGTH_SHORT).show();
                }
            }

            default :
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_recycler_view, menu);
        menu.findItem(R.id.menu_search).setVisible(true);

        initActionBarSearchView(menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (isFromQrCode) {
            final MenuItem searchItem = menu.findItem(R.id.menu_search);
            final SearchView search = (SearchView) searchItem.getActionView();

            getSupportActionBar().setTitle(searchString);
            search.setQuery(searchString, true);
            isFromQrCode = false;
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home :
                finish();
                break;

            case R.id.menu_save :
                Toast.makeText(RecyclerViewActivity.this, "Save button", Toast.LENGTH_SHORT).show();
                break;

            case R.id.menu_sort :
                showSortDialog();
                break;

            case R.id.menu_filter :
                showFilterDialog();
                break;

            case R.id.menu_qrcode :
                Intent intent = new Intent(this, QRCodeReaderActivity.class);
                intent.putExtra(GlobalVariable.INTENT_INTERFACE, mQrCodeListener);
                startActivityForResult(intent, REQ_CODE_QR_CODE);
                break;
        }

        return true;
    }

    private void searchViewTextSubmitted(String text) {
        if (mFragment instanceof  StockScanFragment) {
            ((StockScanFragment) mFragment).refreshDataByTextQuery(text);
        }
    }

    private void initActionBarSearchView(Menu menu) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

            final MenuItem searchItem = menu.findItem(R.id.menu_search);
            final SearchView search = (SearchView) searchItem.getActionView();
            search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
            search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    searchViewTextSubmitted(query);
                    if(query == "") getSupportActionBar().setTitle(actionBarTitle);
                    else getSupportActionBar().setTitle(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {

                    return true;
                }
            });

            ImageView searchViewCloseButton = (ImageView) search.findViewById(R.id.search_close_btn);
            searchViewCloseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EditText et = (EditText) search.findViewById(R.id.search_src_text);
                    et.setText("");

                    search.onActionViewCollapsed();
                    searchItem.collapseActionView();

                    searchViewTextSubmitted("");

                    getSupportActionBar().setTitle(actionBarTitle);
                }
            });
        }
    }

    public void saveEpcItemToDatabase(int scanId, String epcRawCode) {
        if(!(mFragment instanceof StockScanFragment)) return;
        else {
            ((StockScanFragment) mFragment).saveScanItemToDatabaseInFragment(scanId, epcRawCode);
        }
    }

    private void showFilterDialog() {
        FilterDialogFragment dialog = new FilterDialogFragment();
        dialog.show(getSupportFragmentManager(), null);
    }

    private void showSortDialog() {
        SortDialogFragment dialog = new SortDialogFragment();
        dialog.show(getSupportFragmentManager(), null);
    }

    private void searchByItemName(String text) {
        if(mFragment instanceof StockScanFragment) {
            ((StockScanFragment) mFragment).filterByItemName(text);
        }
    }

    @Override
    public void OutPutEPC(EPCModel epcModel) {
        if(!isStartPingPong) {
            return;
        }
        toneGenerator
                .startTone(ToneGenerator.TONE_PROP_BEEP);
        if(mFragment instanceof StockScanFragment) {
            ((StockScanFragment) mFragment).doProcessEPCScanResult(epcModel);
        }
    }

    public void pingpongRead() {
        if (isStartPingPong)
            return;
        isStartPingPong = true;
        mIsScannerActivated = true;
        Helper_ThreadPool.ThreadPool_StartSingle(new Runnable() {
            @Override
            public void run() {
                while (isStartPingPong) {
                    try {
                        if (!isPowerLowShow) {
                            if (usingBackBattery && !canUsingBackBattery()) {
                                sendMessage(MSG_UHF_POWERLOW, null);
                            }

                            if (PublicData._IsCommand6Cor6B.equals("6C")) {// ��6C��ǩ
                                CLReader.Read_EPC(_NowReadParam);
                            } else {// ��6B��ǩ
                                CLReader.Get6B(_NowAntennaNo + "|1" + "|1"
                                        + "|" + "1,000F");
                            }

                            Thread.sleep(PublicData._PingPong_ReadTime);

                            if (PublicData._PingPong_StopTime > 0) {
                                CLReader.Stop();
                                Thread.sleep(PublicData._PingPong_StopTime);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void pingpongStop() {
        isStartPingPong = false;
        mIsScannerActivated = false;
        CLReader.Stop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQ_CODE_QR_CODE) {
            if(resultCode == RESULT_OK) {
                isFromQrCode = true;
                searchString = data.getStringExtra(GlobalVariable.INTENT_RETURN_DATA);
                invalidateOptionsMenu();
            }
        }

    }

    // TODO: THIS IS INNER CLASS
    // ----------------------------- INNER CLASS -----------------------//

    public class FilterDialogFragment extends DialogFragment {
        public FilterDialogFragment() {
            super();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final String[] filterOperation = {"Item Name", "Quantity", "Physics", "Delta"};

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
            dialogBuilder.setTitle("Pick Filter");
            dialogBuilder.setItems(filterOperation, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(RecyclerViewActivity.this, "Filter with " + filterOperation[i], Toast.LENGTH_SHORT).show();
                    if(i == 0) {
                        getTopItemNameFilter();
                    } else {
                        mOperation = i;
                        showItemQuantityStatusFilter(i-1);
                    }
                }
            });

            return dialogBuilder.create();
        }

        private void showItemQuantityStatusFilter(int i) {
            ItemQuantityStatusFilterDialogFragment dialog = new ItemQuantityStatusFilterDialogFragment(i);
            dialog.show(getSupportFragmentManager(), null);
        }

        private void getTopItemNameFilter() {
            final TopItemNameCallback callback = new TopItemNameCallback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {

                }

                @Override
                public void onData(final List<String> data) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mListTopItemName.addAll(data);
                            ItemNameFilterDialogFragment dialog = new ItemNameFilterDialogFragment(mListTopItemName);
                            dialog.show(getSupportFragmentManager(), null);
                        }
                    });
                }
            };

            Runnable databaseRunnable = new Runnable() {
                @Override
                public void run() {
                    MyDBHandler dbHandler = new MyDBHandler(RecyclerViewActivity.this, null, null, 1);
                    dbHandler.getTopItemName(callback);
                }
            };

            new Thread(databaseRunnable).start();
        }
    }

    public class ItemNameFilterDialogFragment extends DialogFragment {
        private List<String> topItemName = null;
        private String[] items = null;

        public ItemNameFilterDialogFragment(List<String> listOfTopItemName) {
            topItemName = listOfTopItemName;
            items = new String[topItemName.size()];
            items = topItemName.toArray(items);
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
            dialogBuilder.setTitle("Top Item Name");
            dialogBuilder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(RecyclerViewActivity.this, "Filter with " + topItemName.get(i), Toast.LENGTH_SHORT).show();
                    searchByItemName(topItemName.get(i));
                }
            });

            return dialogBuilder.create();
        }
    }

    public class ItemQuantityStatusFilterDialogFragment extends DialogFragment {
        private String[] quantityOperation = {"Quantity", "Physics","Delta"};
        int status = 0;
        public ItemQuantityStatusFilterDialogFragment(int i) {
            super();
            status = i;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            LayoutInflater inflater = RecyclerViewActivity.this.getLayoutInflater();
            View customView = inflater.inflate(R.layout.dialog_filter_slider, null);
            SeekBar sbFilterSliderMinimum = (SeekBar) customView.findViewById(R.id.sb_filter_minimum);
            final TextView tvFilterValueMinimum = (TextView) customView.findViewById(R.id.tv_seekbar_value_minimum);
            SeekBar sbFilterSliderMaximum = (SeekBar) customView.findViewById(R.id.sb_filter_maximum);
            final TextView tvFilterValueMaximum= (TextView) customView.findViewById(R.id.tv_seekbar_value_maximum);

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
            dialogBuilder.setPositiveButton("Filter", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Log.d("ASDASD", "OPERATION " + mOperation);
                    if(mFragment instanceof  StockScanFragment) {
                        Toast.makeText(RecyclerViewActivity.this, "Filter by " + quantityOperation[mOperation-1] , Toast.LENGTH_SHORT).show();
                        ((StockScanFragment) mFragment).getEpcObjectFilter(mOperation,
                                Integer.parseInt(tvFilterValueMinimum.getText().toString()),
                                Integer.parseInt(tvFilterValueMaximum.getText().toString()));
                    }
                }
            });
            dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });

            dialogBuilder.setView(customView);
            sbFilterSliderMinimum.setMax(50);
            sbFilterSliderMaximum.setMax(50);

            sbFilterSliderMinimum.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    tvFilterValueMinimum.setText(""+ (i-25));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            sbFilterSliderMaximum.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    tvFilterValueMaximum.setText(""+ (i-25));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            return dialogBuilder.create();
        }
    }

    public class SortDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final String[] sortOperation = {"Item Code", "Quantity", "Physics", "Delta"};

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
            dialogBuilder.setTitle("Sort By");
            dialogBuilder.setItems(sortOperation , new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(RecyclerViewActivity.this, "Sort with " + sortOperation[i], Toast.LENGTH_SHORT).show();
                    AscendingAndDescendingSortDialogFragment dialog = new AscendingAndDescendingSortDialogFragment(i, sortOperation[i]);
                    dialog.show(getSupportFragmentManager(), null);
                }
            });

            return dialogBuilder.create();
        }
    }

    public class AscendingAndDescendingSortDialogFragment extends DialogFragment {
        private int mSortOperation = 0;
        private String mSortOperationString = null;
        public AscendingAndDescendingSortDialogFragment(int operation, String operationString) {
            this.mSortOperation = operation;
            this.mSortOperationString = operationString;
        }
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final String[] sortOperation = {"Ascending", "Descending"};

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
            dialogBuilder.setTitle("Pick Filter");
            dialogBuilder.setItems(sortOperation , new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(RecyclerViewActivity.this, "Sort with " + mSortOperationString + " " + sortOperation[i], Toast.LENGTH_SHORT).show();
                    if(mFragment instanceof  StockScanFragment) {
                        ((StockScanFragment) mFragment).sortStockScan(mSortOperation, i);
                    }
                }
            });

            return dialogBuilder.create();
        }

    }

    public interface TopItemNameCallback extends Callbacks {
        void onData(List<String> data);
    }
}
