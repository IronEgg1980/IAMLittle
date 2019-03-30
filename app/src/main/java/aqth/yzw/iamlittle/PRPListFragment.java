package aqth.yzw.iamlittle;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.litepal.LitePal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import aqth.yzw.iamlittle.Adapters.PRPTotalAdapter;
import aqth.yzw.iamlittle.EntityClass.ItemEntity;
import aqth.yzw.iamlittle.EntityClass.ItemEntityJXGZTotal;
import aqth.yzw.iamlittle.EntityClass.JXGZDetails;

public class PRPListFragment extends Fragment {
    private List<ItemEntity> list;
    private RecyclerView recyclerView;
    private PRPTotalAdapter adapter;
    private SimpleDateFormat format;
    public void notifyDataChange(){
        updateList();
        adapter.notifyDataSetChanged();
    }
    private void updateList(){
        if(list == null)
            list = new ArrayList<>();
        list.clear();
        List<Long> tempDate = new ArrayList<>();
        Cursor cursor = LitePal.findBySQL("SELECT DISTINCT recordTime,date FROM JXGZDetails ORDER BY date desc");
        if(cursor!=null&&cursor.moveToFirst()){
            do{
                tempDate.add(cursor.getLong(0));
            }while (cursor.moveToNext());
        }
        if(tempDate.size() == 0){
            list.add(new ItemEntity());
            return;
        }
        for(Long l:tempDate){
            List<JXGZDetails> temp = LitePal.where("recordTime = ?",String.valueOf(l))
                    .find(JXGZDetails.class);
            list.add(new ItemEntityJXGZTotal(temp));
        }
        if(list.size() == 0){
            list.add(new ItemEntity());
        }
    }
    private void showDetails(Date recordTime,Date date){
        Fragment list = getFragmentManager().findFragmentByTag("List");
        PRPDetailsFragment fragment = PRPDetailsFragment.newInstant(1,String.valueOf(recordTime.getTime()));
        getFragmentManager().beginTransaction()
                .add(R.id.common_linerarlayout,fragment)
                .addToBackStack(null)
                .hide(list)
                .show(fragment)
                .commit();
        PRPActivity activity = (PRPActivity)getActivity();
        activity.setShowDetails(true);
        activity.setTitle(format.format(date));
    }
    private void add(){
        Intent intent = new Intent();
        intent.setClass(getContext(),CalculatePRP.class);
        startActivity(intent);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list =  new ArrayList<>();
        format = new SimpleDateFormat("yyyy年M月份", Locale.CHINESE);
        adapter = new PRPTotalAdapter(list);
        adapter.setItemClickListener(new IItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                ItemEntity itemEntity = list.get(position);
                if(itemEntity.getType() == ItemType.JXGZ_TOTAL) {
                    ItemEntityJXGZTotal total = (ItemEntityJXGZTotal) itemEntity;
                    showDetails(total.getRecordTime(), total.getDate());
                }
            }

            @Override
            public void onClick(View view, int x, int y) {

            }
        });
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View  view = inflater.inflate(R.layout.show_total_fragment_layout,container,false);
        recyclerView = view.findViewById(R.id.fragment_recyclerview);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        setHasOptionsMenu(true);
        notifyDataChange();
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
            add();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
