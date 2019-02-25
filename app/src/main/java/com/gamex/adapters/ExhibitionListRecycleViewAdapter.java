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

import com.gamex.EventDetailActivity;
import com.gamex.R;

import java.util.ArrayList;

public class ExhibitionListRecycleViewAdapter extends RecyclerView.Adapter<ExhibitionListRecycleViewAdapter.ExhibitionViewHolder> {

    private Context context;
    private ArrayList<String> exImg = new ArrayList<>();
    private ArrayList<String> exName = new ArrayList<>();
    private ArrayList<String> exDate = new ArrayList<>();

    public ExhibitionListRecycleViewAdapter(Context context, ArrayList<String> exImg, ArrayList<String> exName, ArrayList<String> exDate) {
        this.context = context;
        this.exImg = exImg;
        this.exName = exName;
        this.exDate = exDate;
    }

    @NonNull
    @Override
    public ExhibitionViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycle_view_home_list_exhibition, viewGroup, false);
        return new ExhibitionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ExhibitionViewHolder exhibitionViewHolder, int i) {
        // use when have img url online
//        Glide.with(context)
//                .asBitmap()
//                .load(exImg.get(i))
//                .into(exhibitionViewHolder.imgBanner);
//        exhibitionViewHolder.imgBanner.setImageResource(R.drawable.color_gradient_light);
        exhibitionViewHolder.imgBanner.setImageResource(R.color.bg_grey


        );

        exhibitionViewHolder.txtName.setText(exName.get(i));
        exhibitionViewHolder.txtDate.setText(exDate.get(i));
//        exhibitionViewHolder.txtAddress.setText(exAddr.get(i));

        exhibitionViewHolder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EventDetailActivity.class);
                Bundle options = ActivityOptionsCompat.makeScaleUpAnimation(
                        exhibitionViewHolder.item, 0, 0,
                        exhibitionViewHolder.item.getWidth(),
                        exhibitionViewHolder.item.getHeight())
                        .toBundle();
                ActivityCompat.startActivity(context, intent, options);
            }
        });
    }

    @Override
    public int getItemCount() {
        return exName.size();
    }

    class ExhibitionViewHolder extends RecyclerView.ViewHolder {
        ImageView imgBanner;
        TextView txtName;
        TextView txtDate;
        CardView item;

        ExhibitionViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBanner = itemView.findViewById(R.id.fg_home_rv_img);
            txtName = itemView.findViewById(R.id.fg_home_rv_txt_name);
            txtDate = itemView.findViewById(R.id.fg_home_rv_txt_date);
            item = itemView.findViewById(R.id.fg_home_rv_card);
        }
    }
}
