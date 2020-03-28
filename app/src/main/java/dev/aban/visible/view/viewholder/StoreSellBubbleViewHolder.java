package dev.aban.visible.view.viewholder;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import dev.aban.visible.R;
import dev.aban.visible.listener.BubbleClickListener;
import dev.aban.visible.model.BubbleItem;
import dev.aban.visible.utils.custom_view.TextViewPlus;

public class StoreSellBubbleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private BubbleItem bubbleItem;
    private BubbleClickListener listener;

    private TextViewPlus title;
    private ImageView image;

    public StoreSellBubbleViewHolder(@NonNull View view, BubbleClickListener clickListener) {
        super(view);
        findViews(view);
        this.listener = clickListener;
        view.setOnClickListener(this);
    }

    public void bind(BubbleItem item) {
        this.bubbleItem = item;
        bindValues();
    }

    private void bindValues() {
        title.setText(bubbleItem.getTitle() + (bubbleItem.getPrice() == 0 ? " ( Free ! ) " : ""));
        Picasso.get().load(bubbleItem.getImageURL()).into(image);
    }

    private void findViews(View view) {
        title = view.findViewById(R.id.item_store_sell_bubble_title);
        image = view.findViewById(R.id.item_store_sell_bubble_image);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_store_sell_bubble_viw:
                if (listener != null)
                    listener.OnBubbleItemClicked(bubbleItem);
                break;
        }

    }
}
