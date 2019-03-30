package aqth.yzw.iamlittle;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import aqth.yzw.iamlittle.Adapters.JXGZSingleResultAdapter;
import aqth.yzw.iamlittle.EntityClass.JXGZSingleResultTemp;

public class CalPRPShowResultDialogFragment extends DialogFragment {
    private List<JXGZSingleResultTemp> list;
    private JXGZSingleResultAdapter adapter;
    private RecyclerView recyclerView;
    private TextView cancleBT,confirmBT;
    private OnDialogFragmentDismiss onDialogFragmentDismiss;
    private boolean flag;
    private String content;

    public void setOnDialogFragmentDismiss(OnDialogFragmentDismiss onDialogFragmentDismiss) {
        this.onDialogFragmentDismiss = onDialogFragmentDismiss;
    }
    public static CalPRPShowResultDialogFragment newInstant(String content){
        CalPRPShowResultDialogFragment fragment = new CalPRPShowResultDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("Content",content);
        fragment.setArguments(bundle);
        return fragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(list == null){
            list = new ArrayList<>();
        }
        list.clear();
        list.addAll(LitePal.findAll(JXGZSingleResultTemp.class));
        adapter = new JXGZSingleResultAdapter(list);
        this.content = "";
        if(getArguments() != null)
            this.content = getArguments().getString("Content");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calprp_showandsave_dialogfragment,container,true);
        TextView contentTV = view.findViewById(R.id.calprp_showandsave_fragment_contentTV);
        contentTV.setText(content);
        recyclerView =view.findViewById(R.id.calprp_showandsave_frament_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        cancleBT = view.findViewById(R.id.calprp_showandsave_fragment_cancelBT);
        cancleBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = false;
                dismiss();
            }
        });
        confirmBT = view.findViewById(R.id.calprp_showandsave_fragment_confirmBT);
        confirmBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = true;
                dismiss();
            }
        });
        return view;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        onDialogFragmentDismiss.onDissmiss(flag);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.setCanceledOnTouchOutside(false);// 点击外部不消失
            dialog.setCancelable(false);// 按返回键不消失
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            int width = (int)(Math.min(dm.widthPixels,dm.heightPixels)*0.9);
            dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }
}
