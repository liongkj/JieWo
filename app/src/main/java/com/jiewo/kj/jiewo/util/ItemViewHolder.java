package com.jiewo.kj.jiewo.util;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.jiewo.kj.jiewo.R;
import com.jiewo.kj.jiewo.model.CategoryModel;
import com.jiewo.kj.jiewo.model.ItemModel;


public class ItemViewHolder extends RecyclerView.ViewHolder {

    TextView catTitle;
    TextView catCount;
    private ItemViewHolder.ClickListener mClickListener;

    public ItemViewHolder(View View) {

        super(View);
        catTitle = View.findViewById(R.id.itemName);
        catCount = View.findViewById(R.id.itemDescription);

    }

    public void bindView(ItemModel model){
        catTitle.setText(model.getItemTitle());
        catCount.setText(String.valueOf(model.getItemDescription()));

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onCategoryClick(v,getAdapterPosition());
            }
        });
    }

    public interface ClickListener {
        public void onCategoryClick(View view, int position);
    }
    public void setOnClickListener(ItemViewHolder.ClickListener clickListener){
        mClickListener = clickListener;
    }
}



