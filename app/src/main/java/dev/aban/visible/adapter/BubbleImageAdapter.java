package dev.aban.visible.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import dev.aban.visible.R;
import dev.aban.visible.utils.ContextHelper;

public class BubbleImageAdapter extends RecyclerView.Adapter {
    private List<String> list;

    public BubbleImageAdapter(List<String> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(ContextHelper.retrieveContext()).inflate(R.layout.item_buble_image, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ImageView imageView = holder.itemView.findViewById(R.id.item_bubble_image_img);
        Picasso.get().load(list.get(position)).into(imageView);
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
        }
    }
}
