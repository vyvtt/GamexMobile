package com.gamex.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gamex.R;
import com.gamex.models.LeaderBoard;
import com.gamex.models.Rank;

import java.util.List;

public class LeaderBoardAdapter extends RecyclerView.Adapter<LeaderBoardAdapter.ViewHolder> {
    private List<LeaderBoard> data;
    private Context context;
    private int userRank;

    public LeaderBoardAdapter(Context context, List<LeaderBoard> data, int userRank) {
        this.context = context;
        this.data = data;
        this.userRank = userRank;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycle_view_item_rank, viewGroup, false);
        return new LeaderBoardAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        LeaderBoard currentRank = data.get(i);
        int position = i + 1;

        viewHolder.txtName.setText(currentRank.getFullname());
        viewHolder.txtPoint.setText(currentRank.getTotalPointEarned() + " points");
        viewHolder.txtRank.setText(position + "");

        if (position == 1) {
            viewHolder.imgIcon.setImageResource(R.drawable.ic_medal_1);
        } else if (position == 2) {
            viewHolder.imgIcon.setImageResource(R.drawable.ic_medal_2);
        } else if (position == 3) {
            viewHolder.imgIcon.setImageResource(R.drawable.ic_medal_3);
        } else {
            viewHolder.imgIcon.setImageResource(R.drawable.ic_medal);
            viewHolder.itemLayout.setBackgroundResource(R.drawable.bg_corner_radius);
            viewHolder.txtRank.setTextColor(context.getResources().getColor(R.color.txt_black));
            viewHolder.txtName.setTextColor(context.getResources().getColor(R.color.txt_black));
            viewHolder.txtPoint.setTextColor(context.getResources().getColor(R.color.txt_black));
        }

        if (userRank == position) {
            viewHolder.txtItsYou.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    //    INNER CLASS VIEW HOLDER ---------------------------------------------------------------->
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtRank, txtPoint, txtItsYou;
        ImageView imgIcon;
        RelativeLayout itemLayout;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.item_rank_name);
            txtRank = itemView.findViewById(R.id.item_rank_rank);
            txtPoint = itemView.findViewById(R.id.item_rank_point);
            txtItsYou = itemView.findViewById(R.id.item_rank_your_rank);
            imgIcon = itemView.findViewById(R.id.item_rank_icon);

            itemLayout = itemView.findViewById(R.id.item_rank_layout);
        }
    }
}
