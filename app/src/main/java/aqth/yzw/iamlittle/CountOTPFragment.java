package aqth.yzw.iamlittle;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.LitePal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import aqth.yzw.iamlittle.Adapters.ShiftSelectAdapter;
import aqth.yzw.iamlittle.EntityClass.ItemEntity;
import aqth.yzw.iamlittle.EntityClass.ItemEntitySelectShift;
import aqth.yzw.iamlittle.EntityClass.OverTimePay;
import aqth.yzw.iamlittle.EntityClass.Schedule;

public class CountOTPFragment extends Fragment {
    private List<ItemEntity> list;
    private RecyclerView recyclerView;
    private ShiftSelectAdapter adapter;
    private Button cancelBT, confirmBT;
    private SelectDateDialogFragment selectDateDialog;
    private TextView startDayTV, endDayTV;
    private Date startDay, endDay, recordTime;
    private SimpleDateFormat format1, format2;
    private Calendar calendar;
    private String s1, s2;
    private int mode;

    private void fillShiftList() {
        if (endDay.getTime() < startDay.getTime()) {
            return;
        }
        if (list == null)
            list = new ArrayList<>();
        list.clear();
        s1 = MyTool.getDayStartString(startDay);
        s2 = MyTool.getDayEndString(endDay);
        Cursor cursor = LitePal.findBySQL("SELECT DISTINCT shiftName,shiftUnitAmount,shiftType FROM Schedule WHERE date BETWEEN '" + s1 + "' AND '" + s2 + "'");
        if (cursor.moveToFirst()) {
            do {
                String shiftName = cursor.getString(0);
                if(!TextUtils.isEmpty(shiftName)) {
                    double unitAmount = cursor.getDouble(1);
                    int type = cursor.getInt(2);
                    ItemEntitySelectShift selectShift = new ItemEntitySelectShift();
                    if (type == MyTool.SHIFT_NEEDCOUNT)
                        selectShift.setSelect(true);
                    else
                        selectShift.setSelect(false);
                    selectShift.setShiftName(shiftName);
                    selectShift.setUnitAmount(unitAmount);
                    list.add(selectShift);
                }
            } while (cursor.moveToNext());
        }
        if (list.size() == 0) {
            list.add(new ItemEntity());
        }
    }

