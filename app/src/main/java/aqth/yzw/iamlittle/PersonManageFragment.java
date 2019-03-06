package aqth.yzw.iamlittle;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import org.litepal.LitePal;

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
        List<Person> people = LitePal.findAll(Person.class);
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
                    Person p = new Person();
                    p.setName("张三"+position);
                    p.setRatio(1.2);
                    p.save();
                    list.add(position,new ItemEntityPerson(p));
                    adapter.notifyItemInserted(position);
                    adapter.notifyItemRangeChanged(position,2);
                }else{
                    ItemEntityPerson person = (ItemEntityPerson)list.get(position) ;
                    final Person p = person.getPerson();
                    PopupMenu menu = new PopupMenu(getContext(),view);
                    menu.inflate(R.menu.person_manage_popmenu);
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
                                    });
                                    dialogFragment.show(getFragmentManager(),"dialog");
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
