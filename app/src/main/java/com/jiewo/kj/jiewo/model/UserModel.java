package com.jiewo.kj.jiewo.model;

import android.app.Application;
import android.net.Uri;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.Serializable;

/**
 * Created by khaij on 09/12/2017.
 */

public class UserModel extends Application implements Serializable {

    static FirebaseUser firebaseUser;
    private static UserModel User = null;
    private String id;
    private String name;
    private String email;
    private Uri photoURI;
    private String number;
    private double rating;

    public UserModel() {
    }

    private UserModel(String id, String name, String email, Uri photoURI) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.photoURI = photoURI;
    }

    public static UserModel getUser() {
        if (User == null) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            firebaseUser = auth.getCurrentUser();
            User = new UserModel(firebaseUser.getUid(), firebaseUser.getDisplayName(), firebaseUser.getEmail(), firebaseUser.getPhotoUrl());

        }
        return User;
    }

    public void logout() {

        User = null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getEmail() {
        return firebaseUser.getEmail();
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Uri getPhotoURI() {
        if (photoURI != null)
            return photoURI;
        return Uri.parse("https://firebasestorage.googleapis.com/v0/b/jiewo-a02c5.appspot.com/o/profile_image%2Fdefault.png?alt=media&token=0fa0a1ce-b3ac-47af-b3c3-5e0cc91b140f");
    }

    public void setPhotoURI(Uri photoURI) {
        this.photoURI = photoURI;
    }
}


