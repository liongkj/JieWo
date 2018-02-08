package com.jiewo.kj.jiewo.model;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by khaij on 07/02/2018.
 */

public class CategoryModel {
    private String id;
    private String name;
    private List<String> items = new ArrayList<>();
    private int count;

    public CategoryModel(String id, String name, List<String> items, int count) {
        this.id = id;
        this.name = name;
        this.items = items;
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public List<String> getItems() {
        return items;
    }

    public int getCount() {
        return count;
    }

    public String getId() {
        return id;
    }

    @Exclude
    public HashMap<String,Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("name",name);
        result.put("items",items);
        return result;
    }

}

