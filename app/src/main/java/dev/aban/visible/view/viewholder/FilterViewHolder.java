package dev.aban.visible.view.viewholder;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import dev.aban.visible.R;
import dev.aban.visible.listener.BubbleClickListener;
import dev.aban.visible.model.BubbleItem;
import dev.aban.visible.utils.ContextHelper;
import dev.aban.visible.utils.custom_view.TextViewPlus;

public class FilterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private TextViewPlus textViewPlus;
    private ImageView image;
    private BubbleItem item;
    private BubbleClickListener clickListener;

    public FilterViewHolder(@NonNull View itemView, BubbleClickListener clickListener) {
        super(itemView);
        this.clickListener = clickListener;
        itemView.setOnClickListener(this);
        itemView.setOnClickListener(this);
        textViewPlus = itemView.findViewById(R.id.item_filter_txt);
        image = itemView.findViewById(R.id.item_filter_image);
    }

    public void bindItems(BubbleItem item) {
        this.item = item;
        Picasso.get().load(item.getImageURL()).into(image);
        textViewPlus.setText(item.getTitle());
        textViewPlus.setTextColor(ContextHelper.retrieveContext().getResources().getColor(item.isSelected() ? android.R.color.white : R.color.borders_color));
        textViewPlus.setBackgroundColor(ContextHelper.retrieveContext().getResources().getColor(item.isSelected() ? R.color.borders_color : android.R.color.white));

    }

    @Override
    public void onClick(View v) {
        if (clickListener != null && item != null)
            clickListener.OnBubbleItemClicked(item);
    }
}
