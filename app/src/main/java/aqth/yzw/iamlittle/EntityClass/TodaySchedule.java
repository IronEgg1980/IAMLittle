package aqth.yzw.iamlittle.EntityClass;

import java.util.ArrayList;
import java.util.List;

import aqth.yzw.iamlittle.ItemType;

public class TodaySchedule extends ItemEntity {
    private String shfit;
    private List<String> person;

    public String getShfit() {
        return shfit;
    }
    @Override
    public ItemType getType(){
        return ItemType.SCHEDULE_SINGLE_VIEW;
    }
    public void setShfit(String shfit) {
        this.shfit = shfit;
    }

    public List<String> getPerson() {
        return person;
    }

    public void setPerson(List<String> person) {
        this.person = person;
    }
    public TodaySchedule(){
        person = new ArrayList<>();
    }
}
