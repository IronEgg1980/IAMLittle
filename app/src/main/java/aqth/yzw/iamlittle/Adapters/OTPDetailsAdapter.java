package aqth.yzw.iamlittle.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import aqth.yzw.iamlittle.Arith;
import aqth.yzw.iamlittle.EntityClass.ItemEntity;
import aqth.yzw.iamlittle.EntityClass.OTPDetails;
import aqth.yzw.iamlittle.EntityClass.OTPPersonTotalEntity;
import aqth.yzw.iamlittle.ItemType;
import aqth.yzw.iamlittle.R;

public class OTPDetailsAdapter extends RecyclerView.Adapter {
    protected class PersonTotalVH extends RecyclerView.ViewHolder{
        private TextView nameTV,amountTV;
        public PersonTotalVH(@NonNull View itemView) {
            super(itemView);
            nameTV = itemView.findViewById(R.id.otp_person_total_name);
            amountTV = itemView.findViewById(R.id.otp_person_total_totalAmount);
        }
    }
    protected class PersonDetailsVH extends RecyclerView.ViewHolder{
        private TextView shiftNameTV,unitAmountTV,amountTV,countTV;
        private ImageView imageView;
        public PersonDetailsVH(@NonNull View itemView) {
            super(itemView);
            shiftNameTV = itemView.findViewById(R.id.otp_person_details_item_shiftname);
            unitAmountTV = itemView.findViewById(R.id.otp_person_details_item_unitamount);
            amountTV = itemView.findViewById(R.id.otp_person_details_item_amount);
            imageView = itemView.findViewById(R.id.otp_person_details_item_imageview);
            countTV = itemView.findViewById(R.id.otp_person_details_item_count);
        }
    }
    private List<ItemEntity> mList;


    @Override
    public int getItemViewType(int position) {
        ItemEntity itemEntity = mList.get(position);
        if(itemEntity.getType() == ItemType.OTP_PERSON_TOTAL){
            return 1;
        }else{
            return 2;
        }
    }

    public OTPDetailsAdapter(List<ItemEntity> list){
        mList = list;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(i == 1){
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.otp_person_total_item,viewGroup,false);
            return new PersonTotalVH(view);
        }else{
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.otp_person_details_item,viewGroup,false);
            return new PersonDetailsVH(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ItemEntity itemEntity = mList.get(i);
        if(getItemViewType(i) == 1){
            PersonTotalVH holder = (PersonTotalVH)viewHolder;
            OTPPersonTotalEntity entity = (OTPPersonTotalEntity)itemEntity;
            holder.nameTV.setText(entity.getPersonName());
            holder.amountTV.setText(Double.toString(entity.getTotalAmount()));
        }else{
            PersonDetailsVH holder = (PersonDetailsVH)viewHolder;
            OTPDetails entity = (OTPDetails)itemEntity;
            if(i==getItemCount() -1){
                holder.imageView.setImageResource(R.drawable.line2);
            }else if(mList.get(i+1).getType() == ItemType.OTP_PERSON_TOTAL){
                holder.imageView.setImageResource(R.drawable.line2);
            }else{
                holder.imageView.setImageResource(R.drawable.line1);
            }
            holder.shiftNameTV.setText(entity.getShiftName());
            holder.unitAmountTV.setText(Arith.doubleToString(entity.getUniteAmount()));
            holder.amountTV.setText(Arith.doubleToString(entity.getAmount()));
            holder.countTV.setText(Arith.intToString(entity.getCount()));
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
