package aqth.yzw.iamlittle;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MyDialogFragmenSingleButton extends DialogFragment {
    private TextView button;
    private TextView info;
    private String mInfo,mButtonText;
    private int mButtonColor,flag1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInfo = "";
        mButtonText = "";
        flag1 = 1;
        mButtonColor = Color.BLACK;
        if(getArguments()!=null){
            flag1 = getArguments().getInt("Flag");
            mInfo = getArguments().getString("Info");
            mButtonText = getArguments().getString("ButtonText");
            if(flag1 == 2){
                mButtonColor = getArguments().getInt("ButtonColor");
            }
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_single_button_fragment,container,false);
        info = view.findViewById(R.id.dialog_info_textView);
        info.setText(mInfo);
        button = view.findViewById(R.id.dialog_single_button);
        button.setText(mButtonText);
        button.setTextColor(mButtonColor);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            dialog.getWindow().setLayout((int) (dm.widthPixels * 0.6), ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    public static MyDialogFragmenSingleButton newInstant(String info,String buttonText){
        MyDialogFragmenSingleButton myDialogFragment = new MyDialogFragmenSingleButton();
        Bundle bundle = new Bundle();
        bundle.putString("Info",info);
        bundle.putString("ButtonText",buttonText);
        bundle.putInt("Flag",1);
        myDialogFragment.setArguments(bundle);
        return myDialogFragment;
    }
    public static MyDialogFragmenSingleButton newInstant(String info,String buttonText,int buttonColor){
        MyDialogFragmenSingleButton myDialogFragment = new MyDialogFragmenSingleButton();
        Bundle bundle = new Bundle();
        bundle.putString("Info",info);
        bundle.putString("ButtonText",buttonText);
        bundle.putInt("ButtonColor",buttonColor);
        bundle.putInt("Flag",2);
        myDialogFragment.setArguments(bundle);
        return myDialogFragment;
    }
}
