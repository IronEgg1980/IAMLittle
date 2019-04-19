package aqth.yzw.iamlittle;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import org.litepal.LitePal;

import aqth.yzw.iamlittle.EntityClass.ScheduleTemplate;

public class MyDialogFragmentInput extends DialogFragment {
    public static MyDialogFragmentInput newInstant(String text,String hint){
        MyDialogFragmentInput fragmentInput = new MyDialogFragmentInput();
        Bundle bundle = new Bundle();
        bundle.putString("Text",text);
        bundle.putString("Hint",hint);
        fragmentInput.setArguments(bundle);
        return fragmentInput;
    }
    public static MyDialogFragmentInput newInstant(String text,boolean isTemplateInput){
        MyDialogFragmentInput fragmentInput = new MyDialogFragmentInput();
        Bundle bundle = new Bundle();
        bundle.putString("Text",text);
        bundle.putBoolean("IsTemplateInput",isTemplateInput);
        fragmentInput.setArguments(bundle);
        return fragmentInput;
    }
    private EditText editText;
    private boolean flag;
    private String returnText,mText,mHint;
    private boolean isTemplateInput;

    public void setOnDialogFragmentDismiss(OnDialogFragmentDismiss onDialogFragmentDismiss) {
        this.onDialogFragmentDismiss = onDialogFragmentDismiss;
    }

    private OnDialogFragmentDismiss onDialogFragmentDismiss;

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        onDialogFragmentDismiss.onDissmiss(flag,returnText);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        flag = false;
        returnText = "";
        mText = "";
        mHint = "";
        isTemplateInput = false;
        if(getArguments()!=null ){
            isTemplateInput = getArguments().getBoolean("IsTemplateInput");
            mText = getArguments().getString("Text");
            mHint = getArguments().getString("Hint");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_input_fragment,container,false);
        editText = view.findViewById(R.id.dialog_input_edittext);
//        if(!"".equals(mText))
//            editText.setText(mText);
        if(!"".equals(mText)) {
            editText.setText(mText);
        }
        if(isTemplateInput){
            editText.setHint("请输入模板名称");
        }else if(!"".equals(mHint)){
            editText.setHint(mHint);
        }else{
            editText.setHint("请输入文字");
        }
        view.findViewById(R.id.dialog_input_cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = false;
                dismiss();
            }
        });
        view.findViewById(R.id.dialog_input_confirm_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isTemplateInput){
                    if (TextUtils.isEmpty(editText.getText())){
                        editText.requestFocus();
                        editText.setError("请输入模板名称");
                        return;
                    }
                    returnText = editText.getText().toString().trim();
                    if(LitePal.where("name = ?",returnText).find(ScheduleTemplate.class).size() > 0){
                        editText.requestFocus();
                        editText.selectAll();
                        editText.setError("名称重复，请改名！");
                        return;
                    }
                }else {
                    if (TextUtils.isEmpty(editText.getText())) {
                        returnText = "";
                    } else {
                        returnText = editText.getText().toString().trim();
                    }
                }
                flag = true;
                dismiss();
            }
        });

        editText.requestFocus();
        editText.selectAll();
        Window window = getDialog().getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            int width = dm.widthPixels > dm.heightPixels?(int)(dm.heightPixels * 0.75):(int)(dm.widthPixels * 0.75);
            dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}
