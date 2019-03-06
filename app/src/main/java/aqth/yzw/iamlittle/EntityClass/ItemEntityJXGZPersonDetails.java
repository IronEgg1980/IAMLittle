package aqth.yzw.iamlittle.EntityClass;

import aqth.yzw.iamlittle.ItemType;

public class ItemEntityJXGZPersonDetails extends ItemEntity {
    @Override
    public ItemType getType(){
        return ItemType.JXGZ_PERSON_DETAILS;
    }
    public ItemEntityJXGZPersonDetails(JXGZPersonDetails JXGZPersonDetails){
        this.JXGZPersonDetails = JXGZPersonDetails;
        this.isSelect = false;
    }
    private JXGZPersonDetails JXGZPersonDetails;
    private boolean isSelect = false;

    public aqth.yzw.iamlittle.EntityClass.JXGZPersonDetails getJXGZPersonDetails() {
        return JXGZPersonDetails;
    }

    public void setJXGZPersonDetails(aqth.yzw.iamlittle.EntityClass.JXGZPersonDetails JXGZPersonDetails) {
        this.JXGZPersonDetails = JXGZPersonDetails;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
