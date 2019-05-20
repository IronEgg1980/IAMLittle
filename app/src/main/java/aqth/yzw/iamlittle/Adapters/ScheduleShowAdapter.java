package aqth.yzw.iamlittle.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aqth.yzw.iamlittle.EntityClass.ItemEntity;
import aqth.yzw.iamlittle.EntityClass.ItemEntityScheduleInput;
import aqth.yzw.iamlittle.IItemClickListener;
import aqth.yzw.iamlittle.ItemType;
import aqth.yzw.iamlittle.R;

public class ScheduleShowAdapter extends RecyclerView.Adapter {
    private List<ItemEntity> mList;
    private IItemClickListener itemClickListener;
    private IItemClickListener itemLongClickListener;
    private Context mContext;
    public void setItemLongClickListener(IItemClickListener itemLongClickListener) {
        this.itemLongClickListener = itemLongClickListener;
    }

    private boolean isInputMode;

    public void setItemClickListener(IItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public ScheduleShowAdapter(Context context, List<ItemEntity> list){
        mContext = context;
        mList = list;
        isInputMode = false;
    }
    public ScheduleShowAdapter(Context context,List<ItemEntity> list,boolean isInputMode){
        mContext = context;
        mList = list;
        this.isInputMode = true;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if(i == 1){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_empty_layout, viewGroup, false);
            return new RecyclerviewEmptyViewHolder(view);
        }else{
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.schedule_recyclerview_item,viewGroup,false);
            return new ScheduleItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int i) {
        final ItemEntity itemEntity = mList.get(i);
        final int x = i;
        if(itemEntity.getType() == ItemType.EMPTY){
            RecyclerviewEmptyViewHolder holder = (RecyclerviewEmptyViewHolder) viewHolder;
            if(isInputMode){
                holder.getTextView().setText("增加一行");
                holder.getTextView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.onClick(v,x,0);
                    }
                });
            }else {
                holder.getTextView().setText("没有本周排班数据");
            }
        }else{
            ScheduleItemViewHolder holder = (ScheduleItemViewHolder)viewHolder;
            ItemEntityScheduleInput inputItem = (ItemEntityScheduleInput)itemEntity;
            if(isInputMode && i < getItemCount() -1){
                holder.getRowNumberTV().setText((i+1)+"");
            }else{
                holder.getRowNumberTV().setVisibility(View.GONE);
            }
            for(int k = 0;k<9;k++){
                final int y = k;
                holder.getTVs()[k].setText(inputItem.getValues(k));
                holder.getTVs()[k].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setCurrentCell(x, y);
                        notifyDataSetChanged();
                        itemClickListener.onClick(v, x, y);
                    }
                });
                if (isInputMode) {
                    if(inputItem.getCurrent(k)){
                        holder.getTVs()[k].setBackgroundColor(Color.WHITE);
                    }else{
                        holder.getTVs()[k].setBackgroundColor(mContext.getColor(R.color.scheduleItemTextViewBG));
                    }
                    holder.getTVs()[k].setLongClickable(true);
                    holder.getTVs()[k].setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            setCurrentCell(x, y);
                            notifyDataSetChanged();
                            itemLongClickListener.onClick(v, x, y);
                            return true;
                        }
                    });
                }else{
                    if(x % 2 == 0){
                        holder.getTVs()[k].setBackgroundColor(mContext.getColor(R.color.scheduleItemTextViewBG));
                    }else{
                        holder.getTVs()[k].setBackgroundColor(Color.WHITE);
                    }
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
