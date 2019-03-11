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

import com.gamex.GamexApplication;
import com.gamex.R;
import com.gamex.activity.ExhibitionDetailActivity;
import com.gamex.models.Exhibition;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.inject.Inject;

public class EndlessRecycleViewAdapter extends RecyclerView.Adapter<EndlessRecycleViewAdapter.ItemViewHolder> {
    @Inject
    Picasso picasso;
    private Context context;
    public List<Exhibition> dataExhibition;

    public EndlessRecycleViewAdapter(Context context, List<Exhibition> dataExhibition) {
        ((GamexApplication) context.getApplicationContext()).getAppComponent().inject(this);
        this.context = context;
        this.dataExhibition = dataExhibition;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycle_view_yourexhibition_list, viewGroup, false);
        return new EndlessRecycleViewAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder viewHolder, int i) {

        picasso.load(dataExhibition.get(i).getLogo())
                .placeholder((R.color.bg_grey))
                .error(R.color.bg_grey)
                .into(viewHolder.imgBanner);
        String date = dataExhibition.get(i).getStartDate() + " - " + dataExhibition.get(i).getEndDate();
        viewHolder.txtName.setText(dataExhibition.get(i).getName());
        viewHolder.txtDate.setText(date);
        viewHolder.txtAddr.setText(dataExhibition.get(i).getAddress());

        viewHolder.item.setOnClickListener(v -> {
            Intent intent = new Intent(context, ExhibitionDetailActivity.class);
            Bundle options = ActivityOptionsCompat.makeScaleUpAnimation(
                    viewHolder.item, 0, 0,
                    viewHolder.item.getWidth(),
                    viewHolder.item.getHeight())
                    .toBundle();
            intent.putExtra("EXTRA_EX_NAME", dataExhibition.get(i).getName());
            intent.putExtra("EXTRA_EX_ID", dataExhibition.get(i).getExhibitionId());
            intent.putExtra("EXTRA_EX_IMG", dataExhibition.get(i).getLogo());
            ActivityCompat.startActivity(context, intent, options);
        });
    }

    @Override
    public int getItemCount() {
        return dataExhibition == null ? 0 : dataExhibition.size();
    }


    class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView imgBanner;
        TextView txtName;
        TextView txtDate;
        TextView txtAddr;
        CardView item;

        ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBanner = itemView.findViewById(R.id.rv_item_img);
            txtName = itemView.findViewById(R.id.rv_item_name);
            txtDate = itemView.findViewById(R.id.rv_item_date);
            txtAddr = itemView.findViewById(R.id.rv_item_address);
            item = itemView.findViewById(R.id.rv_item_card);
        }
    }
}