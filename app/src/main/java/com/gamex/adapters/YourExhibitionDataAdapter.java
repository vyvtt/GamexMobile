package com.gamex.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gamex.activity.ExhibitionDetailActivity;
import com.gamex.R;
import com.gamex.models.Exhibition;

import java.util.ArrayList;
import java.util.List;

public class YourExhibitionDataAdapter extends RecyclerView.Adapter<YourExhibitionDataAdapter.ViewHolder> {
    private Context context;
    private List<Exhibition> data;
    YourExhibitionDataAdapter.EventListener listener;

    public interface EventListener {
        void onClickToDetail(String id);
    }

    public YourExhibitionDataAdapter(Context context, List<Exhibition> data, EventListener listener) {
        this.context = context;
        this.data = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycle_view_item_exhibition, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        viewHolder.imgBanner.setImageResource(R.color.bg_grey);

//        viewHolder.txtName.setText(exName.get(i));
//        viewHolder.txtDate.setText(exDate.get(i));
//        viewHolder.txtAddr.setText(exAddr.get(i));

        viewHolder.item.setOnClickListener(v -> {
            Intent intent = new Intent(context, ExhibitionDetailActivity.class);
            Bundle options = ActivityOptionsCompat.makeScaleUpAnimation(
                    viewHolder.item, 0, 0,
                    viewHolder.item.getWidth(),
                    viewHolder.item.getHeight())
                    .toBundle();
            ActivityCompat.startActivity(context, intent, options);
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgBanner;
        TextView txtName;
        TextView txtDate;
        TextView txtAddr;
        CardView item;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBanner = itemView.findViewById(R.id.item_exhibition_img);
            txtName = itemView.findViewById(R.id.item_exhibition_name);
            txtDate = itemView.findViewById(R.id.item_exhibition_date);
            txtAddr = itemView.findViewById(R.id.item_exhibition_address);
            item = itemView.findViewById(R.id.item_exhibition_card);
        }
    }
}
