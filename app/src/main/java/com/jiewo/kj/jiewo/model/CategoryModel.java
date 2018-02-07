package com.jiewo.kj.jiewo.model;

/**
 * Created by khaij on 07/02/2018.
 */


public class CategoryModel {
    private String name;
    private int numOfItem;
    private int thumbnail;

    public CategoryModel() {
    }

    public CategoryModel(String name, int numOfItem, int thumbnail) {
        this.name = name;
        this.numOfItem = numOfItem;
        this.thumbnail = thumbnail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumOfItem() {
        return numOfItem;
    }

    public void setNumOfItem(int numOfItem) {
        this.numOfItem = numOfItem;
    }

    public int getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(int thumbnail) {
        this.thumbnail = thumbnail;
    }
}

