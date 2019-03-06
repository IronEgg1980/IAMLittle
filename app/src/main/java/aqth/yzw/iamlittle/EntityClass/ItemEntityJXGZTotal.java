package aqth.yzw.iamlittle.EntityClass;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import aqth.yzw.iamlittle.ItemType;
import aqth.yzw.iamlittle.MyTool;

public class ItemEntityJXGZTotal extends ItemEntity {
    @Override
    public ItemType getType(){
        return ItemType.JXGZ_TOTAL;
    }
    public ItemEntityJXGZTotal(){
        this.list = new ArrayList<>();
    }
    public ItemEntityJXGZTotal(List<JXGZDetails> list){
        this.list = list;
    }
    private List<JXGZDetails> list;
    private boolean isSelect = false;

    public Date getDate() {
        return list.get(0).getDate();
    }

    public double getTotalAmount() {
        return MyTool.getDouble(getRatioAmount() + getAverageAmount() + getDeduceAmount(),2);
    }

    public double getRatioAmount() {
        double d = 0;
        for(JXGZDetails details : list){
            if(details.getJXGZType() == MyTool.JXGZ_RATIO){
                d += details.getJXGZAmount();
            }
        }
        return MyTool.getDouble(d,2);
    }

    public double getAverageAmount() {
        double d = 0;
        for(JXGZDetails details : list){
            if(details.getJXGZType() == MyTool.JXGZ_AVERAGE){
                d += details.getJXGZAmount();
            }
        }
        return MyTool.getDouble(d,2);
    }

    public double getDeduceAmount() {
        double d = 0;
        for(JXGZDetails details : list){
            if(details.getJXGZType() == MyTool.JXGZ_DEDUCE){
                d += details.getJXGZAmount();
            }
        }
        return MyTool.getDouble(d,2);
    }

    private Date date;
    private double totalAmount;
    private double ratioAmount;
    private double averageAmount;
    private double deduceAmount;

    public List<JXGZDetails> getList() {
        return list;
    }

    public void setList(List<JXGZDetails> list) {
        this.list = list;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
