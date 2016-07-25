package com.clouiotech.pda.demo.uhf;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.clouiotech.pda.demo.Sqlite.MyDBHandler;
import com.clouiotech.pda.demoExample.R;

import java.util.List;

public class HistoryActivity extends Activity {
    List<String> listTANGGAL;
    List<String> listKODE;
    private ListView listView = null ;
    Button btnDeleteAll,btnExportAll;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        btnDeleteAll =(Button)findViewById(R.id.btnDeleteAllHistory);
        btnExportAll =(Button)findViewById(R.id.btnExportAllHistory);
        btnDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(HistoryActivity.this);
                builder.setMessage("Anda yakin akan menghapus semua data report?")
                        .setCancelable(false)
                        .setPositiveButton("Ya",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dg,
                                                        int id) {
                                        MyDBHandler dbHandler = new MyDBHandler(HistoryActivity.this, null, null, 1);
                                        dbHandler.deleteAll("report");
                                        Toast.makeText(getApplicationContext(), "Data dihapus!", Toast.LENGTH_LONG).show();
                                        OpenMainActivity();
                                    }
                                })
                        .setNegativeButton("Tidak",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                dialog.cancel();
                            }
                        }).show();
            }
        });
        btnExportAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDBHandler dbHandler = new MyDBHandler(getApplicationContext(), null, null, 1);
                String date = (DateFormat.format("ddMMyyyy_hhmmss", new java.util.Date()).toString());
                if(dbHandler.exportToCsv("ALL"+"_"+date,"")){
                    Toast.makeText(getApplicationContext(), "Export all history success!", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), "Export all history failed!", Toast.LENGTH_SHORT).show();
                }
                dbHandler.close();
            }
        });
        dataList();
    }

    public void dataList() {
        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
        listKODE = dbHandler.getAllHistory(0);
        listTANGGAL = dbHandler.getAllHistory(1);
        HistoryList adapter = new
                HistoryList(HistoryActivity.this,
                listKODE, listTANGGAL);
        listView = (ListView) this.findViewById(R.id.lv_Main);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
//                Toast.makeText(getApplicationContext(), "You Clicked at code " + listKODE.get(position), Toast.LENGTH_SHORT).show();
                Intent i = new Intent(HistoryActivity.this,DetailHistoryActivity.class);
                i.putExtra("kodeHistory", listKODE.get(position));
                i.putExtra("tanggal", listTANGGAL.get(position));
                startActivity(i);
                HistoryActivity.this.finish();
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "You Clicked at code " + listKODE.get(position), Toast.LENGTH_SHORT).show();
//                dialogDescrition(position);
                return true;
            }
        });
        dbHandler.close();
    }

    public void OpenMainActivity() {
        Intent intent = new Intent();
        intent.setClass(HistoryActivity.this, StokOpnameActivity.class);
        startActivity(intent);
        HistoryActivity.this.finish();
    }

    @Override
    public void onBackPressed() {
        OpenMainActivity();
    }
}
