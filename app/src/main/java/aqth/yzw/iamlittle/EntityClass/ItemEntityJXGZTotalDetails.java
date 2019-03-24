package aqth.yzw.iamlittle.EntityClass;

import aqth.yzw.iamlittle.ItemType;

public class ItemEntityJXGZTotalDetails extends ItemEntity {
    @Override
    public ItemType getType(){
        return ItemType.JXGZ_DETAILS;
    }
    public ItemEntityJXGZTotalDetails(JXGZDetails details){
        this.details = details;
        this.isSelect = false;
    }

    public JXGZDetails getDetails() {
        return details;
    }

    private JXGZDetails details;

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    private boolean isSelect = false;
}
