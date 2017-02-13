package com.clouiotech.pda.demo.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.clouiotech.pda.demo.Activity.RecyclerViewActivity;
import com.clouiotech.pda.demo.BaseObject.EpcObject;
import com.clouiotech.pda.demoExample.R;

import java.util.List;

/**
 * Created by roka on 29/07/16.
 */
public class EpcScanAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    private List<EpcObject> mListData;
    private Context mContext;

    private int VIEW_DUMMY = 0;
    private int VIEW_ITEM = 1;

    public EpcScanAdapter(List<EpcObject> listData, Context context) {
        this.mListData = listData;
        this.mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_epc_scan, null);
        return new ViewHolderEpc(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vh, int position) {
        ViewHolderEpc holder = (ViewHolderEpc) vh;

        EpcObject epc = getDataByPosition(position);

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
    public int getItemViewType(int position) {
        if (position == 0) return VIEW_DUMMY;
        else return VIEW_ITEM;
    }

    private EpcObject getDataByPosition(int position) {
        return mListData.get(position); // Already take 1 space for dummy item
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_card :
                int pos = (int) view.getTag();
                //((RecyclerViewActivity) mContext).saveEpcItemToDatabase(1, mListData.get(pos).getId());
                //Toast.makeText(mContext, "Add Item " + pos, Toast.LENGTH_SHORT).show();
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

    public class ViewHolderEpcDummy extends RecyclerView.ViewHolder {
        public ViewHolderEpcDummy(View v) {
            super(v);
        }
    }

}
