package aqth.yzw.iamlittle;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.litepal.LitePal;

import java.util.Date;

import aqth.yzw.iamlittle.EntityClass.JXGZPersonDetailsTemp;

public class CalculatePRP extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        back();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentManager = getSupportFragmentManager();
        hasCheckouted = false;
        monthHasSeleted = false;
        input_b = true;
        deduce_b=false;
        checkout_b=false;
        save_b = false;
        if (savedInstanceState != null) {
            monthHasSeleted = savedInstanceState.getBoolean("MonthHasSeleted");
            if(monthHasSeleted) {
                hasCheckouted = savedInstanceState.getBoolean("HasCheckouted");
                date = new Date(savedInstanceState.getLong("Date"));
                recordTime = new Date(savedInstanceState.getLong("RecordTime"));
                input_b = savedInstanceState.getBoolean("RB1");
                deduce_b = savedInstanceState.getBoolean("RB2");
                checkout_b = savedInstanceState.getBoolean("RB3");
                save_b = savedInstanceState.getBoolean("RB4");
            }
        }else{
            Fragment fragment = new CalPRPSelectMonthFragment();
            fragmentManager.beginTransaction().add(R.id.calprp_activity_framlayout,fragment,"SelectMonth")
                    .addToBackStack(null)
                    .commit();
        }
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
        inputRB = findViewById(R.id.calprp_activity_bottomRB1);
        inputRB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inputRB.isChecked()){
                    inputData();
                }
            }
        });
        deduceRB = findViewById(R.id.calprp_activity_bottomRB2);
        deduceRB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(deduceRB.isChecked())
                    deduceData();
            }
        });
        checkoutRB = findViewById(R.id.calprp_activity_bottomRB3);
        checkoutRB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkoutRB.isChecked())
                    checkOutData();
            }
        });
        saveRB = findViewById(R.id.calprp_activity_bottomRB4);
        saveRB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(saveRB.isChecked())
                    showAndSave();
            }
        });
        bottomRadioGroup = findViewById(R.id.radioGroup2);
        bottomRadioGroup.setVisibility(View.GONE);
