package aqth.yzw.iamlittle;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.LitePal;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import aqth.yzw.iamlittle.Adapters.PersonManageAdapter;
import aqth.yzw.iamlittle.EntityClass.ItemEntity;
import aqth.yzw.iamlittle.EntityClass.ItemEntityPerson;
import aqth.yzw.iamlittle.EntityClass.Person;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;

public class PersonManageFragment extends Fragment {
    private RecyclerView recyclerView;
    private List<ItemEntity> list;
    private PersonManageAdapter adapter;
    private PopupWindow popupWindow;
    private View contentView;
    private Button dele,setRatio,setLeave,setOnduty,edit;
    private TextView nameTV,ageTV,genderTV,statusTV,ratioTV,phoneTV,noteTV;
    private ImageView close,personHeadshot,call;
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
    protected void showAddPersonDialog(){
        InputEditPersonInfoFragment fragment = new InputEditPersonInfoFragment();
        fragment.setOnDialogFragmentDismiss(new OnDialogFragmentDismiss() {
            @Override
            public void onDissmiss(boolean flag) {
                if(flag){
                    final Person p = LitePal.order("id").findLast(Person.class);
                    if (p != null) {
                        updateList();
                        adapter.notifyDataSetChanged();
                        recyclerView.smoothScrollToPosition(adapter.getItemCount()-1);
                    }
                }
            }
            @Override
            public void onDissmiss(boolean flag, Object o) {

            }
        });
        //fragment.getDialog().setCancelable(false);
        fragment.show(getFragmentManager(),"AddPerson");
    }
    protected void showCallDialog(Person p){
        final String phoneNumber = p.getPhone();
        if (!TextUtils.isEmpty(phoneNumber)) {
            MyDialogFragment fragment = MyDialogFragment.newInstant("是否需要联系" + p.getName() + "？", "关闭", "是的");
            fragment.setOnDialogFragmentDismiss(new OnDialogFragmentDismiss() {
                @Override
                public void onDissmiss(boolean flag) {
                    if (flag) {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
                        startActivity(intent);
                    }
                }

                @Override
                public void onDissmiss(boolean flag, Object o) {

                }
            });
            fragment.show(getFragmentManager(), "CallPerson");
        } else {
            Toast toast = Toast.makeText(getContext(), "没有找到" + p.getName() + "的电话号码", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }
    protected void editPerson(final int position,final Person p){
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
    }
    protected void delePerson(final int position, final String name){
        MyDialogFragment dialogFragment = MyDialogFragment.newInstant("是否删除"+name,"取消","删除",Color.BLACK,Color.RED);
        ((MyDialogFragment) dialogFragment).setOnDialogFragmentDismiss(new OnDialogFragmentDismiss() {
            @Override
            public void onDissmiss(boolean flag) {
                if (flag){
                    LitePal.deleteAll(Person.class,"Name = ?",name);
                    list.remove(position);
                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeChanged(position,adapter.getItemCount() - position);
                }
            }

            @Override
            public void onDissmiss(boolean flag, Object o) {

            }
        });
        dialogFragment.show(getFragmentManager(),"dialog");
    }
    protected void editRatio(final int position,final Person p){
        final ItemEntityPerson person = (ItemEntityPerson) list.get(position);
        EditPersonRatioFragment fragment1 = EditPersonRatioFragment.newInstant(p.getName(),p.getRatio());
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
                    p.save();
                    person.setRatio(newValue);
                    adapter.notifyItemChanged(position);
                }
            }
        });
        fragment1.show(getFragmentManager(),"EditRatio");
    }
    protected void getPopWindow(final Person p ,final int position){
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width =(int) (dm.widthPixels * 0.95);
        int height = WindowManager.LayoutParams.WRAP_CONTENT;
        popupWindow = new PopupWindow(width,height);
        if(p.getStatus() == MyTool.PERSON_STATUS_ONDUTY) {
            setOnduty.setVisibility(View.GONE);
            setLeave.setVisibility(View.VISIBLE);
        }
        else {
            setLeave.setVisibility(View.GONE);
            setOnduty.setVisibility(View.VISIBLE);
        }
        nameTV.setText(p.getName());
        ageTV.setText(p.getAge() + "岁");
        genderTV.setText(p.getGender()?"男":"女");
        statusTV.setText(p.getStatus()==MyTool.PERSON_STATUS_ONDUTY?"状态：在岗":"状态：离开");
        ratioTV.setText(p.getRatio()+"");
        phoneTV.setText(p.getPhone());
        noteTV.setText(p.getNote());
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCallDialog(p);
                popupWindow.dismiss();
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPerson(position,p);
                popupWindow.dismiss();
            }
        });
        dele.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delePerson(position,p.getName());
                popupWindow.dismiss();
            }
        });
        setRatio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editRatio(position, p);
                popupWindow.dismiss();
            }
        });
        setLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                p.setStatus(MyTool.PERSON_STATUS_LEAVE);
                p.update(p.getId());
                adapter.notifyItemChanged(position);
                popupWindow.dismiss();
            }
        });
        setOnduty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                p.setStatus(MyTool.PERSON_STATUS_ONDUTY);
                p.update(p.getId());
                adapter.notifyItemChanged(position);
                popupWindow.dismiss();
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        popupWindow.setContentView(contentView);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                popupWindow = null;
            }
        });
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(false);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        popupWindow.setAnimationStyle(R.style.pop_window_animation);
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateList();
        adapter = new PersonManageAdapter(list);
        adapter.setItemClickListener(new IItemClickListener() {
            @Override
            public void onClick(View view, final int position) {
                if (position == list.size() - 1) {
                    // 新增一人
                    if (popupWindow!= null && popupWindow.isShowing())
                        popupWindow.dismiss();
                    else
                        showAddPersonDialog();
                } else {
                    ItemEntityPerson person = (ItemEntityPerson) list.get(position);
                    final Person p = person.getPerson();
                    if (popupWindow == null)
                        getPopWindow(p,position);
                    if (popupWindow.isShowing())
                        popupWindow.dismiss();
                    else
                        popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
                }
            }
            @Override
            public void onClick(View view, int x, int y) {

            }
        });
        final PersonManageActivity activity = (PersonManageActivity)getActivity();
        activity.setOnBackPress(new PersonManageActivity.IOnBackPress() {
            @Override
            public void onBackPress() {
                if (popupWindow!=null && popupWindow.isShowing())
                    popupWindow.dismiss();
                else
                    activity.finish();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.person_manage_layout,container,false);
        contentView = LayoutInflater.from(getContext()).inflate(R.layout.person_manage_popwindow,null);
        edit = contentView.findViewById(R.id.popmenu_edit_person);
        dele = contentView.findViewById(R.id.popmenu_dele_person);
        setRatio = contentView.findViewById(R.id.popmenu_set_ratio);
        setLeave = contentView.findViewById(R.id.popmenu_set_person_leave);
        setOnduty = contentView.findViewById(R.id.popmenu_set_person_onduty);
        close = contentView.findViewById(R.id.popmenu_close);
        personHeadshot = contentView.findViewById(R.id.popmenu_person_image);
        nameTV = contentView.findViewById(R.id.popmenu_person_name);
        ageTV = contentView.findViewById(R.id.popmenu_person_age);
        genderTV = contentView.findViewById(R.id.popmenu_person_gender);
        statusTV = contentView.findViewById(R.id.popmenu_person_status);
        ratioTV = contentView.findViewById(R.id.popmenu_person_ratio);
        phoneTV = contentView.findViewById(R.id.popmenu_person_phone);
        noteTV = contentView.findViewById(R.id.popmenu_person_note);
        call = contentView.findViewById(R.id.popmenu_person_call);
        recyclerView = view.findViewById(R.id.person_list_recyclerview);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState != SCROLL_STATE_IDLE){
                    if(popupWindow!=null && popupWindow.isShowing())
                        popupWindow.dismiss();
                }
            }
        });
        return view;
    }
}
