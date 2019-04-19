package aqth.yzw.iamlittle.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import aqth.yzw.iamlittle.IItemClickListener;
import aqth.yzw.iamlittle.R;

public class SingleSelectListAdapter extends RecyclerView.Adapter {
    protected class ViewHolder extends RecyclerView.ViewHolder{
        private TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.single_textview);
        }
    }
    private List<String> mList;
    private IItemClickListener clickListener;

    public void setClickListener(IItemClickListener clickListener) {
        this.clickListener = clickListener;
    }
    public SingleSelectListAdapter(List<String> list){
        this.mList = list;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(i == 0){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_empty_layout,viewGroup,false);
            return new RecyclerviewEmptyViewHolder(view);
        }
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_textview_item,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        if(getItemViewType(i) == 0){
            RecyclerviewEmptyViewHolder holder = (RecyclerviewEmptyViewHolder)viewHolder;
            holder.getTextView().setText("未找到备份记录");
        }else{
            final String s = mList.get(i);
            ViewHolder holder = (ViewHolder)viewHolder;
            holder.textView.setText(s);
            holder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setTag(s);
                    clickListener.onClick(v,i);
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
        String s = mList.get(position);
        if(TextUtils.isEmpty(s))
            return 0;
        else
            return 1;
    }
}
