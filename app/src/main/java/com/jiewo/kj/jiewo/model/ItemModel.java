package com.jiewo.kj.jiewo.model;

import android.net.Uri;

import com.firebase.geofire.GeoLocation;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by khaij on 10/12/2017.
 */
@IgnoreExtraProperties
public class ItemModel{
    public String itemId;
    public UserModel owner;
    public String itemTitle;
    public String itemDescription;
    public String itemCategory;
    public double itemPrice;
    public GeoLocation location;
    public List<Uri> itemImages;
    private HashMap<String, Object> timestampCreated;


    public ItemModel() {
        HashMap<String, Object> timestampNow = new HashMap<>();
        timestampNow.put("timestamp", ServerValue.TIMESTAMP);
        this.timestampCreated = timestampNow;

    }

    public UserModel getOwner() {
        return owner;
    }

    public void setOwner(UserModel owner) {
        this.owner = owner;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
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

    public HashMap<String, Object> getTimestampCreated() {
        return timestampCreated;
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

    public List<Uri> getItemImages() {
        return itemImages;
    }

    public void setImages(List<Uri> images) {
        this.itemImages = images;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("owner", owner.getId());
        result.put("title", itemTitle);
        result.put("description", itemDescription);
        result.put("category", itemCategory);
        result.put("price", itemPrice);
        result.put("location", location);
        result.put("images", itemImages);
        result.put("timestamp", timestampCreated);

        return result;
    }


}
