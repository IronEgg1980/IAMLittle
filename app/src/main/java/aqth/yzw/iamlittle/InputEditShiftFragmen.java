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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.LitePal;

import aqth.yzw.iamlittle.EntityClass.Shift;

public class InputEditShiftFragmen extends DialogFragment {
    private String shiftName,oldName;
    private long id;
    private int shiftType,mode;
    private double unitAmount;
    private EditText shiftNameET,unitAmountET;
    private LinearLayout linearGroup;
    private RadioButton normalRB,leaveRB,countRB;
    private RadioGroup radioGroup;
    private TextView cancelBT,confirmBT;
    private boolean isAdd;

    public void setmShift(Shift mShift) {
        this.mShift = mShift;
    }

    private Shift mShift;
    private OnDialogFragmentDismiss onDialogFragmentDismiss;

    public void setOnDialogFragmentDismiss(OnDialogFragmentDismiss onDialogFragmentDismiss) {
        this.onDialogFragmentDismiss = onDialogFragmentDismiss;
    }

    public static InputEditShiftFragmen newInstant(Shift shift){
        InputEditShiftFragmen fragmen = new InputEditShiftFragmen();
        fragmen.setmShift(shift);
        Bundle bundle = new Bundle();
        bundle.putLong("ID",shift.getId());
        bundle.putString("ShiftName",shift.getName());
        bundle.putInt("ShiftType",shift.getType());
        bundle.putDouble("UnitAmount",shift.getUnitAmount());
        fragmen.setArguments(bundle);
        return fragmen;
    }
    private void initialRadioButton(){
        if(shiftType == MyTool.SHIFT_NORMAL){
            normalRB.setChecked(true);
        }
        if(shiftType == MyTool.SHIFT_LEAVEOFF){
              leaveRB.setChecked(true);
        }
        if(shiftType == MyTool.SHIFT_NEEDCOUNT){
            countRB.setChecked(true);
        }
    }
    private boolean confirmInput(){
        if(TextUtils.isEmpty(shiftNameET.getText())){
            shiftNameET.requestFocus();
            shiftNameET.setError("请输入名称！");
            return false;
        }
        shiftName = shiftNameET.getText().toString().trim();
        if(mode == 1 || !oldName.equals(shiftName)){
            if(LitePal.where("name = ?",shiftName).findFirst(Shift.class) != null){
                shiftNameET.requestFocus();
                shiftNameET.selectAll();
                shiftNameET.setError("名称重复！");
                return false;
            }
        }
        if(normalRB.isChecked())
            shiftType = MyTool.SHIFT_NORMAL;
        if(leaveRB.isChecked())
            shiftType = MyTool.SHIFT_LEAVEOFF;
        if(countRB.isChecked()) {
            shiftType = MyTool.SHIFT_NEEDCOUNT;
            if (TextUtils.isEmpty(unitAmountET.getText())) {
                unitAmountET.requestFocus();
                unitAmountET.setError("请输入单位金额！");
                return false;
            }
            unitAmount = Double.valueOf(unitAmountET.getText().toString().trim());
            if (unitAmount <= 0) {
                unitAmountET.requestFocus();
                unitAmountET.setError("请输入有效金额！");
                return false;
            }
        }
        return true;
    }
    private void initialInput(){
        mShift = null;
        shiftType = MyTool.SHIFT_NORMAL;
        shiftName = "";
        unitAmount = 0;
        shiftNameET.setText("");
        unitAmountET.setText("");
        normalRB.setChecked(true);
        leaveRB.setChecked(false);
        countRB.setChecked(false);
        linearGroup.setVisibility(View.INVISIBLE);
        shiftNameET.requestFocus();
    }
    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if(mode == 1)
            onDialogFragmentDismiss.onDissmiss(isAdd);
        else
            onDialogFragmentDismiss.onDissmiss(isAdd,mShift);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mode = 1;
        id = 0;
        shiftType = MyTool.SHIFT_NORMAL;
        shiftName = "";
        unitAmount = 0;
        isAdd = false;
        Bundle bundle = getArguments();
        if(bundle != null){
            mode = 2;
            id = bundle.getLong("ID");
            shiftName = bundle.getString("ShiftName");
            oldName = bundle.getString("ShiftName");
            shiftType = bundle.getInt("ShiftType");
            unitAmount = bundle.getDouble("UnitAmount");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_edit_shift_layout,container,true);
        shiftNameET = view.findViewById(R.id.input_edit_shift_nameTextView);
        unitAmountET = view.findViewById(R.id.input_edit_shift_unitAmountEditText);
        linearGroup = view.findViewById(R.id.show_unitAmount_group);
        linearGroup.setVisibility(View.INVISIBLE);
        radioGroup = view.findViewById(R.id.input_edit_shift_radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.input_edit_shift_normalType:
                        linearGroup.setVisibility(View.INVISIBLE);
                        break;
                    case R.id.input_edit_shift_leaveType:
                        linearGroup.setVisibility(View.INVISIBLE);
                        break;
                    case R.id.input_edit_shift_countType:
                        linearGroup.setVisibility(View.VISIBLE);
                        unitAmountET.requestFocus();
                        break;
                }
            }
        });
        normalRB = view.findViewById(R.id.input_edit_shift_normalType);
        leaveRB = view.findViewById(R.id.input_edit_shift_leaveType);
        countRB = view.findViewById(R.id.input_edit_shift_countType);
        cancelBT = view.findViewById(R.id.input_edit_shift_cancelButton);
        cancelBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        confirmBT = view.findViewById(R.id.input_edit_shift_confirmButton);
        if(mode == 1)
            confirmBT.setText("添加");
        else
            confirmBT.setText("修改");
        confirmBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(confirmInput()) {
                    if(mShift == null)
                        mShift = new Shift();
                    mShift.setName(shiftName);
                    mShift.setType(shiftType);
                    mShift.setUnitAmount(unitAmount);
                    if (mode == 1) {
                        mShift.save();
                        isAdd = true;
                        Toast toast = Toast.makeText(getContext(),"已保存！可继续添加下一个，或按关闭返回。",Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();
                        initialInput();
                    } else {
                        mShift.update(id);
                        isAdd = true;
                        dismiss();
                    }
                }
            }
        });
        initialRadioButton();
        if(mode == 2){
            shiftNameET.setText(shiftName);
            if(shiftType == MyTool.SHIFT_NEEDCOUNT){
                unitAmountET.setText(unitAmount+"");
            }
        }
        shiftNameET.requestFocus();
        shiftNameET.selectAll();
        Window window = getDialog().getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if(dialog != null){
            dialog.setCanceledOnTouchOutside(false);
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            int width = (int)(Math.min(dm.widthPixels,dm.heightPixels)*0.8);
            dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}
