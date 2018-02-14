package com.jiewo.kj.jiewo.ViewModel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.jiewo.kj.jiewo.model.ItemModel;

/**
 * Created by khaij on 14/02/2018.
 */

public class HomeViewModel extends ViewModel {

    private MutableLiveData<ItemModel> item;

    public HomeViewModel() {
        item = new MutableLiveData<>();
    }


}
