package com.clouiotech.pda.demo.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.clouiotech.pda.demo.BaseObject.CustomQueryResponse;
import com.clouiotech.pda.demo.interfaces.CustomQueryOnClickListener;
import com.clouiotech.pda.demoExample.R;

import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by roka on 17/03/17.
 */

public class CustomQueryDownloadAdapter extends RecyclerView.Adapter<CustomQueryDownloadAdapter.CustomQueryDownloadViewHolder>
    implements View.OnClickListener {
    private List<CustomQueryResponse> mData = null;
    private Context mContext = null;
    private CustomQueryOnClickListener mListener = null;

    public CustomQueryDownloadAdapter(Context context, List<CustomQueryResponse> data, CustomQueryOnClickListener listener) {
        this.mData = data;
        this.mContext = context;
        mListener = listener;
    }

    @Override
    public CustomQueryDownloadViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_download, parent, false);

        return new CustomQueryDownloadViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomQueryDownloadViewHolder holder, int position) {
        CustomQueryResponse data = mData.get(position);
        holder.tvQueryReal.setText(data.getQuery());
        holder.tvQueryAlias.setText(data.getQueryAlias());
        holder.rlContainer.setTag(data.getId());
        holder.rlContainer.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public void onClick(View view) {
        if(mListener != null) {
            int id = (int )view.getTag();
            mListener.onQueryClicked(id);
        }
    }

    public class CustomQueryDownloadViewHolder extends RecyclerView.ViewHolder {
        private TextView tvQueryAlias, tvQueryReal = null;
        private RelativeLayout rlContainer = null;
        public CustomQueryDownloadViewHolder(View view) {
            super(view);

            tvQueryAlias = (TextView) view.findViewById(R.id.tv_query_alias);
            tvQueryReal = (TextView) view.findViewById(R.id.tv_query_real);
            rlContainer = (RelativeLayout) view.findViewById(R.id.ll_card);
        }
    }
}
