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

import com.gamex.activity.EventDetailActivity;
import com.gamex.R;
import com.gamex.models.Exhibition;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.util.List;

public class HomeRVAdapter extends RecyclerView.Adapter<HomeRVAdapter.ViewHolder> {

    private Context context;
    private List<Exhibition> dataList;

    public HomeRVAdapter(Context context, List<Exhibition> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycle_view_home_list_exhibition, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        // use when have img url online
//        Glide.with(context)
//                .asBitmap()
//                .load(exImg.get(i))
//                .into(viewHolder.imgBanner);
//        viewHolder.imgBanner.setImageResource(R.drawable.color_gradient_light);

        Picasso.Builder builder = new Picasso.Builder(context);
        builder.downloader(new OkHttp3Downloader(context));
        builder.build().load(dataList.get(i).getLogo())
                .placeholder((R.drawable.ic_launcher_background))
                .error(R.color.bg_grey)
                .into(viewHolder.imgBanner);

        viewHolder.imgBanner.setImageResource(R.color.bg_grey);
        viewHolder.txtName.setText(dataList.get(i).getName());
        String exDate = dataList.get(i).getStartDate() + dataList.get(i).getEndDate();
        viewHolder.txtDate.setText(exDate);

//        viewHolder.txtName.setText(exName.get(i));
//        viewHolder.txtDate.setText(exDate.get(i));
//        viewHolder.txtAddress.setText(exAddr.get(i));

        viewHolder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EventDetailActivity.class);
                Bundle options = ActivityOptionsCompat.makeScaleUpAnimation(
                        viewHolder.item, 0, 0,
                        viewHolder.item.getWidth(),
                        viewHolder.item.getHeight())
                        .toBundle();
                ActivityCompat.startActivity(context, intent, options);
            }
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
