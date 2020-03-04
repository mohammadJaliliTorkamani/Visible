package dev.aban.visible.adapter;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import dev.aban.visible.R;
import dev.aban.visible.model.MoreApp;
import dev.aban.visible.utils.ContextHelper;
import dev.aban.visible.utils.Helper;

public class MoreAppsAdapter extends RecyclerView.Adapter {
    private List<MoreApp> list;

    public MoreAppsAdapter(List<MoreApp> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return new ViewHolder(LayoutInflater.from(ContextHelper.retrieveContext()).inflate(R.layout.item_more_app, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        TextView title = viewHolder.itemView.findViewById(R.id.item_more_app_title);
        CircleImageView image = viewHolder.itemView.findViewById(R.id.item_more_app_image);
        TextView download = viewHolder.itemView.findViewById(R.id.item_more_app_download);
        title.setText(list.get(position).getTitle());
        Picasso.get().load(list.get(position).getImageURL()).into(image);
        download.setOnClickListener(v -> {
            Helper.recordEventClick("MainActivity", "more app download button of " + list.get(position).getTitle());
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(list.get(position).getLink()));
            browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ContextHelper.retrieveContext().startActivity(browserIntent);
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
