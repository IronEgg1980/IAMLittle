package aqth.yzw.iamlittle.EntityClass;

import org.litepal.crud.LitePalSupport;

public class JXGZDetailsTemp extends LitePalSupport {
    private String JXGZName;
    private String JXGZAmount;
    private int JXGZType;

    public String getJXGZName() {
        return JXGZName;
    }

    public void setJXGZName(String JXGZName) {
        this.JXGZName = JXGZName;
    }

    public String getJXGZAmount() {
        return JXGZAmount;
    }

    public void setJXGZAmount(String JXGZAmount) {
        this.JXGZAmount = JXGZAmount;
    }

    public int getJXGZType() {
        return JXGZType;
    }

    public void setJXGZType(int JXGZType) {
        this.JXGZType = JXGZType;
    }
}
