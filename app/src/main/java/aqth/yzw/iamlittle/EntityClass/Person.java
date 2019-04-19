package aqth.yzw.iamlittle.EntityClass;

import org.litepal.crud.LitePalSupport;

public class Person extends LitePalSupport {
    public long getId() {
        return id;
    }

    private long id;
    private String name;
    private boolean gender;
    private int age;
    private double ratio;
    private int status;
    private String phone;
    private String note;
    private double remaningLeave;

    public double getRemaningLeave() {
        return remaningLeave;
    }

    public void setRemaningLeave(double remaningLeave) {
        this.remaningLeave = remaningLeave;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public double getRatio() {
        return ratio;
    }

    public void setRatio(double ratio) {
        this.ratio = ratio;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
