package dev.aban.visible.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;
import java.util.List;

import dev.aban.visible.R;
import dev.aban.visible.listener.DrawerClickListener;
import dev.aban.visible.model.DrawerItem;
import dev.aban.visible.utils.ContextHelper;
import dev.aban.visible.utils.Helper;
import dev.aban.visible.view.viewholder.DrawerViewHolder;

public class DrawerAdapter extends RecyclerView.Adapter<DrawerViewHolder> {
    private DrawerClickListener listener;
    private List<DrawerItem> list = new LinkedList<>();

    public DrawerAdapter(DrawerClickListener listener) {
        this.listener = listener;
        list.addAll(Helper.getDrawerItems());
    }

    @NonNull
    @Override
    public DrawerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DrawerViewHolder(LayoutInflater.from(ContextHelper.retrieveContext())
                .inflate(R.layout.item_navigation_drawer, parent, false), listener);
    }

    @Override
    public void onBindViewHolder(@NonNull DrawerViewHolder holder, int position) {
        holder.bindItem(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }
}