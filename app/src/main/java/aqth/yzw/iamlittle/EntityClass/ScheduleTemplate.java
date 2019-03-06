package aqth.yzw.iamlittle.EntityClass;

import org.litepal.crud.LitePalSupport;

public class ScheduleTemplate extends LitePalSupport {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }
    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getShift1() {
        return shift1;
    }

    public void setShift1(String shift1) {
        this.shift1 = shift1;
    }

    public String getShift2() {
        return shift2;
    }

    public void setShift2(String shift2) {
        this.shift2 = shift2;
    }

    public String getShift3() {
        return shift3;
    }

    public void setShift3(String shift3) {
        this.shift3 = shift3;
    }

    public String getShift4() {
        return shift4;
    }

    public void setShift4(String shift4) {
        this.shift4 = shift4;
    }

    public String getShift5() {
        return shift5;
    }

    public void setShift5(String shift5) {
        this.shift5 = shift5;
    }

    public String getShift6() {
        return shift6;
    }

    public void setShift6(String shift6) {
        this.shift6 = shift6;
    }

    public String getShift7() {
        return shift7;
    }

    public void setShift7(String shift7) {
        this.shift7 = shift7;
    }

    private String name;
    private int rowNumber;
    private String personName;
    private String shift1;
    private String shift2;
    private String shift3;
    private String shift4;
    private String shift5;
    private String shift6;
    private String shift7;
    private String note;
}
