package aqth.yzw.iamlittle;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import aqth.yzw.iamlittle.Adapters.JXGZCheckoutAdapter;
import aqth.yzw.iamlittle.EntityClass.ItemEntityJXGZPersonTotalTemp;
import aqth.yzw.iamlittle.EntityClass.JXGZDetailsTemp;
import aqth.yzw.iamlittle.EntityClass.JXGZPersonDetailsTemp;

public class CalPRPCheckoutFragment extends Fragment {
    private CalculatePRP activity;
    private TextView infactTotalAmountTV, totalAmountTV, diffTV;
    private double diffValue;
    private RecyclerView recyclerView;
    private JXGZCheckoutAdapter adapter;
    private List<ItemEntityJXGZPersonTotalTemp> list;
    private int amountFlag = 2;
    private String TAG = "殷宗旺";
    private void calculateAndShow() {
        activity.setHasCheckouted(false);
        diffValue = -1.0;
        double infactTotal = 0;
        double total = 0;
        for(ItemEntityJXGZPersonTotalTemp temp:list){
            infactTotal = Arith.add(infactTotal,temp.getAmount());
        }
        for(JXGZDetailsTemp temp:LitePal.findAll(JXGZDetailsTemp.class)){
            total = Arith.add(total,temp.getJXGZAmount());
        }
        diffValue = Arith.sub(total, infactTotal);
        infactTotalAmountTV.setText(Arith.doubleToString(infactTotal,amountFlag));
        totalAmountTV.setText(Arith.doubleToString(total,amountFlag));
        if(diffValue == 0){
            diffTV.setTextColor(Color.GREEN);
            activity.setHasCheckouted(true);
        }else if(diffValue > 0){
            diffTV.setTextColor(getContext().getColor(android.R.color.holo_orange_dark));
        }else{
            diffTV.setTextColor(Color.RED);
        }
        diffTV.setText(Arith.doubleToString(diffValue,amountFlag));
    }

    private void fillData() {
        if (list == null) {
            list = new ArrayList<>();
        }
        list.clear();
        List<String> personList = new ArrayList<>();
        Cursor cursor = LitePal.findBySQL("SELECT DISTINCT personname FROM JXGZPersonDetailsTemp");
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String name = cursor.getString(0);
                if(TextUtils.isEmpty(name))
                    continue;
                personList.add(name);
            } while (cursor.moveToNext());
        }
        for (String name : personList) {
            if(TextUtils.isEmpty(name))
                continue;
            List<JXGZPersonDetailsTemp> tempList = new ArrayList<>();
//            Cursor cursor1 = LitePal.findBySQL("SELECT * FROM JXGZPersonDetailsTemp WHERE personname = ?",name);
//            if(cursor1 !=null && cursor1.moveToFirst()){
//                do{
//                    JXGZPersonDetailsTemp temp = new JXGZPersonDetailsTemp();
//                    temp.setPersonName(name);
//                    temp.setJXGZName(cursor1.getString(cursor1.getColumnIndex("jxgzname")));
//                    temp.setThatRatio(cursor1.getDouble(cursor1.getColumnIndex("thatratio")));
//                    temp.setJXGZType(cursor1.getInt(cursor1.getColumnIndex("jxgztype")));
//                    temp.setJXGZAmount(cursor1.getDouble(cursor1.getColumnIndex("jxgzamount")));
//                    tempList.add(temp);
//                }while (cursor1.moveToNext());
//            }
            tempList.addAll(LitePal.where("personname = ?",name).find(JXGZPersonDetailsTemp.class));
            if(tempList.size() > 0)
                list.add(new ItemEntityJXGZPersonTotalTemp(tempList));
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        amountFlag =(int) new SharedPreferencesHelper(getContext()).getValue("AmountFlag",2);
        activity = (CalculatePRP) getActivity();
        list = new ArrayList<>();
        fillData();
        adapter = new JXGZCheckoutAdapter(list);
        adapter.setItemClickListener(new IItemClickListener() {
            @Override
            public void onClick(View view, final int position) {
                final ItemEntityJXGZPersonTotalTemp temp = list.get(position);
                final double oldValue = temp.getAmount();
                EditShiftUnitAmountFragment fragment = EditShiftUnitAmountFragment.newInstant(temp.getPersonName(),oldValue);
                fragment.setOnDialogFragmentDismiss(new OnDialogFragmentDismiss() {
                    @Override
                    public void onDissmiss(boolean flag) {

                    }

                    @Override
                    public void onDissmiss(boolean flag, Object object) {
                        if(flag) {
                            double newValue = (double) object;
                            double diffValue = Arith.sub(newValue,oldValue);
                            if(diffValue !=0){
                                JXGZPersonDetailsTemp detailsTemp = new JXGZPersonDetailsTemp();
                                detailsTemp.setPersonName(temp.getPersonName());
                                detailsTemp.setThatRatio(temp.getThatRatio());
                                detailsTemp.setJXGZName("金额调整");
                                detailsTemp.setJXGZType(MyTool.JXGZ_ADJUST);
                                detailsTemp.setJXGZAmount(diffValue);
                                detailsTemp.setScale(amountFlag);
                                detailsTemp.save();
                                temp.getList().add(detailsTemp);
                                adapter.notifyItemChanged(position);
                                calculateAndShow();
                            }
                        }
                    }
                });
                fragment.show(getFragmentManager(),"EditTotalAmount");
            }

            @Override
            public void onClick(View view, int x, int y) {

            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calprp_checkout_fragment_layout, container, false);
        diffTV = view.findViewById(R.id.calprp_checkout_fragment_diff);
        infactTotalAmountTV = view.findViewById(R.id.calprp_checkout_fragment_infactTotal);
        totalAmountTV = view.findViewById(R.id.calprp_checkout_fragment_total);
        recyclerView = view.findViewById(R.id.calprp_checkout_fragment_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        view.findViewById(R.id.calprp_checkout_fragment_confirmBT).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateAndShow();
                if(diffValue == 0.00){
                    activity.setHasCheckouted(true);
                    activity.showToast("调整成功");
                }else{
                    activity.setHasCheckouted(false);
                }
            }
        });
        view.findViewById(R.id.calprp_checkout_fragment_cancelBT).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LitePal.deleteAll(JXGZPersonDetailsTemp.class,"jxgztype = ?",String.valueOf(MyTool.JXGZ_ADJUST));
                fillData();
                adapter.notifyDataSetChanged();
                calculateAndShow();
            }
        });
        calculateAndShow();
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        fillData();
        adapter.notifyDataSetChanged();
        calculateAndShow();
    }
}
