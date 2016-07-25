package com.clouiotech.pda.demo.uhf;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.clouiotech.pda.demo.Sqlite.DataOrder;
import com.clouiotech.pda.demo.Sqlite.DataTemp;
import com.clouiotech.pda.demo.Sqlite.MyDBHandler;
import com.clouiotech.pda.demoExample.R;
import com.clouiotech.pda.demo.BaseActivity;

import java.util.List;

/**
 * @author RFID_lx
 * ????????
 */
public class DetailHistoryActivity extends BaseActivity {

    List<String> listRECID;
    List<String> listKODE;
    List<String> listJUMLAHDATA;
    List<String> listJUMLAHCEK;
    List<String> listWAREHOUSE;
    List<String> listPERIOD;
    List<String> listGROUP;
    List<String> listTGL;
    List<String> listDESKRIPSI;
    List<String> listCATATAN;
    List<String> listSELISIH;
    TextView tvKode, tvQty, tvFisik, tvSelisih, tvTanggal;
    ImageButton btnCari;
    EditText txtCari;
    String sortKode = "ASC", sortQty = "ASC", sortFisik = "ASC",sortSelish = "ASC";
//    String dataSort = " order by kode_barang ASC";
    String dataSort = " order by warehouse DESC,kode_barang ASC";
    String dataCari = "";
    Dialog dialogDeskripsi;
    Button closeYa, closeCancel, btnExport;
    EditText edtDeskripsi, edtCatan;
    TextView tvTitleDialog, tvInformasi;
    MyDBHandler dbHandler;
    final Context context = this;
    private ListView listView = null ;
    String kodeHistory, tanggal;



    public void dataList() {
        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
        listKODE = dbHandler.getAllReportDetail("kode_barang",dataSort,dataCari,kodeHistory);
        listJUMLAHDATA = dbHandler.getAllReportDetail("jumlah_data",dataSort,dataCari,kodeHistory);
        listJUMLAHCEK = dbHandler.getAllReportDetail("jumlah_cek",dataSort,dataCari,kodeHistory);
        listWAREHOUSE = dbHandler.getAllReportDetail("warehouse",dataSort,dataCari,kodeHistory);
        listPERIOD = dbHandler.getAllReportDetail("period",dataSort,dataCari,kodeHistory);
        listGROUP = dbHandler.getAllReportDetail("grup",dataSort,dataCari,kodeHistory);
        listTGL = dbHandler.getAllReportDetail("tgl_cek",dataSort,dataCari,kodeHistory);
        listDESKRIPSI = dbHandler.getAllReportDetail("deskripsi",dataSort,dataCari,kodeHistory);
        listCATATAN = dbHandler.getAllReportDetail("catatan",dataSort,dataCari,kodeHistory);
        listSELISIH = dbHandler.getAllReportDetail("selisih",dataSort,dataCari,kodeHistory);
        listRECID = dbHandler.getAllReportDetail("rec_id",dataSort,dataCari,kodeHistory);
//        updateJumlah("ABC12301");
//        Toast.makeText(getApplicationContext(),listKODE.get(0),Toast.LENGTH_LONG).show();
        CustomList adapter = new
                CustomList(DetailHistoryActivity.this,
                listKODE, listJUMLAHDATA, listJUMLAHCEK, listWAREHOUSE, listPERIOD, listGROUP, listTGL, listDESKRIPSI,
                listCATATAN, listSELISIH);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                dialogDescrition(position);
//                Toast.makeText(getApplicationContext(), "You Clicked at code datalist" + listKODE.get(position), Toast.LENGTH_SHORT).show();
            }
        });

        tvInformasi.setText(dbHandler.informasiUpdate());
        dbHandler.close();
    }


    public void Back(View v){
        Intent intent = new Intent();
        intent.setClass(DetailHistoryActivity.this, StokOpnameActivity.class);
        startActivity(intent);
        DetailHistoryActivity.this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO ????
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        this.setContentView(R.layout.activity_detail_history);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        Bundle extras = getIntent().getExtras();
        kodeHistory = "";
        tanggal = "";
        if(extras!=null){
            kodeHistory = extras.getString("kodeHistory");
            tanggal = extras.getString("tanggal");
        }
        tvKode = (TextView)findViewById(R.id.tv_TitleTagID);
        tvQty = (TextView)findViewById(R.id.tv_TitleTagQty);
        tvFisik = (TextView)findViewById(R.id.tv_TitleTagFisik);
        tvSelisih = (TextView)findViewById(R.id.tv_TitleTagSelisih);
        btnCari = (ImageButton)findViewById(R.id.btnSearch);
        txtCari = (EditText) findViewById(R.id.edtSearch);
        tvInformasi = (TextView) findViewById(R.id.lb_Informasi);
        listView = (ListView) this.findViewById(R.id.lv_Main);
        tvTanggal = (TextView)findViewById(R.id.lb_DetailHistory);
        btnExport = (Button) findViewById(R.id.btnExport);
        tvTanggal.setText(tanggal.toString());
        listView = (ListView) this.findViewById(R.id.lv_Main);
        dbHandler = new MyDBHandler(DetailHistoryActivity.this, null, null, 1);
        tvKode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sortKode.equals("ASC")){
                    sortKode = "DESC";
                }else{
                    sortKode = "ASC";
                }
                dataSort = " order by kode_barang " + sortKode;
                dataList();
            }
        });
        tvQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sortQty.equals("ASC")){
                    sortQty= "DESC";
                }else{
                    sortQty= "ASC";
                }
                dataSort = " order by CAST(jumlah_data AS INTEGER) " + sortQty;
                dataList();
            }
        });
        tvFisik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sortFisik.equals("ASC")){
                    sortFisik = "DESC";
                }else{
                    sortFisik = "ASC";
                }
                dataSort = " order by CAST(jumlah_cek AS INTEGER) " + sortFisik;
                dataList();
            }
        });
        tvSelisih.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sortSelish.equals("ASC")){
                    sortSelish = "DESC";
                }else{
                    sortSelish = "ASC";
                }
                dataSort = " order by CAST(selisih AS INTEGER) " + sortSelish;
                dataList();
            }
        });

        btnCari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String key;
