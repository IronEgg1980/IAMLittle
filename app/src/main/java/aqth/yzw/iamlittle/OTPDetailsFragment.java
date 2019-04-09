package aqth.yzw.iamlittle;

import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.LitePal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import aqth.yzw.iamlittle.Adapters.OTPDetailsAdapter;
import aqth.yzw.iamlittle.EntityClass.ItemEntity;
import aqth.yzw.iamlittle.EntityClass.OTPDetails;
import aqth.yzw.iamlittle.EntityClass.OTPPersonTotalEntity;
import aqth.yzw.iamlittle.EntityClass.OverTimePay;
import jxl.Workbook;
import jxl.format.CellFormat;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class OTPDetailsFragment extends Fragment {
    private String TAG = "殷宗旺";
    private List<ItemEntity> list;
    private RecyclerView recyclerView;
    private OTPDetailsAdapter adapter;
    private Date startDay,endDay;
    private SimpleDateFormat format;
    private long recordTime;
    private TextView titleTV;
    private double totalAmount;
    private void fillList(){
        if(list == null)
            list = new ArrayList<>();
        list.clear();
        totalAmount = 0;
        Cursor cursor = LitePal.findBySQL("SELECT DISTINCT personName FROM OverTimePay WHERE recordTime = '"+String.valueOf(recordTime)+"'");
        List<String> person = new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                person.add(cursor.getString(0));
            }while (cursor.moveToNext());
        }
        for(String personName:person) {
            if(TextUtils.isEmpty(personName))
                continue;
            List<OverTimePay> temp = LitePal.order("shiftName")
                    .where("recordtime = ? and personName = ?", String.valueOf(recordTime),personName).find(OverTimePay.class);
            OTPPersonTotalEntity total = new OTPPersonTotalEntity(temp);
            list.add(total);
            for (OverTimePay o: temp) {
                OTPDetails details = new OTPDetails(o);
                list.add(details);
                totalAmount  = Arith.add(totalAmount,details.getAmount());
            }
        }
        if(list.size() > 0){
            OTPPersonTotalEntity entity =(OTPPersonTotalEntity) list.get(0);
            startDay = entity.getStartDay();
            endDay = entity.getEndDay();
        }else{
            Calendar calendar = new GregorianCalendar();
            startDay = calendar.getTime();
            endDay = calendar.getTime();
        }
        titleTV.setText("总金额："+Double.toString(totalAmount));
        adapter.notifyDataSetChanged();
    }
    private void deleData(){
        MyDialogFragment dialogFragment = MyDialogFragment.newInstant("是否清除当前时间段的所有数据？注意：清除后不能恢复！","取消","清除",
                Color.BLACK,Color.RED);
        dialogFragment.setOnDialogFragmentDismiss(new OnDialogFragmentDismiss() {
            @Override
            public void onDissmiss(boolean flag) {
                if(flag){
                    LitePal.deleteAll(OverTimePay.class,"recordTime = ?",String.valueOf(recordTime));
                    Toast toast = Toast.makeText(getContext(),"已删除数据",Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                    OTPFragment fragment =(OTPFragment) getFragmentManager().findFragmentByTag("Total");
                    if(fragment != null){
                        fragment.notifyDataChange();
                        getActivity().setTitle("历史数据列表");
                        ((OTPActivity)getActivity()).setShowDetails(false);
                        getFragmentManager().popBackStackImmediate(0,0);
                    }else{
                        getActivity().finish();
                    }
                }
            }

            @Override
            public void onDissmiss(boolean flag, Object object) {

            }
        });
        dialogFragment.show(getFragmentManager(),"DeleData");
    }
    private void shareData() {
        File sharedFile = getExcelFile();
        if (sharedFile != null) {
            Uri uri = null;
            if (Build.VERSION.SDK_INT >= 24) {
                uri = FileProvider.getUriForFile(getContext(), "th.yzw.iamlittle.fileprovider", sharedFile);
            } else {
                uri = Uri.fromFile(sharedFile);
            }
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("application/*");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(intent, "加班费表"));
        }
    }
    private File getExcelFile(){
        PermissionUtils.verifyStoragePermissions(getActivity());
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            if(list.size() == 0){
                Toast.makeText(getContext(),"当前列表没有数据",Toast.LENGTH_SHORT).show();
                return null;
            }
            String outPath = Environment.getExternalStorageDirectory() + "/IAmLittle/OTP/";
            File dir = new File(outPath);
            try{
                File tempFile = new File(getContext().getFilesDir() + "/otp_template.xls");
                if (!tempFile.exists()) {
                    AssetManager assetManager = getContext().getAssets();
                    InputStream inputStream = assetManager.open("otp_template.xls");
                    FileOutputStream os = new FileOutputStream(tempFile);
                    int bytes = 0;
                    byte[] buffer = new byte[1024];
                    while ((bytes = inputStream.read(buffer, 0, 1024)) != -1) {
                        os.write(buffer, 0, bytes);
                    }
                    os.close();
                    inputStream.close();
                }
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                for(File file:dir.listFiles()){
                    if(!file.isDirectory())
                        file.delete();
                }
                String s = format.format(startDay)+"-"+format.format(endDay);
                FileOutputStream fos = new FileOutputStream(outPath +"otp"+s+".xls");
                Workbook workbook = Workbook.getWorkbook(tempFile);
                WritableWorkbook wwb = Workbook.createWorkbook(fos, workbook);
                WritableSheet sheet = wwb.getSheet(0);
                CellFormat titleFmt = sheet.getCell(0,0).getCellFormat();
                Label titleLb = new Label(0,0,"时间："+s,titleFmt);
                sheet.addCell(titleLb);
                CellFormat totalFmt = sheet.getCell(0,1).getCellFormat();
                Label totalLb = new Label(0,1,"总金额："+Double.toString(totalAmount));
                sheet.addCell(totalLb);
                int totalRow = 4;
                int detailsRow = 4;
                for(ItemEntity itemEntity:list){
                    if(itemEntity.getType() == ItemType.OTP_PERSON_TOTAL){
                        OTPPersonTotalEntity entity = (OTPPersonTotalEntity)itemEntity;
                        String name = entity.getPersonName();
                        double totalAmount = entity.getTotalAmount();
                        int childcount = entity.getChildCount();
                        CellFormat nameFmt = sheet.getCell(0,totalRow).getCellFormat();
                        sheet.mergeCells(0,totalRow,0,totalRow+childcount-1);
                        Label nameLb = new Label(0,totalRow,name,nameFmt);
                        sheet.addCell(nameLb);
                        CellFormat totalAmountFmt = sheet.getCell(1,totalRow).getCellFormat();
                        sheet.mergeCells(1,totalRow,1,totalRow+childcount-1);
                        Number totalAmountLb = new Number(1,totalRow,totalAmount,totalAmountFmt);
                        sheet.addCell(totalAmountLb);
                        totalRow +=childcount;
                    }else{
                        OTPDetails details = (OTPDetails)itemEntity;
                        String shift = details.getShiftName();
                        double unit = details.getUniteAmount();
                        int count = details.getCount();
                        double amount = details.getAmount();
                        CellFormat shiftFmt = sheet.getCell(2,detailsRow).getCellFormat();
                        CellFormat unitFmt = sheet.getCell(3,detailsRow).getCellFormat();
                        CellFormat countFmt = sheet.getCell(4,detailsRow).getCellFormat();
                        CellFormat amountFmt = sheet.getCell(5,detailsRow).getCellFormat();
                        Label shiftLb = new Label(2,detailsRow,shift,shiftFmt);
                        Number unitLb = new Number(3,detailsRow,unit,unitFmt);
                        Number countLb = new Number(4,detailsRow,count,countFmt);
                        Number amountLb = new Number(5,detailsRow,amount,amountFmt);
                        sheet.addCell(shiftLb);
                        sheet.addCell(unitLb);
                        sheet.addCell(countLb);
                        sheet.addCell(amountLb);
                        detailsRow++;
                    }
                }
                wwb.write();
                wwb.close();;
                fos.close();
                workbook.close();
                Toast.makeText(getContext(),"生成Excel文件成功\n文件目录："+dir.getAbsolutePath(),Toast.LENGTH_SHORT).show();
                return new File(outPath+"otp"+s+".xls");
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(getContext(),"生成Excel文件失败\n"+e.getMessage(),Toast.LENGTH_LONG).show();
                return null;
            }
        }else{
            MyDialogFragmenSingleButton dialogFragment = MyDialogFragmenSingleButton.newInstant("您已拒绝本程序存储文件，不能进行分享操作！", "关闭");
            dialogFragment.show(getFragmentManager(), "NotAllowWriteSD");
            return null;
        }
    }
    public static OTPDetailsFragment newInstant(Date date){
        OTPDetailsFragment fragment = new OTPDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("RecordTime",date.getTime());
        fragment.setArguments(bundle);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recordTime = Calendar.getInstance().getTimeInMillis();
        if(getArguments() != null){
            recordTime = getArguments().getLong("RecordTime");
        }
        format = new SimpleDateFormat("yyyyMMdd");
        list = new ArrayList<>();
        adapter = new OTPDetailsAdapter(list);
    }

    @Override
    public void onStart() {
        super.onStart();
        fillList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.show_otp_details_layout,container,false);
        setHasOptionsMenu(true);
        recyclerView = view.findViewById(R.id.fragment_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        titleTV = view.findViewById(R.id.fragment_title_TV);
        return view;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.title_menu_dele_share,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.title_menu_dele){
            deleData();
            return true;
        }
        if(item.getItemId()== R.id.title_menu_share){
            shareData();
            return true;
        }
        return  super.onOptionsItemSelected(item);
    }
}
