package aqth.yzw.iamlittle.EntityClass;

import java.util.Date;
import java.util.List;

import aqth.yzw.iamlittle.Arith;
import aqth.yzw.iamlittle.ItemType;

public class OTPPersonTotalEntity extends ItemEntity {
    @Override
    public ItemType getType() {
        return ItemType.OTP_PERSON_TOTAL;
    }

    private String personName;
    private double totalAmount;
    private Date startDay,endDay;

    public int getChildCount() {
        return childCount;
    }

    private int childCount;

    public Date getStartDay() {
        return startDay;
    }

    public Date getEndDay() {
        return endDay;
    }

    public String getPersonName() {
        return personName;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public OTPPersonTotalEntity(List<OverTimePay> list) {
        this.personName = "";
        this.totalAmount = 0;
        if (list != null && list.size() > 0) {
            OverTimePay otp = list.get(0);
            this.personName =otp.getPersonName();
            this.startDay = otp.getStartDay();
            this.endDay = otp.getEndDay();
            for (OverTimePay otp1 : list) {
                totalAmount = Arith.add(totalAmount, otp1.getAmount());
            }
        }
        this.childCount = list.size();
    }
}
