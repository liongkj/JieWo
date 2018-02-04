package com.jiewo.kj.jiewo.model;

import android.databinding.BaseObservable;
import android.graphics.Bitmap;

import com.firebase.geofire.GeoLocation;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by khaij on 10/12/2017.
 */
@IgnoreExtraProperties
public class ItemModel extends BaseObservable {
    public String userId;
    public String owner;
    public String itemTitle;
    public String itemDescription;
    public String itemCategory;
    public double itemPrice;
    public GeoLocation location;
    public List<Bitmap> images;

    public ItemModel() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public String getItemCategory() {
        return itemCategory;
    }

    public void setItemCategory(String itemCategory) {
        this.itemCategory = itemCategory;
    }

    public double getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(double itemPrice) {
        this.itemPrice = itemPrice;
    }

    public GeoLocation getLocation() {
        return location;
    }

    public void setLocation(GeoLocation location) {
        this.location = location;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("itemTitle", itemTitle);
        result.put("itemDescription", itemDescription);
        result.put("itemCategory", itemCategory);
        result.put("itemPrice", itemPrice);
        result.put("location", location);
        return result;
    }


}
