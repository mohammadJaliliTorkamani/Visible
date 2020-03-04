package dev.aban.visible.adapter;

import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dev.aban.visible.R;
import dev.aban.visible.listener.BubbleClickListener;
import dev.aban.visible.model.BubbleItem;
import dev.aban.visible.utils.ContextHelper;
import dev.aban.visible.view.viewholder.FilterViewHolder;

public class FilterAdapter extends RecyclerView.Adapter<FilterViewHolder> {
    private List<BubbleItem> list;
    private BubbleClickListener clickListener;
    private int lastPosition = -1;

    public FilterAdapter(List<BubbleItem> bubbleItemList, BubbleClickListener clickListener, Dialog dialog) {
        this.list = bubbleItemList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public FilterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FilterViewHolder(LayoutInflater.from(ContextHelper.retrieveContext())
                .inflate(R.layout.item_filter, parent, false), clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull FilterViewHolder holder, int position) {
        holder.bindItems(list.get(position));
        setAnimation(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    private void setAnimation(View viewToAnimate, int position) {
//        If the bound view wasn't previously displayed on screen, it's animated
//        if ((position > lastPosition))
        if (true) {
            Animation animation = AnimationUtils.loadAnimation(ContextHelper.retrieveContext(), R.anim.normal_scale);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
}
