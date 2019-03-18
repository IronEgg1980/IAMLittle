package aqth.yzw.iamlittle.EntityClass;

import org.litepal.crud.LitePalSupport;

import java.util.Date;

public class OverTimePay extends LitePalSupport {
    private long id;
    private Date recordTime;
    private Date startDay,endDay;
    private String personName;
    private String shiftName;
    private double shiftUA;
    private int shiftCount;
    private double amount;
    public Date getStartDay() {
        return startDay;
    }

    public void setStartDay(Date startDay) {
        this.startDay = startDay;
    }

    public Date getEndDay() {
        return endDay;
    }

    public void setEndDay(Date endDay) {
        this.endDay = endDay;
    }

    public long getId() {
        return id;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getShiftName() {
        return shiftName;
    }

    public void setShiftName(String shiftName) {
        this.shiftName = shiftName;
    }

    public double getShiftUA() {
        return shiftUA;
    }

    public void setShiftUA(double shiftUA) {
        this.shiftUA = shiftUA;
    }

    public int getShiftCount() {
        return shiftCount;
    }

    public void setShiftCount(int shiftCount) {
        this.shiftCount = shiftCount;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
    public Date getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(Date recordTime) {
        this.recordTime = recordTime;
    }

}
