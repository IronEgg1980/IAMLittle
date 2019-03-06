package aqth.yzw.iamlittle.EntityClass;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import aqth.yzw.iamlittle.ItemType;

public class ItemEntityJXGZPersonTotal extends ItemEntity {
    @Override
    public ItemType getType(){
        return ItemType.JXGZ_PERSON_TOTAL;
    }
    public ItemEntityJXGZPersonTotal(){
        this.list = new ArrayList<>();
        this.isSelect =false;
        this.isExpand = false;
    }
    public ItemEntityJXGZPersonTotal(List<JXGZPersonDetails> list){
        this.list = list;
        this.isSelect =false;
        this.isExpand = false;
    }

    private List<JXGZPersonDetails> list;
    private boolean isSelect;
    private boolean isExpand;
    private Date date;
    private String personName;
    private double amount;

    public Date getDate() {
        return list.get(0).getDate();
    }

    public String getPersonName() {
        return list.get(0).getPersonName();
    }

    public double getAmount() {
        amount = 0;
        for (JXGZPersonDetails details : list){
            amount += details.getJXGZAmount();
        }
        return amount;
    }

    public List<JXGZPersonDetails> getList() {
        return list;
    }

    public void setList(List<JXGZPersonDetails> list) {
        this.list = list;
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
