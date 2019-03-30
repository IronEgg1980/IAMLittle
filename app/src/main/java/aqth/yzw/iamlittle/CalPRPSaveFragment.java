package aqth.yzw.iamlittle;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.litepal.LitePal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import aqth.yzw.iamlittle.Adapters.PersonDetailsTempAdapter;
import aqth.yzw.iamlittle.EntityClass.ItemEntity;
import aqth.yzw.iamlittle.EntityClass.ItemEntityJXGZPersonDetailsTemp;
import aqth.yzw.iamlittle.EntityClass.ItemEntityJXGZPersonTotalTemp;
import aqth.yzw.iamlittle.EntityClass.ItemEntityJXGZTotalDetailsTemp;
import aqth.yzw.iamlittle.EntityClass.JXGZDetails;
import aqth.yzw.iamlittle.EntityClass.JXGZDetailsTemp;
import aqth.yzw.iamlittle.EntityClass.JXGZPersonDetails;
import aqth.yzw.iamlittle.EntityClass.JXGZPersonDetailsTemp;

public class CalPRPSaveFragment extends Fragment {
    private CalculatePRP activity;
    private List<ItemEntity> totalDetailsList, personDetailsList;
    private RecyclerView totalRecyclerView, personRecyclerView;
    private PersonDetailsTempAdapter totalAdapter, personAdapter;
    private TextView total;
    private SimpleDateFormat format;
    private int amountFlag = 2;
    private Button confirmBT;
    private Date recordTime;
    private void updateTotalList() {
        double totalValue  = 0;
        if (totalDetailsList == null)
            totalDetailsList = new ArrayList<>();
        totalDetailsList.clear();
        List<JXGZDetailsTemp> temp1 = LitePal.order("JXGZType").find(JXGZDetailsTemp.class);
        if (temp1 != null && temp1.size() > 0) {
            for (JXGZDetailsTemp details : temp1) {
                totalValue+=details.getJXGZAmount();
                totalDetailsList.add(new ItemEntityJXGZTotalDetailsTemp(details));
            }
        }else{
            totalDetailsList.add(new ItemEntity());
        }
        total.setText(format.format(activity.getDate())+"\n总金额："+MyTool.doubleToString(totalValue,amountFlag));
    }

