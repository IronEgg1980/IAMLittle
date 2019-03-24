package aqth.yzw.iamlittle.EntityClass;

import org.litepal.crud.LitePalSupport;

import java.util.Date;

public class JXGZPersonDetails extends LitePalSupport {
    private Date date;

    public Date getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(Date recordTime) {
        this.recordTime = recordTime;
    }
    public double getThatRatio() {
        return thatRatio;
    }

    public void setThatRatio(double thatRatio) {
        this.thatRatio = thatRatio;
    }

    private Date recordTime;
    private String personName;
    private String JXGZName;
    private double JXGZAmount;
    private double thatRatio;
    private int JXGZType;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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
