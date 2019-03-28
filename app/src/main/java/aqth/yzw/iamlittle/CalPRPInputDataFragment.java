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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aqth.yzw.iamlittle.Adapters.PersonSelectAdapter;
import aqth.yzw.iamlittle.EntityClass.ItemEntity;
import aqth.yzw.iamlittle.EntityClass.ItemEntityPerson;
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
    private Date date, recordTime;
    private PersonSelectAdapter adapter;
    private int ratioCount, averageCount, deduceCount;
    private String hintString;

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

    private void checkInput() {

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
        if (isDeduce) {
            deduceCount++;
        } else {
            if (isByRatio) {
                ratioCount++;
            } else {
                averageCount++;
            }
        }
    }

    private void showResultDialog() {

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
        activity.setTitle("输入数据");
        hintString = "";
        itemName = "";
        totalAmount = 0.0;
        ratioCount = 1;
        averageCount = 1;
        deduceCount = 1;
        isDeduce = false;
        isByRatio = true;
        date = activity.getDate();
        recordTime = activity.getRecordTime();
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
            itemName = savedInstanceState.getString("ItemName", itemName);
            totalAmount = savedInstanceState.getDouble("TotalAmount", totalAmount);
            ratioCount = savedInstanceState.getInt("RatioCount", ratioCount);
            averageCount = savedInstanceState.getInt("AverageCount", averageCount);
            deduceCount = savedInstanceState.getInt("DeduceCount", deduceCount);
            isDeduce = savedInstanceState.getBoolean("IsDeduce", isDeduce);
            isByRatio = savedInstanceState.getBoolean("IsByRatio", isByRatio);
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
                showResultDialog();
            }
        });
        isAsignRB.setChecked(!isDeduce);
        isDeduceRB.setChecked(isDeduce);
        isByRatioRB.setChecked(isByRatio);
        isAverageRB.setChecked(!isByRatio);
        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (!TextUtils.isEmpty(itemNameET.getText()))
            itemName = itemNameET.getText().toString().trim();
        if (!TextUtils.isEmpty(totalAmountET.getText()))
            totalAmount = Double.valueOf(totalAmountET.getText().toString().trim());
        outState.putString("ItemName", itemName);
        outState.putDouble("TotalAmount", totalAmount);
        outState.putInt("RatioCount", ratioCount);
        outState.putInt("AverageCount", averageCount);
        outState.putInt("DeduceCount", deduceCount);
        outState.putBoolean("IsDeduce", isDeduce);
        outState.putBoolean("IsByRatio", isByRatio);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        updatePersonList();
        adapter.notifyDataSetChanged();
        setHint();
        itemNameET.setText(itemName);
        if (totalAmount > 0)
            totalAmountET.setText(totalAmount + "");
        super.onStart();
//        initialInput();
    }
}
