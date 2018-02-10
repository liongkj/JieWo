package com.jiewo.kj.jiewo.util;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jiewo.kj.jiewo.R;
import com.jiewo.kj.jiewo.model.CategoryModel;
import com.jiewo.kj.jiewo.model.ItemModel;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;


public class ItemViewHolder extends RecyclerView.ViewHolder {

    private TextView itemTitle,itemDescription,itemCost;
    private ImageView itemImage;
    Context mContext;
    ItemViewHolder.ClickListener mClickListener;

    public ItemViewHolder(View View, Context context) {

        super(View);
        mContext = context;
        itemTitle = View.findViewById(R.id.itemName);
        itemDescription = View.findViewById(R.id.itemDescription);
        itemCost = View.findViewById(R.id.itemCost);
        itemImage = View.findViewById(R.id.itemImage);

    }

    public void bindView(ItemModel model){
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
        Log.e("item",model.getItemTitle());
        itemTitle.setText(model.getItemTitle());
        itemDescription.setText(model.getItemDescription());
        itemCost.setText(format.format(model.getItemPrice()));
        Picasso.with(mContext)
                .load(model.getItemImages().get(0))
                .centerCrop()
                .resize(100,100)
                .into(itemImage);


        itemView.setOnClickListener(v -> mClickListener.onCategoryClick(v,getAdapterPosition()));
    }

    public interface ClickListener {
        public void onCategoryClick(View view, int position);
    }
    public void setOnClickListener(ItemViewHolder.ClickListener clickListener){
        mClickListener = clickListener;
    }
}



