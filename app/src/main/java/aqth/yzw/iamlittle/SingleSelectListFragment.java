package aqth.yzw.iamlittle;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import aqth.yzw.iamlittle.Adapters.SingleSelectListAdapter;

public class SingleSelectListFragment extends DialogFragment {
    private RecyclerView recyclerView;
    private boolean flag;
    private SingleSelectListAdapter adapter;

    public void setOnDialogFragmentDismiss(OnDialogFragmentDismiss onDialogFragmentDismiss) {
        this.onDialogFragmentDismiss = onDialogFragmentDismiss;
    }

    private OnDialogFragmentDismiss onDialogFragmentDismiss;
    private String result;
    private List<String> list;
    protected void updateList(){
        if(list == null)
            list = new ArrayList<>();
        list.clear();
        File backUpPath = new File(Environment.getExternalStorageDirectory() + "/IAmLittle/Backup");
        File[] files = backUpPath.listFiles();
        for(File file:files){
            if(file.isFile()) {
                String name = file.getName();
                if(name.length() > 18) {
                    name = name.substring(8, 16);
                    list.add(name);
                }
            }
        }
        if(list.size() == 0)
            list.add("");
        adapter.notifyDataSetChanged();
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        flag = false;
        result = "";
        list = new ArrayList<>();
        adapter = new SingleSelectListAdapter(list);
        adapter.setClickListener(new IItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                flag = true;
                result = (String) view.getTag();
                dismiss();
            }

            @Override
            public void onClick(View view, int x, int y) {

            }
        });
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        onDialogFragmentDismiss.onDissmiss(flag,result);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_singleselect_fragment,container,false);
        view.findViewById(R.id.dialog_singleselect_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = false;
                dismiss();
            }
        });
        recyclerView = view.findViewById(R.id.dialog_singleselect_recyclerview);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        getDialog().getWindow().setBackgroundDrawable( new ColorDrawable(Color.TRANSPARENT));
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            int width = (int)(Math.min(dm.widthPixels,dm.heightPixels)*0.8);
            int height = (int)(Math.max(dm.widthPixels,dm.heightPixels)*0.6);
            dialog.getWindow().setLayout(width,height);
        }
        updateList();
    }
}
