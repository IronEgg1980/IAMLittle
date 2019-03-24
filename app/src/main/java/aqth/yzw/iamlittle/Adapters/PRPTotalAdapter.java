package aqth.yzw.iamlittle.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import aqth.yzw.iamlittle.EntityClass.ItemEntity;
import aqth.yzw.iamlittle.EntityClass.ItemEntityJXGZTotal;
import aqth.yzw.iamlittle.IItemClickListener;
import aqth.yzw.iamlittle.ItemType;
import aqth.yzw.iamlittle.R;

public class PRPTotalAdapter extends RecyclerView.Adapter {
    private List<ItemEntity> mList;
    private IItemClickListener itemClickListener;
    private SimpleDateFormat format1,format2;
    protected class ViewHolder extends RecyclerView.ViewHolder{
        private CardView root;
        private TextView dateTV,recordTimeTV,totalAmountTV,ratioAmountTV,averageAmountTV,deducTV;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.prptotal_item_root);
            dateTV = itemView.findViewById(R.id.prptotal_item_date);
            recordTimeTV =itemView.findViewById(R.id.prptotal_item_recordTime);
            totalAmountTV=itemView.findViewById(R.id.prptotal_item_totalAmount);
            ratioAmountTV = itemView.findViewById(R.id.prptotal_item_ratioAmount);
            averageAmountTV=itemView.findViewById(R.id.prptotal_item_averageAmount);
            deducTV=itemView.findViewById(R.id.prptotal_item_deduceAmount);
        }
    }
    public void setItemClickListener(IItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
    public PRPTotalAdapter(List<ItemEntity> list){
        this.mList = list;
        this.format1 = new SimpleDateFormat("yyyy年M月份");
        this.format2 = new SimpleDateFormat("yyyy年M月d日 HH:mm");
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(i == 0){
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.recyclerview_empty_layout,viewGroup,false);
           return new RecyclerviewEmptyViewHolder(view);
        }else{
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.prptotal_item,viewGroup,false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        ItemEntity itemEntity = mList.get(i);
        if(itemEntity.getType() == ItemType.EMPTY){
            RecyclerviewEmptyViewHolder holder = (RecyclerviewEmptyViewHolder)viewHolder;
            holder.getTextView().setText("没有绩效工资数据");
        }else{
            ViewHolder holder = (ViewHolder)viewHolder;
            final ItemEntityJXGZTotal total = (ItemEntityJXGZTotal)itemEntity;
            holder.dateTV.setText(format1.format(total.getDate()));
            holder.recordTimeTV.setText(format2.format(total.getRecordTime()));
            holder.totalAmountTV.setText(total.getTAString(2));
            holder.ratioAmountTV.setText(total.getRAString(2));
            holder.averageAmountTV.setText(total.getAAString(2));
            holder.deducTV.setText(total.getDAString(2));
            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setTag(total.getRecordTime());
                    itemClickListener.onClick(v,i);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(mList.get(position).getType() == ItemType.EMPTY){
            return 0;
        }else{
            return 1;
        }
    }
}
