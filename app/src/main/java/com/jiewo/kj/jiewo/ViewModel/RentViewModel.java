package com.jiewo.kj.jiewo.ViewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.databinding.Bindable;
import android.databinding.ObservableInt;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jiewo.kj.jiewo.Util.Constants;
import com.jiewo.kj.jiewo.Util.FirebaseQueryLiveData;

/**
 * Created by khaij on 10/12/2017.
 */

public class RentViewModel extends ViewModel {

    private static final DatabaseReference ITEM_CATEGORY = FirebaseDatabase.getInstance().getReference(Constants.ITEM_CATOGORY);
    private final FirebaseQueryLiveData liveData = new FirebaseQueryLiveData(ITEM_CATEGORY);

    @Bindable
    public final ObservableInt imageNo = new ObservableInt();


    @NonNull
    public LiveData<DataSnapshot> getDataSnapshotLiveData() {
        return liveData;
    }


//    public RentViewModel() {
//        getItemList();
//    }
//
//    public LiveData<List<ItemModel>> getItemList(){
//        return firebaseRepo.getItemList();
//    }


}
