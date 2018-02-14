package com.jiewo.kj.jiewo.ViewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.location.Location;
import android.net.Uri;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.jiewo.kj.jiewo.model.CategoryModel;
import com.jiewo.kj.jiewo.model.ItemModel;
import com.jiewo.kj.jiewo.model.UserModel;

import java.util.ArrayList;
import java.util.List;

import static com.jiewo.kj.jiewo.util.Constants.DATABASE_REF;

/**
 * Created by kj on 07/02/2018.
 */

public class CategoryViewModel extends ViewModel {

    private final MutableLiveData<List<ItemModel>> itemList = new MutableLiveData();
    private MutableLiveData<CategoryModel> selectedCat;
    private MutableLiveData<ItemModel> selectedItem;
    private MutableLiveData<Location> currentLoc;
    private MutableLiveData<Location> itemLoc;
    private MutableLiveData<Float> distance;

    public CategoryViewModel() {
        this.selectedCat = new MutableLiveData<>();
        this.selectedItem = new MutableLiveData<>();
        distance = new MutableLiveData<>();
        currentLoc = new MutableLiveData<>();
        itemLoc = new MutableLiveData<>();
    }

    public void selectCategory(CategoryModel categoryId) {
        selectedCat.setValue(categoryId);
    }

    public MutableLiveData<CategoryModel> getCategoryId() {
        return selectedCat;
    }

    public MutableLiveData<ItemModel> getItemId() {
        return selectedItem;
    }

    public void selectItem(ItemModel selectedItem) {
        this.selectedItem.setValue(selectedItem);
    }

    public void selectItem(String itemId) {
        DATABASE_REF.child("Item").child(itemId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                ItemModel im = new ItemModel();
                UserModel um = new UserModel();
                List<Uri> images = new ArrayList<>();
                String id = snapshot.getKey();
                String title = snapshot.child("title").getValue().toString();
                String description = snapshot.child("description").getValue().toString();
                Double cost = Double.valueOf(snapshot.child("price").getValue().toString());
                um.setId(snapshot.child("owner").getValue().toString());
                String category = snapshot.child("category").getValue().toString();
                for (DataSnapshot ds : snapshot.child("picture").getChildren()) {
                    images.add(Uri.parse(ds.getValue().toString()));
                }
                im.setItemId(id);
                im.setItemTitle(title);
                im.setItemDescription(description);
                im.setItemPrice(cost);
                im.setImages(images);
                im.setOwner(um);
                im.setItemCategory(category);
                selectedItem.setValue(im);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void setCurrentLocation(Location location) {
        currentLoc.setValue(location);
    }

    public void setItemLocation(Location location) {
        itemLoc.setValue(location);

    }

    public LiveData<Float> getDistance() {
        if (!(itemLoc.getValue() == null && currentLoc.getValue() == null)) {
            distance.setValue(currentLoc.getValue().distanceTo(itemLoc.getValue()));
            return distance;
        }
        return null;
    }

}


