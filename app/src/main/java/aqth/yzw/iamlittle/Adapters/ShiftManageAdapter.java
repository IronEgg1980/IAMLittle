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

import aqth.yzw.iamlittle.Arith;
import aqth.yzw.iamlittle.EntityClass.ItemEntity;
import aqth.yzw.iamlittle.EntityClass.ItemEntityShift;
import aqth.yzw.iamlittle.EntityClass.Shift;
import aqth.yzw.iamlittle.IItemClickListener;
import aqth.yzw.iamlittle.ItemType;
import aqth.yzw.iamlittle.MyTool;
import aqth.yzw.iamlittle.R;

public class ShiftManageAdapter extends RecyclerView.Adapter {
    private int ADDBUTTON = 1;
    private List<ItemEntity> mList;
    private IItemClickListener shiftItemClickListener;

    public void setShiftItemClickListener(IItemClickListener shiftItemClickListener) {
        this.shiftItemClickListener = shiftItemClickListener;
    }

    protected class ShiftViewHolder extends RecyclerView.ViewHolder{
        LinearLayout rootView;
        TextView typeColorTV,typeTextTV,nameTV,unitAmountTV,danweijine,emptyTV;
        public ShiftViewHolder(@NonNull View itemView) {
            super(itemView);
            rootView = itemView.findViewById(R.id.shift_recyclerview_item_root);
            typeColorTV = itemView.findViewById(R.id.shift_recyclerview_item_typeColor);
            typeTextTV = itemView.findViewById(R.id.shift_recyclerview_item_typeTextView);
            nameTV = itemView.findViewById(R.id.shift_recyclerview_item_nameTextView);
            unitAmountTV = itemView.findViewById(R.id.shift_recyclerview_item_unitAmountTextView);
            danweijine = itemView.findViewById(R.id.shift_recyclerview_item_danweijine);
        }
    }
    public ShiftManageAdapter(List<ItemEntity> list){
        this.mList = list;
    }
    @Override
    public int getItemViewType(int position) {
        if(position == getItemCount() -1){
            return ADDBUTTON;
        }else{
            return 2;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
           if (i == ADDBUTTON){
               View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_empty_layout,viewGroup,false);
               return new RecyclerviewEmptyViewHolder(v);
           }else{
               View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.shift_recyclerview_item,viewGroup,false);
               return new ShiftViewHolder(v);
           }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        ItemEntity itemEntity = mList.get(i);
        ItemType itemType = itemEntity.getType();
        if (itemType == ItemType.EMPTY){
            RecyclerviewEmptyViewHolder viewHolder1 = (RecyclerviewEmptyViewHolder)viewHolder;
            viewHolder1.getTextView().setText("新增班次");
            viewHolder1.getTextView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setTag("Button");
                    shiftItemClickListener.onClick(v,i);
                }
            });
        }else{
            ShiftViewHolder vieHolder2 = (ShiftViewHolder)viewHolder;
            ItemEntityShift itemEntityShift = (ItemEntityShift)itemEntity;
            final Shift shift = itemEntityShift.getShift();
            vieHolder2.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shiftItemClickListener.onClick(v,i);
                }
            });
            vieHolder2.nameTV.setText(shift.getName());
//            vieHolder2.nameTV.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    shiftItemClickListener.onClick(v,i);
//                }
//            });
            int type = shift.getType();
            if(type == MyTool.SHIFT_NORMAL){
                vieHolder2.typeColorTV.setBackground(new ColorDrawable(Color.BLUE));
                vieHolder2.typeTextTV.setText("类型：常规");
                vieHolder2.danweijine.setText("");
                vieHolder2.unitAmountTV.setText("");
            }else if(type == MyTool.SHIFT_LEAVEOFF){
                vieHolder2.typeColorTV.setBackground(new ColorDrawable(Color.RED));
                vieHolder2.typeTextTV.setText("类型：缺勤");
                vieHolder2.danweijine.setText("");
                vieHolder2.unitAmountTV.setText("");
            }else{
                vieHolder2.typeColorTV.setBackground(new ColorDrawable(Color.GREEN));
                vieHolder2.typeTextTV.setText("类型：统计");
                vieHolder2.danweijine.setText("单位金额：");
                vieHolder2.unitAmountTV.setText(Arith.doubleToString(shift.getUnitAmount()));
            }
//            vieHolder2.unitAmountTV.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    shiftItemClickListener.onClick(v,i);
//                }
//            });
//            vieHolder2.danweijine.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    shiftItemClickListener.onClick(v,i);
//                }
//            });
//            vieHolder2.typeTextTV.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    shiftItemClickListener.onClick(v,i);
//                }
//            });
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
