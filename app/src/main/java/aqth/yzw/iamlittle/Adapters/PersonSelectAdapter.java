package aqth.yzw.iamlittle.Adapters;

import android.content.ClipData;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import aqth.yzw.iamlittle.EntityClass.ItemEntity;
import aqth.yzw.iamlittle.EntityClass.ItemEntityPerson;
import aqth.yzw.iamlittle.EntityClass.Person;
import aqth.yzw.iamlittle.IItemClickListener;
import aqth.yzw.iamlittle.ItemType;
import aqth.yzw.iamlittle.R;

public class PersonSelectAdapter extends RecyclerView.Adapter {
    protected class ViewHolder extends RecyclerView.ViewHolder{
        private TextView nameTV,ratioTV;
        private CheckBox checkBox;
        private LinearLayout root;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.select_person_root);
            nameTV = itemView.findViewById(R.id.select_person_name);
            ratioTV = itemView.findViewById(R.id.select_person_ratio);
            checkBox = itemView.findViewById(R.id.select_person_checkbox);
        }
    }
    private List<ItemEntity> mList;

    public void setiItemClickListener(IItemClickListener iItemClickListener) {
        this.iItemClickListener = iItemClickListener;
    }

    private IItemClickListener iItemClickListener;
    public PersonSelectAdapter(List<ItemEntity> list){
        this.mList = list;
    }
    @Override
    public int getItemViewType(int position) {
        ItemEntity itemEntity = mList.get(position);
        if(itemEntity.getType() == ItemType.EMPTY){
            return 0;
        }else{
            return 1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(i == 0){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_empty_layout,viewGroup,false);
            return new RecyclerviewEmptyViewHolder(view);
        }else{
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.select_person_item,viewGroup,false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        ItemEntity itemEntity = mList.get(i);
        if(itemEntity.getType() == ItemType.EMPTY){
            RecyclerviewEmptyViewHolder holder = (RecyclerviewEmptyViewHolder)viewHolder;
            holder.getTextView().setText("新增人员");
            holder.getTextView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iItemClickListener.onClick(v,i);
                }
            });
        }else{
            final ViewHolder holder = (ViewHolder)viewHolder;
            final ItemEntityPerson itemEntityPerson = (ItemEntityPerson)itemEntity;
            holder.nameTV.setText(itemEntityPerson.getName());
            holder.ratioTV.setText(Double.toString(itemEntityPerson.getRatio()));
            holder.checkBox.setChecked(itemEntityPerson.isSelect());
            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemEntityPerson.setSelect(holder.checkBox.isChecked());
                }
            });
            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iItemClickListener.onClick(v,i);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
