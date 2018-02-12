package com.jiewo.kj.jiewo.ViewModel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.jiewo.kj.jiewo.model.CategoryModel;
import com.jiewo.kj.jiewo.model.ItemModel;

import java.util.List;

/**
 * Created by kj on 07/02/2018.
 */

public class CategoryViewModel extends ViewModel {

    private final MutableLiveData<List<ItemModel>> itemList = new MutableLiveData();
    private MutableLiveData<CategoryModel> selectedCat;
    private MutableLiveData<ItemModel> selectedItem;

    public CategoryViewModel() {
        this.selectedCat = new MutableLiveData<>();
        this.selectedItem = new MutableLiveData<>();
    }

    public void selectCategory(CategoryModel categoryId) {
        selectedCat.setValue(categoryId);
    }

    public MutableLiveData<CategoryModel> getCategoryId(){
        return selectedCat;
    }

    public MutableLiveData<ItemModel> getItemId() {
        return selectedItem;
    }

    public void selectItem(ItemModel selectedItem) {
        this.selectedItem.setValue(selectedItem);
    }




}


