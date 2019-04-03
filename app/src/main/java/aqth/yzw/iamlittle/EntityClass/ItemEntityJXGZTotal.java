package aqth.yzw.iamlittle.EntityClass;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import aqth.yzw.iamlittle.Arith;
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
        if(list.size() > 0)
            return list.get(0).getDate();
        else
            return new GregorianCalendar().getTime();
    }
    public Date getRecordTime(){
        if(list.size() > 0)
            return list.get(0).getRecordTime();
        else
            return new GregorianCalendar().getTime();
    }
    public String getTAString(int flag){
        return Arith.doubleToString(getTotalAmount(),flag);
    }
    public String getRAString(int flag){
        return Arith.doubleToString(getRatioAmount(),flag);
    }
    public String getAAString(int flag){
        return Arith.doubleToString(getAverageAmount(),flag);
    }
    public String getDAString(int flag){
        return Arith.doubleToString(getDeduceAmount(),flag);
    }
    public double getTotalAmount() {
        return Arith.add(Arith.add(getRatioAmount(), getAverageAmount()),getDeduceAmount());
    }

    public double getRatioAmount() {
        double d = 0;
        for(JXGZDetails details : list){
            if(details.getJXGZType() == MyTool.JXGZ_RATIO){
                d =Arith.add(d,details.getJXGZAmount());
            }
        }
        return d;
    }

    public double getAverageAmount() {
        double d = 0;
        for(JXGZDetails details : list){
            if(details.getJXGZType() == MyTool.JXGZ_AVERAGE){
                d =Arith.add(d,details.getJXGZAmount());
            }
        }
        return d;
    }

    public double getDeduceAmount() {
        double d = 0;
        for(JXGZDetails details : list){
            if(details.getJXGZType() == MyTool.JXGZ_DEDUCE){
                d = Arith.add(d, details.getJXGZAmount());
            }
        }
        return d;
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