//        bottomRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                switch (checkedId) {
//                    case R.id.calprp_activity_bottomRB1:
//                        inputData();
//                        break;
//                    case R.id.calprp_activity_bottomRB2:
//                        deduceData();
//                        break;
//                    case R.id.calprp_activity_bottomRB3:
//                        checkOutData();
//                        break;
//                    case R.id.calprp_activity_bottomRB4:
//                        showAndSave();
//                        break;
//                }
//            }
//        });
        if(monthHasSeleted) {
            bottomRadioGroup.setVisibility(View.VISIBLE);
            inputRB.setChecked(input_b);
            deduceRB.setChecked(deduce_b);
            checkoutRB.setChecked(checkout_b);
            saveRB.setChecked(save_b);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("MonthHasSeleted",monthHasSeleted);
        outState.putBoolean("HasCheckouted",hasCheckouted);
        outState.putLong("Date",date.getTime());
        outState.putLong("RecordTime",recordTime.getTime());
        outState.putBoolean("RB1",inputRB.isChecked());
        outState.putBoolean("RB2",deduceRB.isChecked());
        outState.putBoolean("RB3",checkoutRB.isChecked());
        outState.putBoolean("RB4",saveRB.isChecked());
    }
    public void back() {
        if (LitePal.isExist(JXGZPersonDetailsTemp.class)) {
            MyDialogFragment dialogFragment = MyDialogFragment.newInstant("数据未保存，是否退出当前页面？", "否", "是");
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
        input_b = true;
        deduce_b=false;
        checkout_b=false;
        save_b = false;
        inputRB.setChecked(true);
        if (bottomRadioGroup.getVisibility() == View.GONE)
            bottomRadioGroup.setVisibility(View.VISIBLE);
        Fragment fragment2 = fragmentManager.findFragmentByTag("Input");
        Fragment currentFragment = new Fragment();
        for(Fragment fragment:fragmentManager.getFragments())
        {
            if(fragment.isVisible()){
                currentFragment = fragment;
                break;
            }
        }
        if(fragment2 == null) {
            fragment2 = new CalPRPInputDataFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.calprp_activity_framlayout,fragment2, "Input")
                    .addToBackStack(null)
                    .hide(currentFragment)
                    .show(fragment2)
                    .commit();
        }else{
            fragmentManager.beginTransaction()
                    .hide(currentFragment)
                    .show(fragment2)
                    .commit();
        }
        currentFragment = fragment2;
    }
    public void deduceData() {
        input_b = false;
        deduce_b=true;
        checkout_b=false;
        save_b = false;
        deduceRB.setChecked(true);
        if (bottomRadioGroup.getVisibility() == View.GONE)
            bottomRadioGroup.setVisibility(View.VISIBLE);
        Fragment currentFragment = new Fragment() ;
        for(Fragment fragment:fragmentManager.getFragments())
        {
            if(fragment.isVisible()){
                currentFragment = fragment;
                break;
            }
        }
        Fragment fragment2 = fragmentManager.findFragmentByTag("Deduce");
        if(fragment2==null){
            fragment2 = new CalPRPDeduceFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.calprp_activity_framlayout,fragment2, "Deduce")
                    .addToBackStack(null)
                    .commit();
        }
        fragmentManager.beginTransaction()
                .hide(currentFragment)
                .show(fragment2)
                .commit();
        currentFragment = fragment2;
    }
    public void checkOutData() {
        input_b = false;
        deduce_b=false;
        checkout_b=true;
        save_b = false;
        checkoutRB.setChecked(true);
        if (bottomRadioGroup.getVisibility() == View.GONE)
            bottomRadioGroup.setVisibility(View.VISIBLE);
        Fragment currentFragment = new Fragment() ;
        for(Fragment fragment:fragmentManager.getFragments())
        {
            if(fragment.isVisible()){
                currentFragment = fragment;
                break;
            }
        }
        Fragment fragment2 = fragmentManager.findFragmentByTag("CheckOut");
        if(fragment2==null){
            fragment2 = new CalPRPCheckoutFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.calprp_activity_framlayout,fragment2, "CheckOut")
                    .addToBackStack(null)
                    .commit();
        }
        fragmentManager.beginTransaction()
                .hide(currentFragment)
                .show(fragment2)
                .commit();
        currentFragment = fragment2;
    }
    public void showAndSave() {
        input_b = false;
        deduce_b=false;
        checkout_b=false;
        save_b = true;
        saveRB.setChecked(true);
        if (bottomRadioGroup.getVisibility() == View.GONE)
            bottomRadioGroup.setVisibility(View.VISIBLE);
        Fragment currentFragment = new Fragment() ;
        for(Fragment fragment:fragmentManager.getFragments())
        {
            if(fragment.isVisible()){
                currentFragment = fragment;
                break;
            }
        }
        Fragment fragment2 = fragmentManager.findFragmentByTag("ShowAndSave");
        if(fragment2==null){
            fragment2 = new CalPRPSaveFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.calprp_activity_framlayout,fragment2, "ShowAndSave")
                    .addToBackStack(null)
                    .commit();
        }
        fragmentManager.beginTransaction()
                .hide(currentFragment)
                .show(fragment2)
                .commit();
        currentFragment = fragment2;
    }
    public void setMonthHasSeleted(boolean monthHasSeleted) {
        this.monthHasSeleted = monthHasSeleted;
    }
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
    public boolean isHasCheckouted() {
        return hasCheckouted;
    }
    public void setHasCheckouted(boolean hasCheckouted) {
        this.hasCheckouted = hasCheckouted;
    }
    public boolean isMonthHasSeleted() {
        return monthHasSeleted;
    }
    public void showToast(String content){
        Toast toast = Toast.makeText(CalculatePRP.this,content,Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }
    private Date date;
    private Date recordTime;
    private boolean hasCheckouted;
    private boolean monthHasSeleted;
    private FragmentManager fragmentManager;
    private RadioGroup bottomRadioGroup;
    private RadioButton inputRB, deduceRB, checkoutRB, saveRB;
    private boolean input_b,deduce_b,checkout_b,save_b;
}
