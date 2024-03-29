package com.gamex.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {
    @Inject Picasso picasso;
    private Context context;
    private List<Exhibition> dataList;

    public HomeAdapter(Context context, List<Exhibition> dataList) {
        this.context = context;
        this.dataList = dataList;
        ((GamexApplication) context.getApplicationContext()).getAppComponent().inject(this);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycle_view_home_list_exhibition, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        picasso.load(dataList.get(i).getLogo())
                .placeholder((R.color.bg_grey))
                .error(R.color.bg_grey)
                .into(viewHolder.imgBanner);

        viewHolder.txtName.setText(dataList.get(i).getName());
        String exDate = dataList.get(i).getStartDate() + dataList.get(i).getEndDate();
        viewHolder.txtDate.setText(exDate);

        viewHolder.item.setOnClickListener(v -> {

            Intent intent = new Intent(context, ExhibitionDetailActivity.class);
            Bundle options = ActivityOptionsCompat.makeScaleUpAnimation(
                    viewHolder.item, 0, 0,
                    viewHolder.item.getWidth(),
                    viewHolder.item.getHeight())
                    .toBundle();

            intent.putExtra("EXTRA_EX_NAME", dataList.get(i).getName());
            intent.putExtra("EXTRA_EX_ID", dataList.get(i).getExhibitionId());
            intent.putExtra("EXTRA_EX_IMG", dataList.get(i).getLogo());

            ActivityCompat.startActivity(context, intent, options);
        });
    }

    @Override
    public int getItemCount() {
        if (dataList != null) {
            return dataList.size();
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgBanner;
        TextView txtName;
        TextView txtDate;
        CardView item;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBanner = itemView.findViewById(R.id.fg_home_rv_img);
            txtName = itemView.findViewById(R.id.fg_home_rv_txt_name);
            txtDate = itemView.findViewById(R.id.fg_home_rv_txt_date);
            item = itemView.findViewById(R.id.fg_home_rv_card);
        }
    }
}
