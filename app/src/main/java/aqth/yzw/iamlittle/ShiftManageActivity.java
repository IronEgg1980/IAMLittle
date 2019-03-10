package aqth.yzw.iamlittle;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import aqth.yzw.iamlittle.Adapters.ScheduleItemTitleDecoration;
import aqth.yzw.iamlittle.Adapters.ShiftManageAdapter;
import aqth.yzw.iamlittle.EntityClass.ItemEntity;
import aqth.yzw.iamlittle.EntityClass.ItemEntityShift;
import aqth.yzw.iamlittle.EntityClass.Shift;

public class ShiftManageActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ShiftManageAdapter adapter;
    private List<ItemEntity> list;

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
        recyclerView = findViewById(R.id.shift_manage_recyclerview);
        adapter = new ShiftManageAdapter(list);
        adapter.setShiftItemClickListener(new IItemClickListener() {
            @Override
            public void onClick(View view, final int position) {
                if(position == adapter.getItemCount() -1){
                    // 新增按钮
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
                }else{
                    ItemEntityShift itemEntity = (ItemEntityShift) list.get(position);
                    final Shift shift = itemEntity.getShift();
                    PopupMenu popupMenu = new PopupMenu(getApplicationContext(),view);
                    Menu menu = popupMenu.getMenu();
                    menu.clear();
                    menu.add(0,1,0,"修改");
                    menu.add(0,2,0,"删除");
                    if(shift.getType() == MyTool.SHIFT_NEEDCOUNT)
                        menu.add(0,3,0,"调整单位金额");
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            int id = menuItem.getItemId();
                            switch (id){
                                case 1:
                                    // 修改
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
                                    break;
                                case 2:
                                    // 删除
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
                                    break;
                                case 3:
                                    // 修改单位金额
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
                                    break;
                            }
                            return true;
                        }
                    });
                    popupMenu.show();
                }
            }

            @Override
            public void onClick(View view, int x, int y) {

            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(ShiftManageActivity.this));
    }
}
