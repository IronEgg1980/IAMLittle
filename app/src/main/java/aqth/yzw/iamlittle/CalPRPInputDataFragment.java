package aqth.yzw.iamlittle;

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
import android.widget.EditText;
import android.widget.RadioButton;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import aqth.yzw.iamlittle.Adapters.PersonSelectAdapter;
import aqth.yzw.iamlittle.EntityClass.ItemEntity;
import aqth.yzw.iamlittle.EntityClass.ItemEntityPerson;
import aqth.yzw.iamlittle.EntityClass.JXGZDetailsTemp;
import aqth.yzw.iamlittle.EntityClass.JXGZPersonDetailsTemp;
import aqth.yzw.iamlittle.EntityClass.JXGZSingleResultTemp;
import aqth.yzw.iamlittle.EntityClass.Person;

public class CalPRPInputDataFragment extends Fragment {
    private CalculatePRP activity;
    private String itemName;
    private double totalAmount;
    private boolean isDeduce;
    private boolean isByRatio;
    private EditText itemNameET, totalAmountET;
    private RadioButton isAsignRB, isDeduceRB, isByRatioRB, isAverageRB;
    private RecyclerView recyclerView;
    private Button cancelBT, confirmBT;
    private List<ItemEntity> list;
    private Date date;
    private PersonSelectAdapter adapter;
    private int ratioCount, averageCount, deduceCount;
    private String hintString;
    private double totalRatio;
    private int personCount;
    private int type;
    private double perAmount;
    private void updatePersonList() {
        String[] dates = MyTool.getMonthStartAndEndString(date);
        if (list == null) {
            list = new ArrayList<>();
        }
        list.clear();
        List<Person> temp = LitePal.order("Id").find(Person.class);
        for (Person p : temp) {
            String name = p.getName();
            double ratio = p.getRatio();
            list.add(new ItemEntityPerson(name, ratio));
        }
        list.add(new ItemEntity());
    }

    private boolean checkInput() {
        isDeduce = isDeduceRB.isChecked();
        isByRatio = isByRatioRB.isChecked();
        if(TextUtils.isEmpty(totalAmountET.getText())) {
            totalAmountET.setError("请输入金额");
            totalAmountET.requestFocus();
            return false;
        }
        totalAmount = Double.valueOf(totalAmountET.getText().toString().trim());
        if(totalAmount <0){
            totalAmountET.setError("请输入有效金额");
            totalAmountET.requestFocus();
            return false;
        }
        boolean b = false;
        totalRatio = 0;
        personCount = 0;
        for(ItemEntity itemEntity : list){
            if(itemEntity.getType() == ItemType.PERSON){
                ItemEntityPerson person = (ItemEntityPerson)itemEntity;
                if(person.isSelect()){
                    b = true;
                    totalRatio += person.getRatio();
                    personCount++;
                }
            }
        }
        if(!b){
            activity.showToast("没有选择分配人员");
            return false;
        }
        if(isByRatio && totalRatio==0){
            activity.showToast("总系数为0，请核对后再试！");
            return false;
        }
        return true;
    }

    private void initialInput() {
        itemNameET.setText("");
        itemNameET.setHint("系数分配项目" + ratioCount);
        totalAmountET.setText("");
        totalAmountET.setHint("0.00");
        isDeduce = false;
        isByRatio = true;
        isAsignRB.setChecked(!isDeduce);
        isDeduceRB.setChecked(isDeduce);
        isByRatioRB.setChecked(isByRatio);
        isAverageRB.setChecked(!isByRatio);
        setHint();
        for (int i = 0; i < list.size() - 1; i++) {
            ItemEntityPerson person = (ItemEntityPerson) list.get(i);
            person.setSelect(false);
        }
        adapter.notifyDataSetChanged();
        recyclerView.smoothScrollToPosition(0);
    }

