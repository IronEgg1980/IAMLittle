package aqth.yzw.iamlittle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.LitePal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aqth.yzw.iamlittle.Adapters.PersonShiftInputAdapter;
import aqth.yzw.iamlittle.Adapters.ScheduleShowAdapter;
import aqth.yzw.iamlittle.EntityClass.ItemEntity;
import aqth.yzw.iamlittle.EntityClass.ItemEntityPerson;
import aqth.yzw.iamlittle.EntityClass.ItemEntityScheduleInput;
import aqth.yzw.iamlittle.EntityClass.ItemEntityScheduleTemplate;
import aqth.yzw.iamlittle.EntityClass.ItemEntityShift;
import aqth.yzw.iamlittle.EntityClass.Person;
import aqth.yzw.iamlittle.EntityClass.Schedule;
import aqth.yzw.iamlittle.EntityClass.ScheduleTemplate;
import aqth.yzw.iamlittle.EntityClass.Shift;

public class ScheduleInputEditActivity extends AppCompatActivity {

    private TextView[] dateTVs;
    private Date[] dates;
    private Date today;
    private SimpleDateFormat format1,simpleDateFormat;
    private Calendar c;
    private boolean showNongLi,isAddMode;
    private List<ItemEntity> list;
    private RecyclerView recyclerView,bottomRecyclerView;
    private ScheduleShowAdapter adapter;
    private SharedPreferences preference;
    private CheckBox showNongLiCB;
    private PersonShiftInputAdapter personAdapter,shiftAdapter;
    private List<ItemEntity> people,shifts;
    private int mX,mY;
    private AppBarLayout appBarLayout;
    private void fillPersonList(){
        if(people == null)
            people = new ArrayList<>();
        people.clear();
        List<Person> temp = LitePal.order("Status").find(Person.class);
        for(Person p:temp){
            boolean notExist = true;
            String name = p.getName();
            for(int i = 0;i<list.size() -1;i++){
                ItemEntityScheduleInput input = (ItemEntityScheduleInput)list.get(i);
                if(name.equals(input.getValues(0))) {
                    notExist = false;
                    break;
                }
            }
            if(notExist)
                people.add(new ItemEntityPerson(p));
        }
    }
    private void fillShiftList(){
        if(shifts == null)
            shifts=new ArrayList<>();
        List<Shift> temp = LitePal.findAll(Shift.class);
        for(Shift shift:temp){
            shifts.add(new ItemEntityShift(shift));
        }
    }
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
        final long l = intent.getLongExtra("Date",c.getTimeInMillis());
        c.setTimeInMillis(l);
        list = new ArrayList<>();
        people = new ArrayList<>();
        shifts = new ArrayList<>();
        appBarLayout = findViewById(R.id.activity_schedule_input_edit_appbar);
        bottomRecyclerView = findViewById(R.id.schedule_inputedit_activity_recyclerview2);
        LinearLayoutManager manager = new LinearLayoutManager(ScheduleInputEditActivity.this,LinearLayoutManager.HORIZONTAL,false);
        bottomRecyclerView.setLayoutManager(manager);
        personAdapter = new PersonShiftInputAdapter(ScheduleInputEditActivity.this,people);
        personAdapter.setItemClickListener(new IItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                if(mX <0 || mY != 0){
                    return;
                }
                if(mY == 0) {
                    String name = (String) view.getTag(R.id.tag_text);
                    ItemEntityPerson itemEntityPerson = (ItemEntityPerson) view.getTag(R.id.tag_itemEntityPerson);
                    ItemEntityScheduleInput input = (ItemEntityScheduleInput) list.get(mX);
                    input.setValues(mY,name);
                    adapter.notifyItemChanged(mX);
                    if(mX < adapter.getItemCount()-2) {
                        mX = mX + 1;
                        adapter.setCurrentCell(mX,0);
                        adapter.notifyItemChanged(mX);
                    }
                    fillPersonList();
                    personAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onClick(View view, int x, int y) {


            }
        });
        shiftAdapter = new PersonShiftInputAdapter(ScheduleInputEditActivity.this,shifts);
        shiftAdapter.setItemClickListener(new IItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                if(mX < 0|| mY<1 ||mY == 8)
                    return;
                if(mY > 0 && mY <8){
                    int old = mX;
                    String name = (String)view.getTag();
                    ItemEntityScheduleInput input = (ItemEntityScheduleInput) list.get(mX);
                    input.setValues(mY,name);
                    if(mY< 7 ) {
                        mY = mY + 1;
                    }else if(mX <adapter.getItemCount() -2){
                        mY = 1;
                        mX = mX+1;
                        adapter.notifyItemChanged(old);
                    }
                    adapter.setCurrentCell(mX,mY);
                    adapter.notifyItemChanged(mX);
                }
            }

