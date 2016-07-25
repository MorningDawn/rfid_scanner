package com.clouiotech.pda.demo.uhf;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.clouiotech.pda.demo.ItemMainActivity;
import com.clouiotech.pda.demo.Sqlite.MyDBHandler;
import com.clouiotech.pda.demoExample.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class StokOpnameActivity extends Activity {
    Button btnScan, btnHistory, btnDownload, btnClear;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stok_opname);
        context = this;
//        copyDatabase(this.context,"rfid.db");
        btnScan = (Button)findViewById(R.id.btnScanData);
        btnHistory = (Button)findViewById(R.id.btnHistoryData);
        btnDownload = (Button)findViewById(R.id.btnDownloadData);
        btnClear = (Button)findViewById(R.id.btnClearData);

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenProsesActivity();
            }
        });

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenDownloadActivity();
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(StokOpnameActivity.this);
                builder.setMessage("Anda yakin akan menghapus semua data di table order?")
                        .setCancelable(false)
                        .setPositiveButton("Ya",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dg,
                                                        int id) {
                                        MyDBHandler dbHandler = new MyDBHandler(StokOpnameActivity.this, null, null, 1);
                                        dbHandler.deleteAll("data_order");
                                        dbHandler.deleteAll("data_asli");
                                        dbHandler.deleteAll("temp_code");
                                        Toast.makeText(getApplicationContext(), "Data dihapus!", Toast.LENGTH_LONG).show();
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

        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenHistoryActivity();
            }
        });
    }

    public void OpenProsesActivity() {
        Intent intent = new Intent();
        intent.setClass(StokOpnameActivity.this, StokReadEPCActivity.class);
        startActivity(intent);
        StokOpnameActivity.this.finish();
    }
    public void OpenHistoryActivity() {
        Intent intent = new Intent();
        intent.setClass(StokOpnameActivity.this, HistoryActivity.class);
        startActivity(intent);
        StokOpnameActivity.this.finish();
    }
    public void OpenDownloadActivity() {
        Intent intent = new Intent();
        intent.setClass(StokOpnameActivity.this, DownloadActivity.class);
        startActivity(intent);
        StokOpnameActivity.this.finish();
    }

    public void OpenMainActivity() {
        Intent intent = new Intent();
        intent.setClass(StokOpnameActivity.this, ItemMainActivity.class);
        startActivity(intent);
        StokOpnameActivity.this.finish();
    }

    public static void copyDatabase(Context c, String DATABASE_NAME) {
        String databasePath = c.getDatabasePath(DATABASE_NAME).getPath();
        File f = new File(databasePath);
        OutputStream myOutput = null;
        InputStream myInput = null;
        Log.d("testing", " testing db path " + databasePath);
        Log.d("testing", " testing db exist " + f.exists());

        if (f.exists()) {
            try {

                File directory = new File("/mnt/sdcard/DB_DEBUG");
                if (!directory.exists())
                    directory.mkdir();

                myOutput = new FileOutputStream(directory.getAbsolutePath()
                        + "/" + DATABASE_NAME);
                myInput = new FileInputStream(databasePath);

                byte[] buffer = new byte[1024];
                int length;
                while ((length = myInput.read(buffer)) > 0) {
                    myOutput.write(buffer, 0, length);
                }

                myOutput.flush();
            } catch (Exception e) {
            } finally {
                try {
                    if (myOutput != null) {
                        myOutput.close();
                        myOutput = null;
                    }
                    if (myInput != null) {
                        myInput.close();
                        myInput = null;
                    }
                } catch (Exception e) {
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        OpenMainActivity();
    }
}
