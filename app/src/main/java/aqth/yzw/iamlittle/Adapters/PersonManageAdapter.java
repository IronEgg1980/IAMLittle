package aqth.yzw.iamlittle.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import aqth.yzw.iamlittle.EntityClass.ItemEntity;
import aqth.yzw.iamlittle.EntityClass.ItemEntityPerson;
import aqth.yzw.iamlittle.EntityClass.Person;
import aqth.yzw.iamlittle.IItemClickListener;
import aqth.yzw.iamlittle.ItemType;
import aqth.yzw.iamlittle.R;

public class PersonManageAdapter extends RecyclerView.Adapter {
    private List<ItemEntity> mList;
    private IItemClickListener itemClickListener;
    private final int ADD_BUTTON_TYPE = 1;
    private final int NORMAL_TYPE =2;

    public void setItemClickListener(IItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
    public PersonManageAdapter(List<ItemEntity> list){
        this.mList = list;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(i == ADD_BUTTON_TYPE){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_addbutton_layout,viewGroup,false);
            return new RecyclerViewAddButtonViewHolder(view);
        }else{
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.person_manage_recyclerview_item,viewGroup,false);
            return new PersonViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        ItemEntity itemEntity = mList.get(i);
        ItemType itemType = itemEntity.getType();
        switch (itemType){
            case EMPTY:
                RecyclerViewAddButtonViewHolder recyclerViewAddButtonViewHolder = (RecyclerViewAddButtonViewHolder)viewHolder;
                ((RecyclerViewAddButtonViewHolder) viewHolder).getButton().setText("增加一人");
                ((RecyclerViewAddButtonViewHolder) viewHolder).getButton().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.onClick(v,i);
                    }
                });
                break;
            case PERSON:
                PersonViewHolder personViewHolder = (PersonViewHolder)viewHolder;
                ItemEntityPerson itemEntityPerson = (ItemEntityPerson)itemEntity;
                final Person p = itemEntityPerson.getPerson();
                personViewHolder.nameTextView.setText(p.getName());
                personViewHolder.ratioTextView.setText(p.getRatio()+"");
                personViewHolder.view.setClickable(true);
                personViewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.setTag(p);
                        itemClickListener.onClick(v,i);
                    }
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
    @Override
    public int getItemViewType(int position) {
        if(position == getItemCount() -1)
            return ADD_BUTTON_TYPE;
        else
            return NORMAL_TYPE;
    }
    protected class PersonViewHolder extends RecyclerView.ViewHolder{
        private TextView nameTextView,ratioTextView;
        private LinearLayout view;
        public PersonViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView.findViewById(R.id.recyclerview_item_rootview);
            nameTextView = itemView.findViewById(R.id.recyclerview_item_person_name);
            ratioTextView = itemView.findViewById(R.id.recyclerview_item_person_ratio);
        }
    }
}
