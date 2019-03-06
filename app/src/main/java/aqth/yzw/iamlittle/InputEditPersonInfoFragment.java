package aqth.yzw.iamlittle;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;

import org.litepal.LitePal;

import aqth.yzw.iamlittle.EntityClass.Person;

public class InputEditPersonInfoFragment extends DialogFragment {
    private long personID;
    private String name,phone,note,oldName;
    private boolean gender;
    private int age,mode;
    private double ratio;
    private boolean flag;
    private EditText nameET,ageET,ratioET,phoneET,noteET;
    private RadioButton man,woman;

    public void setOnDialogFragmentDismiss(OnDialogFragmentDismiss onDialogFragmentDismiss) {
        this.onDialogFragmentDismiss = onDialogFragmentDismiss;
    }

    private OnDialogFragmentDismiss onDialogFragmentDismiss;
    public static InputEditPersonInfoFragment newInstant(Person p){
        InputEditPersonInfoFragment fragment= new InputEditPersonInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putString("Name",p.getName());
        bundle.putInt("Age",p.getAge());
        bundle.putBoolean("Gender",p.getGender());
        bundle.putDouble("Ratio",p.getRatio());
        bundle.putString("Phone",p.getPhone());
        bundle.putString("Note",p.getNote());
        bundle.putLong("ID",p.getId());
        fragment.setArguments(bundle);
        return fragment;
    }
    protected boolean confirmInput(){
        if(TextUtils.isEmpty(nameET.getText())){
            nameET.requestFocus();
            nameET.setError("请输入姓名!");
            return false;
        }
        name = nameET.getText().toString().trim();
        if(TextUtils.isEmpty(ageET.getText())){
            ageET.requestFocus();
            ageET.setError("请输入年龄！");
            return false;
        }
        age = Integer.valueOf(ageET.getText().toString().trim());
        if(age <=0){
            ageET.requestFocus();
            ageET.setError("请输入有效年龄！");
            return false;
        }
        if(TextUtils.isEmpty(ratioET.getText())){
            ratioET.requestFocus();
            ratioET.setError("请输入系数！");
            return false;
        }
        ratio = Double.valueOf(ratioET.getText().toString().trim());
        if(ratio  <=0){
            ratioET.requestFocus();
            ratioET.setError("请输入有效系数！");
            return false;
        }
        if(!name.equals(oldName)&&exist(name)){
            nameET.requestFocus();
            nameET.selectAll();
            nameET.setError("姓名重复,请改名！");
            return false;
        }
        return true;
    }
    protected boolean exist(String _name){
        Person person = LitePal.where("name = ?",_name).findFirst(Person.class);
        return person != null;
    }
//    protected void initialInput(){
//        flag = false;
//        name = "";
//        phone = "";
//        note = "";
//        gender = MyTool.GENDER_MAN;
//        age = 0;
//        ratio = 0.00;
//        nameET.setText(name);
//        man.setChecked(gender);
//        woman.setChecked(!gender);
//        if(age > 0){
//            ageET.setText(age +"");
//        }
//        if(ratio > 0){
//            ratioET.setText(ratio+"");
//        }
//        phoneET.setText(phone);
//        noteET.setText(note);
//    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        flag = false;
        mode = 1;//新增人员
        name = "";
        phone = "";
        note = "";
        gender = MyTool.GENDER_MAN;
        age = 0;
        ratio = 0.00;
        personID = 1;
        Bundle  bundle = getArguments();
        if(bundle != null){
            mode=2;//修改信息
            oldName = bundle.getString("Name");
            name = bundle.getString("Name");
            age = bundle.getInt("Age");
            gender = bundle.getBoolean("Gender");
            ratio = bundle.getDouble("Ratio");
            phone = bundle.getString("Phone");
            note = bundle.getString("Note");
            personID = bundle.getLong("ID");
        }
    }

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.input_person_info_layout,container,true);
        nameET = view.findViewById(R.id.input_person_name_edittext);
        nameET.setText(name);
        man = view.findViewById(R.id.input_person_info_genderMan);
        man.setChecked(gender);
        man.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                gender=isChecked;
            }
        });
        woman = view.findViewById(R.id.input_person_info_genderWoman);
        woman.setChecked(!gender);
        ageET = view.findViewById(R.id.input_person_info_age);
        if(age > 0){
            ageET.setText(age +"");
        }
        ratioET = view.findViewById(R.id.input_person_info_ratio);
        if(ratio > 0){
            ratioET.setText(ratio+"");
        }
        phoneET = view.findViewById(R.id.input_person_info_phone);
        if(!"".equals(phone)){
            phoneET.setText(phone);
        }
        noteET = view.findViewById(R.id.input_person_info_note);
        if(!"".equals(note)){
            noteET.setText(note);
        }
        view.findViewById(R.id.input_person_info_cancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = false;
                dismiss();
            }
        });
        view.findViewById(R.id.input_person_info_confirmButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(confirmInput()) {
                    name = nameET.getText().toString().trim();
                    gender = man.isChecked();
                    age = Integer.valueOf(ageET.getText().toString().trim());
                    ratio = Double.valueOf(ratioET.getText().toString().trim());
                    phone = phoneET.getText().toString().trim();
                    note = noteET.getText().toString().trim();
                    Person p = new Person();
                    p.setName(name);
                    p.setGender(gender);
                    p.setAge(age);
                    p.setRatio(ratio);
                    p.setStatus(MyTool.PERSON_STATUS_ONDUTY);
                    p.setPhone(phone);
                    p.setNote(note);
                    if (mode == 1) {
                        p.save();
                    } else {
                        p.update(personID);
                    }
                    flag = true;
                    dismiss();
                }
            }
        });
        nameET.requestFocus();
        nameET.setSelection(name.length());
        Window window= getDialog().getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        return view;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        onDialogFragmentDismiss.onDissmiss(flag);
    }
}
