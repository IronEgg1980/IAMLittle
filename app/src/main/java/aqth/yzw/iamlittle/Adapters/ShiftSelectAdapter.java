package aqth.yzw.iamlittle.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import aqth.yzw.iamlittle.EntityClass.ItemEntity;
import aqth.yzw.iamlittle.EntityClass.ItemEntitySelectShift;
import aqth.yzw.iamlittle.IItemClickListener;
import aqth.yzw.iamlittle.ItemType;
import aqth.yzw.iamlittle.R;

public class ShiftSelectAdapter extends RecyclerView.Adapter {
    protected class ViewHolder extends RecyclerView.ViewHolder{
        private TextView nameTV,unitAmountTV;
        private CheckBox checkBox;
        private LinearLayout root;
        public ViewHolder(View itemView) {
            super(itemView);
            root  = itemView.findViewById(R.id.select_shift_root);
            checkBox = itemView.findViewById(R.id.select_shift_checkbox);
            nameTV = itemView.findViewById(R.id.select_shift_name);
            unitAmountTV = itemView.findViewById(R.id.select_shift_unitamount);
        }
    }
    private List<ItemEntity> mList;
    private IItemClickListener itemClickListener;

    public void setItemClickListener(IItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        ItemEntity itemEntity = mList.get(position);
        if(itemEntity.getType() == ItemType.EMPTY){
            return 1;
        }else{
            return 2;
        }
    }

    public ShiftSelectAdapter(List<ItemEntity> list){
        mList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if(i == 1){
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.recyclerview_empty_layout,viewGroup,false);
            return new RecyclerviewEmptyViewHolder(view);
        }else{
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.select_shift_item,viewGroup,false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int i) {
        ItemEntity itemEntity = mList.get(i);
        if(itemEntity.getType() == ItemType.SHIFT){
            final ItemEntitySelectShift entityShift = (ItemEntitySelectShift)itemEntity;
            final ViewHolder holder = (ViewHolder)viewHolder;
            holder.checkBox.setChecked(entityShift.isSelect());
            holder.nameTV.setText(entityShift.getShiftName());
            holder.nameTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onClick(v,i);
                }
            });
            holder.unitAmountTV.setText(entityShift.getUnitAmount()+"");
            holder.unitAmountTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onClick(v,i);
                }
            });
            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    entityShift.setSelect(holder.checkBox.isChecked());
                    notifyItemChanged(i);
                }
            });
        }else{
            RecyclerviewEmptyViewHolder holder = (RecyclerviewEmptyViewHolder)viewHolder;
            holder.getTextView().setText("当前时间段没有排班信息");
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
