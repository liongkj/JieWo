package com.jiewo.kj.jiewo.model;

import android.databinding.BaseObservable;
import android.net.Uri;

import com.firebase.geofire.GeoLocation;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by khaij on 10/12/2017.
 */
@IgnoreExtraProperties
public class ItemModel{
    public String itemId;
    public String owner;
    public String itemTitle;
    public String itemDescription;
    public String itemCategory;
    public double itemPrice;
    public GeoLocation location;
    public List<Uri> itemImages;



    public ItemModel() {


    }

    public String getOwner() {
        return owner;
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

    public String getItemDescription() {
        return itemDescription;
    }

    public String getItemCategory() {
        return itemCategory;
    }

    public double getItemPrice() {
        return itemPrice;
    }

    public GeoLocation getLocation() {
        return location;
    }

    public List<Uri> getItemImages() {
        return itemImages;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public void setItemCategory(String itemCategory) {
        this.itemCategory = itemCategory;
    }

    public void setItemPrice(double itemPrice) {
        this.itemPrice = itemPrice;
    }

    public void setLocation(GeoLocation location) {
        this.location = location;
    }

    public void setImages(List<Uri> images) {
        this.itemImages = images;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("owner", owner);
        result.put("title", itemTitle);
        result.put("description", itemDescription);
        result.put("category", itemCategory);
        result.put("price", itemPrice);
        result.put("location", location);
        result.put("images", itemImages);
        return result;
    }


}
