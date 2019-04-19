package aqth.yzw.iamlittle;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.litepal.LitePal;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

import aqth.yzw.iamlittle.Adapters.TodayScheduleAdapter;
import aqth.yzw.iamlittle.EntityClass.AppSetup;
import aqth.yzw.iamlittle.EntityClass.ItemEntity;
import aqth.yzw.iamlittle.EntityClass.Schedule;
import aqth.yzw.iamlittle.EntityClass.TodaySchedule;

public class MainActivity extends MyActivity {
    private String TAG = "殷宗旺";
    private RecyclerView recyclerView;
    private TodayScheduleAdapter adapter;
    private List<ItemEntity> list;
    private Calendar c;
    private AppBarLayout appBarLayout;
    private CollapsingToolbarLayout toolbarLayout;
    private SimpleDateFormat format;
    private TextView calendarTV;
    private ImageView titleIV;
    private boolean isFirstRun;
    private Button beginBT;
    private long firstTime;
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
    private void showTitleImage(){
        try{
            String[] path = getAssets().list("TitleImage");
            if(path.length > 0) {
                Random random = new Random();
                int i = random.nextInt(path.length);
                InputStream inputStream = getAssets().open("TitleImage/"+path[i]);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                Glide.with(this)
                        .load(bitmap)
                        .error(R.drawable.defaulttitleimage)
                        .into(titleIV);
                inputStream.close();
            }else{
                throw new Exception("No Image File");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private long getUnScheduledWeek(long l){
        long result = l;
        Date date = new Date(l);
        String[] dates = MyTool.getWeekStartEndString(date);
        List<Schedule> temp = LitePal.order("personname")
                .where("date >= ? and date <= ?",dates[0],dates[1]).find(Schedule.class);
        if(temp != null && temp.size() >0){
            result = result + MyTool.ONE_WEEK_MILLISECOND;
            return getUnScheduledWeek(result);
        }else {
            return result;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS |
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.main_activity);
        toolbarLayout = findViewById(R.id.toolbarLayout);
        toolbarLayout.setExpandedTitleColor(Color.WHITE);
        toolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
        final android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.icon_little);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,SetupActivity.class));
            }
        });
        c =  new GregorianCalendar();
        firstTime = c.getTimeInMillis();
        format = new SimpleDateFormat("今天是：yyyy年M月d日   EEEE");
        titleIV = findViewById(R.id.activity_main_title_image);
        calendarTV = findViewById(R.id.main_activity_canlendarTV);
        calendarTV.setText(format.format(c.getTime())+"    农历："+MyTool.getNongLi(c.getTime()));
        showTitleImage();
        findViewById(R.id.shift_manage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,ShiftManageActivity.class));
            }
        });
        findViewById(R.id.schedule_activity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // startActivity(new Intent(MainActivity.this,ScheduleActivity.class));
                startActivity(new Intent( MainActivity.this,ScheduleActivity2.class));
            }
        });
        findViewById(R.id.schedule_input_activity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long l = getUnScheduledWeek(c.getTimeInMillis());
                Intent intent = new Intent();
                intent.putExtra("IsAddMode",true);
                intent.putExtra("Date",l);
                intent.setClass(MainActivity.this,ScheduleInputEditActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.person_manage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,PersonManageActivity.class));
            }
        });
        findViewById(R.id.count_shift_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("Mode",1);
                intent.setClass(MainActivity.this, OTPActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.count_shift).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("Mode",2);
                intent.setClass(MainActivity.this, OTPActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.JXGZ).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,PRPActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.JXGZ_cal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,CalculatePRP.class);
                startActivity(intent);
            }
        });
        list = new ArrayList<>();
        recyclerView = findViewById(R.id.activity_main_recyclerview);
        adapter = new TodayScheduleAdapter(list);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        beginBT = findViewById(R.id.main_activity_title_button);
        beginBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appBarLayout.setExpanded(false);
            }
        });
        appBarLayout = findViewById(R.id.appbarlayout);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                if(i == 0){
                    toolbarLayout.setTitle(" ");
                    beginBT.setVisibility(View.VISIBLE);

                }else{
                    toolbarLayout.setTitle("小小的助手");
                    beginBT.setVisibility(View.INVISIBLE);
                }
            }
        });
        isFirstRun = !LitePal.isExist(AppSetup.class,"key = ?","isFirstRun");
        if(isFirstRun){
            AppSetup appSetup = new AppSetup();
            appSetup.setKey("isFirstRun");
            appSetup.setValue("no");
            appSetup.save();
            AppSetup firstRunTime = new AppSetup();
            firstRunTime.setKey("firstruntime");
            firstRunTime.setValue(Long.toString(c.getTimeInMillis()));
            firstRunTime.save();
        }
    }

    @Override
    public void onBackPressed() {
        Calendar calendar = new GregorianCalendar();
        long secondTime = calendar.getTimeInMillis();
        if(secondTime - firstTime < 2000){
            finish();
            System.exit(0);
        }else{
            Toast.makeText(MainActivity.this,"再按一次退出程序",Toast.LENGTH_SHORT).show();
            firstTime = secondTime;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTodayList();
        adapter.notifyDataSetChanged();
    }
}
