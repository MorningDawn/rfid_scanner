package com.clouiotech.pda.demo.uhf;

/**
 * Created by ASUS on 27/11/2015.
 */

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.clouiotech.pda.demoExample.R;

import java.util.ArrayList;
import java.util.List;

public class CustomList extends ArrayAdapter<String> {
    private final Activity context;
    List<String> KODE = new ArrayList<String>();
    List<String> JUMLAHDATA = new ArrayList<String>();
    List<String> JUMLAHCEK = new ArrayList<String>();
    List<String> WAREHOUSE = new ArrayList<String>();
    List<String> PERIOD = new ArrayList<String>();
    List<String> GROUP = new ArrayList<String>();
    List<String> TGL = new ArrayList<String>();
    List<String> DESKRIPSI = new ArrayList<String>();
    List<String> CATATAN = new ArrayList<String>();
    List<String> SELISIH = new ArrayList<String>();

    public CustomList(Activity context,
                      List<String> kode, List<String> jumlahdata, List<String> jumlahcek,
                      List<String> warehouse, List<String> period,
                      List<String> group, List<String> tgl,
                      List<String> deksripsi, List<String> catatan, List<String> selisih) {
        super(context, R.layout.epclist_item_new, kode);

        this.context = context;
        this.KODE = kode;
        this.JUMLAHDATA = jumlahdata;
        this.JUMLAHCEK = jumlahcek;
        this.WAREHOUSE = warehouse;
        this.PERIOD = period;
        this.GROUP = group;
        this.TGL = tgl;
        this.DESKRIPSI = deksripsi;
        this.CATATAN = catatan;
        this.SELISIH = selisih;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.epclist_item_new, null, true);
        TextView txtEpc = (TextView) rowView.findViewById(R.id.EPCList_TagID);
        TextView txtQty = (TextView) rowView.findViewById(R.id.EPCList_ReadQty);
        TextView txtFisik = (TextView) rowView.findViewById(R.id.EPCList_ReadCount);
        TextView txtSelisih = (TextView) rowView.findViewById(R.id.EPCList_ReadSelisih);

        txtEpc.setText(KODE.get(position).toString());
        txtQty.setText(JUMLAHDATA.get(position).toString());

//        txtFisik.setText(DESKRIPSI.get(position).toString());
        txtFisik.setText(JUMLAHCEK.get(position).toString());
//        txtSelisih.setText(CATATAN.get(position).toString());
        txtSelisih.setText(SELISIH.get(position).toString());
        txtSelisih.setTextColor(Color.WHITE);
        if((!SELISIH.get(position).trim().equals("-"))&&(!SELISIH.get(position).trim().equals("+"))) {
            if(Integer.valueOf(SELISIH.get(position))<0){
                txtSelisih.setTextColor(Color.RED);
            }else if(Integer.valueOf(SELISIH.get(position))==0){
                txtSelisih.setTextColor(Color.GREEN);
            }else if(Integer.valueOf(SELISIH.get(position))>0) {
                txtSelisih.setTextColor(Color.BLUE);
            }
        }else{
            txtSelisih.setTextColor(Color.WHITE);
        }
        return rowView;
    }



}