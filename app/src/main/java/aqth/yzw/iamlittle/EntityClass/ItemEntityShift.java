package aqth.yzw.iamlittle.EntityClass;

import aqth.yzw.iamlittle.ItemType;

public class ItemEntityShift extends ItemEntity {
    @Override
    public ItemType getType(){
        return ItemType.SHIFT;
    }
    public ItemEntityShift(Shift shift){
        this.shift = shift;
        this.isSelect = false;
    }
    private Shift shift;

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    private boolean isSelect = false;
    public Shift getShift() {
        return shift;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
    }
}
