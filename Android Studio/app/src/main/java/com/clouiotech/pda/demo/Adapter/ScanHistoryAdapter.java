package com.clouiotech.pda.demo.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.clouiotech.pda.demo.BaseObject.ScanHistoryObject;
import com.clouiotech.pda.demoExample.R;

import java.util.List;

/**
 * Created by roka on 30/07/16.
 */
public class ScanHistoryAdapter extends RecyclerView.Adapter<ScanHistoryAdapter.ViewHolderScanHistory>
    implements View.OnClickListener{
    private Context mContext;
    private List<ScanHistoryObject> mListData;

    public ScanHistoryAdapter(Context context, List<ScanHistoryObject> listData) {
        this.mContext = context;
        this.mListData = listData;
    }

    @Override
    public ViewHolderScanHistory onCreateViewHolder(ViewGroup parent, int viewType) {
        View v  = LayoutInflater.from(mContext).inflate(R.layout.item_scan_history, null);

        return new ViewHolderScanHistory(v);
    }

    @Override
    public void onBindViewHolder(ViewHolderScanHistory holder, int position) {
        ScanHistoryObject scanHistoryObject = mListData.get(position);

        holder.tvScanNumber.setText("" + scanHistoryObject.getHistoryNumber());
        holder.tvScanCode.setText("" + scanHistoryObject.getHistoryCode());
        holder.tvScanDate.setText("" + scanHistoryObject.getHistoryDate());

        holder.llCard.setOnClickListener(this);
        holder.llCard.setTag(position);
    }

    @Override
    public int getItemCount() {
        return mListData.size();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_card : {
                int position = (int) view.getTag();
                Toast.makeText(mContext, "item " + position, Toast.LENGTH_SHORT).show();

                // intent to detail here
            }
        }
    }


    public class ViewHolderScanHistory extends RecyclerView.ViewHolder {
        public LinearLayout llCard;
        public TextView tvScanNumber, tvScanCode, tvScanDate;

        public ViewHolderScanHistory(View v) {
            super(v);
            llCard = (LinearLayout) v.findViewById(R.id.ll_card);
            tvScanNumber = (TextView) v.findViewById(R.id.tv_scan_number);
            tvScanCode = (TextView) v.findViewById(R.id.tv_scan_code);
            tvScanDate = (TextView) v.findViewById(R.id.tv_scan_date);
        }
    }

}
