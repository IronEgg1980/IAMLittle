package aqth.yzw.iamlittle.EntityClass;

import java.util.Date;

import aqth.yzw.iamlittle.ItemType;

public class OTPTotalEntity extends ItemEntity {
    @Override
    public ItemType getType() {
        return ItemType.OTP_TOTAL;
    }
    private Date recordTime;
    private Date startDay;
    private Date endDay;

    public Date getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(Date recordTime) {
        this.recordTime = recordTime;
    }

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
}
