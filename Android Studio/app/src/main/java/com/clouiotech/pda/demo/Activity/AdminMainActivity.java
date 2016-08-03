package com.clouiotech.pda.demo.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.clouiotech.pda.demo.BaseObject.GlobalVariable;
import com.clouiotech.pda.demoExample.R;

/**
 * Created by roka on 03/08/16.
 */
public class AdminMainActivity extends ActionBarActivity implements View.OnClickListener {
    private ImageView mIvScanUHF;
    private LinearLayout mLlDownload;
    private LinearLayout mLlScan;
    private LinearLayout mLlClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

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
        getSupportActionBar().setTitle("PDADemo Edit" + " Admin");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem admin = menu.findItem(R.id.menu_admin_privillege);
        admin.setVisible(false);
        admin.setEnabled(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menu) {
        switch (menu.getItemId()) {
            case R.id.menu_version :
                Toast.makeText(AdminMainActivity.this, "Version clicked", Toast.LENGTH_LONG).show();
                break;
            case R.id.menu_admin_privillege :
                Toast.makeText(AdminMainActivity.this, "Admin Mode On", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(AdminMainActivity.this, "Scan UHF set", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AdminMainActivity.this, RecyclerViewActivity.class);
                intent.putExtra(GlobalVariable.INTENT_EXTRA_PAGE, GlobalVariable.PAGE_TO_STOCK_SCAN_FRAGMENT);
                startActivity(intent);
            } break;

            case R.id.ll_download : {
                Toast.makeText(AdminMainActivity.this, "Download Data", Toast.LENGTH_SHORT).show();
            } break;

            case R.id.ll_scan : {
                Toast.makeText(AdminMainActivity.this, "History Scan", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AdminMainActivity.this, RecyclerViewActivity.class);
                intent.putExtra(GlobalVariable.INTENT_EXTRA_PAGE, GlobalVariable.PAGE_TO_HISTORY_SCAN_FRAGMENT);
                startActivity(intent);
            } break;

            case R.id.ll_clear : {
                Toast.makeText(AdminMainActivity.this, "Clear Data", Toast.LENGTH_SHORT).show();
            } break;

            case R.id.ll_config : {
                Toast.makeText(AdminMainActivity.this, "Config", Toast.LENGTH_SHORT).show();
            } break;

            case R.id.ll_read_test : {
                Toast.makeText(AdminMainActivity.this, "Read Test", Toast.LENGTH_SHORT).show();
            } break;

            default:
                break;
        }
    }
}
