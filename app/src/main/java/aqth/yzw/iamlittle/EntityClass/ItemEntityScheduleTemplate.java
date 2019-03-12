package aqth.yzw.iamlittle.EntityClass;

import aqth.yzw.iamlittle.ItemType;

public class ItemEntityScheduleTemplate extends ItemEntity {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    private boolean isSelect;
    public ItemEntityScheduleTemplate(String name){
        this.name = name;
        isSelect = false;
    }
    @Override
    public ItemType getType(){
        return ItemType.SCHEDULE_TEMPLATE;
    }
}
