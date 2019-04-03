package aqth.yzw.iamlittle.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import aqth.yzw.iamlittle.Arith;
import aqth.yzw.iamlittle.EntityClass.ItemEntityJXGZPersonDetailsTemp;
import aqth.yzw.iamlittle.MyTool;
import aqth.yzw.iamlittle.R;

public class JXGZDeduceRecyclerViewAdapter extends RecyclerView.Adapter {
    protected class DeduceItemViewHolder extends RecyclerView.ViewHolder{
        private LinearLayout root;
        private TextView itemNameTV,itemAmountTV;
        private CheckBox checkBox;
        public DeduceItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemNameTV = itemView.findViewById(R.id.select_shift_name);
            itemAmountTV = itemView.findViewById(R.id.select_shift_unitamount);
            checkBox = itemView.findViewById(R.id.select_shift_checkbox);
            root = itemView.findViewById(R.id.select_shift_root);
        }
    }
    protected class SelectOthersViewHolder extends RecyclerView.ViewHolder{
        private TextView personName,personRatio;
        private LinearLayout root;
        private CheckBox checkBox;
        public SelectOthersViewHolder(@NonNull View itemView) {
            super(itemView);
            personName = itemView.findViewById(R.id.select_person_name);
            personRatio = itemView.findViewById(R.id.select_person_ratio);
            checkBox = itemView.findViewById(R.id.select_person_checkbox);
            root = itemView.findViewById(R.id.select_person_root);
        }
    }
    private List<ItemEntityJXGZPersonDetailsTemp> mList;
    private int mode;
    public JXGZDeduceRecyclerViewAdapter(List<ItemEntityJXGZPersonDetailsTemp> list,int mode){
        this.mode = mode;
        this.mList = list;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(mode == MyTool.DEDUCE_ITEM_MODE){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.select_shift_item,viewGroup,false);
            return new DeduceItemViewHolder(view);
        }else if(mode == MyTool.SELECT_OTHERSPERSON_MODE){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.select_person_item,viewGroup,false);
            return new SelectOthersViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        final ItemEntityJXGZPersonDetailsTemp temp = mList.get(i);
        if(viewHolder !=null) {
            if (mode == MyTool.DEDUCE_ITEM_MODE){
                DeduceItemViewHolder holder = (DeduceItemViewHolder)viewHolder;
                int scale = temp.getJXGZPersonDetails().getScale();
                holder.itemNameTV.setText(temp.getJXGZPersonDetails().getJXGZName());
                holder.itemAmountTV.setText(Arith.doubleToString(temp.getJXGZPersonDetails().getJXGZAmount(),scale));
                holder.checkBox.setChecked(temp.isSelect());
                holder.checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        temp.setSelect(!temp.isSelect());
                        notifyItemChanged(i);
                    }
                });
                holder.root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        temp.setSelect(!temp.isSelect());
                        notifyItemChanged(i);
                    }
                });
            }else{
                SelectOthersViewHolder holder = (SelectOthersViewHolder) viewHolder;
                holder.personName.setText(temp.getJXGZPersonDetails().getPersonName());
                holder.personRatio.setText(Double.toString(temp.getJXGZPersonDetails().getThatRatio()));
                holder.checkBox.setChecked(temp.isSelect());
                holder.checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        temp.setSelect(!temp.isSelect());
                        notifyItemChanged(i);
                    }
                });
                holder.root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        temp.setSelect(!temp.isSelect());
                        notifyItemChanged(i);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
