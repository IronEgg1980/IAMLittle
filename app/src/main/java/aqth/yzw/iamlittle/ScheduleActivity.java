package aqth.yzw.iamlittle;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.LitePal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import aqth.yzw.iamlittle.Adapters.ScheduleShowAdapter;
import aqth.yzw.iamlittle.EntityClass.ItemEntity;
import aqth.yzw.iamlittle.EntityClass.ItemEntityScheduleInput;
import aqth.yzw.iamlittle.EntityClass.Schedule;

public class ScheduleActivity extends AppCompatActivity {
    private TextView[] dateTVs;
    private Date[] dates;
    private Date today;
    private SimpleDateFormat format1,simpleDateFormat;
    private Calendar c;
    private boolean showNongLi;
    private List<ItemEntity> list;
    private RecyclerView recyclerView;
    private ScheduleShowAdapter adapter;
    private SharedPreferences preference;
    private String nongli;
    private TextView title;
    private CheckBox showNongLiCB;

    private void updateList(){
        if(list == null)
            list = new ArrayList<>();
        list.clear();
        String[] dates = MyTool.getWeekStartEndString(c.getTime());
        List<Schedule> temp = LitePal.order("personname")
                .where("date >= ? and date <= ?",dates[0],dates[1]).find(Schedule.class);
        List<String> person = new ArrayList<>();
        String currentPerson = "";
        for(int i = 0;i<temp.size();i++){
            if(i == 0) {
                currentPerson = temp.get(0).getPersonName();
                person.add(currentPerson);
            }else{
                String s = temp.get(i).getPersonName();
                if(!s.equals(currentPerson)){
                    currentPerson = s;
                    person.add(currentPerson);
                }
            }
        }
        for(String s:person){
            List<Schedule> schedules = LitePal.order("date")
                    .where("personname = ? and date >= ? and date <= ?",s,dates[0],dates[1]).find(Schedule.class);
            ItemEntityScheduleInput item = new ItemEntityScheduleInput();
            item.setValues(0,s);
            String note = "";
            for(int i = 1;i<=schedules.size();i++){
                Schedule schedule = schedules.get(i-1);
                item.setValues(i,schedule.getShiftName());
                note = schedule.getNote();
            }
            item.setValues(8,note);
            list.add(item);
        }

        if(list.size() == 0)
            list.add(new ItemEntity());
        adapter.notifyDataSetChanged();
        recyclerView.smoothScrollToPosition(0);
    }
    private void thisWeek(){
        c = new GregorianCalendar();
        today = c.getTime();
        changeTitleText();
        updateList();
    }
    private void nextWeek(){
        c.add(Calendar.DAY_OF_MONTH,7);
        changeTitleText();
        updateList();
    }
    private void preWeek(){
        c.add(Calendar.DAY_OF_MONTH,-7);
        changeTitleText();
        updateList();
    }
    private void selectDate(){
        changeTitleText();
        updateList();
    }
    private void changeTitleText(){
        dates = MyTool.getAWeekDates(c.getTime());
        c.setTime(dates[0]);
        for(int i = 0;i<7;i++){
            dateTVs[i].setTextSize(11);
            if(showNongLi) {
                String nongli = MyTool.getNongLiNoYear(dates[i]);
                dateTVs[i].setText(format1.format(dates[i]) + "\n" + nongli);
            }else {
                dateTVs[i].setText(format1.format(dates[i]));
            }
            if(simpleDateFormat.format(today).equals(simpleDateFormat.format(dates[i]))){
                dateTVs[i].setText("今天");
                dateTVs[i].setTextSize(14);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000 && resultCode == 1001){
            boolean b = data.getBooleanExtra("IsSaved",false);
            if (b){
                Toast.makeText(ScheduleActivity.this,"保存成功",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(ScheduleActivity.this,"保存失败",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        preference = PreferenceManager.getDefaultSharedPreferences(ScheduleActivity.this);
        showNongLi = preference.getBoolean("ShowNongLi",false);
        showNongLiCB = findViewById(R.id.schedule_activity_showNongLiCheckBox);
        showNongLiCB.setChecked(showNongLi);
        showNongLiCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = preference.edit();
                editor.putBoolean("ShowNongLi",isChecked);
                editor.apply();
                showNongLi = isChecked;
                changeTitleText();
            }
        });
        dateTVs = new TextView[7];
        format1 = new SimpleDateFormat("M月d日");
        simpleDateFormat = new SimpleDateFormat("yyyy年M月d日  ");
        dateTVs[0] = findViewById(R.id.schedule_item_title_date1);
        dateTVs[1] = findViewById(R.id.schedule_item_title_date2);
        dateTVs[2] = findViewById(R.id.schedule_item_title_date3);
        dateTVs[3] = findViewById(R.id.schedule_item_title_date4);
        dateTVs[4] = findViewById(R.id.schedule_item_title_date5);
        dateTVs[5] = findViewById(R.id.schedule_item_title_date6);
        dateTVs[6] = findViewById(R.id.schedule_item_title_date7);
        title = findViewById(R.id.schedule_activity_title);
        nongli = "";
        findViewById(R.id.schedule_activity_thisWeek).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thisWeek();
            }
        });
        findViewById(R.id.schedule_activity_preWeek).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preWeek();
            }
        });
        findViewById(R.id.schedule_activity_nextWeek).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextWeek();
            }
        });

        findViewById(R.id.schedule_activity_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        list = new ArrayList<>();
        recyclerView = findViewById(R.id.schedule_show_recyclerview);
        adapter = new ScheduleShowAdapter(this,list);
        adapter.setItemClickListener(new IItemClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onClick(View view, int x, int y) {
                if(y == 8){
                    ItemEntityScheduleInput input =(ItemEntityScheduleInput)list.get(x);
                    PopupWindow popupWindow = new PopupWindow(view,480,RelativeLayout.LayoutParams.WRAP_CONTENT);
                    popupWindow.setBackgroundDrawable(new ColorDrawable());
                    popupWindow.setOutsideTouchable(true);
                    View v = LayoutInflater.from(view.getContext()).inflate(R.layout.popwindow_layout,null);
                    TextView textView = v.findViewById(R.id.popwindow_textview);
                    textView.setText(input.getValues(y));
                    popupWindow.setContentView(v);
                    int[] local = new int[2];
                    view.getLocationOnScreen(local);
                    popupWindow.showAtLocation(view,Gravity.NO_GRAVITY,local[0],local[1]);
                }
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        thisWeek();
        nongli = MyTool.getNongLi(today);
        title.setText("今天是："+simpleDateFormat.format(today)+nongli);
        findViewById(R.id.schedule_activity_selectDate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectDateDialogFragment fragment = SelectDateDialogFragment.newInstant(c.getTimeInMillis());
                fragment.setOnDialogFragmentDismiss(new OnDialogFragmentDismiss() {
                    @Override
                    public void onDissmiss(boolean flag) {

                    }

                    @Override
                    public void onDissmiss(boolean flag, Object object) {
                        if (flag){
                            long l = (long)object;
                            c.setTimeInMillis(l);
                            selectDate();
                        }
                    }
                });
                fragment.show(getSupportFragmentManager(),"SelectDate");
            }
        });
        findViewById(R.id.schedule_activity_addschedule).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("IsAddMode",true);
                intent.putExtra("Date",c.getTimeInMillis());
                intent.setClass(ScheduleActivity.this,ScheduleInputEditActivity.class);
                startActivityForResult(intent,1000);
            }
        });

    }
}
