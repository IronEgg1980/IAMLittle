package aqth.yzw.iamlittle;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import org.litepal.LitePal;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import aqth.yzw.iamlittle.EntityClass.JXGZDetails;
import aqth.yzw.iamlittle.EntityClass.JXGZDetailsTemp;
import aqth.yzw.iamlittle.EntityClass.JXGZPersonDetails;
import aqth.yzw.iamlittle.EntityClass.JXGZPersonDetailsTemp;

public class CalPRPSelectMonthFragment extends Fragment {
    private CalculatePRP activity;
    private DatePicker datePicker;
    private Calendar calendar;
    private SimpleDateFormat format;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        calendar = new GregorianCalendar();
        activity = (CalculatePRP)getActivity();
        activity.setDate(calendar.getTime());
        format = new SimpleDateFormat("yyyy年M月份");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calprp_selectmonth_fragment,container,false);
        datePicker = view.findViewById(R.id.calprp_selectmonth_fragment_datepicker);
        datePicker.setMaxDate(activity.getDate().getTime());
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONDAY), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(year,monthOfYear,dayOfMonth);
            }
        });
        Button confirm = view.findViewById(R.id.calprp_selectmonth_fragment_confirm);
        final Button cancel = view.findViewById(R.id.calprp_selectmonth_fragment_cancel);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LitePal.deleteAll(JXGZDetailsTemp.class);
                LitePal.deleteAll(JXGZPersonDetailsTemp.class);
                activity.setDate(calendar.getTime());
                final String[] dates = MyTool.getMonthStartAndEndString(calendar.getTime());
                if(LitePal.isExist(JXGZDetails.class,"date >= ? and date <= ?",dates[0],dates[1])){
                    MyDialogFragment dialogFragment = MyDialogFragment.newInstant("已存在【"+format.format(calendar.getTime())+"】的记录，是否重新计算？",
                            "否","是");
                    dialogFragment.setOnDialogFragmentDismiss(new OnDialogFragmentDismiss() {
                        @Override
                        public void onDissmiss(boolean flag) {
                            if (flag){
                                LitePal.deleteAll(JXGZDetails.class,"date >= ? and date <= ?",dates[0],dates[1]);
                                LitePal.deleteAll(JXGZPersonDetails.class,"date >= ? and date <= ?",dates[0],dates[1]);
                                activity.setMonthHasSeleted(true);
                                activity.inputData();
                            }else{
                                return;
                            }
                        }

                        @Override
                        public void onDissmiss(boolean flag, Object object) {

                        }
                    });
                    dialogFragment.show(getFragmentManager(),"Dialog");
                }else {
                    activity.setMonthHasSeleted(true);
                    activity.inputData();
                }

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.back();
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // 隐藏日期控件的日期列（第3个getChildAt(i)，i=0 年;i=1 月;i=2 日）
        ((ViewGroup)((ViewGroup)datePicker.getChildAt(0)).getChildAt(0)).getChildAt(2).setVisibility(View.GONE);
    }
}
