package aqth.yzw.iamlittle.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import aqth.yzw.iamlittle.EntityClass.JXGZSingleResultTemp;
import aqth.yzw.iamlittle.MyTool;
import aqth.yzw.iamlittle.R;

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
        viewHolder.nameTV.setText(jxgzSingleResultTemp.getPersonName());
        viewHolder.ratioTV.setText(jxgzSingleResultTemp.getRatio()+"");
        String s = MyTool.doubleToString(jxgzSingleResultTemp.getAmount(),2);
        viewHolder.amountTV.setText(s);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
