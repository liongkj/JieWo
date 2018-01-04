package com.jiewo.kj.jiewo.Model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by khaij on 10/12/2017.
 */
@IgnoreExtraProperties
public class ItemModel {
    private String userId;
    private String owner;
    private String itemTitle;
    private String itemDescription;
    private String category;
    private double price;
    private int[] Resources = {};

    public ItemModel() {
    }

    public ItemModel(String itemId, String owner, String title, String description, Double price, String category) {
        this.userId = itemId;
        this.owner = owner;
        this.itemTitle = title;
        this.itemDescription = description;
        this.price = price;
        this.category = category;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("itemTitle", itemTitle);
        result.put("itemDescription", itemDescription);
        result.put("category", category);
        result.put("price", price);
        result.put("owner", owner);

        return result;
    }


}
