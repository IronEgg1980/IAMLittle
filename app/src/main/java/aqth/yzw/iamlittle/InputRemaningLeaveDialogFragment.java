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

public class InputRemaningLeaveDialogFragment extends DialogFragment {
    private double oldValue,newValue;
    private String personname;
    private TextView textView,title;
    private EditText editText;
    public void setOnDialogFragmentDismiss(OnDialogFragmentDismiss onDialogFragmentDismiss) {
        this.onDialogFragmentDismiss = onDialogFragmentDismiss;
    }

    private OnDialogFragmentDismiss onDialogFragmentDismiss;
    private boolean flag;

    public static InputRemaningLeaveDialogFragment newInstant(String personname,double oldValue){
        InputRemaningLeaveDialogFragment fragmen = new InputRemaningLeaveDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("Name",personname);
        bundle.putDouble("OldValue",oldValue);
        fragmen.setArguments(bundle);
        return fragmen;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        flag=false;
        oldValue = 0;
        newValue = 0;
        Bundle bundle = getArguments();
        if(bundle!=null){
            oldValue = bundle.getDouble("UnitAmount");
            personname = bundle.getString("Name");
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        onDialogFragmentDismiss.onDissmiss(flag,newValue);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.input_remaning_leave_fragment,container,false);
        textView = v.findViewById(R.id.edit_shift_unitamount_textview);
        title = v.findViewById(R.id.edit_shift_unitamount_title);
        title.setText(personname);
        textView.setText("剩余假期天数为："+oldValue);
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
                    editText.setError("请输入天数!");
                    return;
                }
                newValue = Double.valueOf(editText.getText().toString().trim());
                if(newValue != oldValue) {
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
