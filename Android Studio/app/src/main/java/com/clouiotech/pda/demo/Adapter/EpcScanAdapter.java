package com.clouiotech.pda.demo.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.clouiotech.pda.demo.BaseObject.EpcObject;
import com.clouiotech.pda.demoExample.R;
import com.clouiotech.pda.rfid.EPCModel;

import java.util.List;

/**
 * Created by roka on 29/07/16.
 */
public class EpcScanAdapter extends RecyclerView.Adapter<EpcScanAdapter.ViewHolderEpc> {
    private List<EpcObject> mListData;
    private Context mContext;

    public EpcScanAdapter(List<EpcObject> listData, Context context) {
        this.mListData = listData;
        this.mContext = context;
    }

    @Override
    public ViewHolderEpc onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_epc_scan, null);

        return new ViewHolderEpc(view);
    }

    @Override
    public void onBindViewHolder(ViewHolderEpc holder, int position) {
        EpcObject number = mListData.get(position);
    }

    @Override
    public int getItemCount() {
        return mListData.size();
    }

    public class ViewHolderEpc extends RecyclerView.ViewHolder {
        public ViewHolderEpc(View v) {
            super(v);
            TextView tvEpcId = (TextView) v.findViewById(R.id.tv_epc_id);
            TextView tvEpcDesc = (TextView) v.findViewById(R.id.tv_epc_desc);
            TextView tvEpcQuantity = (TextView) v.findViewById(R.id.tv_quantity);
            TextView tvEpcPhysic = (TextView) v.findViewById(R.id.tv_physic);
            TextView tvEpcDelta = (TextView) v.findViewById(R.id.tv_delta);
        }
    }

}
