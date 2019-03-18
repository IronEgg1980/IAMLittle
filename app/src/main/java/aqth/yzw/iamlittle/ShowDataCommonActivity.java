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
    private final int JXGZ_MODE = 2;
    private Toolbar toolbar;
    private LinearLayout linearLayout;
    private boolean showDetails;
    private String title;
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
        Intent intent = getIntent();
        if(intent != null){
            title = intent.getStringExtra("Title");
            mode = intent.getIntExtra("Mode",1);
        }
        setContentView(R.layout.activity_show_data_common);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = ShowDataCommonActivity.this;
        fragmentManager = getSupportFragmentManager();
        linearLayout = findViewById(R.id.common_linerarlayout);
        showDetails = false;
        toolbar.setTitle(title);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(showDetails){
                    showDetails = false;
                    fragmentManager.popBackStackImmediate();
                }else
                    finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mode == OTP_MODE){

        }else{

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KEYCODE_BACK){
            // 写逻辑
            if(isShowDetails()){
                showDetails = false;
                fragmentManager.popBackStackImmediate();
            }else{
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
