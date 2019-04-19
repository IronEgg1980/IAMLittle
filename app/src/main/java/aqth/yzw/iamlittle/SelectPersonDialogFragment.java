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

import aqth.yzw.iamlittle.Adapters.SelectPersonAdapter;
import aqth.yzw.iamlittle.EntityClass.Person;

public class SelectPersonDialogFragment extends DialogFragment {
    private RecyclerView recyclerView;
    private List<Person> list;
    private String personName;
    private boolean flag;
    private SelectPersonAdapter adapter;
    private TextView cancelTV;
    public void setDialogFragmentDismiss(OnDialogFragmentDismiss dialogFragmentDismiss) {
        this.dialogFragmentDismiss = dialogFragmentDismiss;
    }

    private OnDialogFragmentDismiss dialogFragmentDismiss;
    private void fillData(){
        if(list == null)
            list = new ArrayList<>();
        list.clear();
        list.addAll(LitePal.findAll(Person.class));
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        flag = false;
        personName = "";
        list = new ArrayList<>();
        adapter = new SelectPersonAdapter(list);
        adapter.setItemClickListener(new IItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                personName = list.get(position).getName();
                flag = true;
                dismiss();
            }

            @Override
            public void onClick(View view, int x, int y) {

            }
        });
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_singleselect_fragment,container,false);
        recyclerView = view.findViewById(R.id.dialog_singleselect_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        cancelTV = view.findViewById(R.id.dialog_singleselect_button);
        cancelTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = false;
                dismiss();
            }
        });
        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            int width = (int)(Math.min(dm.widthPixels,dm.heightPixels)*0.8);
            int height = (int)(Math.max(dm.widthPixels,dm.heightPixels)*0.5);
            dialog.getWindow().setLayout(width,height);
        }
        fillData();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        dialogFragmentDismiss.onDissmiss(flag,personName);
    }
}
