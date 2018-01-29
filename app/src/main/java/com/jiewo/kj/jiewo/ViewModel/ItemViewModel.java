package com.jiewo.kj.jiewo.ViewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jiewo.kj.jiewo.Util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by khaij on 27/01/2018.
 */

public class ItemViewModel extends ViewModel {

    private static final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference itemCategoryRef = database.child(Constants.ITEM_CATOGORY);

    private MutableLiveData<List<String>> categoryList;

    @NonNull


    public LiveData<List<String>> getCategoryList() {
        if (categoryList == null) {
            categoryList = new MutableLiveData<>();
            loadCategory();
        }
        return categoryList;
    }

    private void loadCategory() {
        // do async operation to fetch users
        List<String> categoryStringList = new ArrayList<>();

        itemCategoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String data = ds.child("Category").getValue().toString();
                    categoryStringList.add(data);
                }
                Log.e("test", String.valueOf(categoryStringList.size()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        categoryList.setValue(categoryStringList);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d("vmclass", "on cleared called");
    }
}
