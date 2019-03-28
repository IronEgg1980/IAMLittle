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
import android.widget.TextView;

import org.litepal.LitePal;

import javax.xml.transform.Templates;

import aqth.yzw.iamlittle.EntityClass.Person;

public class EditPersonRatioFragment extends DialogFragment {
    private double ratio,oldRatio;
    private long id;
    private String name;
    private TextView textView;
    private EditText editText;
    private TextView title;
    public void setOnDialogFragmentDismiss(OnDialogFragmentDismiss onDialogFragmentDismiss) {
        this.onDialogFragmentDismiss = onDialogFragmentDismiss;
    }

    private OnDialogFragmentDismiss onDialogFragmentDismiss;
    private boolean flag;
//    public static EditPersonRatioFragment newInstant(Person p){
//        EditPersonRatioFragment fragment = new EditPersonRatioFragment();
//        Bundle bundle = new Bundle();
//        bundle.putLong("ID",p.getId());
//        bundle.putDouble("Ratio",p.getRatio());
//        bundle.putString("Name",p.getName());
//        fragment.setArguments(bundle);
//        return fragment;
//    }
    public static EditPersonRatioFragment newInstant(String name,double ratio){
        EditPersonRatioFragment fragment = new EditPersonRatioFragment();
        Bundle bundle = new Bundle();
        bundle.putDouble("Ratio",ratio);
        bundle.putString("Name",name);
        fragment.setArguments(bundle);
        return fragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        flag=false;
        ratio = 0;
        oldRatio = 0;
        id = 0;
        name = "";
        Bundle bundle = getArguments();
        if(bundle!=null){
            id = bundle.getLong("ID");
            oldRatio = bundle.getDouble("Ratio");
            ratio = bundle.getDouble("Ratio");
            name = bundle.getString("Name");
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        onDialogFragmentDismiss.onDissmiss(flag,ratio);
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.edit_person_ratio_layout,container,false);
        textView = v.findViewById(R.id.edit_person_textview);
        title = v.findViewById(R.id.edit_person_ratio_title);
        title.setText(name);
        textView.setText("原系数为："+oldRatio+"，请输入新系数");
        editText = v.findViewById(R.id.edit_person_edittext);
        v.findViewById(R.id.edit_person_info_cancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = false;
                dismiss();
            }
        });
        v.findViewById(R.id.edit_person_info_confirmButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(editText.getText())){
                    editText.requestFocus();
                    editText.setError("请输入新系数!");
                    return;
                }
                ratio = Double.valueOf(editText.getText().toString().trim());
                if(ratio <=0){
                    editText.requestFocus();
                    editText.setError("请输入有效系数!");
                    return;
                }
                if(ratio != oldRatio) {
//                    Person p = LitePal.find(Person.class,id);
//                    p.setRatio(ratio);
//                    p.save();
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
