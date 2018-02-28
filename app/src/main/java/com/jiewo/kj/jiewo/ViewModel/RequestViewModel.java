package com.jiewo.kj.jiewo.ViewModel;

import android.arch.lifecycle.ViewModel;

import com.jiewo.kj.jiewo.model.ItemModel;
import com.jiewo.kj.jiewo.model.UserModel;

import java.util.Objects;

import static com.jiewo.kj.jiewo.util.Constants.DATABASE_REF;

/**
 * Created by khaij on 28/02/2018.
 */

public class RequestViewModel extends ViewModel {
    public boolean request(ItemModel item, UserModel user) {
        if (!Objects.equals(item.getOwner().getId(), user.getId())) {
            String requestId = DATABASE_REF.child("Request").push().getKey();
            DATABASE_REF.child("Request").child(requestId).child("Id").setValue(requestId);

            DATABASE_REF.child("Request").child(requestId).child("Rating").setValue(user.getRating());
//            DATABASE_REF.child("Request").child(requestId).child("From").setValue(user);
            DATABASE_REF.child("Request").child(requestId).child("FromName").setValue(user.getName());
            if (!item.getItemImages().isEmpty()) {
                DATABASE_REF.child("Request").child(requestId).child("ItemPic").setValue(item.getItemImages().get(0).toString());
            }
            DATABASE_REF.child("Request").child(requestId).child("To").setValue(item.getOwner().getId());
            DATABASE_REF.child("Request").child(requestId).child("Item").setValue(item.getItemId());

            DATABASE_REF.child("Request").child(requestId).child("Itemname").setValue(item.getItemTitle());
            DATABASE_REF.child("User").child(item.getOwner().getId()).child("request").child(requestId).setValue(true);
            return true;
        }
        return false;
    }
}
