package aqth.yzw.iamlittle.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import aqth.yzw.iamlittle.IItemClickListener;
import aqth.yzw.iamlittle.R;

public class SelectDeducePersonAdapter extends RecyclerView.Adapter<SelectDeducePersonAdapter.ViewHolder> {
    protected class ViewHolder extends RecyclerView.ViewHolder{
        private TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.single_textview);
        }
    }
    private List<String> mList;

    public void setItemClickListener(IItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    private IItemClickListener itemClickListener;

    public SelectDeducePersonAdapter(List<String> list){
        this.mList =  list;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_textview_item,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        String temp = mList.get(i);
        viewHolder.textView.setText(temp);
        viewHolder.textView.setOnClickListener(new View.OnClickListener() {
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
}