    private void calculate() {
        if(checkInput()) {
            LitePal.deleteAll(JXGZSingleResultTemp.class);
            type = 0;
            perAmount = 0.0;
            itemName = TextUtils.isEmpty(itemNameET.getText()) ?
                    hintString : itemNameET.getText().toString().trim();
            totalAmount = Double.valueOf(totalAmountET.getText().toString().trim());
            if(isDeduce){
                type = MyTool.JXGZ_DEDUCE;
                totalAmount = -totalAmount;
                if(isByRatio){
                    perAmount = totalAmount / totalRatio;
                }else{
                    perAmount = totalAmount / personCount;
                }
            }else{
                if(isByRatio){
                    type = MyTool.JXGZ_RATIO;
                    perAmount = totalAmount / totalRatio;
                }else{
                    type = MyTool.JXGZ_AVERAGE;
                    perAmount = totalAmount / personCount;
                }
            }
            for(ItemEntity itemEntity:list){
                if(itemEntity.getType() ==ItemType.PERSON){
                    ItemEntityPerson person = (ItemEntityPerson)itemEntity;
                    if(person.isSelect()){
                        String name = person.getName();
                        double ratio = person.getRatio();
                        double amount = 0;
                        if(isByRatio){
                            amount = MyTool.getDouble(perAmount * ratio,2);
                        }else{
                            amount = MyTool.getDouble(perAmount,2);
                        }
                        // 保存到单次计算结果数据库
                        JXGZSingleResultTemp singleResultTemp = new JXGZSingleResultTemp();
                        singleResultTemp.setPersonName(name);
                        singleResultTemp.setRatio(ratio);
                        singleResultTemp.setAmount(amount);
                        singleResultTemp.save();
                    }
                }
            }
            showResultDialog();
        }
    }

    private void showResultDialog() {
        String s = "项目名称："+itemName+"，总金额："+totalAmount+"\n";
        switch (type){
            case MyTool.JXGZ_DEDUCE:
                if(isByRatio){
                    s+="按系数扣款，总系数："+MyTool.doubleToString(totalRatio,2)+"，1.0系数扣款金额："+MyTool.doubleToString(perAmount,2);
                }else{
                    s+="平均扣款，总人数："+ MyTool.intToString(personCount) +"人，每人扣款金额："+MyTool.doubleToString(perAmount,2);
                }
                s+="\n\n扣款明细如下：";
                break;
            case MyTool.JXGZ_RATIO:
                s+="按系数分配，总系数："+MyTool.doubleToString(totalRatio,2)+"，1.0系数金额："+MyTool.doubleToString(perAmount,2);
                s+="\n\n分配明细如下：";
                break;
            case MyTool.JXGZ_AVERAGE:
                s+="平均分配，分配人数："+ MyTool.intToString(personCount) +"人，每人金额："+MyTool.doubleToString(perAmount,2);
                s+="\n\n分配明细如下：";
                break;
        }

        CalPRPShowResultDialogFragment fragment = CalPRPShowResultDialogFragment.newInstant(s);
        fragment.setOnDialogFragmentDismiss(new OnDialogFragmentDismiss() {
            @Override
            public void onDissmiss(boolean flag) {
                if(flag){
                    JXGZDetailsTemp jxgzDetailsTemp = new JXGZDetailsTemp();
                    jxgzDetailsTemp.setJXGZName(itemName);
                    jxgzDetailsTemp.setJXGZType(type);
                    jxgzDetailsTemp.setJXGZAmount(totalAmount);
                    jxgzDetailsTemp.save();
                    for(JXGZSingleResultTemp temp:LitePal.findAll(JXGZSingleResultTemp.class)){
                        JXGZPersonDetailsTemp personDetailsTemp = new JXGZPersonDetailsTemp();
                        personDetailsTemp.setPersonName(temp.getPersonName());
                        personDetailsTemp.setJXGZName(itemName);
                        personDetailsTemp.setThatRatio(temp.getRatio());
                        personDetailsTemp.setJXGZType(type);
                        personDetailsTemp.setJXGZAmount(temp.getAmount());
                        personDetailsTemp.save();
                    }
                    // 保存了才能++
                    if (isDeduce) {
                        deduceCount++;
                    } else {
                        if (isByRatio) {
                            ratioCount++;
                        } else {
                            averageCount++;
                        }
                    }
                    initialInput();
                    setHint();
                    LitePal.deleteAll(JXGZSingleResultTemp.class);
                    activity.setHasCheckouted(false);
                    activity.showToast("保存成功！");
                }
            }

            @Override
            public void onDissmiss(boolean flag, Object object) {

            }
        });
        fragment.show(getFragmentManager(),"ShowResult");
    }

    private void setHint() {
        isDeduce = isDeduceRB.isChecked();
        isByRatio = isByRatioRB.isChecked();
        if (isDeduce) {
            hintString = ("扣款项目" + deduceCount);
        } else {
            if (isByRatio) {
                hintString = ("系数分配项目" + ratioCount);
            } else {
                hintString = ("平均分配项目" + averageCount);
            }
        }
        itemNameET.setHint(hintString);
        totalAmountET.setHint("0.00");
    }

