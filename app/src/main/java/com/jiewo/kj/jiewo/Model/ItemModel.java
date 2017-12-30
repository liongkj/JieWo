package com.jiewo.kj.jiewo.Model;

/**
 * Created by khaij on 10/12/2017.
 */

public class ItemModel {
    private String itemId;
    private String category;
    private UserModel userModel;
    private int[] Resources = {};


    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    public int[] getResources() {
        return Resources;
    }

    public int getImage(int i) {
        return Resources[i];
    }


}
