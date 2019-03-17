package com.gamex.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gamex.R;
import com.gamex.models.Bookmark;

import java.util.List;

public class BookmarkExhibitionAdapter extends RecyclerView.Adapter<BookmarkExhibitionAdapter.ViewHolder> {
    public List<Bookmark> data;
    EventListener listener;

    public interface EventListener {
        void onClickToRemove(String id, String name, int position);
        void onClickToDetail(String id);
    }

    public BookmarkExhibitionAdapter(List<Bookmark> data, EventListener listener) {
        this.data = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycle_view_item_bookmark, viewGroup, false);
        return new BookmarkExhibitionAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.txtName.setText(data.get(i).getTargetName());
        viewHolder.txtTime.setText("Bookmark Date: " + data.get(i).getBookmarkDate());

        viewHolder.btnRemove.setOnClickListener(v -> listener.onClickToRemove(data.get(i).getTargetId(), data.get(i).getTargetName(), i));
        viewHolder.btnDetail.setOnClickListener(v -> listener.onClickToDetail(data.get(i).getTargetId()));
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public void remove(int position) {
        data.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, data.size());
    }

    //    INNER CLASS VIEW HOLDER ---------------------------------------------------------------->
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtTime;
        RelativeLayout btnRemove, btnDetail;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.item_bookmark_name);
            txtTime = itemView.findViewById(R.id.item_bookmark_time);
            btnRemove = itemView.findViewById(R.id.item_bookmark_click_remove);
            btnDetail = itemView.findViewById(R.id.item_bookmark_click_detail);
        }
    }
}
