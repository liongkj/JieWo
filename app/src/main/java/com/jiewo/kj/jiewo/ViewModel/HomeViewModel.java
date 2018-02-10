package com.jiewo.kj.jiewo.ViewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jiewo.kj.jiewo.model.CategoryModel;
import com.jiewo.kj.jiewo.model.ItemModel;
import com.jiewo.kj.jiewo.util.Constants;


import java.util.ArrayList;
import java.util.List;

import static com.jiewo.kj.jiewo.util.Constants.CATEGORY;
import static com.jiewo.kj.jiewo.util.Constants.DATABASE_REF;

/**
 * Created by kj on 07/02/2018.
 */

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<List<ItemModel>> itemList = new MutableLiveData();
    private MutableLiveData<CategoryModel> selectedCat;
    private MutableLiveData<ItemModel> selectedItem;

    public HomeViewModel() {
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

    public MutableLiveData<List<ItemModel>> getCategory() {
        List<ItemModel> itemModelList = new ArrayList<>();
        Query query = DATABASE_REF.child("Category/" + selectedCat);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ItemModel item = ds.getValue(ItemModel.class);
                    Log.e("item", item.itemTitle);
                    itemModelList.add(item);
                }
                itemList.postValue(itemModelList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return itemList;
    }


}


