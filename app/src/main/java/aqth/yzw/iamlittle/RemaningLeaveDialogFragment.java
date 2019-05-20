package aqth.yzw.iamlittle;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import aqth.yzw.iamlittle.Adapters.RemaningLeaveAdapter;
import aqth.yzw.iamlittle.EntityClass.Person;

public class RemaningLeaveDialogFragment extends DialogFragment {
    private TextView cancelBT,saveBT;
    private RecyclerView recyclerView;
    private RemaningLeaveAdapter adapter;
    private List<Person> list;
    private boolean flag;
    private void updateList(){
        if(list == null)
            list = new ArrayList<>();
        list.addAll(LitePal.order("remaningLeave desc").find(Person.class));
        adapter.notifyDataSetChanged();
    }
    private void inputValue(final int position){
        final Person p = list.get(position);
        InputRemaningLeaveDialogFragment fragment = InputRemaningLeaveDialogFragment.newInstant(p.getName(),p.getRemaningLeave());
        fragment.setOnDialogFragmentDismiss(new OnDialogFragmentDismiss() {
            @Override
            public void onDissmiss(boolean flag) {

            }

            @Override
            public void onDissmiss(boolean flag, Object object) {
                if(flag){
                    double newValue = (double)object;
                    p.setRemaningLeave(newValue);
                    adapter.notifyItemChanged(position);
                }
            }
        });
        fragment.show(getFragmentManager(),"InputValue");
    }
    private OnDialogFragmentDismiss onDialogFragmentDismiss;
    public void setOnDialogFragmentDismiss(OnDialogFragmentDismiss onDialogFragmentDismiss) {
        this.onDialogFragmentDismiss = onDialogFragmentDismiss;
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
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            int width = dm.widthPixels > dm.heightPixels ? (int) (dm.heightPixels * 0.8) : (int) (dm.widthPixels * 0.8);
            int height = dm.widthPixels > dm.heightPixels ? (int) (dm.widthPixels * 0.8) : (int) (dm.heightPixels * 0.8);
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        updateList();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        list = new ArrayList<>();
        adapter = new RemaningLeaveAdapter(list);
        adapter.setInputClickListenner(new IItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                inputValue(position);
            }

            @Override
            public void onClick(View view, int x, int y) {

            }
        });
        View view = inflater.inflate(R.layout.remaning_leave_layout,container,false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),RecyclerView.VERTICAL));
        cancelBT = view.findViewById(R.id.cancel_button);
        cancelBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = false;
                dismiss();
            }
        });
        saveBT = view.findViewById(R.id.save_button);
        saveBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LitePal.saveAll(list);
                flag = true;
                dismiss();
            }
        });
        return view;
    }
}
