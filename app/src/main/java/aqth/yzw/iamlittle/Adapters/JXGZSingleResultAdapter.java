package aqth.yzw.iamlittle.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import aqth.yzw.iamlittle.Arith;
import aqth.yzw.iamlittle.EntityClass.JXGZSingleResultTemp;
import aqth.yzw.iamlittle.MyTool;
import aqth.yzw.iamlittle.R;
import aqth.yzw.iamlittle.SharedPreferencesHelper;

public class JXGZSingleResultAdapter extends RecyclerView.Adapter<JXGZSingleResultAdapter.ViewHolder> {
    private List<JXGZSingleResultTemp> mList;
    protected class ViewHolder extends RecyclerView.ViewHolder{
        private TextView nameTV,ratioTV,amountTV;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTV = itemView.findViewById(R.id.calprp_singleresult_item_name);
            ratioTV = itemView.findViewById(R.id.calprp_singleresult_item_ratio);
            amountTV = itemView.findViewById(R.id.calprp_singleresult_item_amount);
        }
    }
    public JXGZSingleResultAdapter(List<JXGZSingleResultTemp> list){
        this.mList = list;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.calprp_singleresult_item,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder( ViewHolder viewHolder, int i) {
        JXGZSingleResultTemp jxgzSingleResultTemp = mList.get(i);
        int scale = jxgzSingleResultTemp.getScale();
        viewHolder.nameTV.setText(jxgzSingleResultTemp.getPersonName());
        viewHolder.ratioTV.setText(Double.toString(jxgzSingleResultTemp.getRatio()));
        viewHolder.amountTV.setText(Double.toString(jxgzSingleResultTemp.getAmount()));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