    private void setPersonRatio(View view, final int position) {
        final ItemEntityPerson person = (ItemEntityPerson) list.get(position);
        EditPersonRatioFragment fragment = EditPersonRatioFragment.newInstant(person.getName(), person.getRatio());
        fragment.setOnDialogFragmentDismiss(new OnDialogFragmentDismiss() {
            @Override
            public void onDissmiss(boolean flag) {

            }

            @Override
            public void onDissmiss(boolean flag, Object object) {
                if (flag) {
                    double newValue = (double) object;
                    person.setRatio(newValue);
                    person.setSelect(true);
                    adapter.notifyItemChanged(position);
                }
            }
        });
        fragment.show(getFragmentManager(), "EditRatio");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (CalculatePRP) getActivity();
        hintString = "";
        itemName = "";
        totalAmount = 0.0;
        ratioCount = 1;
        averageCount = 1;
        deduceCount = 1;
        isDeduce = false;
        isByRatio = true;
        date = activity.getDate();
        list = new ArrayList<>();
        adapter = new PersonSelectAdapter(list);
        adapter.setiItemClickListener(new IItemClickListener() {
            @Override
            public void onClick(View view, final int position) {
                if (position == list.size() - 1) {
                    InputEditPersonInfoFragment inputEditPersonInfoFragment = new InputEditPersonInfoFragment();
                    inputEditPersonInfoFragment.setOnDialogFragmentDismiss(new OnDialogFragmentDismiss() {
                        @Override
                        public void onDissmiss(boolean flag) {
                            if (flag) {
                                int count1 = list.size() - 1;
                                double[] temp1 = new double[count1];
                                boolean[] temp2 = new boolean[count1];
                                for (int i = 0; i < count1; i++) {
                                    ItemEntityPerson person = (ItemEntityPerson) list.get(i);
                                    temp1[i] = person.getRatio();
                                    temp2[i] = person.isSelect();
                                }
                                updatePersonList();
                                for (int i = 0; i < temp1.length; i++) {
                                    ItemEntityPerson person = (ItemEntityPerson) list.get(i);
                                    person.setSelect(temp2[i]);
                                    person.setRatio(temp1[i]);
                                }
                                int count2 = list.size() - 2;
                                for (int i = 0; i < list.size() - 1 - count1; i++) {
                                    ItemEntityPerson person = (ItemEntityPerson) list.get(count2--);
                                    person.setSelect(true);
                                }
                                adapter.notifyDataSetChanged();
                                recyclerView.smoothScrollToPosition(list.size() - 1);
                            }
                        }

                        @Override
                        public void onDissmiss(boolean flag, Object object) {

                        }
                    });
                    inputEditPersonInfoFragment.show(getFragmentManager(), "AddPerson");
                } else {
                    setPersonRatio(view, position);
                }
            }

            @Override
            public void onClick(View view, int x, int y) {

            }
        });
        if (savedInstanceState != null) {
            ratioCount = savedInstanceState.getInt("RatioCount", ratioCount);
            averageCount = savedInstanceState.getInt("AverageCount", averageCount);
            deduceCount = savedInstanceState.getInt("DeduceCount", deduceCount);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calprp_input_fragment_layout, container, false);
        itemNameET = view.findViewById(R.id.calprp_input_fragment_name);
        totalAmountET = view.findViewById(R.id.calprp_input_fragment_amount);
        isAsignRB = view.findViewById(R.id.calprp_input_fragment_signRB);
        isAsignRB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setHint();
            }
        });
        isDeduceRB = view.findViewById(R.id.calprp_input_fragment_deduceRB);
        isDeduceRB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setHint();
            }
        });
        isByRatioRB = view.findViewById(R.id.calprp_input_fragment_ratioRB);
        isByRatioRB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setHint();
            }
        });
        isAverageRB = view.findViewById(R.id.calprp_input_fragment_averageRB);
        isAverageRB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setHint();
            }
        });
        recyclerView = view.findViewById(R.id.calprp_input_fragment_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        cancelBT = view.findViewById(R.id.calprp_input_fragment_cancelBT);
        cancelBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initialInput();
            }
        });
        confirmBT = view.findViewById(R.id.calprp_input_fragment_confirmBT);
        confirmBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculate();
            }
        });
        isAsignRB.setChecked(true);
        isByRatioRB.setChecked(true);
        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt("RatioCount", ratioCount);
        outState.putInt("AverageCount", averageCount);
        outState.putInt("DeduceCount", deduceCount);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        updatePersonList();
        adapter.notifyDataSetChanged();
        setHint();
        super.onStart();
    }
}
