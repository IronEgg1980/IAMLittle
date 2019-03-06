package aqth.yzw.iamlittle.Adapters;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import aqth.yzw.iamlittle.R;

public class RecyclerViewAddButtonViewHolder extends RecyclerView.ViewHolder {
    public Button getButton() {
        return button;
    }

    private Button button;
    public RecyclerViewAddButtonViewHolder(View itemView) {
        super(itemView);
        button = itemView.findViewById(R.id.recyclerview_addbutton);
    }
}
