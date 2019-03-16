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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gamex.GamexApplication;
import com.gamex.R;
import com.gamex.activity.ExhibitionDetailActivity;
import com.gamex.models.Exhibition;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.inject.Inject;

public class EndlessRvExhibitionAdapter extends RecyclerView.Adapter<EndlessRvExhibitionAdapter.ItemViewHolder> {
    @Inject
    Picasso picasso;
    private Context context;
    public List<Exhibition> data;

    public EndlessRvExhibitionAdapter(Context context, List<Exhibition> data) {
        ((GamexApplication) context.getApplicationContext()).getAppComponent().inject(this);
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycle_view_item_exhibition, viewGroup, false);
        return new EndlessRvExhibitionAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder viewHolder, int i) {

        if (data.get(i) == null) {
            viewHolder.item.setVisibility(View.GONE);
            viewHolder.loadingCircle.setVisibility(View.VISIBLE);

        } else {

            viewHolder.item.setVisibility(View.VISIBLE);
            viewHolder.loadingCircle.setVisibility(View.GONE);

            picasso.load(data.get(i).getLogo())
                    .placeholder((R.color.bg_grey))
                    .error(R.color.bg_grey)
                    .into(viewHolder.imgBanner);
            viewHolder.txtName.setText(data.get(i).getName());
            viewHolder.txtDate.setText(data.get(i).getStartDate());
            viewHolder.txtAddr.setText(data.get(i).getAddress());

            viewHolder.item.setOnClickListener(v -> {
                Intent intent = new Intent(context, ExhibitionDetailActivity.class);
                Bundle options = ActivityOptionsCompat.makeScaleUpAnimation(
                        viewHolder.item, 0, 0,
                        viewHolder.item.getWidth(),
                        viewHolder.item.getHeight())
                        .toBundle();
                intent.putExtra("EXTRA_EX_NAME", data.get(i).getName());
                intent.putExtra("EXTRA_EX_ID", data.get(i).getExhibitionId());
                intent.putExtra("EXTRA_EX_IMG", data.get(i).getLogo());
                ActivityCompat.startActivity(context, intent, options);
            });
        }
    }

    public void removeAt(int position) {
        data.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, data.size());
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }


    class ItemViewHolder extends RecyclerView.ViewHolder {
        LinearLayout loadingCircle;
        ImageView imgBanner;
        TextView txtName;
        TextView txtDate;
        TextView txtAddr;
        CardView item;

        ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBanner = itemView.findViewById(R.id.item_exhibition_img);
            txtName = itemView.findViewById(R.id.item_exhibition_name);
            txtDate = itemView.findViewById(R.id.item_exhibition_date);
            txtAddr = itemView.findViewById(R.id.item_exhibition_address);
            item = itemView.findViewById(R.id.item_exhibition_card);
            loadingCircle = itemView.findViewById(R.id.item_exhibition_loading_circle);
        }
    }
}