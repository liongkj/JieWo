package com.jiewo.kj.jiewo.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jiewo.kj.jiewo.R;
import com.jiewo.kj.jiewo.model.ItemModel;
import com.squareup.picasso.Picasso;


public class ItemViewHolder extends RecyclerView.ViewHolder {

    Context mContext;
    ItemViewHolder.ClickListener mClickListener;
    private TextView itemTitle, itemDescription, itemCost;
    private ImageView itemImage;

    public ItemViewHolder(View View, Context context) {

        super(View);
        mContext = context;
        itemTitle = View.findViewById(R.id.itemName);
//        itemDescription = View.findViewById(R.id.itemDescription);
        itemCost = View.findViewById(R.id.itemCost);
        itemImage = View.findViewById(R.id.itemImage);

    }

    public void bindView(ItemModel model, boolean bool) {

        itemTitle.setText(model.getItemTitle());
        if (!model.getItemImages().isEmpty()) {
            Picasso.with(mContext)
                    .load(model.getItemImages().get(0))
                    .centerCrop()
                    .resize(100, 100)
                    .into(itemImage);
        }
//        itemDescription.setText(model.getItemDescription());
        if (bool) {
            itemCost.setText(model.getOwner().getName()); // to hold the requester name
        } else {
            itemCost.setText(String.format("RM " + "%.2f", model.getItemPrice()));

        }

        itemView.setOnClickListener(v -> mClickListener.onCategoryClick(v, getAdapterPosition()));
    }

    public void setOnClickListener(ItemViewHolder.ClickListener clickListener) {
        mClickListener = clickListener;
    }

    public interface ClickListener {
        void onCategoryClick(View view, int position);
    }
}



