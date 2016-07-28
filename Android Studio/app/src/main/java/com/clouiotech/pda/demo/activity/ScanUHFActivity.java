package com.clouiotech.pda.demo.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.clouiotech.pda.demo.fragment.StockScanFragment;
import com.clouiotech.pda.demoExample.R;

import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by roka on 28/07/16.
 */
public class ScanUHFActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.scan_uhf_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        StockScanFragment fragment = StockScanFragment.newInstance();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fr = fm.beginTransaction();
        fr.replace(R.id.ll_activity, fragment);
        fr.commit();
    }
}
