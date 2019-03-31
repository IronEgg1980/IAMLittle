package aqth.yzw.iamlittle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class PRPActivity extends AppCompatActivity {
    public boolean isShowDetails() {
        return isShowDetails;
    }

    public void setShowDetails(boolean showDetails) {
        isShowDetails = showDetails;
    }

    private boolean isShowDetails;
    private int mode;
    private FragmentManager fragmentManager;
    private void back(){
        //finish();
        if(mode == 1) {
            if (isShowDetails) {
                setTitle("绩效工资列表");
                isShowDetails = false;
                PRPListFragment fragment1 = (PRPListFragment) getSupportFragmentManager().findFragmentByTag("List");
                fragment1.notifyDataChange();
                fragmentManager.popBackStackImmediate(0, 0);
            } else
                finish();
        }else{
            finish();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_data_common);
        fragmentManager = getSupportFragmentManager();
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });
        isShowDetails = false;
        Intent intent = getIntent();
        if(intent != null){
            mode = intent.getIntExtra("Mode",1);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(mode == 1){
            if(fragmentManager != null && fragmentManager.getBackStackEntryCount() > 0){
                fragmentManager.popBackStackImmediate(0,1);
            }
            fragmentManager.beginTransaction()
                    .add(R.id.common_linerarlayout,new PRPListFragment(),"List")
                    .addToBackStack(null)
                    .commit();
            setTitle("绩效工资列表");
        }
//        else{
//            PRPDetailsFragment fragment = new PRPDetailsFragment();
//            fragmentManager.beginTransaction()
//                    .replace(R.id.common_linerarlayout,fragment).commit();
//            setTitle("开始计算");
//        }
    }
    @Override
    public void onBackPressed() {
        back();
    }
}
