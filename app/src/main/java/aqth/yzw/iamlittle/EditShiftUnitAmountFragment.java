package aqth.yzw.iamlittle;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import org.litepal.LitePal;

import aqth.yzw.iamlittle.EntityClass.Shift;

public class EditShiftUnitAmountFragment extends DialogFragment {
    private double unitAmount,oldValue;
    private long id;
    private String name;
    private TextView textView,title;
    private EditText editText;
    private int mode;

    public void setOnDialogFragmentDismiss(OnDialogFragmentDismiss onDialogFragmentDismiss) {
        this.onDialogFragmentDismiss = onDialogFragmentDismiss;
    }

    private OnDialogFragmentDismiss onDialogFragmentDismiss;
    private boolean flag;
    public static EditShiftUnitAmountFragment newInstant(Shift shift){
        EditShiftUnitAmountFragment fragment = new EditShiftUnitAmountFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("ID",shift.getId());
        bundle.putDouble("UnitAmount",shift.getUnitAmount());
        bundle.putString("Name",shift.getName());
        bundle.putInt("Mode",1);
        fragment.setArguments(bundle);
        return fragment;
    }
    public static EditShiftUnitAmountFragment newInstant(String name,double oldValue){
        EditShiftUnitAmountFragment fragmen = new EditShiftUnitAmountFragment();
        Bundle bundle = new Bundle();
        bundle.putString("Name",name);
        bundle.putDouble("UnitAmount",oldValue);
        bundle.putInt("Mode",2);
        fragmen.setArguments(bundle);
        return fragmen;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mode = 0;
        flag=false;
        unitAmount = 0;
        oldValue = 0;
        id = 0;
        name = "";
        Bundle bundle = getArguments();
        if(bundle!=null){
            mode = bundle.getInt("Mode");
            if(mode == 1) {
                id = bundle.getLong("ID");
            }
            oldValue = bundle.getDouble("UnitAmount");
            unitAmount = bundle.getDouble("UnitAmount");
            name = bundle.getString("Name");
        }
    }
    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        onDialogFragmentDismiss.onDissmiss(flag,unitAmount);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if(dialog!=null){
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            dialog.getWindow().setLayout((int) (dm.widthPixels * 0.75), ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.edit_shift_unitamount,container,false);
        textView = v.findViewById(R.id.edit_shift_unitamount_textview);
        title = v.findViewById(R.id.edit_shift_unitamount_title);
        title.setText(name);
        textView.setText("原金额为："+oldValue+"，请输入新金额");
        editText = v.findViewById(R.id.edit_shift_unitamount_edittext);
        v.findViewById(R.id.edit_shift_unitamount_cancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = false;
                dismiss();
            }
        });
        v.findViewById(R.id.edit_shift_unitamount_confirmButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(editText.getText())){
                    editText.requestFocus();
                    editText.setError("请输入新金额!");
                    return;
                }
                unitAmount = Double.valueOf(editText.getText().toString().trim());
                if(unitAmount <=0){
                    editText.requestFocus();
                    editText.setError("请输入有效金额!");
                    return;
                }
                if(unitAmount != oldValue) {
                    if(mode == 1) {
                        Shift shift = LitePal.find(Shift.class, id);
                        shift.setUnitAmount(unitAmount);
                        shift.save();
                    }
                    flag = true;
                }else{
                    flag = false;
                }
                dismiss();
            }
        });
        editText.requestFocus();
        Window window = getDialog().getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        return v;
    }
}
