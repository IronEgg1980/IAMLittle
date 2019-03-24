package aqth.yzw.iamlittle.EntityClass;

import org.litepal.crud.LitePalSupport;

import java.util.Date;

public class Schedule extends LitePalSupport {
    private Date date;
    private String personName;
    private String shiftName;
    private int shiftType;

    public double getShiftUnitAmount() {
        return shiftUnitAmount;
    }

    public void setShiftUnitAmount(double shiftUnitAmount) {
        this.shiftUnitAmount = shiftUnitAmount;
    }

    private double shiftUnitAmount;
    private String note;
    private int rowNumber;

    public int getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
    }

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

    public String getShiftName() {
        return shiftName;
    }

    public void setShiftName(String shiftName) {
        this.shiftName = shiftName;
    }

    public int getShiftType() {
        return shiftType;
    }

    public void setShiftType(int shiftType) {
        this.shiftType = shiftType;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
