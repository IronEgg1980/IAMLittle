package aqth.yzw.iamlittle.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import aqth.yzw.iamlittle.EntityClass.ItemEntity;
import aqth.yzw.iamlittle.EntityClass.ItemEntityPerson;
import aqth.yzw.iamlittle.EntityClass.ItemEntityShift;
import aqth.yzw.iamlittle.IItemClickListener;
import aqth.yzw.iamlittle.ItemType;
import aqth.yzw.iamlittle.MyTool;
import aqth.yzw.iamlittle.R;

public class PersonShiftInputAdapter extends RecyclerView.Adapter<PersonShiftInputAdapter.ViewHolder> {
    protected class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView imageView;
        private TextView textView;
        private LinearLayout view;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view =(LinearLayout) itemView;
            imageView = itemView.findViewById(R.id.schedule_inputedit_bottom_item_imageview);
            textView = itemView.findViewById(R.id.schedule_inputedit_bottom_item_textview);
        }
    }
    private List<ItemEntity> mList;
    private IItemClickListener itemClickListener;
    private Context mContext;

    public void setItemClickListener(IItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
    public PersonShiftInputAdapter(Context context,List<ItemEntity> list){
        mContext = context;
        mList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.schedule_inputedit_bottom_recyclerview_item,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int i) {
        int type = getItemViewType(i);
        ItemEntity itemEntity = mList.get(i);
        viewHolder.view.setClickable(true);
        if(type == 1){
            // shift
            final ItemEntityShift shift = (ItemEntityShift)itemEntity;
            String name = shift.getShift().getName();
            viewHolder.view.setTag(name);
            viewHolder.textView.setText(name);
            viewHolder.imageView.setImageDrawable(mContext.getDrawable(R.drawable.shift));

        }else if(type == 2){
            // person
            final ItemEntityPerson person = (ItemEntityPerson)itemEntity;
            String name = person.getPerson().getName();
            viewHolder.view.setTag(R.id.tag_text,name);
            viewHolder.view.setTag(R.id.tag_itemEntityPerson,person);
            viewHolder.textView.setText(name);
            if(person.getPerson().getStatus() == MyTool.PERSON_STATUS_LEAVE){
                viewHolder.textView.setTextColor(Color.RED);
            }else{
                viewHolder.textView.setTextColor(mContext.getColor(R.color.colorText));
            }
            viewHolder.imageView.setImageDrawable(mContext.getDrawable(R.drawable.person_image));
        }
        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onClick(v,i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        ItemEntity itemEntity = mList.get(position);
        if(itemEntity.getType() == ItemType.PERSON)
            return  2;
        if(itemEntity.getType() == ItemType.EMPTY)
            return 0;
        return 1;
    }
}
