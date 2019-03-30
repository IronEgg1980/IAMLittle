package aqth.yzw.iamlittle.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import aqth.yzw.iamlittle.EntityClass.JXGZPersonDetailsTemp;

public class JXGZDeduceRecyclerViewAdapter extends RecyclerView.Adapter {
    protected class DeduceItemViewHolder extends RecyclerView.ViewHolder{

        public DeduceItemViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
    protected class SelectOthersViewHolder extends RecyclerView.ViewHolder{

        public SelectOthersViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    private List<JXGZPersonDetailsTemp> mList;
    private int mode;
    public JXGZDeduceRecyclerViewAdapter(List<JXGZPersonDetailsTemp> list,int mode){
        this.mode = mode;
        this.mList = list;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
