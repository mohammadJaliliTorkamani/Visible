package dev.aban.visible.view.viewholder;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import dev.aban.visible.R;
import dev.aban.visible.listener.BubbleClickListener;
import dev.aban.visible.model.BubbleItem;
import dev.aban.visible.utils.custom_view.TextViewPlus;

public class StoreCurrentBubbleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private CircleImageView imageView;
    private TextViewPlus textViewPlus;
    private BubbleItem item;
    private BubbleClickListener listener;

    public StoreCurrentBubbleViewHolder(BubbleClickListener clickListener, @NonNull View view) {
        super(view);
        this.listener = clickListener;
        view.setOnClickListener(this);
        findViews(view);
    }

    public void bind(BubbleItem item) {
        this.item = item;
        bindValues();
    }

    private void bindValues() {
        textViewPlus.setText(item.getTitle());
        Picasso.get().load(item.getImageURL()).into(imageView);
    }

    private void findViews(View view) {
        imageView = view.findViewById(R.id.item_store_current_bubble_image);
        textViewPlus = view.findViewById(R.id.item_store_current_bubble_title);
    }

    @Override
    public void onClick(View v) {
        if (listener != null)
            listener.OnBubbleItemClicked(item);
    }
}
