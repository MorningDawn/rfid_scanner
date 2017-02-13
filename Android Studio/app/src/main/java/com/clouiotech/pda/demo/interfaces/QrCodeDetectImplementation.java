package com.clouiotech.pda.demo.interfaces;

import android.app.Application;
import android.widget.Toast;

import com.clouiotech.pda.demo.Activity.RecyclerViewActivity;

/**
 * Created by roka on 24/01/17.
 */

public class QrCodeDetectImplementation extends Application implements QrCodeDetectListener {
    @Override
    public void onQrCodeDetected(String data) {
        Toast.makeText(getApplicationContext(), data, Toast.LENGTH_SHORT).show();
    }
}
