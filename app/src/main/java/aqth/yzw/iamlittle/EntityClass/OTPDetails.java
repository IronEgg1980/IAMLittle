package aqth.yzw.iamlittle.EntityClass;

import aqth.yzw.iamlittle.ItemType;

public class OTPDetails extends ItemEntity {
    @Override
    public ItemType getType() {
        return ItemType.OTP_DETAILS;
    }
    private String shiftName;
    private double uniteAmount;
    private double amount;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    private int count;

    public String getShiftName() {
        return shiftName;
    }

    public double getUniteAmount() {
        return uniteAmount;
    }

    public double getAmount() {
        return amount;
    }
    public OTPDetails(OverTimePay overTimePay){
        if(overTimePay != null){
            this.shiftName = overTimePay.getShiftName();
            this.uniteAmount = overTimePay.getShiftUA();
            this.count = overTimePay.getShiftCount();
            this.amount = overTimePay.getAmount();
        }
    }
}
