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
import android.widget.Button;
import android.widget.TextView;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import aqth.yzw.iamlittle.Adapters.ScheduleTemplateAdapter;
import aqth.yzw.iamlittle.EntityClass.ItemEntityScheduleTemplate;
import aqth.yzw.iamlittle.EntityClass.ScheduleTemplate;

public class DeleteTemplateDialogFragment extends DialogFragment {
    private RecyclerView recyclerView;
    private ScheduleTemplateAdapter adapter;
    private List<ItemEntityScheduleTemplate> list;
    protected void updateList() {
        if (list == null)
            list = new ArrayList<>();
        list.clear();
        List<ScheduleTemplate> temp = LitePal.select("name").find(ScheduleTemplate.class);
        if (temp != null && temp.size() > 0) {
            String pre = temp.get(0).getName();
            list.add(new ItemEntityScheduleTemplate(pre));
            for (int i = 1; i < temp.size(); i++) {
                String current = temp.get(i).getName();
                if (current.equals(pre))
                    continue;
                pre = current;
                list.add(new ItemEntityScheduleTemplate(pre));
            }
        }
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list = new ArrayList<>();
        updateList();
        adapter = new ScheduleTemplateAdapter(getContext(),list,true);
        adapter.setItemClickListener(new IItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                ItemEntityScheduleTemplate template = list.get(position);
                template.setSelect(!template.isSelect());
                adapter.notifyItemChanged(position);
            }

            @Override
            public void onClick(View view, int x, int y) {

            }
        });
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_multiselect_fragment,container,false);
        view.findViewById(R.id.dialog_multiselect_cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        TextView button = view.findViewById(R.id.dialog_multiselect_confirm_button);
        button.setText("删除");
        button.setTextColor(Color.RED);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(ItemEntityScheduleTemplate template:list){
                    if(template.isSelect()){
                        LitePal.deleteAll(ScheduleTemplate.class,"name = ?",template.getName());
                    }
                }
                updateList();
                adapter.notifyDataSetChanged();
            }
        });
        recyclerView = view.findViewById(R.id.dialog_multiselect_recyclerview);
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
            dialog.getWindow().setLayout(600, (int)(dm.heightPixels*0.75));
        }
    }
}

