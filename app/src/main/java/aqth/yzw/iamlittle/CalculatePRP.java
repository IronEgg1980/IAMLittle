package aqth.yzw.iamlittle;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.litepal.LitePal;

import java.util.Date;

import aqth.yzw.iamlittle.EntityClass.JXGZDetailsTemp;
import aqth.yzw.iamlittle.EntityClass.JXGZPersonDetailsTemp;

public class CalculatePRP extends AppCompatActivity {
    public void back() {
        if (hasInputed && !hasSaved) {
            MyDialogFragment dialogFragment = MyDialogFragment.newInstant("数据未保存，是否当前页面退出？", "否", "是");
            dialogFragment.setOnDialogFragmentDismiss(new OnDialogFragmentDismiss() {
                @Override
                public void onDissmiss(boolean flag) {
                    if (flag) {
                        finish();
                    }
                }

                @Override
                public void onDissmiss(boolean flag, Object object) {

                }
            });
        } else {
            finish();
        }
    }

    public void inputData() {
        inputRB.setChecked(true);
        deduceRB.setChecked(false);
        checkoutRB.setChecked(false);
        saveRB.setChecked(false);
        if (bottomRadioGroup.getVisibility() == View.GONE)
            bottomRadioGroup.setVisibility(View.VISIBLE);
        fragmentManager.beginTransaction().replace(R.id.calprp_activity_framlayout, new CalPRPInputDataFragment(), "Input").commit();
    }

    public void deduceData() {
        fragmentManager.beginTransaction().replace(R.id.calprp_activity_framlayout, new CalPRPDeduceFragment(), "Deduce").commit();
    }

    public void checkOutData() {
        fragmentManager.beginTransaction().replace(R.id.calprp_activity_framlayout, new CalPRPCheckoutFragment(), "Checkout").commit();
    }

    public void showAndSave() {
        fragmentManager.beginTransaction().replace(R.id.calprp_activity_framlayout, new CalPRPSaveFragment(), "ShowAndSave").commit();
    }

    public boolean isMonthHasSeleted() {
        return monthHasSeleted;
    }

    public void setMonthHasSeleted(boolean monthHasSeleted) {
        this.monthHasSeleted = monthHasSeleted;
    }

    private boolean monthHasSeleted;
    private FragmentManager fragmentManager;
    private RadioGroup bottomRadioGroup;
    private RadioButton inputRB, deduceRB, checkoutRB, saveRB;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(Date recordTime) {
        this.recordTime = recordTime;
    }

    public boolean isHasInputed() {
        return hasInputed;
    }

    public void setHasInputed(boolean hasInputed) {
        this.hasInputed = hasInputed;
    }

    public boolean isHasCheckouted() {
        return hasCheckouted;
    }

    public void setHasCheckouted(boolean hasCheckouted) {
        this.hasCheckouted = hasCheckouted;
    }

    public boolean isHasSaved() {
        return hasSaved;
    }

    public void setHasSaved(boolean hasSaved) {
        this.hasSaved = hasSaved;
    }

    private Date date;
    private Date recordTime;
    private boolean hasInputed;
    private boolean hasCheckouted;
    private boolean hasSaved;

    @Override
    public void onBackPressed() {
        back();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calprp_activity_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });
        hasInputed = false;
        hasSaved = false;
        hasCheckouted = false;
        monthHasSeleted = false;
        if (savedInstanceState != null) {
            monthHasSeleted = savedInstanceState.getBoolean("MonthHasSeleted");
            hasInputed = savedInstanceState.getBoolean("HasInputed");
            hasSaved = savedInstanceState.getBoolean("HasSaved");
            hasCheckouted = savedInstanceState.getBoolean("HasCheckouted");
        }
        fragmentManager = getSupportFragmentManager();
        inputRB = findViewById(R.id.calprp_activity_bottomRB1);
        deduceRB = findViewById(R.id.calprp_activity_bottomRB2);
        checkoutRB = findViewById(R.id.calprp_activity_bottomRB3);
        saveRB = findViewById(R.id.calprp_activity_bottomRB4);
        bottomRadioGroup = findViewById(R.id.radioGroup2);
        bottomRadioGroup.setVisibility(View.GONE);
        bottomRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.calprp_activity_bottomRB1:
                        inputData();
                        break;
                    case R.id.calprp_activity_bottomRB2:
                        deduceData();
                        break;
                    case R.id.calprp_activity_bottomRB3:
                        checkOutData();
                        break;
                    case R.id.calprp_activity_bottomRB4:
                        showAndSave();
                        break;
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!monthHasSeleted) {
            fragmentManager.beginTransaction().replace(R.id.calprp_activity_framlayout, new CalPRPSelectMonthFragment(), "SelectMonth").commit();
        }else{
            inputData();
        }
    }

    //这里继续，写保存状态的代码

    @Override
    protected void onDestroy() {
        if (LitePal.isExist(JXGZDetailsTemp.class))
            LitePal.deleteAll(JXGZDetailsTemp.class);
        if (LitePal.isExist(JXGZPersonDetailsTemp.class))
            LitePal.deleteAll(JXGZPersonDetailsTemp.class);
        super.onDestroy();
    }
}
