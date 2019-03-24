package aqth.yzw.iamlittle;

import android.app.Activity;
import android.app.usage.UsageEvents;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;

import static android.view.KeyEvent.KEYCODE_BACK;

public class ShowDataCommonActivity extends AppCompatActivity {
    private final int OTP_MODE = 1;
    private Toolbar toolbar;

    public void setShowDetails(boolean showDetails) {
        this.showDetails = showDetails;
    }

    private boolean showDetails;
    private int mode;
    private FragmentManager fragmentManager;

    public Toolbar getToolbar() {
        return toolbar;
    }

    public boolean isShowDetails() {
        return showDetails;
    }

    public Context getContext() {
        return context;
    }
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = ShowDataCommonActivity.this;
        fragmentManager = getSupportFragmentManager();
        showDetails = false;
        Intent intent = getIntent();
        if(intent != null){
            mode = intent.getIntExtra("Mode",1);
        }
        setContentView(R.layout.activity_show_data_common);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(showDetails){
                    if(mode == 1)
                        setTitle("统计加班");
                    else
                        setTitle("绩效工资");
                    showDetails = false;
                    OTPFragment fragment1 = (OTPFragment) getSupportFragmentManager().findFragmentByTag("Total");
                    fragment1.notifyDataChange();
                    fragmentManager.popBackStackImmediate();
                }else
                    finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(fragmentManager != null && fragmentManager.getBackStackEntryCount() > 0){
            fragmentManager.popBackStackImmediate(0,1);
        }
        if(mode == OTP_MODE){
            fragmentManager.beginTransaction()
                    .add(R.id.common_linerarlayout,new OTPFragment(),"Total")
                    .addToBackStack(null)
                    .commit();
        }else{

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KEYCODE_BACK){
            if(isShowDetails()){
                if(mode == 1)
                    setTitle("统计加班");
                else
                    setTitle("绩效工资");
                showDetails = false;
                OTPFragment fragment1 = (OTPFragment) getSupportFragmentManager().findFragmentByTag("Total");
                fragment1.notifyDataChange();
                fragmentManager.popBackStackImmediate();
            }else{
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
