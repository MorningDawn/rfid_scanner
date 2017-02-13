package com.clouiotech.pda.demo.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.Toast;

import com.clouiotech.pda.demo.BaseObject.EpcObject;
import com.clouiotech.pda.demo.BaseObject.GlobalVariable;
import com.clouiotech.pda.demo.Fragment.StockScanFragment.DatabaseResponseCallback;
import com.clouiotech.pda.demo.Sqlite.MyDBHandler;
import com.clouiotech.pda.demoExample.R;
import com.clouiotech.pda.demo.Activity.MainActivity.Callbacks;

import java.util.List;

import github.nisrulz.qreader.QRDataListener;
import github.nisrulz.qreader.QREader;

/**
 * Created by roka on 24/01/17.
 */

public class QRCodeReaderActivity extends AppCompatActivity {
    private SurfaceView mSvQrCode = null;
    private QREader mQReader = null;
    private volatile boolean isScanned = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_reader);

        mSvQrCode = (SurfaceView) findViewById(R.id.sv_qrcode);
        mQReader = new QREader.Builder(this, mSvQrCode, new QRDataListener() {
            @Override
            public void onDetected(final String data) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra(GlobalVariable.INTENT_RETURN_DATA, data);
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        }).facing(QREader.BACK_CAM).enableAutofocus(true).height(mSvQrCode.getHeight()).width(mSvQrCode.getWidth()).build();

        mQReader.start();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mQReader != null && mSvQrCode != null) mQReader.initAndStart(mSvQrCode);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mQReader != null) mQReader.releaseAndCleanup();
    }
}
