package aqth.yzw.iamlittle;

import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.LitePal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import aqth.yzw.iamlittle.Adapters.ScheduleActivity2Adapter;
import aqth.yzw.iamlittle.EntityClass.AppSetup;
import aqth.yzw.iamlittle.EntityClass.BedAssign;
import aqth.yzw.iamlittle.EntityClass.Schedule;
import aqth.yzw.iamlittle.EntityClass.ScheduleActivity2ItemEntity;
import jxl.Workbook;
import jxl.format.CellFormat;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class ScheduleActivity2 extends AppCompatActivity {
    private String TAG = "殷宗旺";
    private LinearLayout assignBedTitleGroup, remaningLeaveTitleGroup;
    private AppCompatTextView[] titleTVs;
    private View divide1, divide2;
    private TextView weekOfYearTV, emptyTV;
    private boolean showAssignBed, showRemaningLeave, showNongLi;
    private Calendar calendar;
    private SimpleDateFormat format;
    private List<ScheduleActivity2ItemEntity> list;
    private ScheduleActivity2Adapter nameAdapter, shiftAdapter;
    private RecyclerView leftRLV, rightRLV;
    private HorizontalScrollView horizontalScrollView;
    private Switch showNongLiSwitch, showAssignBedSwitch, showRemaningLeaveSwitch;
    private SharedPreferencesHelper sharedPreferencesHelper;
    private long number;

    private void updateList(Date date) {
        if (list == null)
            list = new ArrayList<>();
        list.clear();
        String[] dates = MyTool.getWeekStartEndString(date);
        List<String> person = new ArrayList<>();
        Cursor cursor = LitePal.findBySQL("SELECT DISTINCT personname,rownumber FROM schedule WHERE date >= ? and date <= ? ORDER BY rownumber", dates[0], dates[1]);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String name = cursor.getString(0);
                if (!TextUtils.isEmpty(name))
                    person.add(name);
            } while (cursor.moveToNext());
        }
        List<Schedule> temp = new ArrayList<>();
        for (String s : person) {
            ScheduleActivity2ItemEntity itemEntity = new ScheduleActivity2ItemEntity();
            itemEntity.setName(s);
            BedAssign bedAssign = LitePal.where("number = ? and personname = ?",String.valueOf(number),s).findFirst(BedAssign.class);
            String assignBed = "";
            if(bedAssign != null)
                assignBed = bedAssign.getAssign();
            itemEntity.setBedAssign(assignBed);

            itemEntity.setRemaningLeaveValue(0);
            temp.clear();
            temp.addAll(LitePal.order("date")
                    .where("personname = ? and date >= ? and date <= ?", s, dates[0], dates[1]).find(Schedule.class));
            itemEntity.setNote(temp.get(0).getNote());
            for (int i = 0; i < 7; i++) {
                itemEntity.setShift(i, temp.get(i).getShiftName());
            }
            list.add(itemEntity);
        }
        nameAdapter.notifyDataSetChanged();
        shiftAdapter.notifyDataSetChanged();
        if (list.size() == 0)
            emptyTV.setVisibility(View.VISIBLE);
        else {
            leftRLV.smoothScrollToPosition(0);
            rightRLV.smoothScrollToPosition(0);
            emptyTV.setVisibility(View.GONE);
        }
    }

    private boolean hasData() {
        if (list.size() == 0) {
            Toast toast = Toast.makeText(ScheduleActivity2.this, "没有本周排班数据", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return false;
        }
        return true;
    }

    private void optionMenuSelect(@Nullable MenuItem menuItem) {
        Toast toast = Toast.makeText(ScheduleActivity2.this, "正在完善中，敬请期待", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        int id = menuItem.getItemId();
        switch (id) {
            case R.id.pre_week:
                preWeek();
                break;
            case R.id.next_week:
                nextWeek();
                break;
            case R.id.this_week:
                thisWeek();
                break;
            case R.id.select_date:
                selectDate();
                break;
            case R.id.input_schedule:
                inputSchedule();
                break;
            case R.id.del_schedule:
                if (hasData())
                    deleSchedule();
                break;
            case R.id.edit_schedule:
                if (hasData())
                    editSchedule();
                break;
            case R.id.share_schedule:
                if (hasData())
                    shareSchedule();//需要修改，两种模板选择
                break;
            case R.id.assign_bed:
                assignBed(calendar.getTimeInMillis());
                break;
            case R.id.leave_manage:
                toast.show();
                break;
        }
    }

    private void assignBed(long l) {
        AssignBedDialogFragment fragment = AssignBedDialogFragment.newInstant(l);
        fragment.setOnDialogFragmentDismiss(new OnDialogFragmentDismiss() {
            @Override
            public void onDissmiss(boolean flag) {
                if (flag) {
                    updateList(calendar.getTime());
                }
            }

            @Override
            public void onDissmiss(boolean flag, Object object) {

            }
        });
        fragment.show(getSupportFragmentManager(), "AssignBed");
    }

    private void setShowAssignBed(boolean flag) {
        if (flag) {
            assignBedTitleGroup.setVisibility(View.VISIBLE);
            divide1.setVisibility(View.VISIBLE);
        } else {
            assignBedTitleGroup.setVisibility(View.GONE);
            divide1.setVisibility(View.GONE);
        }
        shiftAdapter.setShowAssignedBed(flag);
        horizontalScrollView.smoothScrollTo(0,0);
    }

    private void setShowRemaningLeave(boolean flag) {
        if (flag) {
            remaningLeaveTitleGroup.setVisibility(View.VISIBLE);
            divide2.setVisibility(View.VISIBLE);
        } else {
            remaningLeaveTitleGroup.setVisibility(View.GONE);
            divide2.setVisibility(View.GONE);
        }
        shiftAdapter.setShowRemaningLeave(flag);
        horizontalScrollView.smoothScrollTo(0,0);
    }

    private void setScheduleTitle(Date date) {
        Date[] aWeekDates = MyTool.getAWeekDates(date);
        Calendar _c = new GregorianCalendar();
        _c.setTime(aWeekDates[6]);
        int _year = _c.get(Calendar.YEAR);
        int _weekofyear = _c.get(Calendar.WEEK_OF_YEAR);
        number = _year*100 + _weekofyear;
        weekOfYearTV.setText(_year + " 年度 第 " + _weekofyear + " 周");
        for (int i = 0; i < 7; i++) {
            if (showNongLi) {
                String nongli = MyTool.getNongLiNoYear(aWeekDates[i]);
                titleTVs[i].setText(format.format(aWeekDates[i]) + "\n" + nongli);
            } else {
                titleTVs[i].setText(format.format(aWeekDates[i]));
            }
            if (DateUtils.isToday(aWeekDates[i].getTime())) {
                titleTVs[i].setText("今天");
            }
        }
    }

    private long getUnScheduledWeek(long l) {
        long result = l;
        Date date = new Date(l);
        String[] dates = MyTool.getWeekStartEndString(date);
        List<Schedule> temp = LitePal.order("personname")
                .where("date >= ? and date <= ?", dates[0], dates[1]).find(Schedule.class);
        if (temp != null && temp.size() > 0) {
            result = result + MyTool.ONE_WEEK_MILLISECOND;
            return getUnScheduledWeek(result);
        } else {
            return result;
        }
    }

    private void inputSchedule() {
        long l = getUnScheduledWeek(calendar.getTimeInMillis());
        Intent intent = new Intent();
        intent.putExtra("IsAddMode", true);
        intent.putExtra("Date", l);
        intent.setClass(ScheduleActivity2.this, ScheduleInputEditActivity.class);
        startActivityForResult(intent, 1000);
    }

    private void deleSchedule() {
        if (list.size() == 1) {
            Toast toast = Toast.makeText(ScheduleActivity2.this, "本周没有排班", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return;
        }
        MyDialogFragment dialogFragment = MyDialogFragment.newInstant("是否删除本周排班？", "取消", "删除",
                Color.BLACK, Color.RED);
        dialogFragment.setOnDialogFragmentDismiss(new OnDialogFragmentDismiss() {
            @Override
            public void onDissmiss(boolean flag) {
                if (flag) {
                    String[] _dates = MyTool.getWeekStartEndString(calendar.getTime());
                    LitePal.deleteAll(Schedule.class, "date >= ? and date <= ?", _dates[0], _dates[1]);
                    updateList(calendar.getTime());
                }
            }

            @Override
            public void onDissmiss(boolean flag, Object object) {

            }
        });
        dialogFragment.show(getSupportFragmentManager(), "DelSchedule");
    }

    private void editSchedule() {
        if (list.size() == 1) {
            Toast toast = Toast.makeText(ScheduleActivity2.this, "本周没有排班", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return;
        }
        Intent intent = new Intent();
        intent.putExtra("IsAddMode", false);
        intent.putExtra("Date", calendar.getTimeInMillis());
        intent.setClass(ScheduleActivity2.this, ScheduleInputEditActivity.class);
        startActivityForResult(intent, 1000);
    }

    private void shareSchedule() {
        SelectPrintLayoutFragment fragment = new SelectPrintLayoutFragment();
        fragment.setOnDialogFragmentDismiss(new OnDialogFragmentDismiss() {
            @Override
            public void onDissmiss(boolean flag) {
            }

            @Override
            public void onDissmiss(boolean flag, Object object) {
                int mode = (int) object;
                if (flag) {
                    if (mode == 1)
                        shareSchedule1();
                    else
                        shareSchedule2();
                }
            }
        });
        fragment.show(getSupportFragmentManager(), "SelectPrintLayout");
    }

    private void shareSchedule1() {
        getExecelFilePortrait(); // 竖版
        getZipFile();
        File outZipFile = new File(getCacheDir() + "/schedule_portrait.zip");
        Uri uri = null;
        if (Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(ScheduleActivity2.this, "th.yzw.iamlittle.fileprovider", outZipFile);
        } else {
            uri = Uri.fromFile(outZipFile);
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/x-zip-compressed");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(intent, "排班表分享"));
    }

    private void shareSchedule2() {
        getExecelFileLandscape();// 横版
        getZipFile();
        File outZipFile = new File(getCacheDir() + "/schedule_landscape.zip");
        Uri uri = null;
        if (Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(ScheduleActivity2.this, "th.yzw.iamlittle.fileprovider", outZipFile);
        } else {
            uri = Uri.fromFile(outZipFile);
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/x-zip-compressed");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(intent, "排班表分享"));
    }

    private void getExecelFilePortrait() {
        if (list.size() == 0)
            return;
        Calendar _c = new GregorianCalendar();
        Date[] mDates = MyTool.getAWeekDates(calendar.getTime());
        _c.setTime(mDates[6]);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String outPath = Environment.getExternalStorageDirectory() + "/IAmLittle/Schedule/";
            File dir = new File(outPath);
            try {
                File tempFile = new File(getFilesDir() + "/schedule_template.xls");
                if (!tempFile.exists()) {
                    AssetManager assetManager = getAssets();
                    InputStream inputStream = assetManager.open("schedule_template.xls");
                    FileOutputStream os = new FileOutputStream(tempFile);
                    int bytes = 0;
                    byte[] buffer = new byte[1024];
                    while ((bytes = inputStream.read(buffer, 0, 1024)) != -1) {
                        os.write(buffer, 0, bytes);
                    }
                    os.close();
                    inputStream.close();
                }
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                for (File file : dir.listFiles()) {
                    if (!file.isDirectory())
                        file.delete();
                }
                int pages = (list.size() - 1) / 20 + 1;
                int _year = _c.get(Calendar.YEAR);
                int _weekofyear = _c.get(Calendar.WEEK_OF_YEAR);
                String weekOfYear = _year + " 年度 第 " + _weekofyear + " 周";
                String title = "";
                AppSetup appSetup = LitePal.where("key = 'organizename'").findFirst(AppSetup.class);
                if (appSetup != null) {
                    title = appSetup.getValue();
                }
                for (int m = 1; m <= pages; m++) {
                    int start = 0 + 20 * (m - 1);
                    FileOutputStream fos = new FileOutputStream(outPath + "SchedulePortrait" + m + ".xls");
                    Workbook workbook = Workbook.getWorkbook(tempFile);
                    WritableWorkbook wwb = Workbook.createWorkbook(fos, workbook);
                    WritableSheet sheet = wwb.getSheet(0);
                    CellFormat titleFmt = sheet.getCell(0, 0).getCellFormat();
                    Label titleLb = new Label(0, 0, title, titleFmt);// 标题
                    sheet.addCell(titleLb);
                    CellFormat pbbFmt = sheet.getCell(0, 1).getCellFormat();
                    Label pbbLb = new Label(0, 1, "排班表", pbbFmt);// 排班表
                    sheet.addCell(pbbLb);
                    CellFormat woyFmt = sheet.getCell(0, 2).getCellFormat();
                    Label weekOfYearLb = new Label(0, 2, weekOfYear, woyFmt);// 年度第几周
                    sheet.addCell(weekOfYearLb);
                    Calendar _calendar = new GregorianCalendar();
                    int _month = 0;
                    int pre_month = 0;
                    CellFormat dateFmt = sheet.getCell(2, 3).getCellFormat();
                    for (int i = 0; i < 7; i++) {
                        _calendar.setTime(mDates[i]);
                        pre_month = _month;
                        _month = _calendar.get(Calendar.MONTH);
                        int _day = _calendar.get(Calendar.DAY_OF_MONTH);
                        if (i == 0) {
                            Label mondayLb = new Label(2, 3, Integer.toString(_month + 1), dateFmt);
                            sheet.addCell(mondayLb);
                        } else if (pre_month != _month) {
                            Label monthLb = new Label(2 + i, 3, Integer.toString(_month + 1), dateFmt);
                            sheet.addCell(monthLb);
                        } // 月份列
                        Label dateLb = new Label(2 + i, 4, Integer.toString(_day), dateFmt);// 日期列
                        sheet.addCell(dateLb);
                        if (showNongLi) {//填充农历
                            CellFormat nlFmt = sheet.getCell(2, 6).getCellFormat();
                            String nongli = MyTool.getNongLiNoYear(mDates[i]);
                            Label nongliLb = new Label(2 + i, 6, nongli, nlFmt);
                            sheet.addCell(nongliLb);
                        }
                    }

                    for (int k = 0; k < 20; k++) {
                        int position = start + k;
                        if (position >= list.size())
                            break;
                        ScheduleActivity2ItemEntity entity = list.get(position);
                        int row = 7 + k;
                        CellFormat nameFmt = sheet.getCell(0, row).getCellFormat();
                        Label nameLb = new Label(0, row, entity.getName(), nameFmt);
                        sheet.addCell(nameLb);
                        CellFormat noteFmt = sheet.getCell(9, row).getCellFormat();
                        Label noteLb = new Label(9, row, entity.getNote(), noteFmt);
                        sheet.addCell(noteLb);
                        for (int j = 2; j < 9; j++) { //循环添加班次
                            CellFormat shiftFmt = sheet.getCell(j, row).getCellFormat();
                            Label lb = new Label(j, row, entity.getShifts()[j - 2], shiftFmt);
                            sheet.addCell(lb);
                        }

//                        ItemEntity item = list.get(position);
//                        if (item.getType() == ItemType.SCHEDULE_WEEK_VIEW) {
//                            int row = 7 + k;
//                            ItemEntityScheduleInput _item = (ItemEntityScheduleInput) item;
//                            CellFormat nameFmt = sheet.getCell(0, row).getCellFormat();
//                            Label nameLb = new Label(0, row, _item.getValues(0), nameFmt);
//                            sheet.addCell(nameLb);
//                            for (int j = 1; j < 9; j++) { //循环添加
//                                if (j == 8) {// j = 8为备注，如要改字体，需要加代码在这里
//
//                                }
//                                CellFormat shiftFmt = sheet.getCell(j + 1, row).getCellFormat();
//                                Label lb = new Label(j + 1, row, _item.getValues(j), shiftFmt);
//                                sheet.addCell(lb);
//                            }
//                        }
                    }
                    CellFormat footFmt1 = sheet.getCell(0, 27).getCellFormat();
                    Label footLb1 = new Label(0, 27, "页码：" + m + "/" + pages, footFmt1);
                    sheet.addCell(footLb1);
                    CellFormat footFmt2 = sheet.getCell(8, 27).getCellFormat();
                    Label footLb2 = new Label(8, 27, "By:YZW", footFmt2);
                    sheet.addCell(footLb2);
                    wwb.write();
                    wwb.close();
                    fos.close();
                    workbook.close();
                }
                Toast.makeText(ScheduleActivity2.this, "导出EXCEL文件成功\n文件目录：" + dir.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(ScheduleActivity2.this, "导出EXCEL文件失败\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            PermissionUtils.verifyStoragePermissions(this);
//            MyDialogFragmenSingleButton dialogFragment = MyDialogFragmenSingleButton.newInstant("您已拒绝本程序存储文件，不能进行导出操作！", "关闭");
//            dialogFragment.show(getSupportFragmentManager(), "NotAllowWriteSD");
        }
    }

    private void getExecelFileLandscape() {
        if (list.size() == 0)
            return;
        Calendar _c = new GregorianCalendar();
        Date[] mDates = MyTool.getAWeekDates(calendar.getTime());
        _c.setTime(mDates[6]);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String outPath = Environment.getExternalStorageDirectory() + "/IAmLittle/Schedule/";
            File dir = new File(outPath);
            try {
                File tempFile = new File(getFilesDir() + "/schedule_template_2.xls");
                if (!tempFile.exists()) {
                    AssetManager assetManager = getAssets();
                    InputStream inputStream = assetManager.open("schedule_template_2.xls");
                    FileOutputStream os = new FileOutputStream(tempFile);
                    int bytes = 0;
                    byte[] buffer = new byte[1024];
                    while ((bytes = inputStream.read(buffer, 0, 1024)) != -1) {
                        os.write(buffer, 0, bytes);
                    }
                    os.close();
                    inputStream.close();
                }
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                for (File file : dir.listFiles()) {
                    if (!file.isDirectory())
                        file.delete();
                }
                int pages = (list.size() - 1) / 20 + 1;
                int _year = _c.get(Calendar.YEAR);
                int _weekofyear = _c.get(Calendar.WEEK_OF_YEAR);
                String weekOfYear = _year + " 年度 第 " + _weekofyear + " 周";
                String title = "";
                AppSetup appSetup = LitePal.where("key = 'organizename'").findFirst(AppSetup.class);
                if (appSetup != null) {
                    title = appSetup.getValue();
                }
                for (int m = 1; m <= pages; m++) {
                    int start = 0 + 20 * (m - 1);
                    FileOutputStream fos = new FileOutputStream(outPath + "ScheduleLandscape" + m + ".xls");
                    Workbook workbook = Workbook.getWorkbook(tempFile);
                    WritableWorkbook wwb = Workbook.createWorkbook(fos, workbook);
                    WritableSheet sheet = wwb.getSheet(0);
                    CellFormat titleFmt = sheet.getCell(0, 0).getCellFormat();
                    Label titleLb = new Label(0, 0, title, titleFmt);// 标题
                    sheet.addCell(titleLb);
                    CellFormat woyFmt = sheet.getCell(8, 0).getCellFormat();
                    Label weekOfYearLb = new Label(8, 0, weekOfYear, woyFmt);// 年度第几周
                    sheet.addCell(weekOfYearLb);
                    for (int i = 0; i < 7; i++) {
                        String dateString = format.format(mDates[i]);
                        int index = 2 * i + 3;
                        CellFormat dateFmt = sheet.getCell(index, 3).getCellFormat();
                        Label dateLb = new Label(index, 3, dateString, dateFmt);
                        sheet.addCell(dateLb);
                        if (showNongLi) {//填充农历
                            CellFormat nlFmt = sheet.getCell(index, 4).getCellFormat();
                            String nongli = MyTool.getNongLiNoYear(mDates[i]);
                            Label nongliLb = new Label(index, 4, nongli, nlFmt);
                            sheet.addCell(nongliLb);
                        }
                    }
                    for (int k = 0; k < 20; k++) {
                        int position = start + k;
                        if (position >= list.size())
                            break;
                        ScheduleActivity2ItemEntity entity = list.get(position);
                        int row = 6 + k;//从第七行开始填充
                        CellFormat assignBedFmt = sheet.getCell(0, row).getCellFormat();
                        Label assignBedLb = new Label(0, row, "暂未分配", assignBedFmt);
                        sheet.addCell(assignBedLb);
                        CellFormat remaningLeaveFmt = sheet.getCell(17, row).getCellFormat();
                        Label remaningLeaveLb = new Label(17, row, "0.0", remaningLeaveFmt);
                        sheet.addCell(remaningLeaveLb);
                        CellFormat nameFmt = sheet.getCell(1, row).getCellFormat();
                        Label nameLb = new Label(1, row, entity.getName(), nameFmt);
                        sheet.addCell(nameLb);
                        CellFormat noteFmt = sheet.getCell(18, row).getCellFormat();
                        Label noteLb = new Label(18, row, entity.getNote(), noteFmt);
                        sheet.addCell(noteLb);
                        for (int j = 0; j < 7; j++) { //循环添加班次
                            int index = 2 * j + 3;
                            CellFormat shiftFmt = sheet.getCell(index, row).getCellFormat();
                            Label lb = new Label(index, row, entity.getShifts()[j], shiftFmt);
                            sheet.addCell(lb);
                        }
                    }
                    CellFormat footFmt1 = sheet.getCell(0, 26).getCellFormat();
                    Label footLb1 = new Label(0, 26, "页码：" + m + "/" + pages, footFmt1);
                    sheet.addCell(footLb1);
                    CellFormat footFmt2 = sheet.getCell(17, 26).getCellFormat();
                    Label footLb2 = new Label(17, 26, "By:YZW", footFmt2);
                    sheet.addCell(footLb2);
                    wwb.write();
                    wwb.close();
                    fos.close();
                    workbook.close();
                }
                Toast.makeText(ScheduleActivity2.this, "导出EXCEL文件成功\n文件目录：" + dir.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(ScheduleActivity2.this, "导出EXCEL文件失败\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            PermissionUtils.verifyStoragePermissions(this);
        }
    }

    private void getZipFile() {
        String excelPath = Environment.getExternalStorageDirectory() + "/IAmLittle/Schedule/";
        File dir = new File(excelPath);
        File outZipFile = new File(getCacheDir() + "/schedule_" + MyTool.getRandomString(6) + ".zip");
        ZipOutputStream zos = null;
        FileInputStream fis = null;
        try {
            zos = new ZipOutputStream(new FileOutputStream(outZipFile));
            byte[] buffer = new byte[4096];
            int bytes_read;
            for (File file : dir.listFiles()) {
                fis = new FileInputStream(file);
                ZipEntry entry = new ZipEntry(file.getName());
                zos.putNextEntry(entry);
                while ((bytes_read = fis.read(buffer)) != -1) {
                    zos.write(buffer, 0, bytes_read);
                }
                zos.closeEntry();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(ScheduleActivity2.this, "生成压缩文件失败\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            try {
                if (fis != null)
                    fis.close();
                if (zos != null)
                    zos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void showNote(View view, int x) {
        ScheduleActivity2ItemEntity entity = list.get(x);
        String note = entity.getNote();
        PopupWindow popupWindow = new PopupWindow(view, 480, RelativeLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        popupWindow.setOutsideTouchable(true);
        View v = LayoutInflater.from(view.getContext()).inflate(R.layout.popwindow_layout, null);
        TextView textView = v.findViewById(R.id.popwindow_textview);
        textView.setText(note);
        popupWindow.setContentView(v);
        int[] local = new int[2];
        view.getLocationOnScreen(local);
        popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, local[0], local[1]);
    }

    private void selectDate() {
        SelectDateDialogFragment fragment = SelectDateDialogFragment.newInstant(calendar.getTimeInMillis());
        fragment.setOnDialogFragmentDismiss(new OnDialogFragmentDismiss() {
            @Override
            public void onDissmiss(boolean flag) {

            }

            @Override
            public void onDissmiss(boolean flag, Object object) {
                if (flag) {
                    long l = (long) object;
                    calendar.setTimeInMillis(l);
                    setScheduleTitle(calendar.getTime());
                    updateList(calendar.getTime());
                }
            }
        });
        fragment.show(getSupportFragmentManager(), "SelectDate");
    }

    private void preWeek() {
        calendar.add(Calendar.DAY_OF_MONTH, -7);
        setScheduleTitle(calendar.getTime());
        updateList(calendar.getTime());
        horizontalScrollView.scrollTo(0, 0);
    }

    private void nextWeek() {
        calendar.add(Calendar.DAY_OF_MONTH, 7);
        setScheduleTitle(calendar.getTime());
        updateList(calendar.getTime());
        horizontalScrollView.scrollTo(0, 0);
    }

    private void thisWeek() {
        calendar = new GregorianCalendar();
        setScheduleTitle(calendar.getTime());
        updateList(calendar.getTime());
        horizontalScrollView.scrollTo(0, 0);
    }

    private void findView() {
        titleTVs = new AppCompatTextView[7];
        titleTVs[0] = findViewById(R.id.schedule2_item_title_date1);
        titleTVs[1] = findViewById(R.id.schedule2_item_title_date2);
        titleTVs[2] = findViewById(R.id.schedule2_item_title_date3);
        titleTVs[3] = findViewById(R.id.schedule2_item_title_date4);
        titleTVs[4] = findViewById(R.id.schedule2_item_title_date5);
        titleTVs[5] = findViewById(R.id.schedule2_item_title_date6);
        titleTVs[6] = findViewById(R.id.schedule2_item_title_date7);
        assignBedTitleGroup = findViewById(R.id.schedule2_activity_title_assignbedGroup);
        remaningLeaveTitleGroup = findViewById(R.id.schedule2_activity_title_remaningleaveGroup);
        divide1 = findViewById(R.id.schedule2_activity_title_divide1);
        divide2 = findViewById(R.id.schedule2_activity_title_divide2);
        leftRLV = findViewById(R.id.activity_schedule2_recyclerview_left);
        rightRLV = findViewById(R.id.activity_schedule2_recyclerview_right);
        horizontalScrollView = findViewById(R.id.horizontalScrollView);
        showNongLiSwitch = findViewById(R.id.showNongLiSwitch);
        showAssignBedSwitch = findViewById(R.id.showAssignBedSwitch);
        showRemaningLeaveSwitch = findViewById(R.id.showRemaningLeaveSwitch);
        weekOfYearTV = findViewById(R.id.schedule_activity2_weekofyear);
        emptyTV = findViewById(R.id.empty_showTV);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("排班表");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.inflateMenu(R.menu.schedule_activity2_toolbar_menu);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                optionMenuSelect(menuItem);
                return true;
            }
        });
        number = 0;
        sharedPreferencesHelper = new SharedPreferencesHelper(ScheduleActivity2.this);
        showAssignBed = (boolean) sharedPreferencesHelper.getValue("ShowAssignBed", false);
        showRemaningLeave = (boolean) sharedPreferencesHelper.getValue("ShowRemaningLeave", false);
        showNongLi = (boolean) sharedPreferencesHelper.getValue("ShowNongLi", false);
        list = new ArrayList<>();
        nameAdapter = new ScheduleActivity2Adapter(list, 1, showAssignBed, showRemaningLeave);
        shiftAdapter = new ScheduleActivity2Adapter(list, 2, showAssignBed, showRemaningLeave);
        shiftAdapter.setClickListener(new IItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                TextView tv = (TextView) view;
                int line = tv.getLineCount();
                if (line > 1 && tv.getLayout().getEllipsisCount(2) > 0) {
                    showNote(view, position);
                }
            }

            @Override
            public void onClick(View view, int x, int y) {

            }
        });
        findView();
        showNongLiSwitch.setChecked(showNongLi);
        showRemaningLeaveSwitch.setChecked(showRemaningLeave);
        showAssignBedSwitch.setChecked(showAssignBed);
        showNongLiSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                showNongLi = isChecked;
                sharedPreferencesHelper.put("ShowNongLi", showNongLi);
                setScheduleTitle(calendar.getTime());
            }
        });
        showAssignBedSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                showAssignBed = isChecked;
                sharedPreferencesHelper.put("ShowAssignBed", showAssignBed);
                setShowAssignBed(showAssignBed);
            }
        });
        showRemaningLeaveSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                showRemaningLeave = isChecked;
                sharedPreferencesHelper.put("ShowRemaningLeave", showRemaningLeave);
                setShowRemaningLeave(showRemaningLeave);
            }
        });
        calendar = new GregorianCalendar();
        format = new SimpleDateFormat("M月d日");
        leftRLV.setAdapter(nameAdapter);
        leftRLV.addItemDecoration(new DividerItemDecoration(ScheduleActivity2.this, DividerItemDecoration.VERTICAL));
        rightRLV.setAdapter(shiftAdapter);
        rightRLV.addItemDecoration(new DividerItemDecoration(ScheduleActivity2.this, DividerItemDecoration.VERTICAL));
        leftRLV.setLayoutManager(new LinearLayoutManager(ScheduleActivity2.this));
        rightRLV.setLayoutManager(new LinearLayoutManager(ScheduleActivity2.this));
        leftRLV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (recyclerView.getScrollState() != RecyclerView.SCROLL_STATE_IDLE) {
                    rightRLV.scrollBy(dx, dy);
                }

            }
        });
        rightRLV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (recyclerView.getScrollState() != RecyclerView.SCROLL_STATE_IDLE) {
                    leftRLV.scrollBy(dx, dy);
                }
            }
        });
        setScheduleTitle(calendar.getTime());
        updateList(calendar.getTime());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == 1001) {
            boolean b = data.getBooleanExtra("IsSaved", false);
            if (b) {
                long l = data.getLongExtra("Date", calendar.getTimeInMillis());
                calendar.setTimeInMillis(l);
                updateList(calendar.getTime());
                setScheduleTitle(calendar.getTime());
                Toast.makeText(ScheduleActivity2.this, "排班已保存", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setShowAssignBed(showAssignBed);
        setShowRemaningLeave(showRemaningLeave);
    }
}
