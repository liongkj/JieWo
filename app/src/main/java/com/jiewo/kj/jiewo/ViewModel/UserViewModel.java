package com.jiewo.kj.jiewo.ViewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jiewo.kj.jiewo.Model.UserModel;

/**
 * Created by khaij on 25/01/2018.
 */

public class UserViewModel extends ViewModel {

    private String userId;
    private LiveData<UserModel> user;
    FirebaseUser firebaseUser;

    public void init(String userId) {
        this.userId = userId;
    }

    public LiveData<UserModel> getUser() {
        if (user == null) {
            user = new MutableLiveData<UserModel>();
            loadUsers();
        }
        return user;
    }

    private void loadUsers() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        // Do an asyncronous operation to fetch users.
    }
}
