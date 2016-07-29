package com.clouiotech.pda.demo.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.clouiotech.pda.demoExample.R;
/**
 * Created by roka on 25/07/16.
 */
public class MainActivity extends AppCompatActivity {
    private ImageView mIvScanUHF;
    private LinearLayout mLlDownload;
    private LinearLayout mLlScan;
    private LinearLayout mLlClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        mIvScanUHF = (ImageView) findViewById(R.id.iv_uhf);
        mLlDownload = (LinearLayout) findViewById(R.id.ll_download);
        mLlScan = (LinearLayout) findViewById(R.id.ll_scan);
        mLlClear = (LinearLayout) findViewById(R.id.ll_clear);

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.iv_uhf : {
                        Toast.makeText(MainActivity.this, "Scan UHF set", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, ScanUHFActivity.class);
                        startActivity(intent);
                    } break;

                    case R.id.ll_download : {
                        Toast.makeText(MainActivity.this, "Download Data", Toast.LENGTH_SHORT).show();
                    } break;

                    case R.id.ll_scan : {
                        Toast.makeText(MainActivity.this, "History Scan", Toast.LENGTH_SHORT).show();
                    } break;

                    case R.id.ll_clear : {
                        Toast.makeText(MainActivity.this, "Clear Data", Toast.LENGTH_SHORT).show();
                    } break;

                    default:
                        break;
                }
            }
        };

        mIvScanUHF.setOnClickListener(clickListener);
        mLlClear.setOnClickListener(clickListener);
        mLlScan.setOnClickListener(clickListener);
        mLlDownload.setOnClickListener(clickListener);

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
            default:
                break;
        }
        return true;
    }
}
