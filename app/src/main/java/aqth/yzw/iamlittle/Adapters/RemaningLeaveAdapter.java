package aqth.yzw.iamlittle.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;


import java.util.List;

import aqth.yzw.iamlittle.Arith;
import aqth.yzw.iamlittle.EntityClass.Person;
import aqth.yzw.iamlittle.IItemClickListener;
import aqth.yzw.iamlittle.R;

public class RemaningLeaveAdapter extends RecyclerView.Adapter<RemaningLeaveAdapter.ViewHolder> {
    protected class ViewHolder extends RecyclerView.ViewHolder{
        TextView nameTV,rlTV;
        Button deduceBT,addBT;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTV = itemView.findViewById(R.id.person_name_textview);
            rlTV = itemView.findViewById(R.id.remaning_leaveTV);
            deduceBT = itemView.findViewById(R.id.deduce_button);
            addBT = itemView.findViewById(R.id.add_button);
        }
    }
    private List<Person> mList;
    private IItemClickListener inputClickListenner;

    public void setInputClickListenner(IItemClickListener clickListener) {
        this.inputClickListenner = clickListener;
    }
    public RemaningLeaveAdapter(List<Person> list){
        this.mList = list;
    }

    private void showPopMenu(final View view, final int position){
        PopupMenu popupMenu = new PopupMenu(view.getContext(),view);
        final Menu menu = popupMenu.getMenu();
        menu.clear();
        menu.add(0,1,0,"输入");
        menu.add(0,2,0,"清零");
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                final Person p = mList.get(position);
                if(item.getItemId() == 1){
                    inputClickListenner.onClick(view,position);
                }else{
                    p.setRemaningLeave(0.00);
                    notifyItemChanged(position);
                }
                return true;
            }
        });
        popupMenu.show();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.remaning_leave_item,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        final Person p = mList.get(i);
        viewHolder.nameTV.setText(p.getName());
        viewHolder.rlTV.setText(String.valueOf(p.getRemaningLeave()));
        viewHolder.deduceBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                p.setRemaningLeave(Arith.add(p.getRemaningLeave(),-0.5));
                notifyItemChanged(i);
            }
        });
        viewHolder.addBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                p.setRemaningLeave(Arith.add(p.getRemaningLeave(),0.5));
                notifyItemChanged(i);
            }
        });
        viewHolder.rlTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopMenu(v,i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
