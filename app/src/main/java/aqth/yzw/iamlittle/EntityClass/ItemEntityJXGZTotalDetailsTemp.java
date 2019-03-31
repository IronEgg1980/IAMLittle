package aqth.yzw.iamlittle.EntityClass;

import aqth.yzw.iamlittle.ItemType;

public class ItemEntityJXGZTotalDetailsTemp extends ItemEntity {
    @Override
    public ItemType getType(){
        return ItemType.JXGZ_DETAILS;
    }

    public ItemEntityJXGZTotalDetailsTemp(JXGZDetailsTemp details){
        this.details = details;
        this.isSelect = false;
    }

    public JXGZDetailsTemp getDetails() {
        return details;
    }

    private JXGZDetailsTemp details;

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    private boolean isSelect = false;
}
