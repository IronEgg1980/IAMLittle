package aqth.yzw.iamlittle.EntityClass;

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
            this.personName = list.get(0).getPersonName();
            for (OverTimePay otp : list) {
                totalAmount = Arith.add(totalAmount, otp.getAmount());
            }
        }
    }
}
