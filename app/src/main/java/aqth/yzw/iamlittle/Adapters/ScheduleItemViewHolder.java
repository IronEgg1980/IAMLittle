package aqth.yzw.iamlittle.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import aqth.yzw.iamlittle.R;

public class ScheduleItemViewHolder extends RecyclerView.ViewHolder {
    private TextView[] TVs;
    public TextView[] getTVs() {
        return TVs;
    }
    public ScheduleItemViewHolder(@NonNull View itemView) {
        super(itemView);
        TVs = new TextView[9];
        TVs[0] = itemView.findViewById(R.id.schedule_item_nameTV);
        TVs[1] = itemView.findViewById(R.id.schedule_item_shiftTV1);
        TVs[2] = itemView.findViewById(R.id.schedule_item_shiftTV2);
        TVs[3] = itemView.findViewById(R.id.schedule_item_shiftTV3);
        TVs[4] = itemView.findViewById(R.id.schedule_item_shiftTV4);
        TVs[5] = itemView.findViewById(R.id.schedule_item_shiftTV5);
        TVs[6] = itemView.findViewById(R.id.schedule_item_shiftTV6);
        TVs[7] = itemView.findViewById(R.id.schedule_item_shiftTV7);
        TVs[8] = itemView.findViewById(R.id.schedule_item_noteTV);
    }
}
