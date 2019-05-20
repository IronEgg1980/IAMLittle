package aqth.yzw.iamlittle;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import aqth.yzw.iamlittle.Adapters.AssignBedAdapter;
import aqth.yzw.iamlittle.EntityClass.BedAssign;

public class AssignBedDialogFragment extends DialogFragment {
    private List<BedAssign> list;
    private RecyclerView recyclerView;
    private TextView clearAll, confirm;
    private AssignBedAdapter assignBedAdapter;
    private long date;
    private Calendar calendar;
    private boolean flag;
    private OnDialogFragmentDismiss onDialogFragmentDismiss;

    public void setOnDialogFragmentDismiss(OnDialogFragmentDismiss onDialogFragmentDismiss) {
        this.onDialogFragmentDismiss = onDialogFragmentDismiss;
    }

    public static AssignBedDialogFragment newInstant(long date) {
        AssignBedDialogFragment fragment = new AssignBedDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("Date", date);
        fragment.setArguments(bundle);
        return fragment;
    }

    private void fillList(long l, int flag) {
        if (list == null)
            list = new ArrayList<>();
        list.clear();
        Calendar _c = new GregorianCalendar();
        _c.setTimeInMillis(l);
        Date[] dates = MyTool.getAWeekDates(_c.getTime());
        _c.setTime(dates[6]);
        int year = _c.get(Calendar.YEAR);
        int weekOfYear = _c.get(Calendar.WEEK_OF_YEAR);
        int number = year * 100 + weekOfYear;
        if (LitePal.isExist(BedAssign.class, "number = ?", String.valueOf(number))|| flag > 10) {
            list.addAll(LitePal.where("number = ?", String.valueOf(number)).find(BedAssign.class));
            list.add(new BedAssign());
            assignBedAdapter.notifyDataSetChanged();
        } else{
            _c.add(Calendar.DAY_OF_MONTH, -7);
            fillList(_c.getTimeInMillis(), ++flag);
        }
    }

    private void selectPerson(final View view, final int position) {
        final BedAssign bedAssign = list.get(position);
        SelectPersonDialogFragment fragment = new SelectPersonDialogFragment();
        fragment.setDialogFragmentDismiss(new OnDialogFragmentDismiss() {
            @Override
            public void onDissmiss(boolean flag) {

            }

            @Override
            public void onDissmiss(boolean flag, Object object) {
                if (flag) {
                    boolean noSame = true;
                    String name = (String) object;
                    for(BedAssign bedAssign1 : list){
                        if(name.equals(bedAssign1.getPersonName())){
                            noSame = false;
                            break;
                        }
                    }
                    if(noSame) {
                        bedAssign.setPersonName(name);
                        assignBedAdapter.notifyItemChanged(position);
                    }else{
                        Toast.makeText(getContext(),"人员重复，请重新选择",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        fragment.show(getFragmentManager(),"SelectPerson");
    }

    private void inputAssignBed(View view, final int position) {
        final BedAssign bedAssign = list.get(position);
        String assignBed = bedAssign.getAssign();
        MyDialogFragmentInput fragmentInput = MyDialogFragmentInput.newInstant(assignBed, "输入床位安排");
        fragmentInput.setOnDialogFragmentDismiss(new OnDialogFragmentDismiss() {
            @Override
            public void onDissmiss(boolean flag) {

            }

            @Override
            public void onDissmiss(boolean flag, Object object) {
                if (flag) {
                    bedAssign.setAssign((String) object);
                    assignBedAdapter.notifyItemChanged(position);
                }
            }
        });
        fragmentInput.show(getFragmentManager(), "InputAssignBed");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list = new ArrayList<>();
        assignBedAdapter = new AssignBedAdapter(list);
        assignBedAdapter.setSelectPersonClick(new IItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                selectPerson(view, position);
            }

            @Override
            public void onClick(View view, int x, int y) {

            }
        });
        assignBedAdapter.setInputAssignBedClick(new IItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                inputAssignBed(view, position);
            }

            @Override
            public void onClick(View view, int x, int y) {

            }
        });
        assignBedAdapter.setLongClick(new IItemClickListener() {
            @Override
            public void onClick(View view, final int position) {
                PopupMenu popupMenu = new PopupMenu(getContext(), view);
                final Menu menu = popupMenu.getMenu();
                menu.add(0, 1, 0, "删除");
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        BedAssign bedAssign = list.get(position);
                        if(bedAssign.isSaved())
                            bedAssign.delete();
                        list.remove(bedAssign);
                        assignBedAdapter.notifyItemRemoved(position);
                        assignBedAdapter.notifyItemRangeChanged(position, list.size() - position);
                        return true;
                    }
                });
                popupMenu.show();
            }

            @Override
            public void onClick(View view, int x, int y) {

            }
        });
        calendar = new GregorianCalendar();
        date = calendar.getTimeInMillis();
        Bundle bundle = getArguments();
        if (bundle != null) {
            date = bundle.getLong("Date");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.assign_bed_layout, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(assignBedAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),RecyclerView.VERTICAL));
        clearAll = view.findViewById(R.id.clear_button);
        clearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = false;
                dismiss();
            }
        });
        confirm = view.findViewById(R.id.confirm_button);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (BedAssign bedAssign : list) {
                    if (!TextUtils.isEmpty(bedAssign.getPersonName())) {
                        Calendar _c = new GregorianCalendar();
                        _c.setTimeInMillis(date);
                        Date[] dates = MyTool.getAWeekDates(_c.getTime());
                        _c.setTime(dates[6]);
                        int year = _c.get(Calendar.YEAR);
                        int weekOfYear = _c.get(Calendar.WEEK_OF_YEAR);
                        int number = year * 100 + weekOfYear;
                        if(number != bedAssign.getNumber()){
                            BedAssign temp = new BedAssign();
                            temp.setNumber(number);
                            temp.setPersonName(bedAssign.getPersonName());
                            temp.setAssign(bedAssign.getAssign());
                            temp.save();
                        }else{
                            bedAssign.save();
                        }
                    }
                }
                flag = true;
                dismiss();
            }
        });
        return view;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        onDialogFragmentDismiss.onDissmiss(flag);
    }

    @Override
    public void onStart() {
        super.onStart();
        fillList(date, 1);
        Dialog dialog = getDialog();
        if (dialog != null) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            int width = dm.widthPixels > dm.heightPixels ? (int) (dm.heightPixels * 0.8) : (int) (dm.widthPixels * 0.8);
            int height = dm.widthPixels > dm.heightPixels ? (int) (dm.widthPixels * 0.8) : (int) (dm.heightPixels * 0.8);
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }
}
