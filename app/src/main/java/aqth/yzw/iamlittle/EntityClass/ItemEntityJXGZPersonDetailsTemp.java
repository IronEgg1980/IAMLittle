package aqth.yzw.iamlittle.EntityClass;

import aqth.yzw.iamlittle.ItemType;

public class ItemEntityJXGZPersonDetailsTemp extends ItemEntity {
    @Override
    public ItemType getType(){
        return ItemType.JXGZ_PERSON_DETAILS;
    }
    public ItemEntityJXGZPersonDetailsTemp(JXGZPersonDetailsTemp JXGZPersonDetails){
        this.JXGZPersonDetails = JXGZPersonDetails;
        this.isSelect = false;
    }
    private JXGZPersonDetailsTemp JXGZPersonDetails;
    private boolean isSelect = false;

    public aqth.yzw.iamlittle.EntityClass.JXGZPersonDetailsTemp getJXGZPersonDetails() {
        return JXGZPersonDetails;
    }

    public void setJXGZPersonDetails(aqth.yzw.iamlittle.EntityClass.JXGZPersonDetailsTemp JXGZPersonDetails) {
        this.JXGZPersonDetails = JXGZPersonDetails;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
