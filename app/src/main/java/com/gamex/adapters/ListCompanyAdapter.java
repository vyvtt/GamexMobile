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

import com.gamex.R;
import com.gamex.activity.ExhibitionDetailActivity;
import com.gamex.models.Company;
import com.gamex.models.CompanyInExhibition;
import com.gamex.models.Exhibition;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ListCompanyAdapter extends RecyclerView.Adapter<ListCompanyAdapter.ViewHolder> {
    private Context context;
    private List<CompanyInExhibition> listCompany = new ArrayList<>();

    public ListCompanyAdapter(Context context, List<CompanyInExhibition> listCompany) {
        this.context = context;
        this.listCompany = listCompany;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycle_view_list_company, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Picasso.Builder builder = new Picasso.Builder(context);
        builder.downloader(new OkHttp3Downloader(context));
        builder.build().load(listCompany.get(i).getLogo())
                .placeholder((R.drawable.ic_launcher_background))
                .error(R.color.bg_grey)
                .into(viewHolder.imgLogo);

        viewHolder.txtName.setText(listCompany.get(i).getName());

        viewHolder.row.setOnClickListener(v -> {
            Intent intent = new Intent(context, ExhibitionDetailActivity.class);
            Bundle options = ActivityOptionsCompat.makeScaleUpAnimation(
                    viewHolder.row, 0, 0,
                    viewHolder.row.getWidth(),
                    viewHolder.row.getHeight())
                    .toBundle();
            ActivityCompat.startActivity(context, intent, options);
        });
    }

    @Override
    public int getItemCount() {
        if (listCompany != null) {
            return listCompany.size();
        }
        return 0;
    }

//    INNER CLASS VIEW HOLDER ---------------------------------------------------------------->
    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgLogo;
        TextView txtName;
        CardView row;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgLogo = itemView.findViewById(R.id.fg_list_company_rv_logo);
            txtName = itemView.findViewById(R.id.fg_list_company_rv_name);
            row = itemView.findViewById(R.id.fg_yourEx_rv_card);
        }
    }
}
