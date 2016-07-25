package com.clouiotech.pda.demo.uhf;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.clouiotech.pda.demo.Sqlite.DataAsli;
import com.clouiotech.pda.demo.Sqlite.DataOrder;
import com.clouiotech.pda.demo.Sqlite.DataSetting;
import com.clouiotech.pda.demo.Sqlite.MyDBHandler;
import com.clouiotech.pda.demoExample.R;
import com.clouiotech.pda.demo.BaseActivity;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.List;

/**
 * @author RFID_lx
 */
public class DownloadActivity extends BaseActivity {
    //	private static String NAMESPACE = "http://192.168.43.187/service_stok/";
//    private static String URL = "http://192.168.43.187/service_stok/service_stok.php";
    private static String NAMESPACE;// = "http://192.168.1.179/service_stok/";
    private static String URL;// = "http://192.168.1.179/service_stok/service_stok.php";
    //	private static String NAMESPACE = "http://192.168.1.179/service_meter/";
//	private static String URL = "http://192.168.1.179/service_meter/service_meter.php";
    String METHOD_NAME;
    String SOAP_ACTION;
    SoapObject request;
    EditText namaDatabase, namaUser, password, queryManual, namaSetting, namaServer, namaTable;
    Spinner filter;
    Button btnDownload, btnSimpan, btnHapus, btnUpdate;
    List<String> listnamaDatabase, listnamaUser, listpassword, listqueryManual, listnamaSetting, listnamaServer, listIdSetting;
    Spinner spinDaftarSetting;
    String idSettingPilih = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_download);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        namaSetting = (EditText)findViewById(R.id.edtNamaSetting);
        namaServer = (EditText)findViewById(R.id.edtServerDatabase);
        namaDatabase = (EditText)findViewById(R.id.edtNamaDatabase);
        namaUser = (EditText)findViewById(R.id.edtUser);
        password = (EditText)findViewById(R.id.edtPassword);
        queryManual = (EditText)findViewById(R.id.edtQuery);
        namaTable = (EditText)findViewById(R.id.edtTabel);
        filter = (Spinner)findViewById(R.id.spinFilter);
        btnDownload = (Button)findViewById(R.id.btnDownloadData);
        btnSimpan = (Button)findViewById(R.id.btnNewSetting);
        btnUpdate = (Button)findViewById(R.id.btnUpdateSetting);
        btnHapus = (Button)findViewById(R.id.btnDeleteSetting);
        spinDaftarSetting = (Spinner)findViewById(R.id.spinFilter);
        daftarSetting();
        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                downloadData();
            }
        });
        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simpanSetting();
            }
        });
        spinDaftarSetting.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(listnamaSetting.size()>0){
                    namaSetting.setText(listnamaSetting.get(position));
                    namaServer.setText(listnamaServer.get(position));
                    namaDatabase.setText(listnamaDatabase.get(position));
                    namaUser.setText(listnamaUser.get(position));
                    password.setText(listpassword.get(position));
                    queryManual.setText(listqueryManual.get(position));
                    idSettingPilih = listIdSetting.get(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnHapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(),idSettingPilih,Toast.LENGTH_LONG).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(DownloadActivity.this);
                builder.setMessage("Anda yakin akan menghapus data?")
                        .setCancelable(false)
                        .setPositiveButton("Ya",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dg,
                                                        int id) {
                                        hapusSetting();
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

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                updateSetting();
            }
        });
    }
    public void daftarSetting(){
        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
        listnamaSetting = dbHandler.getAllSetting("filter");
        listnamaServer = dbHandler.getAllSetting("server");
        listnamaDatabase = dbHandler.getAllSetting("database");
        listnamaUser = dbHandler.getAllSetting("user");
        listpassword = dbHandler.getAllSetting("password");
        listqueryManual = dbHandler.getAllSetting("query");
        listIdSetting = dbHandler.getAllSetting("rec_id");
        if(listnamaSetting.size()>0){
            ArrayAdapter<String> settingSpinAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, listnamaSetting);
//            ArrayAdapter<String> settingSpinAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listnamaSetting);
            settingSpinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinDaftarSetting.setAdapter(settingSpinAdapter);
//            btnUpdate.setEnabled(true);
        }else{
//            MyDBHandler dbHandler = new MyDBHandler(DownloadActivity.this, null, null, 1);
//            Toast.makeText(getApplicationContext(),"TIdak ada setting tersimpan!",Toast.LENGTH_SHORT).show();
//            btnUpdate.setEnabled(false);
            DataSetting data = new DataSetting(
                    "http://192.168.1.179/service_stok/",
                    "stokopname",
                    "root",
                    "",
                    "Filter Gudang",
                    "SELECT table_item.ITEM_CODE,table_item_balance.QTY_AWAL," +
                            "table_item_balance.WAREHOUSE_ID,table_item_balance.PERIOD_NAME," +
                            "table_item.GROUP_ID,table_item.ITEM_DESCRIPTION FROM table_item,table_item_balance " +
                            "WHERE table_item.ITEM_ID = table_item_balance.ITEM_ID " +
                            "AND table_item_balance.WAREHOUSE_ID = 1",
                    "ITEM_CODE,QTY_AWAL,WAREHOUSE_ID,PERIOD_NAME,GROUP_ID,ITEM_DESCRIPTION");
            dbHandler.addDataSetting(data);
            data = new DataSetting(
                    "http://192.168.1.179/service_stok/",
                    "stokopname",
                    "root",
                    "",
                    "Filter Period",
                    "SELECT table_item.ITEM_CODE,table_item_balance.QTY_AWAL," +
                            "table_item_balance.WAREHOUSE_ID,table_item_balance.PERIOD_NAME," +
                            "table_item.GROUP_ID,table_item.ITEM_DESCRIPTION FROM table_item,table_item_balance " +
                            "WHERE table_item.ITEM_ID = table_item_balance.ITEM_ID " +
                            "AND table_item_balance.PERIOD_NAME = 201601",
                    "ITEM_CODE,QTY_AWAL,WAREHOUSE_ID,PERIOD_NAME,GROUP_ID,ITEM_DESCRIPTION");
            dbHandler.addDataSetting(data);
            data = new DataSetting(
                    "http://192.168.1.179/service_stok/",
                    "stokopname",
                    "root",
                    "",
                    "Filter Period dan Gudang",
                    "SELECT table_item.ITEM_CODE,table_item_balance.QTY_AWAL," +
                            "table_item_balance.WAREHOUSE_ID,table_item_balance.PERIOD_NAME," +
                            "table_item.GROUP_ID,table_item.ITEM_DESCRIPTION FROM table_item,table_item_balance " +
                            "WHERE table_item.ITEM_ID = table_item_balance.ITEM_ID " +
                            "AND table_item_balance.WAREHOUSE_ID = 1 AND table_item_balance.PERIOD_NAME = 201601",
                    "ITEM_CODE,QTY_AWAL,WAREHOUSE_ID,PERIOD_NAME,GROUP_ID,ITEM_DESCRIPTION");
            dbHandler.addDataSetting(data);
            daftarSetting();
        }
    }

    public void simpanSetting(){
        if((!namaSetting.getText().toString().equals(""))&&(!namaServer.getText().toString().equals(""))&&
                (!namaDatabase.getText().toString().equals(""))&&(!namaUser.getText().toString().equals(""))
            &&(!queryManual.getText().toString().equals(""))&&(!namaTable.getText().toString().equals(""))){
            MyDBHandler dbHandler = new MyDBHandler(DownloadActivity.this, null, null, 1);
            //String _SERVER, String _DATABASE, String _USER, String _PASSWORD, String _FILTER, String _QUERY
            String pass = "";
            if(password.getText().length()==0){
                pass = "";
            }else{
                pass = password.getText().toString();
            }
            DataSetting data = new DataSetting(namaServer.getText().toString(),namaDatabase.getText().toString(),
                    namaUser.getText().toString(),pass,namaSetting.getText().toString(),
                    queryManual.getText().toString(),namaTable.getText().toString());

            Cursor checkSetting = dbHandler.selectNameSetting(namaSetting.getText().toString());
            if(checkSetting.getCount()>0)
            {
                dbHandler.updateDataSetting(data,idSettingPilih);
                Toast.makeText(getApplicationContext(), "Data berhasil diupdate!", Toast.LENGTH_LONG).show();
            }else{
                dbHandler.addDataSetting(data);
//                daftarSetting();
                Toast.makeText(getApplicationContext(), "Data berhasil disimpan!", Toast.LENGTH_LONG).show();
            }

        }else{
            Toast.makeText(getApplicationContext(), "Data Harus Diisi Semua!", Toast.LENGTH_LONG).show();
        }
    }

    public void updateSetting(){
        if((!namaSetting.getText().toString().equals(""))&&(!namaServer.getText().toString().equals(""))&&
                (!namaDatabase.getText().toString().equals(""))&&(!namaUser.getText().toString().equals(""))
                &&(!queryManual.getText().toString().equals(""))){
            MyDBHandler dbHandler = new MyDBHandler(DownloadActivity.this, null, null, 1);
            String pass = "";
            if(password.getText().length()==0){
                pass = "";
            }else{
                pass = password.getText().toString();
            }
            DataSetting data = new DataSetting(namaServer.getText().toString(),namaDatabase.getText().toString(),
                    namaUser.getText().toString(),pass,namaSetting.getText().toString(),
                    queryManual.getText().toString(),namaTable.getText().toString());
            dbHandler.updateDataSetting(data,idSettingPilih);
            Toast.makeText(getApplicationContext(), "Data berhasil diupdate!", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getApplicationContext(), "Data Harus Diisi Semua!", Toast.LENGTH_LONG).show();
        }
    }

    public void hapusSetting(){
        MyDBHandler dbHandler = new MyDBHandler(DownloadActivity.this, null, null, 1);
        dbHandler.deleteSetting(idSettingPilih);
        Toast.makeText(getApplicationContext(), "Data berhasil di hapus!", Toast.LENGTH_LONG).show();
        dbHandler.close();
        daftarSetting();
    }

    public void downloadData(){
        METHOD_NAME = "ListProduk";
        NAMESPACE = namaServer.getText().toString();
        URL = NAMESPACE+"service_stok.php";
        SOAP_ACTION =NAMESPACE+METHOD_NAME;
        request = new SoapObject(NAMESPACE, METHOD_NAME);
        request.addProperty("DATABASE", "");
        request.addProperty("USER", "");
        request.addProperty("PASS", "");
        request.addProperty("FILTER", "");
        request.addProperty("HASIL", "");
        request.setProperty(0, namaDatabase.getText().toString());
        request.setProperty(1, namaUser.getText().toString());
        request.setProperty(2, password.getText().toString());
        request.setProperty(3, queryManual.getText().toString());
        request.setProperty(4, namaTable.getText().toString());
        findViewById(R.id.pB).setVisibility(View.VISIBLE);
        new fetchData().execute();
    }

    class fetchData extends AsyncTask<String, Void, String>
    {
        String response="";
        @Override
        protected String doInBackground(String... params) {
            final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;
            try {
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL,20000);
                androidHttpTransport.call(SOAP_ACTION, envelope);
                SoapPrimitive result = (SoapPrimitive) envelope.getResponse();
                response = result.toString();
            }
            catch (Exception e)
            {
                response = "Koneksi gagal, mohon periksa settingan yang anda gunakan!";
            }
            Log.i("MenuDownload", "Hasil String: " + response);
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
//                Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();
            if((!result.equals("Koneksi gagal, mohon periksa settingan yang anda gunakan!"))||(result.equals(""))||(!result.equals("Koneksi Gagal"))
                    ||(!result.equals("Data harus lengkap!"))){
                String[] pesan = result.split("JUMLAH");
                if(pesan.length>1){
                    String[] listorder = pesan[1].split("#");
                    MyDBHandler dbHandler = new MyDBHandler(DownloadActivity.this, null, null, 1);
                    dbHandler.deleteAll("data_order");
                    dbHandler.deleteAll("data_asli");
                    dbHandler.deleteAll("temp_code");
                    for(int i=0;i<listorder.length;i++){
                        String[] a = listorder[i].split("\\*");
//                        if(Integer.valueOf(a[1])<0){
//                            a[1] = "0";
//                        }
                        double d = Double.valueOf(a[1]);
                        int aa = (int)d;
                        int selisih = 0;
                        if(aa<0){
                            selisih = 0;
                        }else{
                            selisih = 0 - aa;
                        }

//                        DataOrder data = new DataOrder(a[0],a[1],"0",a[2],a[3],"0","","","","0");

                        DataOrder data = new DataOrder(a[0], String.valueOf(aa),"0",a[2],a[3],a[4],"",a[5],"", String.valueOf(selisih));
                        DataAsli dataAsli = new DataAsli(a[0], String.valueOf(aa),"0",a[2],a[3],a[4],"",a[5],"", String.valueOf(selisih));
//                        DataAsli dataAsli = new DataAsli(a[0],a[1],"0",a[2],a[3],"0","","","","0");
                        dbHandler.addDataOrder(data);
                        dbHandler.addDataAsli(dataAsli);
                    }
                    Toast.makeText(getApplicationContext(), "Download data berhasil!", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(), "Koneksi gagal, mohon periksa settingan yang anda gunakan!", Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            }
            findViewById(R.id.pB).setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        // TODO
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        // TODO
        super.onPause();
    }

    @Override
    protected void onResume() {
        // TODO
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setClass(DownloadActivity.this, StokOpnameActivity.class);
        startActivity(intent);
        DownloadActivity.this.finish();
    }

}
