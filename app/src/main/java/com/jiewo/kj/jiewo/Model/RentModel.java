package com.jiewo.kj.jiewo.Model;

import android.graphics.Bitmap;
import android.location.Location;

import java.util.List;

/**
 * Created by khaij on 28/01/2018.
 */

public class RentModel {
    private UserModel user;
    private ItemModel item;
    private Location location;
    private List<Bitmap> imageList;

    public RentModel() {
    }

    public RentModel(UserModel user, ItemModel item, Location location) {
        this.user = user;
        this.item = item;
        this.location = location;
    }

    public int getImageList() {
        return imageList.size();
    }
}
