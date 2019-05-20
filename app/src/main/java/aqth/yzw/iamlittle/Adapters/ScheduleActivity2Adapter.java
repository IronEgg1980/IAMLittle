package aqth.yzw.iamlittle.Adapters;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import aqth.yzw.iamlittle.EntityClass.ScheduleActivity2ItemEntity;
import aqth.yzw.iamlittle.IItemClickListener;
import aqth.yzw.iamlittle.R;

public class ScheduleActivity2Adapter extends RecyclerView.Adapter{
    protected class ViewHolder1 extends RecyclerView.ViewHolder{
        LinearLayout root;
        TextView assignBedTV,remaningLeaveTV,noteTV;
        TextView[] shiftTVs = new TextView[7];
        View divid1,divid2;
        public ViewHolder1(@NonNull View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.root);
            assignBedTV = itemView.findViewById(R.id.assign_bedTV);
            remaningLeaveTV = itemView.findViewById(R.id.remaning_leaveTV);
            noteTV = itemView.findViewById(R.id.noteTV);
            divid1 = itemView.findViewById(R.id.divide1);
            divid2 = itemView.findViewById(R.id.divide2);
            shiftTVs[0] = itemView.findViewById(R.id.shift1);
            shiftTVs[1] = itemView.findViewById(R.id.shift2);
            shiftTVs[2] = itemView.findViewById(R.id.shift3);
            shiftTVs[3] = itemView.findViewById(R.id.shift4);
            shiftTVs[4] = itemView.findViewById(R.id.shift5);
            shiftTVs[5] = itemView.findViewById(R.id.shift6);
            shiftTVs[6] = itemView.findViewById(R.id.shift7);
        }
    }
    protected class ViewHolder2 extends RecyclerView.ViewHolder{
        TextView textView;
        public ViewHolder2(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.appCompatTextView);
        }
    }
    private IItemClickListener clickListener;

    public void setClickListener(IItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setShowAssignedBed(boolean showAssignedBed) {
        this.showAssignedBed = showAssignedBed;
        notifyDataSetChanged();
    }

    public void setShowRemaningLeave(boolean showRemaningLeave) {
        this.showRemaningLeave = showRemaningLeave;
        notifyDataSetChanged();
    }

    private boolean showAssignedBed;
    private boolean showRemaningLeave;
    private List<ScheduleActivity2ItemEntity> mList;
    private int mode;// mode = 1 显示姓名
    public ScheduleActivity2Adapter(List<ScheduleActivity2ItemEntity> list,int mode,boolean showAssignedBed,boolean showRemaningLeave){
        this.mList = list;
        this.mode = mode;
        this.showAssignedBed = showAssignedBed;
        this.showRemaningLeave = showRemaningLeave;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(mode == 1){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.schedule2_name_item,viewGroup,false);
            return new ViewHolder2(view);
        }else {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.schedule2_item, viewGroup, false);
            return new ViewHolder1(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        final ScheduleActivity2ItemEntity itemEntity = mList.get(i);
        if(mode == 1){
            ViewHolder2 holder = (ViewHolder2)viewHolder;
            holder.textView.setText(itemEntity.getName());
            if(i%2 == 0){
                holder.textView.setBackground(new ColorDrawable(Color.WHITE));
            }
        }else{
            ViewHolder1 holder = (ViewHolder1)viewHolder;
            if(i%2 == 0){
                holder.root.setBackground(new ColorDrawable(Color.WHITE));
            }
            if(showAssignedBed) {
                holder.assignBedTV.setVisibility(View.VISIBLE);
                holder.divid1.setVisibility(View.VISIBLE);
                holder.assignBedTV.setText(itemEntity.getBedAssign());
                holder.assignBedTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.setTag(itemEntity.getBedAssign());
                        clickListener.onClick(v,i);
                    }
                });
            }else {
                holder.assignBedTV.setVisibility(View.GONE);
                holder.divid1.setVisibility(View.GONE);
            }
            if(showRemaningLeave){
                holder.remaningLeaveTV.setVisibility(View.VISIBLE);
                holder.divid2.setVisibility(View.VISIBLE);
                holder.remaningLeaveTV.setText(String.valueOf(itemEntity.getRemaningLeaveValue()));
            }else{
                holder.divid2.setVisibility(View.GONE);
                holder.remaningLeaveTV.setVisibility(View.GONE);
            }
            holder.noteTV.setText(itemEntity.getNote());
            holder.noteTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setTag(itemEntity.getNote());
                    clickListener.onClick(v,i);
                }
            });
            for(int k = 0;k<7;k++){
                holder.shiftTVs[k].setText(itemEntity.getShifts()[k]);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
