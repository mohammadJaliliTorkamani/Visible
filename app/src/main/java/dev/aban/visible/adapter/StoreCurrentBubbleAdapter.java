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
import dev.aban.visible.view.viewholder.StoreCurrentBubbleViewHolder;

public class StoreCurrentBubbleAdapter extends RecyclerView.Adapter<StoreCurrentBubbleViewHolder> {
    private List<BubbleItem> list;
    private BubbleClickListener clickListener;
    private int lastPosition = -1;

    public StoreCurrentBubbleAdapter(List<BubbleItem> list, BubbleClickListener listener) {
        this.list = list;
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public StoreCurrentBubbleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StoreCurrentBubbleViewHolder(clickListener, LayoutInflater.from(ContextHelper.retrieveContext())
                .inflate(R.layout.item_store_current_bubble, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull StoreCurrentBubbleViewHolder holder, int position) {
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
