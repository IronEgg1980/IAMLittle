package aqth.yzw.iamlittle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
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
import android.widget.ProgressBar;
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
    private TextView bottomTV;
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
    private void setBottomRecyclerViewAdapter(int adapterMode){
        if(adapterMode == 1){
            bottomTV.setText("未排班\n人员");
            bottomRecyclerView.setAdapter(personAdapter);
            fillPersonList();
            personAdapter.notifyDataSetChanged();
        }else{
            bottomTV.setText("班次\n列表");
            bottomRecyclerView.setAdapter(shiftAdapter);
        }
    }
    private void showTemplateMenu(View v){
        PopupMenu popupMenu = new PopupMenu(ScheduleInputEditActivity.this,v);
        Menu menu = popupMenu.getMenu();
        menu.add(0,1,0,"读取模板");
        menu.add(0,2,0,"存为模板");
        menu.add(0,3,0,"删除模板");
        menu.add(1,4,1 ,"读取上周排班");
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case 1:
                        //调取模板
                        loadTemplate();
                        break;
                    case 2:
                        //存为模板模板
                        saveTemplate();
                        break;
                    case 3:
                        //删除模板
                        DeleteTemplateDialogFragment fragment = new DeleteTemplateDialogFragment();
                        fragment.show(getSupportFragmentManager(),"DeleteTemplate");
                        break;
                    case 4:
                        // 读取上周
                        Calendar calendar = new GregorianCalendar();
                        calendar.setTimeInMillis(c.getTimeInMillis());
                        calendar.add(Calendar.DAY_OF_MONTH,-7);
                        updateList(MyTool.getWeekStartEndString(calendar.getTime()),true);
                        break;
                }
                return true;
            }
        });
        popupMenu.show();
    }
    private void showSettingMenu(View v){
        PopupMenu popupMenu = new PopupMenu(v.getContext(),v);
        Menu menu = popupMenu.getMenu();
        menu.add(0,1,0,"增加人员");
        menu.add(0,2,0,"增加班次");
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case 1:
                        InputEditPersonInfoFragment fragment = new InputEditPersonInfoFragment();
                        fragment.setOnDialogFragmentDismiss(new OnDialogFragmentDismiss() {
                            @Override
                            public void onDissmiss(boolean flag) {
                                if(flag){
                                    appBarLayout.setExpanded(false);
                                    fillPersonList();
                                    personAdapter.notifyDataSetChanged();
                                }
                            }
                            @Override
                            public void onDissmiss(boolean flag, Object o) {

                            }
                        });
                        fragment.show(getSupportFragmentManager(),"AddPerson");
                        break;
                    case 2:
                        InputEditShiftFragmen fragmen = new InputEditShiftFragmen();
                        fragmen.setOnDialogFragmentDismiss(new OnDialogFragmentDismiss() {
                            @Override
                            public void onDissmiss(boolean flag) {
                                if (flag){
                                    appBarLayout.setExpanded(false);
                                    fillShiftList();
                                    shiftAdapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onDissmiss(boolean flag, Object o) {

                            }
                        });
                        fragmen.show(getSupportFragmentManager(),"AddShift");
                        break;
                }
                return true;
            }
        });
        popupMenu.show();
    }
    private void saveTemplate(){
        if(list.size() == 1){
            Toast toast = Toast.makeText(recyclerView.getContext(),"没有排班数据，无法保存模板！",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            return;
        }
        MyDialogFragmentInput myDialogFragmentInput = MyDialogFragmentInput.newInstant("",true);
        myDialogFragmentInput.setOnDialogFragmentDismiss(new OnDialogFragmentDismiss() {
            @Override
            public void onDissmiss(boolean flag) {

            }

            @Override
            public void onDissmiss(boolean flag, Object object) {
                if(flag){
                    for(int i = 0;i<list.size() -1;i++){
                        ItemEntity itemEntity = list.get(i);
                        if(itemEntity.getType() == ItemType.SCHEDULE_WEEK_VIEW){
                            ScheduleTemplate template = new ScheduleTemplate();
                            template.setName((String)object);
                            template.setRowNumber(i+1);
                            ItemEntityScheduleInput input = (ItemEntityScheduleInput)itemEntity;
                            template.setPersonName(input.getValues(0));
                            template.setShift1(input.getValues(1));
                            template.setShift2(input.getValues(2));
                            template.setShift3(input.getValues(3));
                            template.setShift4(input.getValues(4));
                            template.setShift5(input.getValues(5));
                            template.setShift6(input.getValues(6));
                            template.setShift7(input.getValues(7));
                            template.setNote(input.getValues(8));
                            template.save();
                        }
                    }
                    Toast.makeText(ScheduleInputEditActivity.this,"已保存【"+(String)object+"】",Toast.LENGTH_SHORT).show();
                }
            }
        });
        myDialogFragmentInput.show(getSupportFragmentManager(),"InputTemplateNae");
    }
    private void loadTemplate(){
        SingleSelectTemplateDialogFragment dialogFragment = new SingleSelectTemplateDialogFragment();
        dialogFragment.setOnDialogFragmentDismiss(new OnDialogFragmentDismiss() {
            @Override
            public void onDissmiss(boolean flag) {

            }

            @Override
            public void onDissmiss(boolean flag, Object object) {
                if(flag){
                    if(list == null)
                        list = new ArrayList<>();
                    list.clear();
                    String templateName = (String)object;
                    List<ScheduleTemplate> temp = LitePal.order("rownumber").where("name = ?",templateName).find(ScheduleTemplate.class);
                    for(ScheduleTemplate template:temp){
                        ItemEntityScheduleInput input = new ItemEntityScheduleInput();
                        input.setValues(0,template.getPersonName());
                        input.setValues(1,template.getShift1());
                        input.setValues(2,template.getShift2());
                        input.setValues(3,template.getShift3());
                        input.setValues(4,template.getShift4());
                        input.setValues(5,template.getShift5());
                        input.setValues(6,template.getShift6());
                        input.setValues(7,template.getShift7());
                        input.setValues(8,template.getNote());
                        list.add(input);
                    }
                    list.add(new ItemEntity());
                    adapter.notifyDataSetChanged();
                    recyclerView.smoothScrollToPosition(0);
                    adapter.setCurrentCell(0,0);
                    mX = 0;
                    mY = 0;
                    setBottomRecyclerViewAdapter(1);
                }
            }
        });
        dialogFragment.show(getSupportFragmentManager(),"LoadTemplate");
    }
    private void saveData(){
        if(list.size() == 1){
            Toast toast = Toast.makeText(recyclerView.getContext(),"请先排班再保存！",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            return;
        }
        final List<Integer> temp = new ArrayList<>();
        for (int k = 0;k<list.size();k++) {
            ItemEntity itemEntity = list.get(k);
            if (itemEntity.getType() == ItemType.SCHEDULE_WEEK_VIEW) {
                ItemEntityScheduleInput input = (ItemEntityScheduleInput) itemEntity;
                String personName = input.getValues(0);
                if(TextUtils.isEmpty(personName)){
                    temp.add(k);
                }
            }
        }
        final int _count = temp.size();
        String s = "";
        for(Integer i : temp){
            s+=(i+1)+"、";
        }
        if(_count > 0) {
            s = s.substring(0, s.length() - 1);
            String s1 = s.length()==1?"这行数据？":"这"+temp.size()+"行数据？";
            MyDialogFragment fragment = MyDialogFragment.newInstant("检测到第" + s + "行未输入姓名，请确认是否放弃保存"+s1,
                    "取消", "确认");
            fragment.setOnDialogFragmentDismiss(new OnDialogFragmentDismiss() {
                @Override
                public void onDissmiss(boolean flag) {
                    if (flag) {
                        for(int i = _count-1;i>=0;i--){
                            int k = temp.get(i);
                            list.remove(k);
                        }
                        adapter.notifyDataSetChanged();
                        if(list.size() == 1){
                            Toast toast = Toast.makeText(recyclerView.getContext(),"已清空数据，无需保存，请继续排班！",Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER,0,0);
                            toast.show();
                        }else{
                            SaveData saveData = new SaveData();
                            saveData.execute();
                        }
                    }else{
                        adapter.setCurrentCell(temp.get(0), 0);
                        mY = 0;
                        adapter.notifyItemChanged(mX);
                        if(mX!=temp.get(0)) {
                            mX = temp.get(0);
                            adapter.notifyItemChanged(mX);
                        }
                        bottomRecyclerView.setAdapter(personAdapter);
                        appBarLayout.setExpanded(false);
                    }
                }
                @Override
                public void onDissmiss(boolean flag, Object object) {

                }
            });
            fragment.show(getSupportFragmentManager(),"SaveData");
        }else {
            SaveData saveData = new SaveData();
            saveData.execute();
        }
    }
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
        shifts.clear();
        List<Shift> temp = LitePal.findAll(Shift.class);
        for(Shift shift:temp){
            shifts.add(new ItemEntityShift(shift));
        }
    }
    private void updateList(String[] dates,boolean _flag){
        if(list == null)
            list = new ArrayList<>();
        list.clear();
        if(_flag){
            List<Schedule> temp = LitePal.order("rownumber")
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
                    Schedule schedule = schedules.get(i-1);
                    if(!TextUtils.isEmpty(schedule.getShiftName()))
                        item.setValues(i,schedule.getShiftName());
                    else
                        item.setValues(i,"");
                    if(i == 1)
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
        bottomTV = findViewById(R.id.schedule_inputedit_activity_bottomTV);
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
                    //ItemEntityPerson itemEntityPerson = (ItemEntityPerson) view.getTag(R.id.tag_itemEntityPerson);
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
                    recyclerView.smoothScrollToPosition(mX + 1);
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
                    String name = (String)view.getTag();
                    ItemEntityScheduleInput input = (ItemEntityScheduleInput) list.get(mX);
                    input.setValues(mY,name);
                    if(mY< 7 ) {
                        mY = mY + 1;
                    }else if(mX <adapter.getItemCount() -2){
                        adapter.notifyItemChanged(mX);
                        mY = 1;
                        mX = mX+1;
                    }
                    adapter.setCurrentCell(mX,mY);
                    adapter.notifyItemChanged(mX);
                    recyclerView.smoothScrollToPosition(mX + 1);
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
        // 取消
        findViewById(R.id.schedule_inputedit_activity_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent result = new Intent();
                result.putExtra("IsSaved",false);
                setResult(1001,result);
                finish();
            }
        });
        // 保存
        findViewById(R.id.schedule_inputedit_activity_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               saveData();
            }
        });
        // 模板
        findViewById(R.id.schedule_inputedit_activity_template).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTemplateMenu(v);
            }
        });
        // 设置
        findViewById(R.id.schedule_inputedit_activity_setup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSettingMenu(v);
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
                   setBottomRecyclerViewAdapter(1);
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
                        setBottomRecyclerViewAdapter(1);
                        appBarLayout.setExpanded(false);
                    }else{
                        setBottomRecyclerViewAdapter(2);
                        appBarLayout.setExpanded(false);
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
        updateList(MyTool.getWeekStartEndString(c.getTime()),!isAddMode);
        fillShiftList();
        changeTitleText();
    }

    protected class SaveData extends AsyncTask<Void,Integer,Boolean>{
        private int hasSaved,count;
        private PopupWindow popupWindow;
        private TextView infoTextView,titleTV;
        private ProgressBar progressBar;
        public SaveData(){
            hasSaved = 0;
            count = (list.size() -1) * 7;
            popupWindow = new PopupWindow(recyclerView,600,LinearLayout.LayoutParams.WRAP_CONTENT);
            View view = LayoutInflater.from(ScheduleInputEditActivity.this).inflate(R.layout.wait_dialog,null);
            infoTextView = view.findViewById(R.id.wait_dialog_info);
            titleTV = view.findViewById(R.id.wait_dialog_title);
            progressBar = view.findViewById(R.id.wait_dialog_pb);
            progressBar.setMax(count);
            titleTV.setText("正在保存数据");
            popupWindow.setContentView(view);
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            popupWindow.setOutsideTouchable(false);
            popupWindow.setTouchable(false);
        }
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                if(!isAddMode){
                    // 编辑排班需要的操作
                    String[] dates = MyTool.getWeekStartEndString(c.getTime());
                    LitePal.deleteAll(Schedule.class,"date >= ? and date <= ?",dates[0],dates[1]);
                    publishProgress(0);
                }
                Thread.sleep(500);
                for (int k = 0;k<list.size() -1;k++) {
                    ItemEntity itemEntity = list.get(k);
                    if (itemEntity.getType() == ItemType.SCHEDULE_WEEK_VIEW) {
                        ItemEntityScheduleInput input = (ItemEntityScheduleInput) itemEntity;
                        String personName = input.getValues(0);
                        if(TextUtils.isEmpty(personName))
                            continue;
                        String note = input.getValues(8);
                        for (int i = 0; i < 7; i++) {
                            String shift = "";
                            if(!TextUtils.isEmpty(input.getValues(i+1)))
                                shift = input.getValues(i + 1);
                            Shift shift1 = LitePal.where("name = ?", shift).findFirst(Shift.class);
                            int type = MyTool.SHIFT_NORMAL;
                            double unitAmont = 0;
                            if (shift1 != null) {
                                type = shift1.getType();
                                unitAmont = shift1.getUnitAmount();
                            }
                            Schedule schedule = new Schedule();
                            schedule.setRowNumber(k+1);
                            schedule.setDate(dates[i]);
                            schedule.setPersonName(personName);
                            schedule.setShiftName(shift);
                            schedule.setShiftType(type);
                            schedule.setShiftUnitAmount(unitAmont);
                            if (i == 0)
                                schedule.setNote(note);
                            schedule.save();
                            hasSaved ++;
                            publishProgress(hasSaved);
                        }
                    }
                }
                return true;
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            int i = values[0];
            if(i == 0){
                infoTextView.setText("更新数据");
            }else {
                infoTextView.setText("已完成：" + i + "/" + count);
                progressBar.setProgress(i);
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            popupWindow.dismiss();
            if(aBoolean) {
                Intent result = new Intent();
                result.putExtra("IsSaved", true);
                result.putExtra("Date",dates[0].getTime());
                setResult(1001, result);
                finish();
            }else{
                Toast toast = Toast.makeText(ScheduleInputEditActivity.this,"保存失败，请重试一次！",Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
            }
        }

        @Override
        protected void onPreExecute() {
            popupWindow.showAtLocation(recyclerView,Gravity.CENTER,0,0);
        }
    }
}
