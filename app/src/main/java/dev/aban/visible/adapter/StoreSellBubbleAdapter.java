package dev.aban.visible.adapter;

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
import dev.aban.visible.view.viewholder.StoreSellBubbleViewHolder;

public class StoreSellBubbleAdapter extends RecyclerView.Adapter<StoreSellBubbleViewHolder> {
    private List<BubbleItem> list;
    private BubbleClickListener clickListener;
    private int lastPosition = -1;

    public StoreSellBubbleAdapter(List<BubbleItem> list, BubbleClickListener clickListener) {
        this.list = list;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public StoreSellBubbleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StoreSellBubbleViewHolder(LayoutInflater.from(ContextHelper.retrieveContext())
                .inflate(R.layout.item_store_sell_bubble, parent, false), clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreSellBubbleViewHolder holder, int position) {
        holder.bind(list.get(position));
        setAnimation(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    private void setAnimation(View viewToAnimate, int position) {
//         If the bound view wasn't previously displayed on screen, it's animated
//        if ((position > lastPosition))
        if (true) {
            Animation animation = AnimationUtils.loadAnimation(ContextHelper.retrieveContext(), R.anim.normal_scale);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
}
