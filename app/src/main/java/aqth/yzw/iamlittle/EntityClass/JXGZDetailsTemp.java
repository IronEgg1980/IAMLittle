package aqth.yzw.iamlittle.EntityClass;

import org.litepal.crud.LitePalSupport;

public class JXGZDetailsTemp extends LitePalSupport {
    private String JXGZName;
    private double JXGZAmount;
    private int JXGZType;

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
