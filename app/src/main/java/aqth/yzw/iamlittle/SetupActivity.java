package aqth.yzw.iamlittle;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.LitePal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
        s+="\n\n感谢您对我的信任，祝您生活愉快！";
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
        PermissionUtils.verifyStoragePermissions(this);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File database_file = new File(LitePal.getDatabase().getPath());
            File another_file = new File(database_file.getParent() + "/database_iamlittle.db-journal");
            File backUpPath = new File(Environment.getExternalStorageDirectory() + "/IAmLittle/Backup");
            if (!backUpPath.exists()) {
                backUpPath.mkdirs();
            }
            File backFile1 = new File(backUpPath + "/database.bak");
            File backFile2 = new File(backUpPath + "/database-journal.bak");
            try {
                if (backFile1.exists())
                    backFile1.delete();
                if (backFile2.exists())
                    backFile2.delete();
                InputStream inputStream1 = new FileInputStream(database_file);
                OutputStream outputStream1 = new FileOutputStream(backFile1);
                InputStream inputStream2 = new FileInputStream(another_file);
                OutputStream outputStream2 = new FileOutputStream(backFile2);
                byte[] bt = new byte[1024];
                int b;
                while ((b = inputStream1.read(bt)) > 0) {
                    outputStream1.write(bt, 0, b);
                }
                inputStream1.close();
                outputStream1.close();
                b = -1;
                while ((b = inputStream2.read(bt)) > 0) {
                    outputStream2.write(bt, 0, b);
                }
                inputStream2.close();
                outputStream2.close();
                Toast.makeText(SetupActivity.this, "备份成功！\n备份路径：" + backUpPath.getAbsolutePath(), Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(SetupActivity.this, "备份失败！\n" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(SetupActivity.this, "您已拒绝访问外部存储，无法备份！", Toast.LENGTH_SHORT).show();
        }
    }
    private void restore(){
        File database_file = new File(LitePal.getDatabase().getPath());
        if(database_file.exists())
            database_file.delete();
        File another_file = new File(database_file.getParent() + "/database_iamlittle.db-journal");
        if(another_file.exists())
            another_file.delete();
        File backUpPath = new File(Environment.getExternalStorageDirectory() + "/IAmLittle/Backup");
        if (!backUpPath.exists()) {
            Toast.makeText(SetupActivity.this, "未找到备份文件，不能还原数据库。" , Toast.LENGTH_LONG).show();
            return;
        }
        File backFile1 = new File(backUpPath + "/database.bak");
        File backFile2 = new File(backUpPath + "/database-journal.bak");
        if ((!backFile1.exists()) || (!backFile2.exists() )){
            Toast.makeText(SetupActivity.this, "未找到备份文件，不能还原数据库。" , Toast.LENGTH_LONG).show();
            return;
        }
        try {
            InputStream inputStream1 = new FileInputStream(backFile1);
            OutputStream outputStream1 = new FileOutputStream(database_file);
            InputStream inputStream2 = new FileInputStream(backFile2);
            OutputStream outputStream2 = new FileOutputStream(another_file);
            byte[] bt = new byte[1024];
            int b;
            while ((b = inputStream1.read(bt)) > 0) {
                outputStream1.write(bt, 0, b);
            }
            inputStream1.close();
            outputStream1.close();
            b = -1;
            while ((b = inputStream2.read(bt)) > 0) {
                outputStream2.write(bt, 0, b);
            }
            inputStream2.close();
            outputStream2.close();
            Toast.makeText(SetupActivity.this, "还原成功！" , Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(SetupActivity.this, "还原失败！\n" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
