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
import java.util.List;

import aqth.yzw.iamlittle.EntityClass.ItemEntityScheduleTemplate;
import aqth.yzw.iamlittle.IItemClickListener;
import aqth.yzw.iamlittle.R;

public class ScheduleTemplateAdapter extends RecyclerView.Adapter<ScheduleTemplateAdapter.ViewHolder> {
    protected class ViewHolder extends RecyclerView.ViewHolder{
        private TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.single_textview);
        }
    }
    private List<ItemEntityScheduleTemplate> mList;
    private boolean isMultiSelect;
    private Context mContext;
    public void setItemClickListener(IItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    private IItemClickListener itemClickListener;
    public ScheduleTemplateAdapter(Context context,List<ItemEntityScheduleTemplate> list,boolean isMultiSelect){
        mContext = context;
        mList = list;
        this.isMultiSelect = isMultiSelect;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_textview_item,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int i) {
        final ItemEntityScheduleTemplate item = mList.get(i);
        viewHolder.textView.setText(item.getName());
        viewHolder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onClick(v,i);
            }
        });
        if(isMultiSelect){
            if(item.isSelect())
                viewHolder.textView.setBackgroundColor(mContext.getColor(R.color.colorAccent));
            else
                viewHolder.textView.setBackgroundColor(Color.WHITE);
        }else{
            viewHolder.textView.setBackgroundColor(Color.WHITE);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
