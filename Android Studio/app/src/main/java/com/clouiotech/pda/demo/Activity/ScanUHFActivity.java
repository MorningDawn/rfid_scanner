package com.clouiotech.pda.demo.Activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;

import com.clouiotech.pda.demo.fragment.StockScanFragment;
import com.clouiotech.pda.demoExample.R;

import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

/**
 * Created by roka on 28/07/16.
 */
public class ScanUHFActivity extends ActionBarActivity implements View.OnClickListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.scan_uhf_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        StockScanFragment fragment = StockScanFragment.newInstance();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fr = fm.beginTransaction();
        fr.replace(R.id.ll_activity, fragment);
        fr.commit();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case android.R.id.home : {
                    finish();
            } break;

            default :
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home :
                finish();
                break;
        }

        return true;
    }
}
