package com.gamex.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gamex.R;
import com.gamex.models.Reward;

import java.util.ArrayList;
import java.util.List;

public class RewardExchangeAdapter extends RecyclerView.Adapter<RewardExchangeAdapter.ViewHolder> {
    private List<Reward> data = new ArrayList<>();

    EventListener listener;

    public interface EventListener {
        void onClickToExchange(int rewardId, String rewardDes, int rewardPoint);
    }

    public RewardExchangeAdapter(List<Reward> data, EventListener listener) {
        this.data = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycle_view_item_reward, viewGroup, false);
        return new RewardExchangeAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        String start = data.get(i).getStartDate();
        String end = data.get(i).getEndDate();
        String time = "No limit time";

        if (start != null && end != null) {
            time = "Available: " + data.get(i).getStartDate() + " - " + data.get(i).getEndDate();
        }

        viewHolder.txtTime.setText(time);
        viewHolder.txtDes.setText(data.get(i).getDescription());
        viewHolder.txtPoint.setText(Html.fromHtml("Point cost: <b>" + data.get(i).getPointCost() + "</b>" ));

        viewHolder.btnExchange.setOnClickListener(v -> listener.onClickToExchange(
                data.get(i).getRewardId(),
                data.get(i).getDescription(),
                data.get(i).getPointCost()));
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    //    INNER CLASS VIEW HOLDER ---------------------------------------------------------------->
    class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout btnExchange;
        TextView txtDes, txtTime, txtPoint;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDes = itemView.findViewById(R.id.item_reward_description);
            txtTime = itemView.findViewById(R.id.item_reward_time);
            txtPoint = itemView.findViewById(R.id.item_reward_point_cost);
            btnExchange = itemView.findViewById(R.id.item_reward_click_exchange);
        }
    }
}
