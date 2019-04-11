package aqth.yzw.iamlittle;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.PopupWindow;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import aqth.yzw.iamlittle.Adapters.ScheduleItemTitleDecoration;
import aqth.yzw.iamlittle.Adapters.ShiftManageAdapter;
import aqth.yzw.iamlittle.EntityClass.ItemEntity;
import aqth.yzw.iamlittle.EntityClass.ItemEntityShift;
import aqth.yzw.iamlittle.EntityClass.Shift;

public class ShiftManageActivity extends MyActivity {
    private String TAG = "殷宗旺";
    private RecyclerView recyclerView;
    private ShiftManageAdapter adapter;
    private List<ItemEntity> list;
    private PopupWindow popupWindow;
    private int xOff;
    private View contentView;
    protected  void updateList(){
        if(list == null)
            list = new ArrayList<>();
        else
            list.clear();
        for(Shift shift:LitePal.findAll(Shift.class)){
            list.add(new ItemEntityShift(shift));
        }
        list.add(new ItemEntity());
    }
    protected void getPopWindow(View view,final Shift shift,final int position){
        if(popupWindow == null) {
            int width = WindowManager.LayoutParams.WRAP_CONTENT;
            int height = view.getHeight();
            popupWindow = new PopupWindow(contentView,width, height);
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            popupWindow.setTouchable(true);
            popupWindow.setOutsideTouchable(false);
            popupWindow.setAnimationStyle(android.R.style.Animation_Translucent);
            contentView.findViewById(R.id.popwindow_edit_shift).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editShift(shift,position);
                    popupWindow.dismiss();
                }
            });
            contentView.findViewById(R.id.popwindow_del_shift).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delShift(shift,position);
                    popupWindow.dismiss();
                }
            });
            Button button = contentView.findViewById(R.id.popwindow_edit_unitamount);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editUnitAmount(shift,position);
                    popupWindow.dismiss();
                }
            });
            if(shift.getType() != MyTool.SHIFT_NEEDCOUNT)
                button.setVisibility(View.GONE);
            else
                button.setVisibility(View.VISIBLE);
            contentView.measure(0,height);
            xOff =view.getMeasuredWidth() - contentView.getMeasuredWidth() + 60;
            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    popupWindow = null;
                }
            });
        }
    }
    protected void addShift(final int position){
        InputEditShiftFragmen fragmen = new InputEditShiftFragmen();
        fragmen.setOnDialogFragmentDismiss(new OnDialogFragmentDismiss() {
            @Override
            public void onDissmiss(boolean flag) {
                if (flag){
                    updateList();
                    adapter.notifyItemRangeChanged(position,adapter.getItemCount() - position);
                    recyclerView.smoothScrollToPosition(adapter.getItemCount() -1 );
                }
            }

            @Override
            public void onDissmiss(boolean flag, Object o) {

            }
        });
        fragmen.show(getSupportFragmentManager(),"AddShift");
    }
    protected void editShift(Shift shift,final int position){
        InputEditShiftFragmen fragmen = InputEditShiftFragmen.newInstant(shift);
        fragmen.setOnDialogFragmentDismiss(new OnDialogFragmentDismiss() {
            @Override
            public void onDissmiss(boolean flag) {

            }

            @Override
            public void onDissmiss(boolean flag, Object o) {
                if (flag){
//                                                Shift shift1 = (Shift)o;
//                                                shift.setName(shift1.getName());
//                                                shift.setType(shift1.getType());
//                                                shift.setUnitAmount(shift1.getUnitAmount());
                    //updateList();
                    adapter.notifyItemChanged(position);
                }
            }
        });
        fragmen.show(getSupportFragmentManager(),"EditShift");
    }
    protected void delShift(final Shift shift,final int position){
        MyDialogFragment fragment = MyDialogFragment.newInstant("是否删除"+shift.getName(),"取消","删除",
                Color.BLACK,Color.RED);
        fragment.setOnDialogFragmentDismiss(new OnDialogFragmentDismiss() {
            @Override
            public void onDissmiss(boolean flag) {
                if(flag){
                    LitePal.delete(Shift.class,shift.getId());
                    list.remove(position);
                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeChanged(position,adapter.getItemCount() - position);
                }
            }

            @Override
            public void onDissmiss(boolean flag, Object o) {

            }
        });
        fragment.show(getSupportFragmentManager(),"Delete");
    }
    protected void editUnitAmount(final Shift shift,final int position){
        EditShiftUnitAmountFragment fragment1 = EditShiftUnitAmountFragment.newInstant(shift);
        fragment1.setOnDialogFragmentDismiss(new OnDialogFragmentDismiss() {
            @Override
            public void onDissmiss(boolean flag) {
            }

            @Override
            public void onDissmiss(boolean flag, Object o) {
                if(flag){
                    //updateList();
                    double newValue = (double)o;
                    shift.setUnitAmount(newValue);
                    adapter.notifyItemChanged(position);
                }
            }
        });
        fragment1.show(getSupportFragmentManager(),"EditUnitAmount");
    }
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shift_manage_layout);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setTitle("班次设置");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        updateList();
        contentView= LayoutInflater.from(ShiftManageActivity.this).inflate(R.layout.shift_manage_popwindow, null);
        recyclerView = findViewById(R.id.shift_manage_recyclerview);
        adapter = new ShiftManageAdapter(list);
        adapter.setShiftItemClickListener(new IItemClickListener() {
            @Override
            public void onClick(View view, final int position) {
                if(position == adapter.getItemCount() -1){
                    // 新增按钮
                    if (popupWindow!= null && popupWindow.isShowing())
                        popupWindow.dismiss();
                    else
                        addShift(position);
                }else{
                    ItemEntityShift itemEntity = (ItemEntityShift) list.get(position);
                    final Shift shift = itemEntity.getShift();
                    if(popupWindow == null)
                        getPopWindow(view,shift,position);
                    int[] local = new int[2];
                    view.getLocationOnScreen(local);
                    if(!popupWindow.isShowing())
                        popupWindow.showAtLocation(view,Gravity.NO_GRAVITY,xOff,local[1]);
                    else {
                        popupWindow.dismiss();
                    }
                }
            }

            @Override
            public void onClick(View view, int x, int y) {

            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(ShiftManageActivity.this));
       // recyclerView.addItemDecoration(new DividerItemDecoration(ShiftManageActivity.this,DividerItemDecoration.VERTICAL));
    }

    @Override
    public void onBackPressed() {
        if (popupWindow!=null && popupWindow.isShowing())
            popupWindow.dismiss();
        else
            finish();
    }
}
