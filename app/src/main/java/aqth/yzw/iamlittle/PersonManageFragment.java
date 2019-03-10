package aqth.yzw.iamlittle;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import org.litepal.LitePal;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import aqth.yzw.iamlittle.Adapters.PersonManageAdapter;
import aqth.yzw.iamlittle.EntityClass.ItemEntity;
import aqth.yzw.iamlittle.EntityClass.ItemEntityPerson;
import aqth.yzw.iamlittle.EntityClass.Person;

public class PersonManageFragment extends Fragment {
    private RecyclerView recyclerView;
    private List<ItemEntity> list;
    private PersonManageAdapter adapter;
    protected void updateList(){
        if(list == null)
            list = new ArrayList<>();
        list.clear();
        List<Person> people = LitePal.order("id").find(Person.class);
        for (Person p : people){
            list.add(new ItemEntityPerson(p));
        }
        list.add(new ItemEntity());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateList();
        adapter = new PersonManageAdapter(list);
        adapter.setItemClickListener(new IItemClickListener() {
            @Override
            public void onClick(View view, final int position) {
                if(position == list.size() -1){
                    // 新增一人
                    InputEditPersonInfoFragment fragment = new InputEditPersonInfoFragment();
                    fragment.setOnDialogFragmentDismiss(new OnDialogFragmentDismiss() {
                        @Override
                        public void onDissmiss(boolean flag) {
                            if(flag){
                                final Person p = LitePal.order("id").findLast(Person.class);
                                if (p != null) {
                                    list.add(position,new ItemEntityPerson(p));
                                    adapter.notifyItemInserted(position);
                                    adapter.notifyItemRangeChanged(position, 2);
                                    recyclerView.smoothScrollToPosition(adapter.getItemCount()-1);
                                }
                            }
                        }
                        @Override
                        public void onDissmiss(boolean flag, Object o) {

                        }
                    });
                    fragment.show(getFragmentManager(),"AddPerson");
                }else{

                    ItemEntityPerson person = (ItemEntityPerson)list.get(position) ;
                    final Person p = person.getPerson();
                    final String phoneNumber = p.getPhone();
                    if (view.getTag() != null || "Call".equals((String) view.getTag())) {
                        if(!TextUtils.isEmpty(phoneNumber)) {
                            MyDialogFragment fragment = MyDialogFragment.newInstant("是否需要联系"+p.getName()+"？","关闭","是的");
                            fragment.setOnDialogFragmentDismiss(new OnDialogFragmentDismiss() {
                                @Override
                                public void onDissmiss(boolean flag) {
                                    if (flag){
                                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+phoneNumber));
                                        startActivity(intent);
                                    }
                                }

                                @Override
                                public void onDissmiss(boolean flag,Object o) {

                                }
                            });
                            fragment.show(getFragmentManager(),"CallPerson");
                        }else{
                            Toast toast = Toast.makeText(getContext(),"没有找到"+p.getName()+"的号码，请修改电话号码后再试一次！",Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER,0,0);
                            toast.show();
                        }
                        return;
                    }
                    PopupMenu menu = new PopupMenu(getContext(),view);
                    if(p.getStatus() == MyTool.PERSON_STATUS_ONDUTY)
                        menu.inflate(R.menu.person_manage_popmenu);
                    else
                        menu.inflate(R.menu.person_manage_popmenu2);
                    menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            int i = item.getItemId();
                            switch (i){
                                case R.id.popmenu_dele_person:
                                    MyDialogFragment dialogFragment = MyDialogFragment.newInstant("是否删除"+p.getName(),"取消","删除",Color.BLACK,Color.RED);
                                    ((MyDialogFragment) dialogFragment).setOnDialogFragmentDismiss(new OnDialogFragmentDismiss() {
                                        @Override
                                        public void onDissmiss(boolean flag) {
                                            if (flag){
                                                LitePal.deleteAll(Person.class,"Name = ?",p.getName());
                                                list.remove(position);
                                                adapter.notifyItemRemoved(position);
                                                adapter.notifyItemRangeChanged(position,adapter.getItemCount() - position);
                                            }else{
                                                Toast.makeText(getContext(),"取消操作",Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onDissmiss(boolean flag, Object o) {

                                        }
                                    });
                                    dialogFragment.show(getFragmentManager(),"dialog");
                                    break;
                                case R.id.popmenu_edit_person:
                                    InputEditPersonInfoFragment fragment = InputEditPersonInfoFragment.newInstant(p);
                                    fragment.setOnDialogFragmentDismiss(new OnDialogFragmentDismiss() {
                                        @Override
                                        public void onDissmiss(boolean flag) {
                                            if(flag){
                                                //updateList();
                                                adapter.notifyItemChanged(position);
                                            }
                                        }

                                        @Override
                                        public void onDissmiss(boolean flag, Object o) {

                                        }
                                    });
                                    fragment.show(getFragmentManager(),"EditPerson");
                                    break;
                                case R.id.popmenu_set_person_leave:
                                    p.setStatus(MyTool.PERSON_STATUS_LEAVE);
                                    p.update(p.getId());
                                    adapter.notifyItemChanged(position);
                                    break;
                                case R.id.popmenu_set_ratio:
                                    EditPersonRatioFragment fragment1 = EditPersonRatioFragment.newInstant(p);
                                    fragment1.setOnDialogFragmentDismiss(new OnDialogFragmentDismiss() {
                                        @Override
                                        public void onDissmiss(boolean flag) {

                                        }

                                        @Override
                                        public void onDissmiss(boolean flag, Object o) {
                                            if(flag){
                                                //updateList();
                                                double newValue = (double)o;
                                                p.setRatio(newValue);
                                                adapter.notifyItemChanged(position);
                                            }
                                        }
                                    });
                                    fragment1.show(getFragmentManager(),"EditRatio");
                                    break;
                                case R.id.popmenu_set_person_onduty:
                                    p.setStatus(MyTool.PERSON_STATUS_ONDUTY);
                                    p.update(p.getId());
                                    adapter.notifyItemChanged(position);
                                    break;

                            }
                            return true;
                        }
                    });
                    menu.show();
                }
            }

            @Override
            public void onClick(View view, int x, int y) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.person_manage_layout,container,false);
        recyclerView = view.findViewById(R.id.person_list_recyclerview);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }
}
