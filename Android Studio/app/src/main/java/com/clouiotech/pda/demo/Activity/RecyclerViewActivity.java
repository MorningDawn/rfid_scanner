package com.clouiotech.pda.demo.Activity;

import android.app.SearchManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;

import com.clouiotech.pda.demo.BaseObject.GlobalVariable;
import com.clouiotech.pda.demo.Fragment.ScanHistoryFragment;
import com.clouiotech.pda.demo.Fragment.StockScanFragment;
import com.clouiotech.pda.demoExample.R;

import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

/**
 * Created by roka on 28/07/16.
 */
public class RecyclerViewActivity extends ActionBarActivity implements View.OnClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_scan_uhf);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int toFragment = getIntent().getIntExtra(GlobalVariable.INTENT_EXTRA_PAGE, -1);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fr = fm.beginTransaction();

        switch (toFragment) {
            case GlobalVariable.PAGE_TO_STOCK_SCAN_FRAGMENT :
                getSupportActionBar().setTitle(getResources().getString(R.string.fragment_stock_scan));
                StockScanFragment stockScanFragment = StockScanFragment.newInstance();
                fr.replace(R.id.ll_activity, stockScanFragment);

                break;

            case GlobalVariable.PAGE_TO_HISTORY_SCAN_FRAGMENT :
                getSupportActionBar().setTitle(getResources().getString(R.string.fragment_scan_history));
                ScanHistoryFragment scanHistoryFragment = ScanHistoryFragment.newInstance();
                fr.replace(R.id.ll_activity, scanHistoryFragment);
                break;

            default :
                break;
        }

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_recycler_view, menu);
        menu.findItem(R.id.menu_search).setVisible(true);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

            MenuItem searchItem = menu.findItem(R.id.menu_search);
            SearchView search = (SearchView) searchItem.getActionView();
            search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
            search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    Toast.makeText(RecyclerViewActivity.this, "Text Submitted " + query, Toast.LENGTH_SHORT).show();
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return true;
                }
            });
        }
        return true;
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
                Toast.makeText(RecyclerViewActivity.this, "Sort button", Toast.LENGTH_SHORT).show();
                break;
        }

        return true;
    }
}
