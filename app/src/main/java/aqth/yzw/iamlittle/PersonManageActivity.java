package aqth.yzw.iamlittle;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

public class PersonManageActivity extends AppCompatActivity {
    public interface IOnBackPress{
        public void onBackPress();
    }
    private IOnBackPress onBackPress;

    public void setOnBackPress(IOnBackPress onBackPress) {
        this.onBackPress = onBackPress;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_manage);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setTitle("人员管理");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPress.onBackPress();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportFragmentManager().beginTransaction().replace(R.id.recyclerview_layout,new PersonManageFragment()).commit();
    }

    @Override
    public void onBackPressed() {
        onBackPress.onBackPress();
    }
}
