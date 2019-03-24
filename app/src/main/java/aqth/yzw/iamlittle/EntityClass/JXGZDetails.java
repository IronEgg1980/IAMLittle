package aqth.yzw.iamlittle.EntityClass;

import org.litepal.crud.LitePalSupport;

import java.util.Date;

public class JXGZDetails extends LitePalSupport {
    private Date date;

    public Date getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(Date recordTime) {
        this.recordTime = recordTime;
    }

    private Date recordTime;
    private String JXGZName;
    private double JXGZAmount;
    private int JXGZType;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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
