package aqth.yzw.iamlittle.EntityClass;


import aqth.yzw.iamlittle.ItemType;

public class ItemEntityPerson extends ItemEntity {
    @Override
    public ItemType getType(){
        return ItemType.PERSON;
    }
    public ItemEntityPerson(String name,double ratio){
        this.name = name;
        this.ratio = ratio;
    }
    public ItemEntityPerson(Person person){
        this.person = person;
        this.name = person.getName();
        this.ratio = person.getRatio();
    }
    private boolean isSelect = false;
    private String name;
    private double ratio;
    private Person person;

    public Person getPerson() {
        return person;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRatio() {
        return ratio;
    }

    public void setRatio(double ratio) {
        this.ratio = ratio;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
