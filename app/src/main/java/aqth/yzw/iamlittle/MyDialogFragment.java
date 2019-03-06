package aqth.yzw.iamlittle;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.zip.CheckedOutputStream;

public class MyDialogFragment extends DialogFragment {
    private TextView cancel,confirm;
    private TextView info;
    private boolean flag;
    private String mInfo,mCancelText,mConfirmText;
    private int mCancelColor,mConfirmColor,flag1;

    public void setOnDialogFragmentDismiss(OnDialogFragmentDismiss onDialogFragmentDismiss) {
        this.onDialogFragmentDismiss = onDialogFragmentDismiss;
    }
    private OnDialogFragmentDismiss onDialogFragmentDismiss;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        flag = false;
        mInfo = "";
        mCancelText = "";
        mConfirmText = "";
        flag1 = 1;
        mCancelColor = Color.BLACK;
        mConfirmColor = Color.BLACK;
        if(getArguments()!=null){
            flag1 = getArguments().getInt("Flag");
            mInfo = getArguments().getString("Info");
            mCancelText = getArguments().getString("CancelText");
            mConfirmText = getArguments().getString("ConfirmText");
            if(flag1 == 2){
                mConfirmColor = getArguments().getInt("ConfirmColor");
                mCancelColor = getArguments().getInt("CancelColor");
            }
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_fragment,container,false);
        info = view.findViewById(R.id.dialog_info_textView);
        info.setText(mInfo);
        cancel = view.findViewById(R.id.dialog_cancel_button);
        cancel.setText(mCancelText);
        cancel.setTextColor(mCancelColor);
        confirm = view.findViewById(R.id.dialog_confirm_button);
        confirm.setText(mConfirmText);
        confirm.setTextColor(mConfirmColor);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = false;
                dismiss();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = true;
                dismiss();
            }
        });
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            dialog.getWindow().setLayout((int) (dm.widthPixels * 0.75), ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        onDialogFragmentDismiss.onDissmiss(flag);
    }
    public static MyDialogFragment newInstant(String info,String cancelText,String confirmText){
        MyDialogFragment myDialogFragment = new MyDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("Info",info);
        bundle.putString("CancelText",cancelText);
        bundle.putString("ConfirmText",confirmText);
        bundle.putInt("Flag",1);
        myDialogFragment.setArguments(bundle);
        return myDialogFragment;
    }
    public static MyDialogFragment newInstant(String info,String cancelText,String confirmText,int cancelColor,int confirmColor){
        MyDialogFragment myDialogFragment = new MyDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("Info",info);
        bundle.putString("CancelText",cancelText);
        bundle.putString("ConfirmText",confirmText);
        bundle.putInt("CancelColor",cancelColor);
        bundle.putInt("ConfirmColor",confirmColor);
        bundle.putInt("Flag",2);
        myDialogFragment.setArguments(bundle);
        return myDialogFragment;
    }
}
