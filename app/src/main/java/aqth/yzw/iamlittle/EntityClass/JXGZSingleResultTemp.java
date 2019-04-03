package aqth.yzw.iamlittle.EntityClass;

import org.litepal.crud.LitePalSupport;

public class JXGZSingleResultTemp extends LitePalSupport {
    private String personName;
    private double ratio;
    private double amount;

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    private int scale;

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public double getRatio() {
        return ratio;
    }

    public void setRatio(double ratio) {
        this.ratio = ratio;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
