package com.jiewo.kj.jiewo.view.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.jiewo.kj.jiewo.R;
import com.jiewo.kj.jiewo.model.CategoryModel;


public class CategoryViewHolder extends RecyclerView.ViewHolder {

    private TextView catTitle;
    private TextView catCount;
    private CardView catCard;
    private CategoryViewHolder.ClickListener mClickListener;

    public CategoryViewHolder(View View) {

        super(View);
        catTitle = View.findViewById(R.id.catTitle);
        catCount = View.findViewById(R.id.catCount);
        catCard = View.findViewById(R.id.card_view);

    }

    public void bindView(CategoryModel model){
        catTitle.setText(model.getName());
        catCount.setText(String.valueOf(model.getCount()));
        catCard.setOnClickListener(v -> mClickListener.onCategoryClick(v, getAdapterPosition()));
    }

    public void setOnClickListener(CategoryViewHolder.ClickListener clickListener){
        mClickListener = clickListener;
    }

    public interface ClickListener {
        public void onCategoryClick(View view, int position);
    }
}



