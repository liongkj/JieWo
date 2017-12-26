package com.jiewo.kj.jiewo.Model;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Objects;

/**
 * Created by khaij on 09/12/2017.
 */

public class UserModel extends Application implements Serializable {

    private String id;
    private String name;
    private String email;
    private Uri photoURI;
    private String number;
    static FirebaseUser firebaseUser;
    private static UserModel User = null;
    Bitmap profilePic = null;

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

    public String getName() {
        if (name != null)
            return name;
        else return "Null";
    }

    public String getNumber() {
        if (number == null) return "0103363030";
        return number;
    }

    public void setNumber(String number) {
        //firebaseUser.setNumber();
    }

    public void setName(String name) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name).build();
        firebaseUser.updateProfile(profileUpdates);
    }

    public String getEmail() {
        if (email != null)
            return email;
        else return "sample@mail.com";
    }

    public void setEmail(String email) {

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(email).build();
        firebaseUser.updateProfile(profileUpdates);
    }

    public Uri getPhotoURI(){

        return photoURI;
    }

    public void setProfilePic(ImageView imageView) {
        if (!Objects.equals(photoURI, null)) {
            new DownloadImageTask(imageView).execute(photoURI.toString());
        }
    }

    public static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        Bitmap mIcon11 = null;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }


        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];

            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);

        }
    }
}


