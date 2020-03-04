package dev.aban.visible.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import dev.aban.visible.R;
import dev.aban.visible.model.DonateItem;
import dev.aban.visible.utils.ContextHelper;
import dev.aban.visible.utils.Helper;
import dev.aban.visible.utils.custom_view.TextViewPlus;

public class DonateAdapter extends RecyclerView.Adapter<DonateAdapter.DonateViewHolder> {
    private List<DonateItem> list;
    private int clicked_index = 0;
    private RadioButton radioButton;
    private CircleImageView imageView;
    private TextViewPlus title;
    private TextViewPlus price;
    private int lastPosition = -1;

    public DonateAdapter(List<DonateItem> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public DonateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DonateViewHolder(LayoutInflater.from(ContextHelper.retrieveContext()).inflate(R.layout.item_donate, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DonateViewHolder holder, int position) {
        DonateItem item = list.get(position);

        radioButton = holder.itemView.findViewById(R.id.item_donate_rb);
        imageView = holder.itemView.findViewById(R.id.item_donate_image);
        title = holder.itemView.findViewById(R.id.item_donate_title);
        price = holder.itemView.findViewById(R.id.item_donate_price);

        title.setText(item.getTitle());
        price.setText("Value : " + item.getPrice() + " " + Helper.getLocalPriceUnit());
        radioButton.setChecked(position == clicked_index);
        Picasso.get().load(item.getImageURL()).into(imageView);
        holder.itemView.setOnClickListener(v -> {
            clicked_index = position;
            notifyDataSetChanged();
        });

        setAnimation(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public float getSelectedPrice() {
        return list.get(clicked_index).getPrice();
    }

    private void setAnimation(View viewToAnimate, int position) {
//        If the bound view wasn't previously displayed on screen, it's animated
//        if ((position > lastPosition))
        if (true) {
            Animation animation = AnimationUtils.loadAnimation(ContextHelper.retrieveContext(), position % 2 == 0 ? R.anim.slide_ltr : R.anim.slide_rtl);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    class DonateViewHolder extends RecyclerView.ViewHolder {
        public DonateViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
