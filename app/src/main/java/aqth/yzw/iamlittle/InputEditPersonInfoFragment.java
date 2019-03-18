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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

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
    private Person person;

    public void setPerson(Person person) {
        this.person = person;
    }

    public void setOnDialogFragmentDismiss(OnDialogFragmentDismiss onDialogFragmentDismiss) {
        this.onDialogFragmentDismiss = onDialogFragmentDismiss;
    }

    private OnDialogFragmentDismiss onDialogFragmentDismiss;
    public static InputEditPersonInfoFragment newInstant(Person p){
        InputEditPersonInfoFragment fragment= new InputEditPersonInfoFragment();
        fragment.setPerson(p);
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
        Person p = LitePal.where("name = ?",_name).findFirst(Person.class);
        return p != null;
    }
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
            dialog.setCanceledOnTouchOutside(false);// 点击外部不消失
            //dialog.setCancelable(false);// 按返回键不消失
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            int width = (int)(Math.min(dm.widthPixels,dm.heightPixels)*0.8);
            dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view =inflater.inflate(R.layout.input_person_info_layout,container,true);
        final Window window= getDialog().getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        flag = false;
        ageET = view.findViewById(R.id.input_person_info_age);
        nameET = view.findViewById(R.id.input_person_name_edittext);
        nameET.setText(name);
        man = view.findViewById(R.id.input_person_info_genderMan);
        man.setChecked(gender);
        man.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                gender=isChecked;
                ageET.requestFocus();
                ageET.selectAll();
            }
        });
        woman = view.findViewById(R.id.input_person_info_genderWoman);
        woman.setChecked(!gender);
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
                dismiss();
            }
        });
        TextView confirm = view.findViewById(R.id.input_person_info_confirmButton);
        if(mode == 1)
            confirm.setText("添加");
        else
            confirm.setText("修改");
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(confirmInput()) {
                    name = nameET.getText().toString().trim();
                    gender = man.isChecked();
                    age = Integer.valueOf(ageET.getText().toString().trim());
                    ratio = Double.valueOf(ratioET.getText().toString().trim());
                    phone = phoneET.getText().toString().trim();
                    note = noteET.getText().toString().trim();
                    if(mode == 1){
                        person = new Person();
                    }
                    person.setName(name);
                    person.setGender(gender);
                    person.setAge(age);
                    person.setRatio(ratio);
                    person.setStatus(MyTool.PERSON_STATUS_ONDUTY);
                    person.setPhone(phone);
                    person.setNote(note);
                    if (mode == 1) {
                        person.save();
                        flag = true;
                        Toast toast = Toast.makeText(view.getContext(),"已保存！可继续添加人员，或按关闭返回",Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();
                        nameET.setText("");
                        man.setChecked(true);
                        woman.setChecked(false);
                        ageET.setText("");
                        ratioET.setText("");
                        phoneET.setText("");
                        noteET.setText("");
                        nameET.requestFocus();
                    } else {
                        person.update(personID);
                        flag = true;
                        dismiss();
                    }
                }
            }
        });
        nameET.requestFocus();
        nameET.setSelection(name.length());
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        return view;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        onDialogFragmentDismiss.onDissmiss(flag);
    }
}
