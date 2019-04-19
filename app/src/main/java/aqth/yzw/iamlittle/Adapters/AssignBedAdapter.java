package aqth.yzw.iamlittle.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import aqth.yzw.iamlittle.EntityClass.BedAssign;
import aqth.yzw.iamlittle.IItemClickListener;
import aqth.yzw.iamlittle.R;

public class AssignBedAdapter extends RecyclerView.Adapter {
    protected class ViewHolder extends RecyclerView.ViewHolder{
        TextView nameTV,assignBedTV;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTV = itemView.findViewById(R.id.person_name_textview);
            assignBedTV = itemView.findViewById(R.id.assign_bed_textview);
        }
    }
    private List<BedAssign> mList;
    private IItemClickListener selectPersonClick;
    private IItemClickListener longClick;
    private IItemClickListener inputAssignBedClick;

    public void setInputAssignBedClick(IItemClickListener inputAssignBedClick) {
        this.inputAssignBedClick = inputAssignBedClick;
    }

    public void setSelectPersonClick(IItemClickListener selectPersonClick) {
        this.selectPersonClick = selectPersonClick;
    }

    public void setLongClick(IItemClickListener longClick) {
        this.longClick = longClick;
    }

    public AssignBedAdapter(List<BedAssign> list){
        this.mList = list;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(i == 0) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.assign_bed_item, viewGroup, false);
            return new ViewHolder(view);
        }else{
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_empty_layout, viewGroup, false);
            return new RecyclerviewEmptyViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position == mList.size() -1)
            return 1;
        else
            return 0;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        if(getItemViewType(i) == 0) {
            BedAssign bedAssign = mList.get(i);
            ViewHolder holder = (ViewHolder)viewHolder;
            holder.nameTV.setText(bedAssign.getPersonName());
            holder.nameTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectPersonClick.onClick(v,i);
                }
            });
            holder.assignBedTV.setText(bedAssign.getAssign());
            holder.assignBedTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    inputAssignBedClick.onClick(v,i);
                }
            });
            holder.nameTV.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    longClick.onClick(v,i);
                    return true;
                }
            });
            holder.assignBedTV.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    longClick.onClick(v,i);
                    return true;
                }
            });
        }else{
            RecyclerviewEmptyViewHolder holder = (RecyclerviewEmptyViewHolder)viewHolder;
            holder.getTextView().setText("新增");
            holder.getTextView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mList.add(i,new BedAssign());
                    notifyItemRangeChanged(i,2);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
