package aqth.yzw.iamlittle.Adapters;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import aqth.yzw.iamlittle.R;

public class ScheduleItemTitleDecoration extends RecyclerView.ItemDecoration {
    private View mview;
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(c,parent,state);
//        if(mview == null){
//            getTitleView(parent);
//        }
//        mview.draw(c);
    }
    void getTitleView(final RecyclerView parent){
        mview = LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_recyclerview_item,parent,false);
        int childWidth = ViewGroup.getChildMeasureSpec(parent.getMeasuredWidth(),parent.getPaddingLeft()+parent.getPaddingRight(),mview.getLayoutParams().width);
        int childHeight = ViewGroup.getChildMeasureSpec(parent.getMeasuredHeight(),parent.getPaddingTop()+parent.getPaddingBottom(),mview.getLayoutParams().height);
        mview.measure(childWidth,childHeight);
        mview.layout(0,0,mview.getMeasuredWidth(),mview.getMeasuredHeight());
    }
}
