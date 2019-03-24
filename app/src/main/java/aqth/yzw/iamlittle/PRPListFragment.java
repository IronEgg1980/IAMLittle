package aqth.yzw.iamlittle;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import aqth.yzw.iamlittle.Adapters.PRPTotalAdapter;
import aqth.yzw.iamlittle.EntityClass.ItemEntity;
import aqth.yzw.iamlittle.EntityClass.ItemEntityJXGZTotal;
import aqth.yzw.iamlittle.EntityClass.JXGZDetails;

public class PRPListFragment extends Fragment {
    private List<ItemEntity> list;
    private RecyclerView recyclerView;
    private PRPTotalAdapter adapter;
    public void notifyDataChange(){
        updateList();
        adapter.notifyDataSetChanged();
    }
    private void updateList(){
        if(list == null)
            list = new ArrayList<>();
        list.clear();
        List<Long> tempDate = new ArrayList<>();
        Cursor cursor = LitePal.findBySQL("SELECT DISTINCT recordTime FROM JXGZDetails ORDER BY recordTime desc");
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
    }
    private void showDetails(Date recordTime){

    }
    private void add(){

    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list =  new ArrayList<>();
        adapter = new PRPTotalAdapter(list);
        adapter.setItemClickListener(new IItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                Date date = (Date)view.getTag();
                showDetails(date);
            }

            @Override
            public void onClick(View view, int x, int y) {

            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View  view = inflater.inflate(R.layout.show_total_fragment_layout,container,false);
        recyclerView = view.findViewById(R.id.fragment_recyclerview);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        setHasOptionsMenu(true);
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
