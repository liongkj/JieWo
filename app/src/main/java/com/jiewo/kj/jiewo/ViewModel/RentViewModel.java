package com.jiewo.kj.jiewo.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import com.jiewo.kj.jiewo.Model.ItemModel;

/**
 * Created by khaij on 10/12/2017.
 */

public class RentViewModel extends AndroidViewModel {

    private MutableLiveData<ItemModel> item;


    public RentViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<ItemModel> getItem() {
        return item;
    }


    public static class addAsyncTask extends AsyncTask<ItemModel, Void, Void> {

        @Override
        protected Void doInBackground(ItemModel... itemModels) {
            return null;
        }
    }
}
