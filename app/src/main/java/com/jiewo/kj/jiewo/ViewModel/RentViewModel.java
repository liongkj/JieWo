package com.jiewo.kj.jiewo.ViewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableInt;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jiewo.kj.jiewo.Model.ItemModel;
import com.jiewo.kj.jiewo.Util.Constants;
import com.jiewo.kj.jiewo.Util.FirebaseQueryLiveData;
import com.mlykotom.valifi.ValiFiForm;
import com.mlykotom.valifi.fields.ValiFieldText;
import com.mlykotom.valifi.fields.number.ValiFieldDouble;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by khaij on 10/12/2017.
 */

public class RentViewModel extends ViewModel {


    private static final DatabaseReference ITEM_CATEGORY = FirebaseDatabase.getInstance().getReference(Constants.ITEM_CATOGORY);
    private final FirebaseQueryLiveData liveData = new FirebaseQueryLiveData(ITEM_CATEGORY);
    private MutableLiveData<List<String>> categoryList;
    public ItemModel itemModel;

    public final ObservableInt imageNo = new ObservableInt();

    public final ValiFieldText itemTitle = new ValiFieldText();
    public final ValiFieldText itemDescription = new ValiFieldText();
    public final ValiFieldDouble itemCost = new ValiFieldDouble();


    public final ValiFiForm form = new ValiFiForm(itemTitle, itemDescription, itemCost);

    public RentViewModel() {
        itemModel = new ItemModel();
        itemTitle.setEmptyAllowed(false).setErrorDelay(1000);
        itemDescription.setEmptyAllowed(false).setErrorDelay(1000);
        itemCost.setErrorDelay(1000);
    }

    @NonNull
    public LiveData<DataSnapshot> getDataSnapshotLiveData() {
        return liveData;
    }

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

        ITEM_CATEGORY.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                categoryStringList.clear();
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


    public void submit() {
        form.refreshError();
        if (form.isValid()) {
            form.destroy();
        }
    }

}


//    public RentViewModel() {
//        getItemList();
//    }
//
//    public LiveData<List<ItemModel>> getItemList(){
//        return firebaseRepo.getItemList();
//    }



