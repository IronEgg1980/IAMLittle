package aqth.yzw.iamlittle;

import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.litepal.LitePal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import aqth.yzw.iamlittle.Adapters.PRPDetailsAdapter;
import aqth.yzw.iamlittle.EntityClass.ItemEntity;
import aqth.yzw.iamlittle.EntityClass.ItemEntityJXGZPersonDetails;
import aqth.yzw.iamlittle.EntityClass.ItemEntityJXGZPersonTotal;
import aqth.yzw.iamlittle.EntityClass.ItemEntityJXGZTotalDetails;
import aqth.yzw.iamlittle.EntityClass.JXGZDetails;
import aqth.yzw.iamlittle.EntityClass.JXGZPersonDetails;
import aqth.yzw.iamlittle.EntityClass.OTPDetails;
import aqth.yzw.iamlittle.EntityClass.OTPPersonTotalEntity;
import jxl.Workbook;
import jxl.format.CellFormat;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class PRPDetailsFragment extends Fragment {
    private List<ItemEntity> totalDetailsList, personDetailsList;
    private RecyclerView totalRecyclerView, personRecyclerView;
    private PRPDetailsAdapter totalAdapter, personAdapter;
    private int mode;
    private String recordTime,dateString;
    private PRPActivity activity;
    private void showToast(String content) {
        Toast toast = Toast.makeText(getContext(), content, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private void updateTotalList() {
        dateString = "no_data";
        if (totalDetailsList == null)
            totalDetailsList = new ArrayList<>();
        totalDetailsList.clear();
        List<JXGZDetails> temp1 = LitePal.order("JXGZType")
                .where("recordTime = ?", recordTime).find(JXGZDetails.class);
        if (temp1 != null && temp1.size() > 0) {
            SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
            Date date = temp1.get(0).getDate();
            dateString = format.format(date);
            for (JXGZDetails details : temp1) {
                totalDetailsList.add(new ItemEntityJXGZTotalDetails(details));
            }
            updatePersonList();
        }else{
            totalDetailsList.add(new ItemEntity());
        }
    }

    private void updatePersonList() {
        if (personDetailsList == null) {
            personDetailsList = new ArrayList<>();
        }
        personDetailsList.clear();
        List<String> people = new ArrayList<>();
        Cursor cursor = LitePal.findBySQL("SELECT DISTINCT personName FROM JXGZPersonDetails");
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String name = cursor.getString(0);
                if(!TextUtils.isEmpty(name))
                    people.add(name);
            } while (cursor.moveToNext());
        }
        if (people.size() > 0) {
            for (String s : people) {
                List<JXGZPersonDetails> details = LitePal.where("personName = ? and recordTime = ?",
                        s, recordTime).find(JXGZPersonDetails.class);
                if (details != null && details.size() > 0) {
                    personDetailsList.add(new ItemEntityJXGZPersonTotal(details));
                }
            }
        }
    }

    private void showChild(int position) {
        ItemEntity itemEntity = personDetailsList.get(position);
        if (itemEntity.getType() == ItemType.JXGZ_PERSON_TOTAL) {
            ItemEntityJXGZPersonTotal total = (ItemEntityJXGZPersonTotal) itemEntity;
            int count = total.getChildCount();
            List<JXGZPersonDetails> temp = total.getList();
            int tempPos = position + 1;
            for (int i = 0; i < count; i++) {
                personDetailsList.add(tempPos + i, new ItemEntityJXGZPersonDetails(temp.get(i)));
            }
            personAdapter.notifyItemRangeChanged(position, personAdapter.getItemCount() - position);
            personRecyclerView.smoothScrollToPosition(position + count);
        }
    }

    private void hideChild(int position) {
        ItemEntity itemEntity = personDetailsList.get(position);
        if (itemEntity.getType() == ItemType.JXGZ_PERSON_TOTAL) {
            ItemEntityJXGZPersonTotal total = (ItemEntityJXGZPersonTotal) itemEntity;
            int count = total.getChildCount();
            for (int i = count; i > 0; i--) {
                personDetailsList.remove(position+i);
                personAdapter.notifyItemRemoved(position+i);
            }
            personAdapter.notifyItemRangeChanged(position, personAdapter.getItemCount() - position);
        }
    }

    private void dele() {
        MyDialogFragment dialogFragment = MyDialogFragment.newInstant("是否删除该月份的所有数据？\n注意：删除后不能恢复！","取消","删除",
                Color.BLACK,Color.RED);
        dialogFragment.setOnDialogFragmentDismiss(new OnDialogFragmentDismiss() {
            @Override
            public void onDissmiss(boolean flag) {
                if(flag){
                    LitePal.deleteAll(JXGZDetails.class,"recordTime = ?",recordTime);
                    LitePal.deleteAll(JXGZPersonDetails.class,"recordTime = ?",recordTime);
                    showToast("已删除");
                    if(mode == 0){
                        activity.finish();
                    }else{
                        activity.setShowDetails(false);
                        activity.setTitle("历史数据列表");
                        PRPListFragment fragment =(PRPListFragment) getFragmentManager().findFragmentByTag("List");
                        if(fragment!=null){
                            fragment.notifyDataChange();
                        }
                        getFragmentManager().popBackStackImmediate();
                    }
                }
            }

            @Override
            public void onDissmiss(boolean flag, Object object) {

            }
        });
        dialogFragment.show(getFragmentManager(),"Delete");

    }

    private void share() {
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
            startActivity(Intent.createChooser(intent, dateString+"绩效工资表"));
        }
    }
    private File getExcelFile(){
        if(totalDetailsList.size()==1 && totalDetailsList.get(0).getType() == ItemType.EMPTY){
            Toast.makeText(getContext(),"当前列表没有数据",Toast.LENGTH_SHORT).show();
            return null;
        }
        if(personDetailsList.size() == 0) {
            Toast.makeText(getContext(),"当前列表没有数据或者数据错误",Toast.LENGTH_SHORT).show();
            return null;
        }
        PermissionUtils.verifyStoragePermissions(getActivity());
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String outPath = Environment.getExternalStorageDirectory() + "/IAmLittle/PRP/";
            File dir = new File(outPath);
            try{
                File tempFile = new File(getContext().getFilesDir() + "/jxgz_template.xls");
                if (!tempFile.exists()) {
                    AssetManager assetManager = getContext().getAssets();
                    InputStream inputStream = assetManager.open("jxgz_template.xls");
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
                double totalRatio = 0;
                double total = 0;
                FileOutputStream fos = new FileOutputStream(outPath +"prp"+dateString+".xls");
                Workbook workbook = Workbook.getWorkbook(tempFile);
                WritableWorkbook wwb = Workbook.createWorkbook(fos, workbook);
                WritableSheet sheet = wwb.getSheet(0);
                CellFormat titleFmt = sheet.getCell(0,0).getCellFormat();
                Label titleLb = new Label(0,0,"时间："+dateString,titleFmt);
                sheet.addCell(titleLb);
                int totalRow = 3; // 从第四行开始填充
                int detailsRow = 3; // 从第四行开始填充
                for(ItemEntity itemEntity:personDetailsList){
                    if(itemEntity.getType() == ItemType.JXGZ_PERSON_TOTAL){
                        ItemEntityJXGZPersonTotal entity = (ItemEntityJXGZPersonTotal)itemEntity;
                        String name = entity.getPersonName();
                        double totalAmount = entity.getAmount();
                        double ratio = entity.getThatRatio();
                        total = Arith.add(total,totalAmount);
                        totalRatio = Arith.add(totalRatio,ratio);
                        int childcount = entity.getChildCount();
                        CellFormat nameFmt = sheet.getCell(0,totalRow).getCellFormat();
                        sheet.mergeCells(0,totalRow,0,totalRow+childcount-1);
                        Label nameLb = new Label(0,totalRow,name,nameFmt);
                        sheet.addCell(nameLb);
                        CellFormat ratioFmt = sheet.getCell(1,totalRow).getCellFormat();
                        sheet.mergeCells(1,totalRow,1,totalRow+childcount-1);
                        Number ratioLb = new Number(1,totalRow,ratio,ratioFmt);
                        sheet.addCell(ratioLb);
                        CellFormat totalAmountFmt = sheet.getCell(2,totalRow).getCellFormat();
                        sheet.mergeCells(2,totalRow,2,totalRow+childcount-1);
                        Number totalAmountLb = new Number(2,totalRow,totalAmount,totalAmountFmt);
                        sheet.addCell(totalAmountLb);
                        totalRow +=childcount;
                        for(JXGZPersonDetails details:entity.getList()){
                            String jxgzName = details.getJXGZName();
                            double amount = details.getJXGZAmount();
                            String type = MyTool.getJXGZ_TypeString(details.getJXGZType());
                            CellFormat jxgzNameFmt = sheet.getCell(3,detailsRow).getCellFormat();
                            CellFormat amountFmt = sheet.getCell(4,detailsRow).getCellFormat();
                            CellFormat typeFmt = sheet.getCell(5,detailsRow).getCellFormat();
                            Label jxgzNameLb = new Label(3,detailsRow,jxgzName,jxgzNameFmt);
                            Number amountLb = new Number(4,detailsRow,amount,amountFmt);
                            Label typeLb = new Label(5,detailsRow,type,typeFmt);
                            sheet.addCell(jxgzNameLb);
                            sheet.addCell(amountLb);
                            sheet.addCell(typeLb);
                            detailsRow++;
                        }
                    }
                }
                CellFormat cellFormat = sheet.getCell(0,totalRow+2).getCellFormat();
                Label sumText = new Label(0,totalRow+2,"合计：",cellFormat);
                sheet.addCell(sumText);
                CellFormat sumRatioCellFormat = sheet.getCell(1,totalRow+2).getCellFormat();
                Number sumRatioCell = new Number(1,totalRow+2,totalRatio,sumRatioCellFormat);
                sheet.addCell(sumRatioCell);
                CellFormat sumAmountCellFormat = sheet.getCell(2,totalRow+2).getCellFormat();
                Number sumAmountCell = new Number(2,totalRow+2,total,sumAmountCellFormat);
                sheet.addCell(sumAmountCell);
                wwb.write();
                wwb.close();;
                fos.close();
                workbook.close();
                Toast.makeText(getContext(),"生成Excel文件成功\n文件目录："+dir.getAbsolutePath(),Toast.LENGTH_SHORT).show();
                return new File(outPath+"prp"+dateString+".xls");
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
    public static PRPDetailsFragment newInstant(int mode, String recordTime) {
        PRPDetailsFragment fragment = new PRPDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("Mode", mode);
        bundle.putString("RecordTime", recordTime);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mode = 0;
        recordTime = "";
        if (getArguments() != null) {
            mode = getArguments().getInt("Mode");
            recordTime = getArguments().getString("RecordTime");
        }
        totalDetailsList = new ArrayList<>();
        personDetailsList = new ArrayList<>();
        totalAdapter = new PRPDetailsAdapter(totalDetailsList);
        personAdapter = new PRPDetailsAdapter(personDetailsList);
        personAdapter.setItemClickListener(new IItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                boolean b = (boolean) view.getTag();
                if (b) {
                    showChild(position);
                } else {
                    hideChild(position);
                }
            }

            @Override
            public void onClick(View view, int x, int y) {

            }
        });
        activity = (PRPActivity)getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.prpdetails_fragment, container, false);
        personRecyclerView = view.findViewById(R.id.prpdetails_recyclerview2);
        personRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        personRecyclerView.setAdapter(personAdapter);
        totalRecyclerView = view.findViewById(R.id.prpdetails_recyclerview1);
        totalRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        totalRecyclerView.setAdapter(totalAdapter);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateTotalList();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.title_menu_dele_share, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.title_menu_dele) {
            dele();
        } else {
            share();
        }
        return super.onOptionsItemSelected(item);
    }
}
