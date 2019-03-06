package aqth.yzw.iamlittle.EntityClass;

import android.content.ClipData;

import aqth.yzw.iamlittle.ItemType;

public class ItemEntityScheduleInput extends ItemEntity {
    public ItemType getType(){
        return ItemType.SCHEDULE_WEEK_VIEW;
    }
    private String[] values;
    private boolean[] current;

    public ItemEntityScheduleInput(){
        values = new String[9];
        current = new boolean[9];
        clearCurrent();
    }

    public void setValues(int y,String text){
        values[y] = text;
    }

    public String getValues(int y){
        return values[y];
    }

    public void setCurrent(int y){
        if(y <0 || y > 8){
            return;
        }
        clearCurrent();
        current[y] = true;
    }

    public boolean getCurrent(int y) {
        return current[y];
    }
    public void clearCurrent(){
        for(int i = 0;i<9;i++){
            current[i] = false;
        }
    }
}
