package dev.aban.visible.view.viewholder;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import dev.aban.visible.R;
import dev.aban.visible.listener.DrawerClickListener;
import dev.aban.visible.model.DrawerItem;
import dev.aban.visible.utils.ContextHelper;
import dev.aban.visible.utils.custom_view.TextViewPlus;

public class DrawerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private DrawerItem drawerItem;
    private DrawerClickListener clickListener;
    private ImageView imageView;
    private TextViewPlus textViewPlus;

    public DrawerViewHolder(@NonNull View view, DrawerClickListener clickListener) {
        super(view);
        bindListener(view, clickListener);
        findViews(view);
    }

    public void bindItem(DrawerItem drawerItem) {
        this.drawerItem = drawerItem;
        bindValues();
    }

    private void bindValues() {
        textViewPlus.setText(ContextHelper.retrieveContext().getString(drawerItem.getName()));
        imageView.setImageResource(drawerItem.getIcon());
    }

    private void bindListener(View view, DrawerClickListener clickListener) {
        this.clickListener = clickListener;
        view.setOnClickListener(this);
    }

    private void findViews(View view) {
        imageView = view.findViewById(R.id.item_navigation_drawer_icon);
        textViewPlus = view.findViewById(R.id.item_navigation_drawer_text);
    }

    @Override
    public void onClick(View v) {
        if (clickListener != null)
            clickListener.OnDrawerItemClicked(drawerItem);
    }
}
