package com.clouiotech.pda.demo.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.clouiotech.pda.demo.BaseObject.EpcObject;
import com.clouiotech.pda.demoExample.R;
import com.clouiotech.pda.rfid.EPCModel;

import java.util.List;

/**
 * Created by roka on 29/07/16.
 */
public class EpcScanAdapter extends RecyclerView.Adapter<EpcScanAdapter.ViewHolderEpc> implements View.OnClickListener {
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
        EpcObject epc = mListData.get(position);

        holder.tvEpcId.setText(epc.getId());
        holder.tvEpcDesc.setText(epc.getDesc());
        holder.tvEpcQuantity.setText("" + epc.getQuantity());
        holder.tvEpcDelta.setText("" + epc.getDelta());
        holder.tvEpcPhysic.setText("" + epc.getPhysic());
        holder.rlCard.setOnClickListener(this);

        holder.rlCard.setTag(position);
    }

    @Override
    public int getItemCount() {
        return mListData.size();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_card :
                int pos = (int) view.getTag();
                Toast.makeText(mContext, "Item " + pos, Toast.LENGTH_SHORT).show();
        }
    }

    public class ViewHolderEpc extends RecyclerView.ViewHolder {
        public RelativeLayout rlCard;
        public TextView tvEpcId, tvEpcDesc, tvEpcQuantity, tvEpcPhysic, tvEpcDelta;

        public ViewHolderEpc(View v) {
            super(v);
            rlCard = (RelativeLayout) v.findViewById(R.id.ll_card);
            tvEpcId = (TextView) v.findViewById(R.id.tv_epc_id);
            tvEpcDesc = (TextView) v.findViewById(R.id.tv_epc_desc);
            tvEpcQuantity = (TextView) v.findViewById(R.id.tv_quantity);
            tvEpcPhysic = (TextView) v.findViewById(R.id.tv_physic);
            tvEpcDelta = (TextView) v.findViewById(R.id.tv_delta);
        }
    }

}
