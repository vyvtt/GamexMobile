package com.gamex.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gamex.R;
import com.gamex.models.History;

import java.util.List;

public class EndlessRvHistoryAdapter extends RecyclerView.Adapter<EndlessRvHistoryAdapter.ItemViewHolder> {
    public List<History> data;

    public EndlessRvHistoryAdapter(List<History> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycle_view_item_history, viewGroup, false);
        return new EndlessRvHistoryAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder viewHolder, int i) {
        if (data.get(i) == null) {

            viewHolder.loadingCircle.setVisibility(View.VISIBLE);
            viewHolder.item.setVisibility(View.GONE);

        } else {

            viewHolder.item.setVisibility(View.VISIBLE);
            viewHolder.loadingCircle.setVisibility(View.GONE);

            viewHolder.txtContent.setText(data.get(i).getActivity());
            viewHolder.txtTime.setText(data.get(i).getTime());
        }
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public void removeAt(int position) {
        data.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, data.size());
    }

    ///////////////////////////////////////
    class ItemViewHolder extends RecyclerView.ViewHolder {
        LinearLayout loadingCircle, item;
        TextView txtContent, txtTime;

        ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            txtContent = itemView.findViewById(R.id.item_history_content);
            txtTime = itemView.findViewById(R.id.item_history_time);
            loadingCircle = itemView.findViewById(R.id.item_history_loading_circle);
            item = itemView.findViewById(R.id.item_history_item);
        }
    }
}
