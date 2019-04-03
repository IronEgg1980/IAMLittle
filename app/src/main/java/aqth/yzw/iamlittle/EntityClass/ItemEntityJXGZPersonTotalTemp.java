package aqth.yzw.iamlittle.EntityClass;

import java.util.ArrayList;
import java.util.List;

import aqth.yzw.iamlittle.Arith;
import aqth.yzw.iamlittle.ItemType;
import aqth.yzw.iamlittle.MyTool;

public class ItemEntityJXGZPersonTotalTemp extends ItemEntity {
    @Override
    public ItemType getType(){
        return ItemType.JXGZ_PERSON_TOTAL;
    }
    public ItemEntityJXGZPersonTotalTemp(){
        this.list = new ArrayList<>();
        this.isSelect =false;
        this.isExpand = false;
    }
    public ItemEntityJXGZPersonTotalTemp(List<JXGZPersonDetailsTemp> list){
        this.list = list;
        this.isSelect =false;
        this.isExpand = false;
    }

    private List<JXGZPersonDetailsTemp> list;
    private boolean isSelect;
    private boolean isExpand;
    private double amount;

    public int getChildCount() {
        return list.size();
    }

    private int childCount;

    public double getThatRatio(){
        return list.get(0).getThatRatio();
    }
    public String getPersonName() {
        if(list.size() > 0)
            return list.get(0).getPersonName();
        else
            return "查无此人";
    }
    public String getAmountString(int flag){
        return Arith.doubleToString(getAmount(),flag);
    }

    public double getAmount() {
        amount = 0;
        for (JXGZPersonDetailsTemp details : list){
            amount = Arith.add(amount,details.getJXGZAmount());
        }
        return amount;
    }

    public List<JXGZPersonDetailsTemp> getList() {
        return list;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public boolean isExpand() {
        return isExpand;
    }

    public void setExpand(boolean expand) {
        isExpand = expand;
    }
}