    private List<String> getPersonList() {
        List<String> personName = new ArrayList<>();
        s1 = MyTool.getDayStartString(startDay);
        s2 = MyTool.getDayEndString(endDay);
        Cursor cursor = LitePal.findBySQL("SELECT DISTINCT personName FROM Schedule WHERE date BETWEEN '" + s1 + "' AND '" + s2 + "'");
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(0);
                personName.add(name);
            } while (cursor.moveToNext());
        }
        return personName;
    }

    private void showToast(String s) {
        Toast toast = Toast.makeText(getContext(), s, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private void selectStartDay() {
        selectDateDialog.setOnDialogFragmentDismiss(new OnDialogFragmentDismiss() {
            @Override
            public void onDissmiss(boolean flag) {

            }

            @Override
            public void onDissmiss(boolean flag, Object object) {
                if (flag) {
                    long l = (long) object;
                    startDay.setTime(l);
                    startDayTV.setText(format1.format(startDay));
                    fillShiftList();
                    adapter.notifyDataSetChanged();
                }
            }
        });
        selectDateDialog.show(getFragmentManager(), "StartDay");
    }

    private void selectEndDay() {
        selectDateDialog.setOnDialogFragmentDismiss(new OnDialogFragmentDismiss() {
            @Override
            public void onDissmiss(boolean flag) {

            }

            @Override
            public void onDissmiss(boolean flag, Object object) {
                if (flag) {
                    long l = (long) object;
                    endDay.setTime(l);
                    if (endDay.getTime() < startDay.getTime()) {
                        showToast("时间选择错误：截止时间小于开始时间");
                        endDayTV.setError("请重新选择！");
                        endDayTV.requestFocus();
                        return;
                    }
                    endDayTV.setError(null);
                    endDayTV.setText(format2.format(endDay));
                    fillShiftList();
                    adapter.notifyDataSetChanged();
                }
            }
        });
        selectDateDialog.show(getFragmentManager(), "EndDay");
    }

    private void cancel() {
        if (mode == 0) {
            getActivity().setTitle("历史数据列表");
            ((OTPActivity) getActivity()).setShowDetails(false);
            getFragmentManager().popBackStackImmediate();
        } else {
            getActivity().finish();
        }
    }

    private void count() {
        for (String personName : getPersonList()) {
            for (ItemEntity itemEntity : list) {
                if (itemEntity.getType() == ItemType.SHIFT) {
                    ItemEntitySelectShift shift = (ItemEntitySelectShift) itemEntity;
                    if (shift.isSelect()) {
                        String shiftName = shift.getShiftName();
                        double unitAmount = shift.getUnitAmount();
                        int count = LitePal.where("personName = ? and shiftName = ? and date >= ? and date <= ?",
                                personName, shiftName, s1, s2).count(Schedule.class);
                        if (count > 0) {
                            OverTimePay overTimePay = new OverTimePay();
                            overTimePay.setPersonName(personName);
                            overTimePay.setShiftName(shiftName);
                            overTimePay.setShiftUA(unitAmount);
                            overTimePay.setShiftCount(count);
                            overTimePay.setStartDay(startDay);
                            overTimePay.setEndDay(endDay);
                            overTimePay.setRecordTime(recordTime);
                            overTimePay.setAmount(Arith.mul(count,unitAmount,2));
                            overTimePay.setScale(2);
                            overTimePay.save();
                        }
                    }
                }
            }
        }
        if (mode == 0) {
            CountOTPFragment fragment1 = (CountOTPFragment) getFragmentManager().findFragmentByTag("Count");
            OTPDetailsFragment fragment2 = OTPDetailsFragment.newInstant(recordTime);
            getFragmentManager().beginTransaction()
                    .add(R.id.common_linerarlayout, fragment2, "Details")
                    .addToBackStack(null)
                    .show(fragment2)
                    .hide(fragment1)
                    .commit();
        } else {
            OTPDetailsFragment fragment2 = OTPDetailsFragment.newInstant(recordTime);
            getFragmentManager().beginTransaction()
                    .replace(R.id.common_linerarlayout, fragment2, "Details")
                    .commit();
        }
        getActivity().setTitle("统计结果");
    }

    private boolean checkOut() {
        boolean b = false;
        if (endDay.getTime() < startDay.getTime()) {
            showToast("时间选择错误：截止时间小于开始时间");
            endDayTV.setError("请重新选择！");
            endDayTV.requestFocus();
            return false;
        }
        endDayTV.setError(null);
        if (list.size() == 1) {
            showToast("当前时间段没有排班信息，无法统计。");
            return false;
        }
        for (ItemEntity itemEntity : list) {
            if (itemEntity.getType() == ItemType.SHIFT) {
                ItemEntitySelectShift shift = (ItemEntitySelectShift) itemEntity;
                if (shift.isSelect()) {
                    b = true;
                    break;
                }
            }
        }
        if(b){
            return true;
        }else{
            showToast("请选择要统计的班次！");
            return false;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list = new ArrayList<>();
        mode = 0;
        if (getArguments() != null) {
            mode = getArguments().getInt("Mode");
        }
        adapter = new ShiftSelectAdapter(list);
        adapter.setItemClickListener(new IItemClickListener() {
            @Override
            public void onClick(View view, final int position) {
                ItemEntity itemEntity = list.get(position);
                if (itemEntity.getType() == ItemType.EMPTY)
                    return;
                final ItemEntitySelectShift item = (ItemEntitySelectShift) itemEntity;
                EditShiftUnitAmountFragment fragment = EditShiftUnitAmountFragment.newInstant(item.getShiftName(), item.getUnitAmount());
                fragment.setOnDialogFragmentDismiss(new OnDialogFragmentDismiss() {
                    @Override
                    public void onDissmiss(boolean flag) {

                    }

                    @Override
                    public void onDissmiss(boolean flag, Object object) {
                        if (flag) {
                            double value = (double) object;
                            item.setUnitAmount(value);
                            item.setSelect(true);
                            adapter.notifyItemChanged(position);
                        }
                    }
                });
                fragment.show(getFragmentManager(), "EditUnitAmount");
            }

            @Override
            public void onClick(View view, int x, int y) {

            }
        });
        calendar = new GregorianCalendar();
        recordTime = calendar.getTime();
        startDay = calendar.getTime();
        endDay = calendar.getTime();
        format1 = new SimpleDateFormat("yyyy年MM月dd日 00:00:00");
        format2 = new SimpleDateFormat("yyyy年MM月dd日 23:59:59");
        fillShiftList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.count_otp_fragment, container, false);
        selectDateDialog = SelectDateDialogFragment.newInstant(recordTime.getTime());
        startDayTV = view.findViewById(R.id.count_otp_startdayTV);
        startDayTV.setText(format1.format(startDay));
        startDayTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectStartDay();
            }
        });
        endDayTV = view.findViewById(R.id.count_otp_enddayTV);
        endDayTV.setText(format2.format(endDay));
        endDayTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectEndDay();
            }
        });
        recyclerView = view.findViewById(R.id.count_otp_recyclerview);
        cancelBT = view.findViewById(R.id.count_otp_cancel);
        cancelBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
        confirmBT = view.findViewById(R.id.count_otp_count);
        confirmBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkOut()) {
                    String[] startDays = MyTool.getDayStartEndString(startDay);
                    String[] endDays = MyTool.getDayStartEndString(endDay);
                    final List<OverTimePay> otp_temp = LitePal.where("startDay >= ? and startDay <= ? and " +
                            "endDay >= ? and endDay <= ?", startDays[0], startDays[1], endDays[0], endDays[1]).find(OverTimePay.class);
                    if (otp_temp != null && otp_temp.size() > 0) {
                        MyDialogFragment fragment = MyDialogFragment.newInstant("已存在当前时间段数据，是否重新统计？",
                                "取消", "重新统计");
                        fragment.setOnDialogFragmentDismiss(new OnDialogFragmentDismiss() {
                            @Override
                            public void onDissmiss(boolean flag) {
                                if (!flag) {
                                    showToast("取消操作!");
                                    return;
                                } else {
                                    for (OverTimePay overTimePay : otp_temp) {
                                        overTimePay.delete();
                                    }
                                    count();
                                }
                            }

                            @Override
                            public void onDissmiss(boolean flag, Object object) {

                            }
                        });
                        fragment.show(getFragmentManager(), "Exist");
                    } else {
                        count();
                    }
                }
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), RecyclerView.VERTICAL));
        return view;
    }

    public static CountOTPFragment newInstant(int mode) {
        CountOTPFragment fragment = new CountOTPFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("Mode", mode);
        fragment.setArguments(bundle);
        return fragment;
    }
}
