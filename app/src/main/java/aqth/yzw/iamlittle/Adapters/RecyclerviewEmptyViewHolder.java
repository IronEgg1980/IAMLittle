package aqth.yzw.iamlittle.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import aqth.yzw.iamlittle.R;

public class RecyclerviewEmptyViewHolder extends RecyclerView.ViewHolder {
    private TextView textView;

    public TextView getTextView() {
        return textView;
    }

    public RecyclerviewEmptyViewHolder(@NonNull View itemView) {
        super(itemView);
        textView = itemView.findViewById(R.id.recylerview_emptyitem);
    }
}
