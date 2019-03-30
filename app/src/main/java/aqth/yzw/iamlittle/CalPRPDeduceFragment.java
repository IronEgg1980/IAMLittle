package aqth.yzw.iamlittle;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import aqth.yzw.iamlittle.Adapters.JXGZDeduceRecyclerViewAdapter;
import aqth.yzw.iamlittle.EntityClass.JXGZPersonDetailsTemp;

public class CalPRPDeduceFragment extends Fragment {
    private CalculatePRP activity;
    private List<JXGZPersonDetailsTemp> deduceItemList,othersList;
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
        deduceByDaysRB = view.findViewById(R.id.calprp_deduce_fragment_bydaysRB);
        deduceByAmountRB = view.findViewById(R.id.calprp_deduce_fragment_byamountRB);
        asignByRatioRB = view.findViewById(R.id.calprp_deduce_fragment_signRB1);
        asignAverageRB = view.findViewById(R.id.calprp_deduce_fragment_signRB2);
        deduceItemRLV = view.findViewById(R.id.calprp_deduce_fragment_deduceItemRecyclerview);
        selectPersonRLV = view.findViewById(R.id.calprp_deduce_fragment_selectPersonRecyclerview);
        deducePersonTV = view.findViewById(R.id.calprp_deduce_fragment_selectPerson);
        deduceModeTV = view.findViewById(R.id.calprp_deduce_fragment_TV);
        inputET = view.findViewById(R.id.calprp_deduce_fragment_deduceDaysET);
        return view;
    }
}
