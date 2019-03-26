package aqth.yzw.iamlittle;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import aqth.yzw.iamlittle.Adapters.PRPDetailsAdapter;
import aqth.yzw.iamlittle.EntityClass.ItemEntity;
import aqth.yzw.iamlittle.EntityClass.ItemEntityJXGZPersonDetails;
import aqth.yzw.iamlittle.EntityClass.ItemEntityJXGZPersonTotal;
import aqth.yzw.iamlittle.EntityClass.ItemEntityJXGZTotalDetails;
import aqth.yzw.iamlittle.EntityClass.JXGZDetails;
import aqth.yzw.iamlittle.EntityClass.JXGZPersonDetails;

public class PRPDetailsFragment extends Fragment {
    private List<ItemEntity> totalDetailsList, personDetailsList;
    private RecyclerView totalRecyclerView, personRecyclerView;
    private PRPDetailsAdapter totalAdapter, personAdapter;
    private int mode;
    private String recordTime;
    private PRPActivity activity;

    private void showToast(String content) {
        Toast toast = Toast.makeText(getContext(), content, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private void updateTotalList() {
        if (totalDetailsList == null)
            totalDetailsList = new ArrayList<>();
        totalDetailsList.clear();
        List<JXGZDetails> temp1 = LitePal.order("JXGZType")
                .where("recordTime = ?", recordTime).find(JXGZDetails.class);
        if (temp1 != null && temp1.size() > 0) {
            for (JXGZDetails details : temp1) {
                totalDetailsList.add(new ItemEntityJXGZTotalDetails(details));
            }
        }else{
            totalDetailsList.add(new ItemEntity());
        }
    }

    private void updatePersonList() {
        if (personDetailsList == null) {
            personDetailsList = new ArrayList<>();
        }
        personDetailsList.clear();
        List<String> people = new ArrayList<>();
        Cursor cursor = LitePal.findBySQL("SELECT DISTINCT personName FROM JXGZPersonDetails ORDER BY personName");
        if (cursor != null && cursor.moveToFirst()) {
            do {
                people.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        if (people.size() > 0) {
            for (String s : people) {
                List<JXGZPersonDetails> details = LitePal.where("personName = ? and recordTime = ?",
                        s, recordTime).find(JXGZPersonDetails.class);
                if (details != null) {
                    personDetailsList.add(new ItemEntityJXGZPersonTotal(details));
                }
            }
        }
    }
    private void updatePersonList2() {
        if (personDetailsList == null) {
            personDetailsList = new ArrayList<>();
        }
        personDetailsList.clear();
       for(int i = 0;i<9;i++){
           String name = "人员"+i;
           List<JXGZPersonDetails> temp = new ArrayList<>();
           for(int j = 1;j<5;j++){
               JXGZPersonDetails details = new JXGZPersonDetails();
               details.setPersonName(name);
               details.setThatRatio(1.2);
               details.setDate(Calendar.getInstance().getTime());
               details.setJXGZName("项目"+j);
               details.setJXGZType(j%4+1);
               details.setJXGZAmount(999.2* j+8.9);
               details.setRecordTime(Calendar.getInstance().getTime());
               temp.add(details);
           }
           personDetailsList.add(new ItemEntityJXGZPersonTotal(temp));
       }
    }
    private void showChild(int position) {
        //personAdapter.notifyItemChanged(position);
        ItemEntity itemEntity = personDetailsList.get(position);
        if (itemEntity.getType() == ItemType.JXGZ_PERSON_TOTAL) {
            ItemEntityJXGZPersonTotal total = (ItemEntityJXGZPersonTotal) itemEntity;
            int count = total.getChildCount();
            List<JXGZPersonDetails> temp = total.getList();
            int tempPos = position + 1;
            for (int i = 0; i < count; i++) {
                personDetailsList.add(tempPos + i, new ItemEntityJXGZPersonDetails(temp.get(i)));
                //personAdapter.notifyItemChanged(tempPos + i);
            }
            personAdapter.notifyItemRangeChanged(position, personAdapter.getItemCount() - position);
        }
    }

    private void hideChild(int position) {
        ItemEntity itemEntity = personDetailsList.get(position);
        if (itemEntity.getType() == ItemType.JXGZ_PERSON_TOTAL) {
            ItemEntityJXGZPersonTotal total = (ItemEntityJXGZPersonTotal) itemEntity;
            int count = total.getChildCount();
            for (int i = count; i > 0; i--) {
                personDetailsList.remove(position+i);
                personAdapter.notifyItemRemoved(position+i);
            }
            personAdapter.notifyItemRangeChanged(position, personAdapter.getItemCount() - position);
        }
    }

    private void dele() {
        LitePal.deleteAll(JXGZDetails.class,"recordTime = ?",recordTime);
        LitePal.deleteAll(JXGZPersonDetails.class,"recordTime = ?",recordTime);
        if(mode == 0){
            activity.finish();
        }else{
            activity.setShowDetails(false);
            activity.setTitle("绩效工资列表");
            getFragmentManager().popBackStackImmediate();
        }
        showToast("已删除");
    }

    private void share() {
        showToast("分享功能暂未启用，正在更新中！");
    }

    public static PRPDetailsFragment newInstant(int mode, String recordTime) {
        PRPDetailsFragment fragment = new PRPDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("Mode", mode);
        bundle.putString("RecordTime", recordTime);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mode = 0;
        recordTime = "";
        if (getArguments() != null) {
            mode = getArguments().getInt("Mode");
            recordTime = getArguments().getString("RecordTime");
        }
        totalDetailsList = new ArrayList<>();
        personDetailsList = new ArrayList<>();
        totalAdapter = new PRPDetailsAdapter(totalDetailsList);
        personAdapter = new PRPDetailsAdapter(personDetailsList);
        personAdapter.setItemClickListener(new IItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                boolean b = (boolean) view.getTag();
                if (b) {
                    showChild(position);
                } else {
                    hideChild(position);
                }
            }

            @Override
            public void onClick(View view, int x, int y) {

            }
        });
        activity = (PRPActivity)getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.prpdetails_fragment, container, false);
        personRecyclerView = view.findViewById(R.id.prpdetails_recyclerview2);
        personRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        personRecyclerView.setAdapter(personAdapter);
        totalRecyclerView = view.findViewById(R.id.prpdetails_recyclerview1);
        totalRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        totalRecyclerView.setAdapter(totalAdapter);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateTotalList();
//        updatePersonList();
        updatePersonList2();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.title_menu_dele_share, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.title_menu_dele) {
            dele();
        } else {
            share();
        }
        return super.onOptionsItemSelected(item);
    }
}
