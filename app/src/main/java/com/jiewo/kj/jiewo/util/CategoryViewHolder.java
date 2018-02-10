package com.jiewo.kj.jiewo.util;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.jiewo.kj.jiewo.R;
import com.jiewo.kj.jiewo.model.CategoryModel;


public class CategoryViewHolder extends RecyclerView.ViewHolder {

    private TextView catTitle;
    private TextView catCount;
    private CategoryViewHolder.ClickListener mClickListener;

    public CategoryViewHolder(View View) {

        super(View);
        catTitle = View.findViewById(R.id.catTitle);
        catCount = View.findViewById(R.id.catCount);

    }

    public void bindView(CategoryModel model){
        catTitle.setText(model.getName());
        catCount.setText(String.valueOf(model.getCount()));
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
    public void setOnClickListener(CategoryViewHolder.ClickListener clickListener){
        mClickListener = clickListener;
    }
}



