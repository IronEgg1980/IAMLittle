package aqth.yzw.iamlittle.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import aqth.yzw.iamlittle.EntityClass.ItemEntity;
import aqth.yzw.iamlittle.EntityClass.OTPTotalEntity;
import aqth.yzw.iamlittle.IItemClickListener;
import aqth.yzw.iamlittle.ItemType;
import aqth.yzw.iamlittle.R;
// 4.2检查到了这里
public class OTPTotalAdapter extends RecyclerView.Adapter {
    protected class ViewHolder extends RecyclerView.ViewHolder{
        private TextView recordTimeTV,startDayTV,endDayTV;
        private LinearLayout root;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.otp_total_item_root);
            recordTimeTV = itemView.findViewById(R.id.otp_total_item_recordTime);
            startDayTV = itemView.findViewById(R.id.otp_total_item_startday);
            endDayTV = itemView.findViewById(R.id.otp_total_item_endday);
        }
    }
    private List<ItemEntity> mList;
    private SimpleDateFormat recordFt,dayFt;
    private IItemClickListener itemClickListener;

    public void setItemClickListener(IItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public OTPTotalAdapter(List<ItemEntity> list){
        mList = list;
        recordFt = new SimpleDateFormat("记录时间：yyyy年M月d日 HH:mm");
        dayFt = new SimpleDateFormat("yyyy年M月d日");
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(i == 1){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_empty_layout,viewGroup,false);
            return new RecyclerviewEmptyViewHolder(view);
        }else{
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.otp_total_item,viewGroup,false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        if(getItemViewType(i) == 1){
            RecyclerviewEmptyViewHolder viewHolder1 = (RecyclerviewEmptyViewHolder)viewHolder;
            viewHolder1.getTextView().setText("没有历史数据");
        }else{
            ViewHolder viewHolder1 = (ViewHolder)viewHolder;
            OTPTotalEntity otpTotalEntity = (OTPTotalEntity)mList.get(i);
            viewHolder1.recordTimeTV.setText(recordFt.format(otpTotalEntity.getRecordTime()));
            viewHolder1.startDayTV.setText(dayFt.format(otpTotalEntity.getStartDay()));
            viewHolder1.endDayTV.setText(dayFt.format(otpTotalEntity.getEndDay()));
            viewHolder1.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
        ItemEntity itemEntity = mList.get(position);
        if(itemEntity.getType() == ItemType.OTP_TOTAL){
            return 2;
        }else
            return 1;
    }
}
