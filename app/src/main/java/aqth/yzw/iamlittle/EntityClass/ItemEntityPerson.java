package aqth.yzw.iamlittle.EntityClass;


import aqth.yzw.iamlittle.ItemType;

public class ItemEntityPerson extends ItemEntity {
    @Override
    public ItemType getType(){
        return ItemType.PERSON;
    }
    public ItemEntityPerson(Person p){
        this.person = p;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    private Person person;
    private boolean isSelect = false;

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
