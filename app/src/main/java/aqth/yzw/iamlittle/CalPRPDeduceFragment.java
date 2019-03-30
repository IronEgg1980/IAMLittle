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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import aqth.yzw.iamlittle.Adapters.JXGZDeduceRecyclerViewAdapter;
import aqth.yzw.iamlittle.EntityClass.ItemEntityJXGZPersonDetailsTemp;
import aqth.yzw.iamlittle.EntityClass.JXGZPersonDetailsTemp;

public class CalPRPDeduceFragment extends Fragment {
    private CalculatePRP activity;
    private List<ItemEntityJXGZPersonDetailsTemp> deduceItemList,othersList;
    private JXGZDeduceRecyclerViewAdapter deduceItemAdapter,selectPersonAdapter;
    private LinearLayout linearLayout;
    private boolean deduceByDays,asignByRatio;
    private RadioButton deduceByDaysRB,asignByRatioRB,deduceByAmountRB,asignAverageRB;
    private int maxDays,deduceDays;
    private String deducePersonName;
    private double deduceAmount;
    private RecyclerView deduceItemRLV,selectPersonRLV;
    private TextView deducePersonTV,deduceModeTV;
    private EditText inputET;
    private void initialFragment(){
        deduceByDaysRB.setChecked(deduceByDays);
        deduceByAmountRB.setChecked(!deduceByDays);
        asignByRatioRB.setChecked(asignByRatio);
        asignAverageRB.setChecked(!asignByRatio);
        if(deduceByDays){
            linearLayout.setVisibility(View.VISIBLE);
            updateDeduceItemRLV(deducePersonName);
        }else{
            linearLayout.setVisibility(View.GONE);
        }
        if(!TextUtils.isEmpty(deducePersonName)) {
            updateSelectPersonRLV(deducePersonName);
            deducePersonTV.setText(deducePersonName);
        }else{
            deducePersonTV.setText("点击选择扣款人员");
        }
    }

    private void updateDeduceItemRLV(String personName){
        if(deduceItemList == null)
            deduceItemList = new ArrayList<>();
        deduceItemList.clear();
        if(!TextUtils.isEmpty(personName)){
            List<JXGZPersonDetailsTemp> list = LitePal.where("personName = ?",personName).find(JXGZPersonDetailsTemp.class);
            for(JXGZPersonDetailsTemp temp:list){
                deduceItemList.add(new ItemEntityJXGZPersonDetailsTemp(temp));
            }
        }
        deduceItemAdapter.notifyDataSetChanged();
    }
    private void updateSelectPersonRLV(String personName){
        if(othersList == null)
            othersList = new ArrayList<>();
        othersList.clear();
        if(!TextUtils.isEmpty(personName)){
            Cursor cursor = LitePal.findBySQL("SELECT DISTINCT personname,thatratio FROM JXGZPersonDetailsTemp WHERE personname != ? GROUPBY personname",personName);
            if(cursor!=null && cursor.moveToFirst()){
                do{
                    JXGZPersonDetailsTemp temp = new JXGZPersonDetailsTemp();
                    temp.setPersonName(cursor.getString(0));
                    temp.setThatRatio(cursor.getDouble(1));
                    othersList.add(new ItemEntityJXGZPersonDetailsTemp(temp));
                }while (cursor.moveToNext());
            }
        }
        selectPersonAdapter.notifyDataSetChanged();
    }
    private void selectPerson(){
        //写到这里
        deducePersonName = "";
        deducePersonTV.setText(deducePersonName);
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (CalculatePRP)getActivity();
        activity.setTitle("计算扣款");
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(activity.getDate());
        deduceItemList = new ArrayList<>();
        othersList = new ArrayList<>();
        deduceItemAdapter = new JXGZDeduceRecyclerViewAdapter(deduceItemList,MyTool.DEDUCE_ITEM_MODE);
        selectPersonAdapter = new JXGZDeduceRecyclerViewAdapter(othersList,MyTool.SELECT_OTHERSPERSON_MODE);
        if(savedInstanceState == null){
            deducePersonName = "";
            deduceByDays = true;
            asignByRatio = true;
            maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            deduceDays = 0;
            deduceAmount = 0;
        }else{
            deduceByDays = savedInstanceState.getBoolean("DeduceByDays");
            asignByRatio = savedInstanceState.getBoolean("AsignByRatio");
            maxDays = savedInstanceState.getInt("MaxDays");
            deducePersonName = savedInstanceState.getString("DeducePersonName");
            deduceDays = savedInstanceState.getInt("DeduceDays");
            deduceAmount = savedInstanceState.getDouble("DeduceAmount");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.calprp_deduce_fragment_layout,container,false);
        linearLayout = view.findViewById(R.id.calprp_deduce_fragment_linear);
        deduceModeTV = view.findViewById(R.id.calprp_deduce_fragment_TV);
        inputET = view.findViewById(R.id.calprp_deduce_fragment_deduceDaysET);
        deducePersonTV = view.findViewById(R.id.calprp_deduce_fragment_selectPerson);
        deducePersonTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPerson();
            }
        });
        deduceByDaysRB = view.findViewById(R.id.calprp_deduce_fragment_bydaysRB);
        deduceByDaysRB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    deduceByDays = true;
                    if (linearLayout.getVisibility() == View.GONE)
                        linearLayout.setVisibility(View.VISIBLE);
                    updateDeduceItemRLV(deducePersonName);
                    deduceModeTV.setText("扣款天数：");
                    inputET.setHint("请输入天数");
                    inputET.requestFocus();
                }
            }
        });
        deduceByAmountRB = view.findViewById(R.id.calprp_deduce_fragment_byamountRB);
        deduceByAmountRB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    deduceByDays = false;
                    if (linearLayout.getVisibility() == View.VISIBLE)
                        linearLayout.setVisibility(View.GONE);
                    deduceModeTV.setText("扣款金额：");
                    inputET.setHint("请输入金额");
                    inputET.requestFocus();
                }
            }
        });
        asignByRatioRB = view.findViewById(R.id.calprp_deduce_fragment_signRB1);
        asignByRatioRB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                asignByRatio = true;
            }
        });
        asignAverageRB = view.findViewById(R.id.calprp_deduce_fragment_signRB2);
        asignAverageRB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                asignByRatio = false;
            }
        });
        deduceItemRLV = view.findViewById(R.id.calprp_deduce_fragment_deduceItemRecyclerview);
        deduceItemRLV.setLayoutManager(new LinearLayoutManager(getContext()));
        deduceItemRLV.setAdapter(deduceItemAdapter);
        selectPersonRLV = view.findViewById(R.id.calprp_deduce_fragment_selectPersonRecyclerview);
        selectPersonRLV.setLayoutManager(new LinearLayoutManager(getContext()));
        selectPersonRLV.setAdapter(selectPersonAdapter);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        initialFragment();
    }
}
