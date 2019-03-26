package aqth.yzw.iamlittle.Adapters;

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
import aqth.yzw.iamlittle.EntityClass.ItemEntityJXGZPersonDetails;
import aqth.yzw.iamlittle.EntityClass.ItemEntityJXGZPersonTotal;
import aqth.yzw.iamlittle.EntityClass.ItemEntityJXGZTotalDetails;
import aqth.yzw.iamlittle.IItemClickListener;
import aqth.yzw.iamlittle.ItemType;
import aqth.yzw.iamlittle.MyTool;
import aqth.yzw.iamlittle.R;

public class PRPDetailsAdapter extends RecyclerView.Adapter {
    private List<ItemEntity> mList;
    private IItemClickListener itemClickListener;
    public void setItemClickListener(IItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    protected class FatherViewHolder extends RecyclerView.ViewHolder{
        private LinearLayout root,bottom;
        private TextView nameTV,ratioTV,totalAmountTV,TV1,TV2,TV3;
        private ImageView imageView;
        public FatherViewHolder(@NonNull View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.prpdetails_person_total_item_root);
            nameTV= itemView.findViewById(R.id.prpdetails_person_total_item_name);
            ratioTV= itemView.findViewById(R.id.prpdetails_person_total_item_ratio);
            totalAmountTV= itemView.findViewById(R.id.prpdetails_person_total_item_amount);
            TV1= itemView.findViewById(R.id.prpdetails_person_total_item_TV1);
            TV2= itemView.findViewById(R.id.prpdetails_person_total_item_TV2);
            TV3 =itemView.findViewById(R.id.prpdetails_person_total_item_TV3);
            imageView = itemView.findViewById(R.id.prpdetails_person_total_item_IV);
            bottom = itemView.findViewById(R.id.prpdetails_person_total_item_bottom);
        }
    }
    protected class ChildViewHolder extends RecyclerView.ViewHolder{
        private TextView nameTV,typeTV,amountTV;
        public ChildViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTV=itemView.findViewById(R.id.prpdetails_person_details_item_name);
            typeTV=itemView.findViewById(R.id.prpdetails_person_details_item_type);
            amountTV=itemView.findViewById(R.id.prpdetails_person_details_item_amount);
        }
    }
    protected class DetailsViewHolder extends RecyclerView.ViewHolder{
        private TextView nameTV,typeTV,amountTV;
        public DetailsViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTV = itemView.findViewById(R.id.prpdetails_item_name);
            typeTV= itemView.findViewById(R.id.prpdetails_item_type);
            amountTV= itemView.findViewById(R.id.prpdetails_item_amount);
        }
    }
    public PRPDetailsAdapter(List<ItemEntity> list){
        this.mList = list;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        switch (i){
            case 0:// empty
                View view0 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_empty_layout,
                        viewGroup,false);
                return new RecyclerviewEmptyViewHolder(view0);
            case 1:// father
                View view2 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.prpdetails_person_total_item,
                        viewGroup,false);
                return new FatherViewHolder(view2);
            case 2:// child
                View view3 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.prpdetails_person_details_item,
                        viewGroup,false);
                return new ChildViewHolder(view3);
            case 3:// details
                View view1 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.prpdetails_item,
                        viewGroup,false);
                return new DetailsViewHolder(view1);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        int type = getItemViewType(i);
        final ItemEntity itemEntity = mList.get(i);

        switch (type){
            case 0:// empty
                RecyclerviewEmptyViewHolder holder0 = (RecyclerviewEmptyViewHolder)viewHolder;
                holder0.getTextView().setText("没有找到本月绩效工资数据┗( T﹏T )┛");
                break;
            case 1:// father
                final FatherViewHolder holder2 = (FatherViewHolder)viewHolder;
                final ItemEntityJXGZPersonTotal personTotal = (ItemEntityJXGZPersonTotal)itemEntity;
                holder2.nameTV.setText(personTotal.getPersonName());
                holder2.totalAmountTV.setText(personTotal.getAmountString(2));
                holder2.ratioTV.setText(personTotal.getThatRatio()+"");
                if(personTotal.isExpand()){
                    holder2.imageView.setImageResource(R.drawable.ic_arrow_up_grey_500_24dp);
                    holder2.bottom.setVisibility(View.VISIBLE);
                }else{
                    holder2.imageView.setImageResource(R.drawable.ic_expand_more_grey_500_24dp);
                    holder2.bottom.setVisibility(View.GONE);
                }
                holder2.root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean b = personTotal.isExpand();
                        personTotal.setExpand(!b);
                        v.setTag(!b);
                        itemClickListener.onClick(v,i);
                    }
                });

                break;
            case 2:// child
                ChildViewHolder holder3 = (ChildViewHolder)viewHolder;
                ItemEntityJXGZPersonDetails personDetails = (ItemEntityJXGZPersonDetails)itemEntity;
                holder3.nameTV.setText(personDetails.getJXGZPersonDetails().getJXGZName());
                holder3.typeTV.setText(MyTool.getJXGZ_TypeString(personDetails.getJXGZPersonDetails().getJXGZType()));
                holder3.amountTV.setText(MyTool.doubleToString(personDetails.getJXGZPersonDetails().getJXGZAmount(),2)+"元");
                break;
            case 3:// details
                DetailsViewHolder holder1 = (DetailsViewHolder)viewHolder;
                ItemEntityJXGZTotalDetails totalDetails = (ItemEntityJXGZTotalDetails)itemEntity;
                holder1.nameTV.setText(totalDetails.getDetails().getJXGZName());
                holder1.typeTV.setText(MyTool.getJXGZ_TypeString(totalDetails.getDetails().getJXGZType()));
                holder1.amountTV.setText(MyTool.doubleToString(totalDetails.getDetails().getJXGZAmount(),2)+"元");
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        ItemType itemType = mList.get(position).getType();
        int i = 0;
        switch (itemType){
            case EMPTY:
                i = 0;
                break;
            case JXGZ_PERSON_TOTAL:
                i=1;
                break;
            case JXGZ_PERSON_DETAILS:
                i=2;
                break;
            case JXGZ_DETAILS:
                i=3;
                break;
        }
        return i;
    }
}
