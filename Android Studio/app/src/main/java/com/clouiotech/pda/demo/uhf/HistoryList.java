package com.clouiotech.pda.demo.uhf;

/**
 * Created by ASUS on 27/11/2015.
 */

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.clouiotech.pda.demoExample.R;

import java.util.ArrayList;
import java.util.List;

public class HistoryList extends ArrayAdapter<String> {
    private final Activity context;
    List<String> KODE = new ArrayList<String>();
    List<String> TANGGAL = new ArrayList<String>();

    public HistoryList(Activity context,
                       List<String> kode, List<String> tanggal) {
        super(context, R.layout.history_list, kode);

        this.context = context;
        this.KODE = kode;
        this.TANGGAL = tanggal;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.history_list, null, true);
        TextView txtTgl = (TextView) rowView.findViewById(R.id.tvTanggalHistory);
        TextView txtKode = (TextView) rowView.findViewById(R.id.tvKodeHistory);
        txtKode.setText("Kode Report : "+KODE.get(position));
        txtTgl.setText(TANGGAL.get(position));
        return rowView;
    }



}