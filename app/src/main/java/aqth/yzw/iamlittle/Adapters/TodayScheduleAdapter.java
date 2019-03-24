package aqth.yzw.iamlittle.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Entity;

import java.util.List;

import aqth.yzw.iamlittle.EntityClass.ItemEntity;
import aqth.yzw.iamlittle.EntityClass.TodaySchedule;
import aqth.yzw.iamlittle.ItemType;
import aqth.yzw.iamlittle.R;

public class TodayScheduleAdapter extends RecyclerView.Adapter {
    protected class TodayScheduleViewHolder extends RecyclerView.ViewHolder{
        private TextView shiftTV,personTV;
        public TodayScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            shiftTV = itemView.findViewById(R.id.today_schedule_item_shift);
            personTV =itemView.findViewById(R.id.today_schedule_item_person);
        }
    }
    private List<ItemEntity> mList;
    public TodayScheduleAdapter(List<ItemEntity> list){
        mList = list;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(i == 2) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.today_schedule_item, viewGroup, false);
            return new TodayScheduleViewHolder(view);
        }else{
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_empty_layout, viewGroup, false);
            return new RecyclerviewEmptyViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        int k = getItemViewType(i);
        if(k==2) {
            TodaySchedule todaySchedule =(TodaySchedule) mList.get(i);
            TodayScheduleViewHolder viewHolder1 = (TodayScheduleViewHolder)viewHolder;
            viewHolder1.shiftTV.setText(todaySchedule.getShfit());
            String person = "";
            for (String s : todaySchedule.getPerson()) {
                person += s + "、";
            }
            if (!"".equals(person)) {
                person = person.substring(0, person.length() - 1);
            }
            viewHolder1.personTV.setText(person);
        }else{
            RecyclerviewEmptyViewHolder viewHolder1 = (RecyclerviewEmptyViewHolder)viewHolder;
            viewHolder1.getTextView().setText("没有今日排班信息(⊙_⊙)");
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        ItemType type = mList.get(position).getType();
        if(type == ItemType.EMPTY)
            return 1;
        else
            return 2;
    }
}
