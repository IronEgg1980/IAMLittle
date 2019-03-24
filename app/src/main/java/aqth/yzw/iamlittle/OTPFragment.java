package aqth.yzw.iamlittle;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import aqth.yzw.iamlittle.Adapters.OTPTotalAdapter;
import aqth.yzw.iamlittle.EntityClass.ItemEntity;
import aqth.yzw.iamlittle.EntityClass.OTPTotalEntity;

public class OTPFragment extends Fragment {
    private String TAG = "殷宗旺";
    public void notifyDataChange(){
        updateList();
        adapter.notifyDataSetChanged();
    }
    private RecyclerView recyclerView;
    private FloatingActionButton actionButton;
    private List<ItemEntity> list;
    private OTPTotalAdapter adapter;
    private void updateList(){
        Calendar c = new GregorianCalendar();
        if(list == null)
            list = new ArrayList<>();
        list.clear();
        Cursor cursor = LitePal.findBySQL("SELECT DISTINCT recordTime,startDay,endDay FROM OverTimePay");
        if(cursor.moveToFirst()){
            do{
                OTPTotalEntity entity = new OTPTotalEntity();
                entity.setRecordTime(new Date(cursor.getLong(0)));
                entity.setStartDay(new Date(cursor.getLong(1)));
                entity.setEndDay(new Date(cursor.getLong(2)));
                list.add(entity);
            }while (cursor.moveToNext());
        }
        if(list.size() == 0)
            list.add(new ItemEntity());
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("统计加班");
        View view = inflater.inflate(R.layout.show_total_fragment_layout,container,false);
        updateList();
        recyclerView = view.findViewById(R.id.fragment_recyclerview);
        actionButton = view.findViewById(R.id.fragment_floatingButton);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getContext(),"添加数据",Toast.LENGTH_SHORT).show();
                ((ShowDataCommonActivity)getActivity()).setShowDetails(true);
                Fragment fragment = new CountOTPFragment();
                Fragment fragment1 = getFragmentManager().findFragmentByTag("Total");
                getFragmentManager().beginTransaction()
                        .add(R.id.common_linerarlayout,fragment,"Details")
                        .addToBackStack(null)
                        .hide(fragment1)
                        .show(fragment)
                        .commit();
            }
        });
        adapter = new OTPTotalAdapter(list);
        adapter.setItemClickListener(new IItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                ((ShowDataCommonActivity)getActivity()).setShowDetails(true);
                OTPFragment fragment1 = (OTPFragment) getFragmentManager().findFragmentByTag("Total");
                Date date =((OTPTotalEntity)list.get(position)).getRecordTime();
                OTPDetailsFragment fragment = OTPDetailsFragment.newInstant(date);
                getFragmentManager().beginTransaction()
                        .add(R.id.common_linerarlayout,fragment,"Details")
                        .addToBackStack(null)
                        .hide(fragment1)
                        .show(fragment)
                        .commit();

            }

            @Override
            public void onClick(View view, int x, int y) {

            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),RecyclerView.VERTICAL));
        return view;
    }
}
