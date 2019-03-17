package com.gamex.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gamex.R;
import com.gamex.models.RewardHistory;

import java.util.List;

public class RewardHistoryAdapter extends RecyclerView.Adapter<RewardHistoryAdapter.ViewHolder> {
    private List<RewardHistory> data;

    public RewardHistoryAdapter(List<RewardHistory> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycle_view_item_reward_history, viewGroup, false);
        return new RewardHistoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.txtDes.setText(data.get(i).getDescription());
        viewHolder.txtTime.setText("Exchanged Date: " + data.get(i).getExchangedDate());
        viewHolder.txtContent.setText(Html.fromHtml("Content: <b>" + data.get(i).getContent() + "</b>"));
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    //    INNER CLASS VIEW HOLDER ---------------------------------------------------------------->
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtDes, txtTime, txtContent;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDes = itemView.findViewById(R.id.item_reward_history_description);
            txtTime = itemView.findViewById(R.id.item_reward_history_time);
            txtContent = itemView.findViewById(R.id.item_reward_history_content);
        }
    }
}
