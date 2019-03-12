package aqth.yzw.iamlittle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

public class ScheduleInputEditActivity extends AppCompatActivity {

    private TextView[] dateTVs;
    private Date[] dates;
    private Date today;
    private SimpleDateFormat format1,simpleDateFormat;
    private Calendar c;
    private boolean showNongLi,isAddMode;
    private List<ItemEntity> list;
    private RecyclerView recyclerView;
    private ScheduleShowAdapter adapter;
    private SharedPreferences preference;
    private CheckBox showNongLiCB;
    private int mX,mY;
    private void updateList(){
        if(list == null)
            list = new ArrayList<>();
        list.clear();
        if(!isAddMode){
            String[] dates = MyTool.getWeekStartEndString(c.getTime());
            List<Schedule> temp = LitePal.order("personname")
                    .where("date >= ? and date <= ?", dates[0], dates[1]).find(Schedule.class);
            List<String> person = new ArrayList<>();
            String currentPerson = "";
            for (int i = 0; i < temp.size(); i++) {
                if (i == 0) {
                    currentPerson = temp.get(0).getPersonName();
                    person.add(currentPerson);
                } else {
                    String s = temp.get(i).getPersonName();
                    if (!s.equals(currentPerson)) {
                        currentPerson = s;
                        person.add(currentPerson);
                    }
                }
            }
            for (String s : person) {
                List<Schedule> schedules = LitePal.order("date")
                        .where("personname = ? and date >= ? and date <= ?", s, dates[0], dates[1]).find(Schedule.class);
                ItemEntityScheduleInput item = new ItemEntityScheduleInput();
                item.setValues(0, s);
                String note = "";
                for (int i = 1; i <= schedules.size(); i++) {
                    Schedule schedule = schedules.get(i - 1);
                    item.setValues(i, schedule.getShiftName());
                    note = schedule.getNote();
                }
                item.setValues(8, note);
                list.add(item);
            }
        }
        list.add(new ItemEntity());
        adapter.notifyDataSetChanged();
        recyclerView.smoothScrollToPosition(0);
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
    private boolean saveData(){

        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_input_edit);
        mX = -1;
        mY = -1;
        final Intent intent = getIntent();
        isAddMode = intent.getBooleanExtra("IsAddMode",true);
        c = new GregorianCalendar();
        today = c.getTime();
        long l = intent.getLongExtra("Date",c.getTimeInMillis());
        c.setTimeInMillis(l);
        list = new ArrayList<>();
        preference = PreferenceManager.getDefaultSharedPreferences(ScheduleInputEditActivity.this);
        showNongLi = preference.getBoolean("ShowNongLi",false);
        showNongLiCB = findViewById(R.id.schedule_inputedit_activity_showNongLiCheckBox);
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
        findViewById(R.id.schedule_inputedit_activity_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent result = new Intent();
                result.putExtra("IsSaved",false);
                setResult(1001,result);
                finish();
            }
        });
        findViewById(R.id.schedule_inputedit_activity_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //保存数据
                if(saveData()) {
                    Intent result = new Intent();
                    result.putExtra("IsSaved", true);
                    setResult(1001, result);
                    finish();
                }else{
                    Toast toast = Toast.makeText(ScheduleInputEditActivity.this,"保存失败，请重试一次！",Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                }
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
        recyclerView = findViewById(R.id.schedule_inputedit_activity_recyclerview);
        adapter = new ScheduleShowAdapter(ScheduleInputEditActivity.this,list,true);
        adapter.setItemClickListener(new IItemClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onClick(View view, int x, int y) {
                mX = x;
                mY = y;
                int position = adapter.getItemCount() -1;
                if(x == position){
                    ItemEntityScheduleInput input = new ItemEntityScheduleInput();
                    list.add(position,input);
                    adapter.setCurrentCell(position,0);
                    adapter.notifyDataSetChanged();
                    recyclerView.smoothScrollToPosition(position+1);
                }else{

                }
            }
        });
        adapter.setItemLongClickListener(new IItemClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onClick(View view, int x, int y) {
                mX = x;
                mY = y;

            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(),DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(ScheduleInputEditActivity.this));
        updateList();
        changeTitleText();
    }
}
