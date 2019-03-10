package aqth.yzw.iamlittle;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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
    private SimpleDateFormat format1;
    private Calendar c;
    private boolean showNontLi;
    private List<ItemEntity> list;
    private RecyclerView recyclerView;
    private ScheduleShowAdapter adapter;
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
    }
    private void thisWeek(){
        c = new GregorianCalendar();
        today = c.getTime();
        dates = MyTool.getAWeekDates(c.getTime());
        c.setTime(dates[0]);
        for(int i = 0;i<7;i++){
            String nongli = MyTool.getNongLiNoYear(dates[i]);
            dateTVs[i].setTextSize(11);
            if(showNontLi)
                dateTVs[i].setText(format1.format(dates[i])+"\n"+nongli);
            else
                dateTVs[i].setText(format1.format(dates[i]));
            if(format1.format(today).equals(format1.format(dates[i]))){
                dateTVs[i].setText("今天");
                dateTVs[i].setTextSize(14);
            }
        }
        updateList();
        adapter.notifyDataSetChanged();
        recyclerView.smoothScrollToPosition(0);
    }
    private void nextWeek(){
        c.add(Calendar.DAY_OF_MONTH,7);
        dates = MyTool.getAWeekDates(c.getTime());
        c.setTime(dates[0]);
        for(int i = 0;i<7;i++){
            String nongli = MyTool.getNongLiNoYear(dates[i]);
            dateTVs[i].setTextSize(11);
            if(showNontLi)
                dateTVs[i].setText(format1.format(dates[i])+"\n"+nongli);
            else
                dateTVs[i].setText(format1.format(dates[i]));
        }
        updateList();
        adapter.notifyDataSetChanged();
        recyclerView.smoothScrollToPosition(0);
    }
    private void preWeek(){
        c.add(Calendar.DAY_OF_MONTH,-7);
        dates = MyTool.getAWeekDates(c.getTime());
        c.setTime(dates[0]);
        for(int i = 0;i<7;i++){
            String nongli = MyTool.getNongLiNoYear(dates[i]);
            dateTVs[i].setTextSize(11);
            if(showNontLi)
                dateTVs[i].setText(format1.format(dates[i])+"\n"+nongli);
            else
                dateTVs[i].setText(format1.format(dates[i]));
        }
        updateList();
        adapter.notifyDataSetChanged();
        recyclerView.smoothScrollToPosition(0);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        showNontLi = true;
        dateTVs = new TextView[7];
        format1 = new SimpleDateFormat("MM月dd日");
        dateTVs[0] = findViewById(R.id.schedule_item_title_date1);
        dateTVs[1] = findViewById(R.id.schedule_item_title_date2);
        dateTVs[2] = findViewById(R.id.schedule_item_title_date3);
        dateTVs[3] = findViewById(R.id.schedule_item_title_date4);
        dateTVs[4] = findViewById(R.id.schedule_item_title_date5);
        dateTVs[5] = findViewById(R.id.schedule_item_title_date6);
        dateTVs[6] = findViewById(R.id.schedule_item_title_date7);
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
        thisWeek();
        updateList();
        recyclerView = findViewById(R.id.schedule_show_recyclerview);
        adapter = new ScheduleShowAdapter(list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}
