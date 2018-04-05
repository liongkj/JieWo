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

    public boolean request(ItemModel item, UserModel renter) {
        if (!Objects.equals(item.getOwner().getId(), renter.getId())) { //check logged = owner
            String requestId = DATABASE_REF.child("Request").push().getKey();

            DATABASE_REF.child("Request").child(requestId).child("Rating").setValue(renter.getRating());
            DATABASE_REF.child("Request").child(requestId).child("From").setValue(renter.getId());
            DATABASE_REF.child("Request").child(requestId).child("Rentername").setValue(renter.getName());
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

    public void acceptRentRequest(ItemModel model) {
        //owner - delete owner request, add to outgoing list
        DATABASE_REF.child("User").child(model.getOwner().getId()).child("request").child(model.getItemCategory()).setValue(null);
        DATABASE_REF.child("User").child(model.getOwner().getId()).child("outgoing").child(model.getItemCategory()).setValue(true);

        //renter - add rented
        DATABASE_REF.child("User").child(model.getItemDescription()).child("rented").child(model.getItemCategory()).setValue(true); //renter id

    }

    public void declineRentRequest(ItemModel model) {
        DATABASE_REF.child("User").child(model.getOwner().getId()).child("request").child(model.getItemCategory()).setValue(null);
        DATABASE_REF.child("Request").child(model.getItemCategory()).setValue(null);
    }

    public void returnRequest(ItemModel model) {
        //owner
        DATABASE_REF.child("User").child(model.getOwner().getId()).child("receive").child(model.itemCategory).setValue(true);
        //DATABASE_REF.child("User").child(model.getOwner().getId()).child("rented/" + model.getItemId()).setValue(null);
    }

    public void acceptReturnRequest(ItemModel model) {
        //owner
        DATABASE_REF.child("User").child(model.getOwner().getId()).child("outgoing").child(model.getItemCategory()).setValue(null);
        DATABASE_REF.child("User").child(model.getOwner().getId()).child("receive").child(model.itemCategory).setValue(null);
        //renter
        DATABASE_REF.child("User").child(model.getItemDescription()).child("rented").child(model.getItemCategory()).setValue(null);
        //request
        DATABASE_REF.child("Request").child(model.getItemCategory()).setValue(null);
    }



}
