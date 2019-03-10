package aqth.yzw.iamlittle.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import aqth.yzw.iamlittle.EntityClass.ItemEntity;
import aqth.yzw.iamlittle.EntityClass.ItemEntityScheduleInput;
import aqth.yzw.iamlittle.IItemClickListener;
import aqth.yzw.iamlittle.ItemType;
import aqth.yzw.iamlittle.R;

public class ScheduleShowAdapter extends RecyclerView.Adapter {
    private List<ItemEntity> mList;
    private IItemClickListener itemClickListener;
    private IItemClickListener itemLongClickListener;

    public void setItemLongClickListener(IItemClickListener itemLongClickListener) {
        this.itemLongClickListener = itemLongClickListener;
    }

    private boolean isInputMode;

    public void setItemClickListener(IItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public ScheduleShowAdapter(List<ItemEntity> list){
        mList = list;
        isInputMode = false;
    }
    public ScheduleShowAdapter(List<ItemEntity> list,boolean isInputMode){
        mList = list;
        isInputMode = true;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if(i == 1){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_empty_layout,viewGroup,false);
            return new RecyclerviewEmptyViewHolder(view);
        }else{
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.schedule_recyclerview_item,viewGroup,false);
            return new ScheduleItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int i) {
        ItemEntity itemEntity = mList.get(i);
        if(itemEntity.getType() == ItemType.EMPTY){
            RecyclerviewEmptyViewHolder holder = (RecyclerviewEmptyViewHolder)viewHolder;
            holder.getTextView().setText("没有本周排班数据");
        }else{
            ScheduleItemViewHolder holder = (ScheduleItemViewHolder)viewHolder;
            ItemEntityScheduleInput inputItem = (ItemEntityScheduleInput)itemEntity;
            for(int k = 0;k<9;k++){
                final int x = i;
                final int y = k;
                holder.getTVs()[k].setText(inputItem.getValues(k));
                if (isInputMode) {
                    holder.getTVs()[k].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setCurrentCell(x, y);
                            itemClickListener.onClick(v, x, y);
                        }
                    });
                    holder.getTVs()[k].setLongClickable(true);
                    holder.getTVs()[k].setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            setCurrentCell(x, y);
                            itemLongClickListener.onClick(v, x, y);
                            return true;
                        }
                    });
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        ItemEntity itemEntity = mList.get(position);
        if(itemEntity.getType() == ItemType.EMPTY)
            return 1;
        else
            return 2;
    }
    public void setCurrentCell(int x,int y){
       for(int i = 0;i<getItemCount();i++){
           ItemEntity itemEntity = mList.get(i);
           if(itemEntity.getType() == ItemType.SCHEDULE_WEEK_VIEW) {
               ItemEntityScheduleInput input = (ItemEntityScheduleInput)itemEntity;
               if (i != x) {
                   input.clearCurrent();
               } else {
                   input.setCurrent(y);
               }
           }
       }
    }
}
