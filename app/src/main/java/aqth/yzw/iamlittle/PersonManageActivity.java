package aqth.yzw.iamlittle;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.FrameLayout;

public class PersonManageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_manage);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setTitle("人员管理");
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportFragmentManager().beginTransaction().replace(R.id.recyclerview_layout,new PersonManageFragment()).commit();
    }
}
