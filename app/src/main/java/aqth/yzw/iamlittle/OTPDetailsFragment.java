package aqth.yzw.iamlittle;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.litepal.LitePal;
import org.w3c.dom.Entity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import aqth.yzw.iamlittle.Adapters.OTPDetailsAdapter;
import aqth.yzw.iamlittle.EntityClass.ItemEntity;
import aqth.yzw.iamlittle.EntityClass.OTPDetails;
import aqth.yzw.iamlittle.EntityClass.OTPPersonTotalEntity;
import aqth.yzw.iamlittle.EntityClass.OverTimePay;

public class OTPDetailsFragment extends Fragment {
    private List<ItemEntity> list;
    private RecyclerView recyclerView;
    private FloatingActionButton actionButton;
    private OTPDetailsAdapter adapter;
    private long recordTime;
    private void fillList(){
        if(list == null)
            list = new ArrayList<>();
        list.clear();
        Cursor cursor = LitePal.findBySQL("SELECT DISTINCT personName FROM OverTimePay WHERE recordTime = '"+String.valueOf(recordTime)+"'");
        List<String> person = new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                person.add(cursor.getString(0));
            }while (cursor.moveToNext());
        }
        for(String personName:person) {
            List<OverTimePay> temp = LitePal.order("shiftName")
                    .where("recordtime = ? and personName = ?", String.valueOf(recordTime),personName).find(OverTimePay.class);

            OTPPersonTotalEntity total = new OTPPersonTotalEntity(temp);
            list.add(total);
            for (OverTimePay o : temp) {
                OTPDetails details = new OTPDetails(o);
                list.add(details);
            }
        }
    }
    private void deleData(){
        MyDialogFragment dialogFragment = MyDialogFragment.newInstant("是否清除当前时间段的所有数据？注意：清除后不能恢复！","取消","清除",
                Color.BLACK,Color.RED);
        dialogFragment.setOnDialogFragmentDismiss(new OnDialogFragmentDismiss() {
            @Override
            public void onDissmiss(boolean flag) {
                if(flag){
                    LitePal.deleteAll(OverTimePay.class,"recordTime = ?",String.valueOf(recordTime));
                    fillList();
                    adapter.notifyDataSetChanged();
                    Toast toast = Toast.makeText(getContext(),"已删除数据",Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                }
            }

            @Override
            public void onDissmiss(boolean flag, Object object) {

            }
        });
        dialogFragment.show(getFragmentManager(),"DeleData");
    }
    public static OTPDetailsFragment newInstant(Date date){
        OTPDetailsFragment fragment = new OTPDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("RecordTime",date.getTime());
        fragment.setArguments(bundle);
        return fragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recordTime = Calendar.getInstance().getTimeInMillis();
        if(getArguments() != null){
            recordTime = getArguments().getLong("RecordTime");
        }
        fillList();
        adapter = new OTPDetailsAdapter(list);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.show_total_fragment_layout,container,false);
        getActivity().setTitle("详细信息");
        actionButton = view.findViewById(R.id.fragment_floatingButton);
        actionButton.setImageDrawable(getActivity().getDrawable(R.drawable.ic_delete_white_24dp));
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleData();
            }
        });
        recyclerView = view.findViewById(R.id.fragment_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        return view;
    }
}