//                key = txtCari.getText().toString();
//                dataCari = key;
//                dataList();
                String key;
                txtCari.setText("");
                key = txtCari.getText().toString();
                dataCari = key;
                dataList();
            }
        });

        btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportDbToCsv();
            }
        });

        txtCari.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String key;
                key = txtCari.getText().toString();
                dataCari = key;
                dataList();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        dataList();
        // Init();
    }

    public void exportDbToCsv(){
        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
        String dt = tanggal;
        dt = dt.trim().replace(" ","_");
        dt = dt.replace("-","");
        dt = dt.replace(":","");
//        Toast.makeText(getApplicationContext(),dt+"_"+"KODE_"+kodeHistory,Toast.LENGTH_SHORT).show();
        if(dbHandler.exportToCsv(dt+"_"+"KODE_"+kodeHistory,kodeHistory)){
            Toast.makeText(getApplicationContext(), "Export history success!", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getApplicationContext(), "Export history failed!", Toast.LENGTH_LONG).show();
        }
        dbHandler.close();
    }

    public void dialogDescrition(final int pos){
        //Dialog exit
        dialogDeskripsi = new Dialog(context);
        dialogDeskripsi.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialogDeskripsi.setTitle("Barcode "+kode);
        dialogDeskripsi.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialogDeskripsi.setContentView(R.layout.dialog_deksripsi);
        edtCatan = (EditText)dialogDeskripsi.findViewById(R.id.edtCatatan);
        edtDeskripsi = (EditText)dialogDeskripsi.findViewById(R.id.edtDeksripsi);
        closeYa = (Button) dialogDeskripsi.findViewById(R.id.btn_close_exit);
        closeCancel = (Button) dialogDeskripsi.findViewById(R.id.btn_cancel_exit);
        tvTitleDialog = (TextView)dialogDeskripsi.findViewById(R.id.tvTitle);
        tvTitleDialog.setText("Barcode "+listKODE.get(pos).toString());
        edtCatan.setText(listCATATAN.get(pos).toString());
        edtDeskripsi.setText(listDESKRIPSI.get(pos).toString());
        closeYa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                updateOrderDescription(Integer.valueOf(listRECID.get(pos)));
                dialogDeskripsi.dismiss();
                dataList();
//                Toast.makeText(getApplicationContext(),edtDeskripsi.getText().toString()+" "+edtCatan.getText().toString(),Toast.LENGTH_LONG).show();
            }
        });
        closeCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDeskripsi.dismiss();
            }
        });
        dialogDeskripsi.show();
    }

    public void updateOrderDescription(int pos){
        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
        DataOrder dataOrder = dbHandler.findDataOrder(String.valueOf(pos));
        if (dataOrder != null) {
            DataOrder data = new DataOrder(
                    dataOrder.getKODE(),
                    dataOrder.getJUMLAHDATA(),
                    dataOrder.getJUMLAHCEK(),
                    dataOrder.getWAREHOUSE(),
                    dataOrder.getPERIOD(),
                    dataOrder.getGROUP(),
                    dataOrder.getTGL(),
                    edtDeskripsi.getText().toString(),
                    edtCatan.getText().toString(),
                    dataOrder.getSELISIH());
            dbHandler.updateDataOrder(data, String.valueOf(pos),0);
            Toast.makeText(getApplicationContext(), "Data disimpan!", Toast.LENGTH_LONG).show();
        }else{

        }
    }




    public boolean cariData(String dataKode) {
        boolean result = false;
        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
        DataOrder dataUser =
                dbHandler.findKode(dataKode);
        if (dataUser != null) {
            result = true;
        } else {
            result = false;
        }
        dbHandler.close();
        return result;
    }

    public boolean cariDataTemp(String dataKode) {
        boolean result = false;
        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
        DataTemp dataUser =
                dbHandler.findKodeTemp(dataKode);
        if (dataUser != null) {
            result = true;
        } else {
            result = false;
        }
        dbHandler.close();
        return result;
    }


    public void OpenMainActivity() {
        Intent intent = new Intent();
        intent.setClass(DetailHistoryActivity.this, HistoryActivity.class);
        startActivity(intent);
        DetailHistoryActivity.this.finish();
    }

    @Override
    public void onBackPressed() {
        OpenMainActivity();
    }
}
