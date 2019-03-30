package aqth.yzw.iamlittle.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import aqth.yzw.iamlittle.EntityClass.ItemEntityJXGZPersonTotalTemp;
import aqth.yzw.iamlittle.IItemClickListener;
import aqth.yzw.iamlittle.MyTool;
import aqth.yzw.iamlittle.R;

public class JXGZCheckoutAdapter extends RecyclerView.Adapter<JXGZCheckoutAdapter.ViewHolder> {
    private List<ItemEntityJXGZPersonTotalTemp> mList;
    public void setItemClickListener(IItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    private IItemClickListener itemClickListener;
    protected class ViewHolder extends RecyclerView.ViewHolder{
        private TextView nameTV,ratioTV,amountTV;
        private LinearLayout linearLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTV = itemView.findViewById(R.id.calprp_singleresult_item_name);
            ratioTV = itemView.findViewById(R.id.calprp_singleresult_item_ratio);
            amountTV = itemView.findViewById(R.id.calprp_singleresult_item_amount);
            linearLayout = itemView.findViewById(R.id.calprp_singleresult_item_root);
        }
    }
    public JXGZCheckoutAdapter(List<ItemEntityJXGZPersonTotalTemp> list){
        this.mList = list;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.calprp_singleresult_item,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        ItemEntityJXGZPersonTotalTemp temp = mList.get(i);
        viewHolder.nameTV.setText(temp.getPersonName());
        viewHolder.ratioTV.setText(temp.getThatRatio()+"");
        String s = MyTool.doubleToString(temp.getAmount(),2);
        viewHolder.amountTV.setText(s);
        viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onClick(v,i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
