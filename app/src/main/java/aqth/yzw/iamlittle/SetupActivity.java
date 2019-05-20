package aqth.yzw.iamlittle;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import org.litepal.LitePal;
import org.litepal.LitePalDB;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import aqth.yzw.iamlittle.EntityClass.AppSetup;

public class SetupActivity extends MyActivity {
    private TextView versionTV,contentTV,organizeNameTV;
    private Button backBT,restoreBT,closeBT;
    private ImageView sendEmail,inputOrganizeName;
    private SimpleDateFormat format1;
    private Calendar calendar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup_activity);
        format1 = new SimpleDateFormat("yyyyMMdd");
        calendar = new GregorianCalendar();
        versionTV = findViewById(R.id.setup_activity_versionTV);
        versionTV.setText("版本："+MyTool.packageName(SetupActivity.this));
        contentTV = findViewById(R.id.setup_activity_contentTV);
        sendEmail = findViewById(R.id.setup_input_sendEmail);
        sendEmail.setOnClickListener(new View.OnClickListener() {
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
        long now = calendar.getTimeInMillis();
        AppSetup temp = LitePal.where("key = 'firstruntime'").findFirst(AppSetup.class);
        long l = Long.parseLong(temp.getValue());
        int[] values = MyTool.getValues(l,now);
//        long diff = now - l;
        SimpleDateFormat format = new SimpleDateFormat("yyyy年M月d日 HH:mm");
        String firstTimeString = format.format(new Date(l));
        String s = "从 "+firstTimeString+" 这一刻开始，我已为您服务了 ";
//        long day = diff / MyTool.ONE_DAY_MILLISECOND;
//        long hour = diff % MyTool.ONE_DAY_MILLISECOND / MyTool.ONE_HOUR_MILLISECOND;
//        long minute = diff % MyTool.ONE_HOUR_MILLISECOND/MyTool.ONE_MINUTE_MILLIISECOND;
        if(values[0] > 0){
            s +=values[0] +" 年 ";
        }
        if(values[1] > 0)
            s+=values[1] + " 个月 ";
        if(values[2] >0)
            s+=values[2] + " 天 ";
        if(values[3] > 0){
            s += values[3]+" 小时 ";
        }
        if(values[4] > 1){
            s += values[4]+" 分钟。";
        }else{
            s+=" 1 分钟。";
        }
//        s+="\n\n\n感谢您的信任，祝您生活愉快！";
        contentTV.setText(s);
        organizeNameTV = findViewById(R.id.setup_organizenameTV);
        String organizeName ="您还没有设置科室或团队名称";
        if(LitePal.isExist(AppSetup.class,"key = 'organizename'")){
            AppSetup appSetup = LitePal.where("key = 'organizename'").findFirst(AppSetup.class);
            organizeName = appSetup.getValue();
        }
        organizeNameTV.setText(organizeName);
        inputOrganizeName = findViewById(R.id.setup_input_organizenameTV);
        inputOrganizeName.setOnClickListener(new View.OnClickListener() {
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
        String _name = "";
        AppSetup appSetup = LitePal.where("key = 'organizename'").findFirst(AppSetup.class);
        if(appSetup == null){
            appSetup = new AppSetup();
            appSetup.setKey("organizename");
        }else{
            _name = appSetup.getValue();
        }
        final AppSetup _appSetup = appSetup;
        MyDialogFragmentInput input = MyDialogFragmentInput.newInstant(_name,"请输入科室、团队或组织名称");
        input.setOnDialogFragmentDismiss(new OnDialogFragmentDismiss() {
            @Override
            public void onDissmiss(boolean flag) {

            }

            @Override
            public void onDissmiss(boolean flag, Object object) {
                if(flag){
                    String name = (String)object;
                    _appSetup.setValue(name);
                    _appSetup.save();
                    organizeNameTV.setText(name);
                }
            }
        });
        input.show(getSupportFragmentManager(),"InputName");
    }
    private void backup(){
        //PermissionUtils.verifyStoragePermissions(this);
        if (XXPermissions.isHasPermission(SetupActivity.this, Permission.Group.STORAGE)) {
            File database_file = new File(LitePal.getDatabase().getPath());
            File backUpPath = new File(Environment.getExternalStorageDirectory() + "/IAmLittle/Backup");
            if (!backUpPath.exists()) {
                backUpPath.mkdirs();
            }
            File backFile1 = new File(backUpPath + "/database"+format1.format(calendar.getTime())+".bak");
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
            XXPermissions.with(this)
                    .request(new OnPermission() {
                        @Override
                        public void hasPermission(List<String> granted, boolean isAll) {
                            if(isAll)
                                backup();
                        }

                        @Override
                        public void noPermission(List<String> denied, boolean quick) {
                            if (quick) {
                                Toast.makeText(SetupActivity.this,"已被永久拒绝使用外置存储权限，请手动授予权限",Toast.LENGTH_SHORT).show();
                                //如果是被永久拒绝就跳转到应用权限系统设置页面
                                XXPermissions.gotoPermissionSettings(SetupActivity.this);
                            } else {
                                Toast.makeText(SetupActivity.this,"您已拒绝使用外置存储权限，不能使用该功能！",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
    private void restore(){
        if (XXPermissions.isHasPermission(SetupActivity.this, Permission.Group.STORAGE)) {
            final File database_file = new File(LitePal.getDatabase().getPath());
            final File backUpPath = new File(Environment.getExternalStorageDirectory() + "/IAmLittle/Backup");
            final File another_file = new File(database_file.getParent() + "/database_iamlittle.db-journal");
            if (!backUpPath.exists()) {
                Toast.makeText(SetupActivity.this, "未找到备份文件，不能还原数据库。", Toast.LENGTH_LONG).show();
                return;
            }
            SingleSelectListFragment fragment = new SingleSelectListFragment();
            fragment.setOnDialogFragmentDismiss(new OnDialogFragmentDismiss() {
                @Override
                public void onDissmiss(boolean flag) {

                }

                @Override
                public void onDissmiss(boolean flag, Object object) {
                    if(flag){
                        final String s = (String)object;
                        MyDialogFragment dialogFragment = MyDialogFragment.newInstant("请确认是否恢复【"+s+"】的备份数据？","取消","确认");
                        dialogFragment.setOnDialogFragmentDismiss(new OnDialogFragmentDismiss() {
                            @Override
                            public void onDissmiss(boolean flag) {
                                if (flag){
                                    File backFile1 = new File(backUpPath + "/database"+s+".bak");
                                    if (!backFile1.exists()) {
                                        Toast.makeText(SetupActivity.this, "未找到备份文件，不能还原数据库。", Toast.LENGTH_LONG).show();
                                        return;
                                    }
                                    File backFile = new File(database_file.getParent()+"/database.bak");
                                    try {
                                        if(backFile.exists())
                                            backFile.delete();
                                        database_file.renameTo(backFile);
                                        //LitePal.deleteDatabase("database_iamlittle");
                                        if (another_file.exists())
                                            another_file.delete();
                                        InputStream inputStream1 = new FileInputStream(backFile1);
                                        OutputStream outputStream1 = new FileOutputStream(database_file);
                                        byte[] bt = new byte[1024];
                                        int b;
                                        while ((b = inputStream1.read(bt)) > 0) {
                                            outputStream1.write(bt, 0, b);
                                        }
                                        inputStream1.close();
                                        outputStream1.close();
                                        backFile.delete();
                                        LitePal.useDefault();
                                        Toast.makeText(SetupActivity.this, "还原成功！", Toast.LENGTH_LONG).show();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        backFile.renameTo(database_file);
                                        Toast.makeText(SetupActivity.this, "还原失败！\n" + e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            }

                            @Override
                            public void onDissmiss(boolean flag, Object object) {

                            }
                        });
                        dialogFragment.show(getSupportFragmentManager(),"RestoreConfirm");
                    }
                }
            });
            fragment.show(getSupportFragmentManager(),"SelectRestoreFile");
        }else{
            XXPermissions.with(this)
                    .request(new OnPermission() {
                        @Override
                        public void hasPermission(List<String> granted, boolean isAll) {
                            if(isAll)
                                restore();
                        }

                        @Override
                        public void noPermission(List<String> denied, boolean quick) {
                            if (quick) {
                                Toast.makeText(SetupActivity.this,"已被永久拒绝使用外置存储权限，请手动授予权限",Toast.LENGTH_SHORT).show();
                                //如果是被永久拒绝就跳转到应用权限系统设置页面
                                XXPermissions.gotoPermissionSettings(SetupActivity.this);
                            } else {
                                Toast.makeText(SetupActivity.this,"您已拒绝使用外置存储权限，不能使用该功能！",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}
