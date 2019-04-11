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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import org.litepal.LitePal;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import aqth.yzw.iamlittle.Adapters.JXGZDeduceRecyclerViewAdapter;
import aqth.yzw.iamlittle.EntityClass.ItemEntity;
import aqth.yzw.iamlittle.EntityClass.ItemEntityJXGZPersonDetailsTemp;
import aqth.yzw.iamlittle.EntityClass.ItemEntityPerson;
import aqth.yzw.iamlittle.EntityClass.JXGZPersonDetailsTemp;
import aqth.yzw.iamlittle.EntityClass.JXGZSingleResultTemp;
import aqth.yzw.iamlittle.EntityClass.Person;

public class CalPRPDeduceFragment extends Fragment {
    private CalculatePRP activity;
    private List<ItemEntityJXGZPersonDetailsTemp> deduceItemList, othersList;
    private JXGZDeduceRecyclerViewAdapter deduceItemAdapter, selectPersonAdapter;
    private LinearLayout linearLayout;
    private boolean deduceByDays, asignByRatio;
    private RadioButton deduceByDaysRB, asignByRatioRB, deduceByAmountRB, asignAverageRB;
    private int maxDays, amountFlag, nameCount;
    private String deducePersonName, deduceName;
    private double deduceAmount, perAmount;
    private RecyclerView deduceItemRLV, selectPersonRLV;
    private TextView deducePersonTV, deduceModeTV;
    private EditText inputET, inputDeduceNameET;
    private CheckBox selectAllCB;
    private void initialFragment() {
        deduceByDaysRB.setChecked(deduceByDays);
        deduceByAmountRB.setChecked(!deduceByDays);
        asignByRatioRB.setChecked(asignByRatio);
        asignAverageRB.setChecked(!asignByRatio);
        if (deduceByDays) {
            linearLayout.setVisibility(View.VISIBLE);
            updateDeduceItemRLV(deducePersonName);
        } else {
            linearLayout.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(deducePersonName)) {
            updateSelectPersonRLV(deducePersonName);
            deducePersonTV.setText(deducePersonName);
        } else {
            deducePersonTV.setText("点击选择扣款人员");
        }
        inputDeduceNameET.setHint(deduceName);
    }

    private void clearInput() {
        deduceByDaysRB.setChecked(true);
        deduceByAmountRB.setChecked(false);
        asignByRatioRB.setChecked(true);
        asignAverageRB.setChecked(false);
        deduceByDays = true;
        asignByRatio = true;
        deducePersonName = "";
        deduceName = "扣款"+nameCount;
        linearLayout.setVisibility(View.VISIBLE);
        deducePersonTV.setText("点击选择扣款人员");
        deducePersonTV.setError(null);
        inputDeduceNameET.setHint(deduceName);
        inputDeduceNameET.setText("");
        inputET.setText("");
        inputET.setError(null);
        inputET.setHint("请输入天数");
        deduceModeTV.setText("扣款天数：");
        deduceItemList.clear();
        deduceItemAdapter.notifyDataSetChanged();
        othersList.clear();
        selectPersonAdapter.notifyDataSetChanged();
        selectAllCB.setChecked(false);
    }

    private void updateDeduceItemRLV(String personName) {
        if (deduceItemList == null)
            deduceItemList = new ArrayList<>();
        deduceItemList.clear();
        if (!TextUtils.isEmpty(personName)) {
            List<JXGZPersonDetailsTemp> list = LitePal.where("personName = ?", personName).find(JXGZPersonDetailsTemp.class);
            for (JXGZPersonDetailsTemp temp : list) {
                deduceItemList.add(new ItemEntityJXGZPersonDetailsTemp(temp));
            }
        }
    }

