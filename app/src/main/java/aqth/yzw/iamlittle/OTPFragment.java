package aqth.yzw.iamlittle;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Date;
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
    private List<ItemEntity> list;
    private OTPTotalAdapter adapter;
    private void updateList(){
        if(list == null)
            list = new ArrayList<>();
        list.clear();
        Cursor cursor = LitePal.findBySQL("SELECT DISTINCT recordTime,startDay,endDay FROM OverTimePay ORDER BY recordTime desc");
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
    private void countOTP(){
        getActivity().setTitle("开始统计");
        ((OTPActivity)getActivity()).setShowDetails(true);
        Fragment fragment = CountOTPFragment.newInstant(0);
        Fragment fragment1 = getFragmentManager().findFragmentByTag("Total");
        getFragmentManager().beginTransaction()
                .add(R.id.common_linerarlayout,fragment,"Count")
                .addToBackStack(null)
                .hide(fragment1)
                .show(fragment)
                .commit();
    }
    private void showDetails(Date recordTime){
        getActivity().setTitle("详细数据");
        ((OTPActivity)getActivity()).setShowDetails(true);
        OTPFragment fragment1 = (OTPFragment) getFragmentManager().findFragmentByTag("Total");
        OTPDetailsFragment fragment = OTPDetailsFragment.newInstant(recordTime);
        getFragmentManager().beginTransaction()
                .add(R.id.common_linerarlayout,fragment,"Details")
                .addToBackStack(null)
                .hide(fragment1)
                .show(fragment)
                .commit();
    }
    @Override
    public View onCreateView( LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle("历史数据列表");
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.show_total_fragment_layout,container,false);
        updateList();
        recyclerView = view.findViewById(R.id.fragment_recyclerview);
        adapter = new OTPTotalAdapter(list);
        adapter.setItemClickListener(new IItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                Date date =((OTPTotalEntity)list.get(position)).getRecordTime();
                showDetails(date);
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.title_menu_add,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.title_menu_add){
            countOTP();
            return true;
        }
        return  super.onOptionsItemSelected(item);
    }
}
