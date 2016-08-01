package com.clouiotech.pda.demo.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.clouiotech.pda.demo.BaseObject.EpcObject;
import com.clouiotech.pda.demoExample.R;
import com.clouiotech.pda.rfid.EPCModel;

import org.w3c.dom.Text;

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

        setDeltaStatus(epc.getStatus(), holder);

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

    private void setDeltaStatus(int delta, ViewHolderEpc vh) {
        int color = mContext.getResources().getColor(R.color.text_primary);
        switch (delta) {
            case EpcObject.EPC_STATUS_LESS :
                color = mContext.getResources().getColor(R.color.alert_less);
                break;

            case EpcObject.EPC_STATUS_NORMAL :
                color = mContext.getResources().getColor(R.color.alert_normal);
                break;

            case EpcObject.EPC_STATUS_OVER :
                color = mContext.getResources().getColor(R.color.alert_more);
                break;
        }

        vh.tvEpcQuantity.setTextColor(color);
        vh.tvEpcPhysic.setTextColor(color);
        vh.tvEpcDelta.setTextColor(color);
        vh.tvEpcDesc.setTextColor(color);
        vh.tvEpcId.setTextColor(color);
    }

    public class ViewHolderEpc extends RecyclerView.ViewHolder {
        public RelativeLayout rlCard;
        public TextView tvEpcId, tvEpcDesc, tvEpcQuantity, tvEpcPhysic, tvEpcDelta;
        public ImageView ivEpcDelta;


        public ViewHolderEpc(View v) {
            super(v);
            rlCard = (RelativeLayout) v.findViewById(R.id.ll_card);
            ivEpcDelta = (ImageView) v.findViewById(R.id.iv_delta);
            tvEpcId = (TextView) v.findViewById(R.id.tv_epc_id);
            tvEpcDesc = (TextView) v.findViewById(R.id.tv_epc_desc);
            tvEpcQuantity = (TextView) v.findViewById(R.id.tv_quantity);
            tvEpcPhysic = (TextView) v.findViewById(R.id.tv_physic);
            tvEpcDelta = (TextView) v.findViewById(R.id.tv_delta);
        }
    }

}
