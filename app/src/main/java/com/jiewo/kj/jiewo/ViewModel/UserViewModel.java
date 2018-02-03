package com.jiewo.kj.jiewo.ViewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseUser;
import com.jiewo.kj.jiewo.model.UserModel;

import java.util.Objects;

/**
 * Created by khaij on 25/01/2018.
 */

public class UserViewModel extends ViewModel {

    private String userId;
    final MutableLiveData<FirebaseUser> login = new MutableLiveData<>();
    private LiveData<UserModel> user;

    public void setLogin(FirebaseUser login) {

        if (Objects.equals(this.login.getValue(), login)) {
            return;
        }
        this.login.setValue(login);
    }


}
