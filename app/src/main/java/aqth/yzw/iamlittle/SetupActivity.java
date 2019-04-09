package aqth.yzw.iamlittle;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.LitePal;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import aqth.yzw.iamlittle.EntityClass.AppSetup;

public class SetupActivity extends AppCompatActivity {
    private TextView versionTV,contentTV,organizeNameTV,inputOrganizeNameTV;
    private Button backBT,restoreBT,closeBT;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup_activity);
        versionTV = findViewById(R.id.setup_activity_versionTV);
        versionTV.setText("版本："+MyTool.packageName(SetupActivity.this));
        contentTV = findViewById(R.id.setup_activity_contentTV);
        long now = new GregorianCalendar().getTimeInMillis();
        AppSetup temp = LitePal.where("key = 'firstruntime'").findFirst(AppSetup.class);
        long l = Long.parseLong(temp.getValue());
        Calendar firstRunTime = new GregorianCalendar();
        firstRunTime.setTimeInMillis(l);
        long diff = now - l;
        SimpleDateFormat format = new SimpleDateFormat("yyyy年M月d日 HH:mm");
        String firstTimeString = format.format(firstRunTime.getTime());
        String s = "从"+firstTimeString+"这一刻开始，我已为您服务了";
        long day = diff / MyTool.ONE_DAY_MILLISECOND;
        long hour = diff % MyTool.ONE_DAY_MILLISECOND / MyTool.ONE_HOUR_MILLISECOND;
        long minute = diff % MyTool.ONE_HOUR_MILLISECOND/MyTool.ONE_MINUTE_MILLIISECOND;
        if(day > 0){
            s +=day +"天";
        }
        if(hour > 0){
            s += hour+"小时";
        }
        if(minute > 0){
            s += minute+"分钟。";
        }
        s+="\n感谢您对我的信任，祝您生活愉快！";
        contentTV.setText(s);
        organizeNameTV = findViewById(R.id.setup_organizenameTV);
        String organizeName ="您还没有设置科室或团队名称";
        if(LitePal.isExist(AppSetup.class,"key = 'organizename'")){
            AppSetup appSetup = LitePal.where("key = 'organizename'").findFirst(AppSetup.class);
            organizeName = appSetup.getValue();
        }
        organizeNameTV.setText(organizeName);
        inputOrganizeNameTV = findViewById(R.id.setup_input_organizenameTV);
        inputOrganizeNameTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputOrganizeNameClick();
            }
        });
        backBT = findViewById(R.id.setup_backup);
        backBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backup();
            }
        });
        restoreBT = findViewById(R.id.setup_restore);
        restoreBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restore();
            }
        });
        closeBT = findViewById(R.id.setup_close);
        closeBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void inputOrganizeNameClick(){
        MyDialogFragmentInput input = MyDialogFragmentInput.newInstant("请输入科室、团队或组织的名称");
        input.setOnDialogFragmentDismiss(new OnDialogFragmentDismiss() {
            @Override
            public void onDissmiss(boolean flag) {

            }

            @Override
            public void onDissmiss(boolean flag, Object object) {
                if(flag){
                    String name = (String)object;
                    AppSetup appSetup = LitePal.where("key = 'organizename'").findFirst(AppSetup.class);
                    if(appSetup == null){
                        appSetup = new AppSetup();
                        appSetup.setKey("organizename");
                    }
                    appSetup.setValue(name);
                    appSetup.save();
                    organizeNameTV.setText(name);
                }
            }
        });
        input.show(getSupportFragmentManager(),"InputName");
    }
    private void backup(){
        File database_file = new File(LitePal.getDatabase().getPath());
        File another_file = new File(database_file.getParent()+"/database_iamlittle.db-journal");
        File backUpPath = new File(Environment.getExternalStorageDirectory()+"/IAmLittle/Backup");
        if(!backUpPath.exists()){
            backUpPath.mkdirs();
        }

    }
    private void restore(){

    }
}
