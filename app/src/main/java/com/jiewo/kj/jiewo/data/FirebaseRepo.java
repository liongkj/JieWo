package com.jiewo.kj.jiewo.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jiewo.kj.jiewo.Model.ItemModel;
import com.jiewo.kj.jiewo.Util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by khaij on 28/01/2018.
 */

public class FirebaseRepo {
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Constants.ITEM_USER);
    private MutableLiveData<List<ItemModel>> itemList;

    public void addNewItem(ItemModel item) {
        String itemId = mDatabase.push().getKey();
        mDatabase.child(itemId).setValue(item);
    }

    public LiveData<List<ItemModel>> getItemList() {
        if (itemList == null) {
            loadItem();
        }
        return itemList;
    }

    private void loadItem() {
        List<ItemModel> items = new ArrayList<>();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ItemModel data = ds.getValue(ItemModel.class);
                    items.add(data);
                }
                itemList.setValue(items);
                Log.e("test", itemList.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }

    public void deleteItem(ItemModel item) {

    }
}
