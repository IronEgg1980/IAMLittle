package aqth.yzw.iamlittle.EntityClass;

import org.litepal.crud.LitePalSupport;

public class JXGZPersonDetailsTemp extends LitePalSupport {
    private String personName;
    private String JXGZName;
    private double JXGZAmount;
    private int JXGZType;
    private double thatRatio;

    public double getThatRatio() {
        return thatRatio;
    }

    public void setThatRatio(double thatRatio) {
        this.thatRatio = thatRatio;
    }
    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getJXGZName() {
        return JXGZName;
    }

    public void setJXGZName(String JXGZName) {
        this.JXGZName = JXGZName;
    }

    public double getJXGZAmount() {
        return JXGZAmount;
    }

    public void setJXGZAmount(double JXGZAmount) {
        this.JXGZAmount = JXGZAmount;
    }

    public int getJXGZType() {
        return JXGZType;
    }

    public void setJXGZType(int JXGZType) {
        this.JXGZType = JXGZType;
    }
}
