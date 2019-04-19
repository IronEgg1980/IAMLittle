package aqth.yzw.iamlittle.EntityClass;

public class ScheduleActivity2ItemEntity {
    private String name;
    private String bedAssign;
    private double remaningLeaveValue;
    private String[] shifts = new String[7];
    private String note;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBedAssign() {
        return bedAssign;
    }

    public void setBedAssign(String bedAssign) {
        this.bedAssign = bedAssign;
    }

    public double getRemaningLeaveValue() {
        return remaningLeaveValue;
    }

    public void setRemaningLeaveValue(double remaningLeaveValue) {
        this.remaningLeaveValue = remaningLeaveValue;
    }

    public String[] getShifts() {
        return shifts;
    }

    public void setShift(int flag,String shift) {
        this.shifts[flag] = shift;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
