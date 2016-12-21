package com.clouiotech.pda.demo.Activity;

import android.app.SearchManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by roka on 28/07/16.
 */
public class RecyclerViewActivity extends ActionBarActivity implements View.OnClickListener {
    private Fragment mFragment = null;
    private FloatingActionButton mFabSwitch = null;
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
    public void onClick(View view) {
        switch(view.getId()) {
            case android.R.id.home : {
                    finish();
            } break;

            case R.id.fab_switch : {
                Toast.makeText(RecyclerViewActivity.this, "FAB CLicked", Toast.LENGTH_SHORT).show();
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
}