    private void updateSelectPersonRLV(String personName) {
        if (othersList == null)
            othersList = new ArrayList<>();
        othersList.clear();
        if (!TextUtils.isEmpty(personName)) {
            Cursor cursor = LitePal.findBySQL("SELECT DISTINCT personname,thatratio FROM JXGZPersonDetailsTemp WHERE personname != ?", personName);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    JXGZPersonDetailsTemp temp = new JXGZPersonDetailsTemp();
                    temp.setPersonName(cursor.getString(0));
                    temp.setThatRatio(cursor.getDouble(1));
                    othersList.add(new ItemEntityJXGZPersonDetailsTemp(temp));
                } while (cursor.moveToNext());
            }
        }
        selectAllCB.setChecked(false);
    }

    private void selectPerson() {
        SelectDeducePersonDialogFragment fragment = new SelectDeducePersonDialogFragment();
        fragment.setDialogFragmentDismiss(new OnDialogFragmentDismiss() {
            @Override
            public void onDissmiss(boolean flag) {

            }

            @Override
            public void onDissmiss(boolean flag, Object object) {
                if (flag) {
                    deducePersonName = (String) object;
                    deducePersonTV.setText(deducePersonName);
                    updateDeduceItemRLV(deducePersonName);
                    deduceItemAdapter.notifyDataSetChanged();
                    updateSelectPersonRLV(deducePersonName);
                    selectPersonAdapter.notifyDataSetChanged();
                }
            }
        });
        fragment.show(getFragmentManager(), "SelectPerson");
    }
    private void calculate() {  // 4.3晚看到这里
        LitePal.deleteAll(JXGZSingleResultTemp.class);
        if (checkInput()) {
            String s = "扣款人员：" + deducePersonName + "，扣款金额：" + Arith.doubleToString(deduceAmount, amountFlag);
            perAmount = 0;
            if (asignByRatio) {
                double totalRatio = 0;
                for (ItemEntityJXGZPersonDetailsTemp temp : othersList) {
                    if (temp.isSelect()) {
                        totalRatio =Arith.add(totalRatio,temp.getJXGZPersonDetails().getThatRatio());
                    }
                }
                if (totalRatio == 0)
                    perAmount = 0;
                else {
                    perAmount = Arith.div(deduceAmount,totalRatio);
                }
                s += "\n分配方式：按系数分配，总系数：" + Double.toString(totalRatio) +
                        "，1.0系数分配金额：" + Arith.doubleToString(perAmount, amountFlag) +
                        "\n\n分配明细如下：";
            } else {
                int count = 0;
                for (ItemEntityJXGZPersonDetailsTemp temp : othersList) {
                    if (temp.isSelect()) {
                        count++;
                    }
                }
                if (count == 0)
                    perAmount = 0;
                else {
                    perAmount = Arith.div(deduceAmount,count);
                }
                s += "\n分配方式：平均分配，分配人数：" + Integer.toString(count) +
                        "人，平均每人分配金额：" + Arith.doubleToString(perAmount, amountFlag) +
                        "\n\n分配明细如下：";
            }
            for (ItemEntityJXGZPersonDetailsTemp temp : othersList) {
                if (temp.isSelect()) {
                    String name = temp.getJXGZPersonDetails().getPersonName();
                    double ratio = temp.getJXGZPersonDetails().getThatRatio();
                    double amount = perAmount;
                    if(asignByRatio){
                        amount = Arith.mul(ratio, perAmount,amountFlag);
                    }
                    JXGZSingleResultTemp singleResultTemp = new JXGZSingleResultTemp();
                    singleResultTemp.setPersonName(name);
                    singleResultTemp.setRatio(ratio);
                    singleResultTemp.setAmount(amount);
                    singleResultTemp.setScale(amountFlag);
                    singleResultTemp.save();
                }
            }
            showResultDialog(s);
        }
    }
    private boolean checkInput() {
        double day = 0;
        boolean b = false, b2 = false;
        deduceAmount = 0;
        if (TextUtils.isEmpty(deducePersonName)) {
//            activity.showToast("没有选择扣款人员");
            deducePersonTV.setError("没有选择人员");
            deducePersonTV.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(inputET.getText())) {
            inputET.setError("没有输入天数或者金额");
            inputET.requestFocus();
            return false;
        }
        if (deduceByDays) {
            day = new BigDecimal(inputET.getText().toString().trim()).doubleValue();
            if (Arith.sub(day,0) < 0) {
                inputET.setError("请输入有效天数");
                inputET.requestFocus();
                return false;
            } else if (Arith.sub(day,maxDays)>0) {
                inputET.setError("大于月份最大天数");
                inputET.setText(maxDays+"");
                inputET.selectAll();
                inputET.requestFocus();
                return false;
            }
            for (ItemEntityJXGZPersonDetailsTemp temp : deduceItemList) {
                if (temp.isSelect()) {
                    b = true;
                    deduceAmount =Arith.add(deduceAmount,temp.getJXGZPersonDetails().getJXGZAmount());
                }
            }
            if (!b) {
                activity.showToast("没有选择扣款项目");
                return false;
            }
            deduceAmount = Arith.div(Arith.mul(deduceAmount ,day ), maxDays);
        } else {
            deduceAmount = new BigDecimal(inputET.getText().toString().trim()).doubleValue();
            if (deduceAmount < 0) {
                inputET.setError("请输入有效金额");
                inputET.requestFocus();
                return false;
            }
        }
        for (ItemEntityJXGZPersonDetailsTemp temp : othersList) {
            if (temp.isSelect()) {
                b2 = true;
                break;
            }
        }
        if (!b2) {
            activity.showToast("没有选择分配人员");
            return false;
        }
        return true;
    }
    private void showResultDialog(String content){
        CalPRPShowResultDialogFragment fragment = CalPRPShowResultDialogFragment.newInstant(content);
        fragment.setOnDialogFragmentDismiss(new OnDialogFragmentDismiss() {
            @Override
            public void onDissmiss(boolean flag) {
                if (flag) {
                    double thatRatio = 0;
                    JXGZPersonDetailsTemp temp1 = LitePal.where("personName = ?", deducePersonName).findFirst(JXGZPersonDetailsTemp.class);
                    if (temp1 == null) {
                        Person person = LitePal.where("name = ?", deducePersonName).findFirst(Person.class);
                        if (person != null) {
                            thatRatio = person.getRatio();
                        }
                    } else {
                        thatRatio = temp1.getThatRatio();
                    }
                    deduceName = "扣款" + nameCount;
                    if (!TextUtils.isEmpty(inputDeduceNameET.getText())) {
                        deduceName = inputDeduceNameET.getText().toString().trim();
                    }
                    // 保存
                    JXGZPersonDetailsTemp temp = new JXGZPersonDetailsTemp();
                    temp.setPersonName(deducePersonName);
                    temp.setJXGZName(deduceName);
                    temp.setJXGZType(MyTool.JXGZ_DEDUCE);
                    temp.setJXGZAmount(Arith.mul(deduceAmount,-1.0,amountFlag));
                    temp.setThatRatio(thatRatio);
                    temp.setScale(amountFlag);
                    temp.save();
                    for (ItemEntityJXGZPersonDetailsTemp item : othersList) {
                        if (item.isSelect()) {
                            JXGZPersonDetailsTemp temp2 = new JXGZPersonDetailsTemp();
                            temp2.setPersonName(item.getJXGZPersonDetails().getPersonName());
                            temp2.setJXGZName("分得" + deducePersonName + "的扣款");
                            temp2.setJXGZType(MyTool.JXGZ_ADD);
                            temp2.setThatRatio(item.getJXGZPersonDetails().getThatRatio());
                            double amount = 0;
                            if (asignByRatio)
                                amount = Arith.mul(perAmount,item.getJXGZPersonDetails().getThatRatio(), amountFlag);
                            else
                                amount = Arith.mul(perAmount,1.0,amountFlag);
                            temp2.setJXGZAmount(amount);
                            temp2.setScale(amountFlag);
                            temp2.save();
                        }
                    }
                    LitePal.deleteAll(JXGZSingleResultTemp.class);
                    nameCount++;
                    clearInput();
                    activity.setHasCheckouted(false);
                    activity.showToast("保存成功");
                }
            }

            @Override
            public void onDissmiss(boolean flag, Object object) {

            }
        });
        fragment.show(getFragmentManager(), "ShowResult");
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (CalculatePRP) getActivity();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(activity.getDate());
        deduceItemList = new ArrayList<>();
        othersList = new ArrayList<>();
        deduceItemAdapter = new JXGZDeduceRecyclerViewAdapter(deduceItemList, MyTool.DEDUCE_ITEM_MODE);
        selectPersonAdapter = new JXGZDeduceRecyclerViewAdapter(othersList, MyTool.SELECT_OTHERSPERSON_MODE);
        selectPersonAdapter.setClickListener(new IItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                selectAll();
            }

            @Override
            public void onClick(View view, int x, int y) {

            }
        });
        if (savedInstanceState == null) {
            nameCount = 1;
            deducePersonName = "";
            deduceName = "扣款" + nameCount;
            deduceByDays = true;
            asignByRatio = true;
            maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            deduceAmount = 0;
        } else {
            deduceByDays = savedInstanceState.getBoolean("DeduceByDays");
            asignByRatio = savedInstanceState.getBoolean("AsignByRatio");
            maxDays = savedInstanceState.getInt("MaxDays");
            deducePersonName = savedInstanceState.getString("DeducePersonName");
            nameCount = savedInstanceState.getInt("NameCount");
            deduceAmount = savedInstanceState.getDouble("DeduceAmount");
            deduceName = "扣款" + nameCount;
        }
        amountFlag = (int)new SharedPreferencesHelper(getContext()).getValue("AmountFlag",2);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("DeduceByDays", deduceByDays);
        outState.putBoolean("AsignByRatio", asignByRatio);
        outState.putInt("MaxDays", maxDays);
        outState.putString("DeducePersonName", deducePersonName);
        outState.putInt("NameCount", nameCount);
        outState.putDouble("DeduceAmount", deduceAmount);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calprp_deduce_fragment_layout, container, false);
        linearLayout = view.findViewById(R.id.calprp_deduce_fragment_linear);
        deduceModeTV = view.findViewById(R.id.calprp_deduce_fragment_TV);
        inputET = view.findViewById(R.id.calprp_deduce_fragment_deduceDaysET);
        inputDeduceNameET = view.findViewById(R.id.calprp_deduce_fragment_deduceName);
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
                if (isChecked) {
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
                if (isChecked) {
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
        view.findViewById(R.id.calprp_deduce_fragment_cancelBT).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearInput();
            }
        });
        view.findViewById(R.id.calprp_deduce_fragment_confirmBT).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculate();
            }
        });
        selectAllCB = view.findViewById(R.id.select_all_checkbox);
        selectAllCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(ItemEntity itemEntity:othersList){
                    if(itemEntity.getType() == ItemType.EMPTY)
                        continue;
                    ItemEntityJXGZPersonDetailsTemp person = (ItemEntityJXGZPersonDetailsTemp)itemEntity;
                    person.setSelect(selectAllCB.isChecked());
                    selectPersonAdapter.notifyDataSetChanged();
                }
            }
        });
        selectAll();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        initialFragment();
    }
    private void selectAll(){
        if(othersList.size() == 0) {
            selectAllCB.setChecked(false);
            return;
        }
        boolean b = true;
        for(ItemEntityJXGZPersonDetailsTemp itemEntity:othersList){
            if(!itemEntity.isSelect()){
                b = false;
                break;
            }
        }
        selectAllCB.setChecked(b);
    }
}
