package aqth.yzw.iamlittle.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import aqth.yzw.iamlittle.EntityClass.Person;
import aqth.yzw.iamlittle.IItemClickListener;
import aqth.yzw.iamlittle.R;

public class SelectPersonAdapter extends RecyclerView.Adapter<SelectPersonAdapter.ViewHolder> {
    protected class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.single_textview);
        }
    }

    private List<Person> mList;

    public void setItemClickListener(IItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    private IItemClickListener itemClickListener;

    public SelectPersonAdapter(List<Person> list) {
        this.mList = list;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_textview_item, viewGroup, false);
        return new SelectPersonAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        Person person = mList.get(i);
        viewHolder.textView.setText(person.getName());
        viewHolder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onClick(v, i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
