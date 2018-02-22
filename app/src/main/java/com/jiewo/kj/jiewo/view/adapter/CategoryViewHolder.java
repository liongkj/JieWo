package com.jiewo.kj.jiewo.view.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jiewo.kj.jiewo.R;
import com.jiewo.kj.jiewo.model.CategoryModel;


public class CategoryViewHolder extends RecyclerView.ViewHolder {

    private TextView catTitle;
    private TextView catCount;
    private CardView catCard;
    private ImageView catThumb;
    private CategoryViewHolder.ClickListener mClickListener;
    private View view;

    public CategoryViewHolder(View View) {

        super(View);
        view = View;
        catTitle = View.findViewById(R.id.catTitle);
        catCount = View.findViewById(R.id.catCount);
        catCard = View.findViewById(R.id.card_view);
        catThumb = View.findViewById(R.id.catThumbnail);
    }

    public void bindView(CategoryModel model) {
        catTitle.setText(model.getName());
        catCount.setText(String.valueOf(model.getCount()));
        view.setOnClickListener(v -> mClickListener.onCategoryClick(catCard, getAdapterPosition()));
        int img;
        switch (model.getId()) {
            case "Computer & Accessories":
                img = R.drawable.ic_cat_electronicaccessories;
                break;
            case "sdasfnjalnol":
                img = R.drawable.ic_cat_babies;
                break;
            case "dnajskndad":
                img = R.drawable.ic_cat_electronicdevices;
                break;
            case "sdklasmdlklsd":
                img = R.drawable.ic_cat_fashionaccessories;
                break;
            case "dsagasgsgwr":
                img = R.drawable.ic_cat_homeliving;
                break;
            case "asjjkfsanjdnosa":
                img = R.drawable.ic_cat_homeappliances;
                break;
            case "asdassadgre":
                img = R.drawable.ic_cat_men;
                break;
            case "nfjasndjsandj":
                img = R.drawable.ic_cat_sports;
                break;
            case "asdnasbfandlnldsa":
                img = R.drawable.ic_cat_women;
                break;
            case "nasjfnjbgkebkn":
                img = R.drawable.ic_cat_pets;
                break;
            default:
                img = R.drawable.ic_cat_babies;
                break;
        }
        catThumb.setImageResource(img);
    }

    public void setOnClickListener(CategoryViewHolder.ClickListener clickListener) {
        mClickListener = clickListener;
    }

    public interface ClickListener {
        void onCategoryClick(View view, int position);
    }
}