    private void updatePersonList() {
        if (personDetailsList == null) {
            personDetailsList = new ArrayList<>();
        }
        personDetailsList.clear();
        List<String> people = new ArrayList<>();
        Cursor cursor = LitePal.findBySQL("SELECT DISTINCT personName FROM JXGZPersonDetailsTemp");
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String name = cursor.getString(0);
                if(TextUtils.isEmpty(name))
                    continue;
                people.add(name);
            } while (cursor.moveToNext());
        }
        if (people.size() > 0) {
            for (String s : people) {
                List<JXGZPersonDetailsTemp> details = LitePal.where("personName = ?",
                        s).find(JXGZPersonDetailsTemp.class);
                if (details != null) {
                    personDetailsList.add(new ItemEntityJXGZPersonTotalTemp(details));
                }
            }
        }
    }
    private void showChild(int position) {
        //personAdapter.notifyItemChanged(position);
        ItemEntity itemEntity = personDetailsList.get(position);
        if (itemEntity.getType() == ItemType.JXGZ_PERSON_TOTAL) {
            ItemEntityJXGZPersonTotalTemp total = (ItemEntityJXGZPersonTotalTemp) itemEntity;
            int count = total.getChildCount();
            List<JXGZPersonDetailsTemp> temp = total.getList();
            int tempPos = position + 1;
            for (int i = 0; i < count; i++) {
                personDetailsList.add(tempPos + i, new ItemEntityJXGZPersonDetailsTemp(temp.get(i)));
                //personAdapter.notifyItemChanged(tempPos + i);
            }
            personAdapter.notifyItemRangeChanged(position, personAdapter.getItemCount() - position);
        }
    }

    private void hideChild(int position) {
        ItemEntity itemEntity = personDetailsList.get(position);
        if (itemEntity.getType() == ItemType.JXGZ_PERSON_TOTAL) {
            ItemEntityJXGZPersonTotalTemp total = (ItemEntityJXGZPersonTotalTemp) itemEntity;
            int count = total.getChildCount();
            for (int i = count; i > 0; i--) {
                personDetailsList.remove(position+i);
                personAdapter.notifyItemRemoved(position+i);
            }
            personAdapter.notifyItemRangeChanged(position, personAdapter.getItemCount() - position);
        }
    }
    private void exit(){
        MyDialogFragment fragment = MyDialogFragment.newInstant("确定不保存吗？","取消","确定");
        fragment.setOnDialogFragmentDismiss(new OnDialogFragmentDismiss() {
            @Override
            public void onDissmiss(boolean flag) {
                if(flag){
                    LitePal.deleteAll(JXGZDetailsTemp.class);
                    LitePal.deleteAll(JXGZPersonDetailsTemp.class);
                    activity.finish();
                }
            }

            @Override
            public void onDissmiss(boolean flag, Object object) {

            }
        });
        fragment.show(getFragmentManager(),"Exit");
    }
    private void save(){
        if(!activity.isHasCheckouted()){
            activity.showToast("请先调整数据，确保数据准确，再保存");
            activity.checkOutData();
            return;
        }
        try {
            recordTime = Calendar.getInstance().getTime();
            confirmBT.setEnabled(false);
            for (JXGZDetailsTemp temp : LitePal.findAll(JXGZDetailsTemp.class)) {
                JXGZDetails details = new JXGZDetails();
                details.setRecordTime(recordTime);
                details.setDate(activity.getDate());
                details.setJXGZName(temp.getJXGZName());
                details.setJXGZType(temp.getJXGZType());
                details.setJXGZAmount(temp.getJXGZAmount());
                details.save();
            }
            for(JXGZPersonDetailsTemp temp : LitePal.findAll(JXGZPersonDetailsTemp.class)){
                JXGZPersonDetails details = new JXGZPersonDetails();
                details.setRecordTime(recordTime);
                details.setDate(activity.getDate());
                details.setJXGZName(temp.getJXGZName());
                details.setJXGZType(temp.getJXGZType());
                details.setJXGZAmount(temp.getJXGZAmount());
                details.setPersonName(temp.getPersonName());
                details.setThatRatio(temp.getThatRatio());
                details.save();
            }
            LitePal.deleteAll(JXGZDetailsTemp.class);
            LitePal.deleteAll(JXGZPersonDetailsTemp.class);
            activity.showToast("保存成功！");
            activity.finish();
        }catch (Exception e){
            e.printStackTrace();
            LitePal.deleteAll(JXGZDetails.class,"recordtime = ?",String.valueOf(recordTime.getTime()));
            LitePal.deleteAll(JXGZPersonDetails.class,"recordtime = ?",String.valueOf(recordTime.getTime()));
            MyDialogFragmenSingleButton fragment = MyDialogFragmenSingleButton.newInstant("保存失败！可能原因："+e.getMessage(),"关闭");
            fragment.show(getFragmentManager(),"SaveFail");
            confirmBT.setEnabled(true);
        }
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (CalculatePRP)getActivity();
        totalDetailsList = new ArrayList<>();
        personDetailsList = new ArrayList<>();
        format = new SimpleDateFormat("绩效工资月份：yyyy年M月份");
        totalAdapter = new PersonDetailsTempAdapter(totalDetailsList);
        personAdapter = new PersonDetailsTempAdapter(personDetailsList);
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
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calprp_showresult_fragment_layout, container, false);
        personRecyclerView = view.findViewById(R.id.calprp_showresult_fragment_recyclerview2);
        personRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        personRecyclerView.setAdapter(personAdapter);
        totalRecyclerView = view.findViewById(R.id.calprp_showresult_fragment_recyclerview1);
        totalRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        totalRecyclerView.setAdapter(totalAdapter);
        view.findViewById(R.id.calprp_showresult_fragment_cancelBT).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exit();
            }
        });
        confirmBT = view.findViewById(R.id.calprp_showresult_fragment_confirmBT);
        confirmBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
        total = view.findViewById(R.id.calprp_showresult_fragment_total);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateTotalList();
        updatePersonList();
        totalAdapter.notifyDataSetChanged();
        personAdapter.notifyDataSetChanged();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        updateTotalList();
        updatePersonList();
        totalAdapter.notifyDataSetChanged();
        personAdapter.notifyDataSetChanged();
    }
}
