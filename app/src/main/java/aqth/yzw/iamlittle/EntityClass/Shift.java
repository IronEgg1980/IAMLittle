package aqth.yzw.iamlittle.EntityClass;

import org.litepal.crud.LitePalSupport;

public class Shift extends LitePalSupport {
    private String name;
    private int type;
    private double unitAmount;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public double getUnitAmount() {
        return unitAmount;
    }

    public void setUnitAmount(double unitAmount) {
        this.unitAmount = unitAmount;
    }
}
