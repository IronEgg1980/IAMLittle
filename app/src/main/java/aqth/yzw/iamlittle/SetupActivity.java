package aqth.yzw.iamlittle;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
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
    private TextView versionTV,contentTV,organizeNameTV,inputOrganizeNameTV,sendEmailTV;
    private Button backBT,restoreBT,closeBT;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup_activity);
        versionTV = findViewById(R.id.setup_activity_versionTV);
        versionTV.setText("版本："+MyTool.packageName(SetupActivity.this));
        contentTV = findViewById(R.id.setup_activity_contentTV);
        sendEmailTV = findViewById(R.id.setup_input_sendEmail);
        sendEmailTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    sendEmail();
                }catch (Exception e){
                    Toast toast = Toast.makeText(SetupActivity.this,"找不到可以发送邮件的App",Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                }
            }
        });
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
        if(minute > 1){
            s += minute+"分钟。";
        }else{
            s+="1分钟。";
        }
        s+="\n\n\n感谢您的信任，祝您生活愉快！";
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
                MyDialogFragment dialogFragment = MyDialogFragment.newInstant("该操作将清除现有数据，并恢复已备份的数据。是否继续？","否","是");
                dialogFragment.setOnDialogFragmentDismiss(new OnDialogFragmentDismiss() {
                    @Override
                    public void onDissmiss(boolean flag) {
                        if (flag){
                            restore();
                        }
                    }

                    @Override
                    public void onDissmiss(boolean flag, Object object) {

                    }
                });
               dialogFragment.show(getSupportFragmentManager(),"Restore");
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
    private void sendEmail(){
        Uri uri = Uri.parse ("mailto: yinzongwang@163.com");
        Intent intent = new Intent (Intent.ACTION_SENDTO, uri);
        this.startActivity(intent);
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
            File backUpPath = new File(Environment.getExternalStorageDirectory() + "/IAmLittle/Backup");
            if (!backUpPath.exists()) {
                backUpPath.mkdirs();
            }
            File backFile1 = new File(backUpPath + "/database.bak");
            try {
                if (backFile1.exists())
                    backFile1.delete();
                InputStream inputStream1 = new FileInputStream(database_file);
                OutputStream outputStream1 = new FileOutputStream(backFile1);
                byte[] bt = new byte[1024];
                int b;
                while ((b = inputStream1.read(bt)) > 0) {
                    outputStream1.write(bt, 0, b);
                }
                inputStream1.close();
                outputStream1.close();
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
        PermissionUtils.verifyStoragePermissions(this);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File database_file = new File(LitePal.getDatabase().getPath());
            File backUpPath = new File(Environment.getExternalStorageDirectory() + "/IAmLittle/Backup");
            File another_file = new File(database_file.getParent() + "/database_iamlittle.db-journal");
            if (!backUpPath.exists()) {
                Toast.makeText(SetupActivity.this, "未找到备份文件，不能还原数据库。", Toast.LENGTH_LONG).show();
                return;
            }
            File backFile1 = new File(backUpPath + "/database.bak");
            if (!backFile1.exists()) {
                Toast.makeText(SetupActivity.this, "未找到备份文件，不能还原数据库。", Toast.LENGTH_LONG).show();
                return;
            }
            LitePal.deleteDatabase("database_iamlittle");
            if (another_file.exists())
                another_file.delete();
            try {
                InputStream inputStream1 = new FileInputStream(backFile1);
                OutputStream outputStream1 = new FileOutputStream(database_file);
                byte[] bt = new byte[1024];
                int b;
                while ((b = inputStream1.read(bt)) > 0) {
                    outputStream1.write(bt, 0, b);
                }
                inputStream1.close();
                outputStream1.close();
                Toast.makeText(SetupActivity.this, "还原成功！", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(SetupActivity.this, "还原失败！\n" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}
