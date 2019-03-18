package aqth.yzw.iamlittle;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import org.litepal.LitePal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Stack;

import aqth.yzw.iamlittle.Adapters.TodayScheduleAdapter;
import aqth.yzw.iamlittle.EntityClass.ItemEntity;
import aqth.yzw.iamlittle.EntityClass.ItemEntityShift;
import aqth.yzw.iamlittle.EntityClass.Schedule;
import aqth.yzw.iamlittle.EntityClass.TodaySchedule;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TodayScheduleAdapter adapter;
    private List<ItemEntity> list;
    private Calendar c;
    private SimpleDateFormat yearMonthFt,weekFt,dayFt;
    private TextView yearMonthTV,dayTV,weekTV,nongliTV;
    private ImageView titleIV;
    private void showDate(){
        yearMonthTV.setText(yearMonthFt.format(c.getTime()));
        if(c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            dayTV.setTextColor(Color.RED);
            weekTV.setTextColor(Color.RED);
        }else if(c.get(Calendar.DAY_OF_WEEK)== Calendar.SATURDAY){
            dayTV.setTextColor(Color.GREEN);
            weekTV.setTextColor(Color.GREEN);
        }else {
            dayTV.setTextColor(Color.WHITE);
            weekTV.setTextColor(Color.WHITE);
        }
        dayTV.setText(dayFt.format(c.getTime()));
        weekTV.setText(weekFt.format(c.getTime()));
        nongliTV.setText(MyTool.getNongLi(c.getTime()));
    }
    private void updateTodayList(){
        if(list == null){
            list = new ArrayList<>();
        }
        list.clear();
        List<String> shifts = new ArrayList<>();
        c =  new GregorianCalendar();
        String[] dates = MyTool.getDayStartEndString(c.getTime());
        List<Schedule> temp = LitePal.order("shiftName").where("date >= ? and date <= ?",
                dates[0],dates[1]).find(Schedule.class);
        if(temp.size() > 0) {
            String pre = temp.get(0).getShiftName();
            shifts.add(pre);
            for (int i = 1; i < temp.size(); i++) {
                String current = temp.get(i).getShiftName();
                if (!pre.equals(current)) {
                    pre = current;
                    shifts.add(pre);
                }
            }
        }
        for(String s :shifts){
            List<Schedule> temp1 =  LitePal.where("date >= ? and date <= ? and shiftName = ?",
                    dates[0],dates[1],s).find(Schedule.class);
            String person = "";
            for(Schedule schedule : temp1){
                person += schedule.getPersonName()+"、";
            }
            if(!"".equals(person)){
                person = person.substring(0,person.length()-1);
            }
            TodaySchedule todaySchedule = new TodaySchedule();
            todaySchedule.setShfit(s);
            todaySchedule.getPerson().add(person);
            list.add(todaySchedule);
        }
        if(list.size() == 0)
            list.add(new ItemEntity());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        CollapsingToolbarLayout toolbarLayout = findViewById(R.id.toolbarLayout);
        toolbarLayout.setExpandedTitleColor(Color.WHITE);
        toolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
        toolbarLayout.setTitle("一个小小的助手");
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_person_indigo_600_24dp);
        c =  new GregorianCalendar();
        yearMonthFt = new SimpleDateFormat("yyyy年M月");
        dayFt = new SimpleDateFormat("d");
        weekFt = new SimpleDateFormat("EEEE");
        yearMonthTV = findViewById(R.id.activity_main_yearmonthTV);
        dayTV =findViewById(R.id.activity_main_dayTV);
        weekTV = findViewById(R.id.activity_main_weekTV);
        nongliTV = findViewById(R.id.activity_main_nongliTV);
        titleIV = findViewById(R.id.activity_main_title_image);
        titleIV.setImageDrawable(getDrawable(R.drawable.person_image));
        showDate();
        findViewById(R.id.shift_manage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,ShiftManageActivity.class));
            }
        });
        findViewById(R.id.schedule_activity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,ScheduleActivity.class));
            }
        });
        findViewById(R.id.person_manage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,PersonManageActivity.class));
            }
        });
        list = new ArrayList<>();
        recyclerView = findViewById(R.id.activity_main_recyclerview);
        adapter = new TodayScheduleAdapter(list);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView.addItemDecoration(new DividerItemDecoration(MainActivity.this,RecyclerView.VERTICAL));
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTodayList();
        adapter.notifyDataSetChanged();
    }
}