            @Override
            public void onClick(View view, int x, int y) {

            }
        });
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
        findViewById(R.id.schedule_inputedit_activity_template).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(ScheduleInputEditActivity.this,v);
                Menu menu = popupMenu.getMenu();
                menu.add(0,1,0,"调取模板");
                menu.add(0,2,0,"存为模板");
                menu.add(0,3,0,"删除模板");
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case 1:
                                //调取模板
                                SingleSelectTemplateDialogFragment dialogFragment = new SingleSelectTemplateDialogFragment();
                                dialogFragment.setOnDialogFragmentDismiss(new OnDialogFragmentDismiss() {
                                    @Override
                                    public void onDissmiss(boolean flag) {

                                    }

                                    @Override
                                    public void onDissmiss(boolean flag, Object object) {
                                        if(flag)
                                            Toast.makeText(ScheduleInputEditActivity.this,(String)object,Toast.LENGTH_SHORT).show();
                                    }
                                });
                                dialogFragment.show(getSupportFragmentManager(),"LoadTemplate");
                                break;
                            case 2:
                                //存为模板模板
                                MyDialogFragmentInput myDialogFragmentInput = MyDialogFragmentInput.newInstant("",true);
                                myDialogFragmentInput.setOnDialogFragmentDismiss(new OnDialogFragmentDismiss() {
                                    @Override
                                    public void onDissmiss(boolean flag) {

                                    }

                                    @Override
                                    public void onDissmiss(boolean flag, Object object) {
                                        if(flag){
                                            ScheduleTemplate template = new ScheduleTemplate();
                                            template.setName((String)object);
                                            template.save();
                                            Toast.makeText(ScheduleInputEditActivity.this,"已保存【"+(String)object+"】",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                myDialogFragmentInput.show(getSupportFragmentManager(),"InputTemplateNae");
                                break;
                            case 3:
                                //删除模板
                                DeleteTemplateDialogFragment fragment = new DeleteTemplateDialogFragment();
                                fragment.show(getSupportFragmentManager(),"DeleteTemplate");
                                break;
                        }
                        return true;
                    }
                });
                popupMenu.show();
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
            public void onClick(View view, final int x, int y) {
                int position = adapter.getItemCount() -1;
                if(x == position){
                    ItemEntityScheduleInput input = new ItemEntityScheduleInput();
                    list.add(position,input);
                    adapter.setCurrentCell(position,0);
                    adapter.notifyItemInserted(position);
                    if(mX > -1){
                        adapter.notifyItemChanged(mX);
                    }
                    adapter.notifyItemRangeChanged(position,2);
                    recyclerView.smoothScrollToPosition(position+1);
                    mY = 0;
                    fillPersonList();
                    bottomRecyclerView.setAdapter(personAdapter);
                    appBarLayout.setExpanded(false);
                }else{
                    if(y == 8){
                        final ItemEntityScheduleInput input = (ItemEntityScheduleInput)list.get(x);
                        String note = input.getValues(y);
                        MyDialogFragmentInput fragmentInput = MyDialogFragmentInput.newInstant(note);
                        fragmentInput.setOnDialogFragmentDismiss(new OnDialogFragmentDismiss() {
                            @Override
                            public void onDissmiss(boolean flag) {

                            }

                            @Override
                            public void onDissmiss(boolean flag, Object object) {
                                if(flag){
                                    input.setValues(8,(String)object);
                                    adapter.notifyItemChanged(x);
                                }
                            }
                        });
                        fragmentInput.show(getSupportFragmentManager(),"InputNote");
                    }else if(y == 0){
                        fillPersonList();
                        bottomRecyclerView.setAdapter(personAdapter);
                    }else{
                        bottomRecyclerView.setAdapter(shiftAdapter);
                    }
                    mY = y;
                }
                mX = x;
            }
        });
        adapter.setItemLongClickListener(new IItemClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onClick(View view, final int x, final int y) {
                if(list.size() == 1 || x == list.size() -1)
                    return;
                final PopupWindow popupWindow = new PopupWindow(view,LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                popupWindow.setBackgroundDrawable(new ColorDrawable());
                popupWindow.setOutsideTouchable(true);
                popupWindow.setTouchable(true);
                View v = LayoutInflater.from(view.getContext()).inflate(R.layout.schedule_input_edit_popwindow,null);
                Button clear = v.findViewById(R.id.schedule_inputedit_popwindow_clear);
                Button delLine = v.findViewById(R.id.schedule_inputedit_popwindow_delLine);
                clear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ItemEntityScheduleInput input = (ItemEntityScheduleInput)list.get(x);
                        input.setValues(y,"");
                        adapter.notifyItemChanged(x);
                        popupWindow.dismiss();
                        fillPersonList();
                        personAdapter.notifyDataSetChanged();
                    }
                });
                delLine.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        list.remove(x);
                        adapter.notifyItemRemoved(x);
                        adapter.notifyItemRangeChanged(x,adapter.getItemCount()-x);
                        popupWindow.dismiss();
                        fillPersonList();
                        personAdapter.notifyDataSetChanged();
                    }
                });
                popupWindow.setContentView(v);
                int[] local = new int[2];
                view.getLocationOnScreen(local);
                popupWindow.showAtLocation(view,Gravity.NO_GRAVITY,local[0],local[1]);
                mX = x;
                mY = y;
                if(y == 0) {
                    bottomRecyclerView.setAdapter(personAdapter);
                }
                if(y >0 && y< 8)
                    bottomRecyclerView.setAdapter(shiftAdapter);
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(),DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(ScheduleInputEditActivity.this));
        updateList();
        fillShiftList();
        changeTitleText();
    }
}
