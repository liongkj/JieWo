package ViewModel;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.InputStream;

/**
 * Created by khaij on 03/12/2017.
 */

abstract class User extends FirebaseUser{
    private static FirebaseAuth auth = FirebaseAuth.getInstance();
    private static FirebaseUser firebaseUser;
    private String displayname;
    private String profile_pic = "";
    private double rating = 5.0;
    private String number;


    private User() {
        this.displayname = firebaseUser.getDisplayName();
        this.profile_pic = firebaseUser.getPhotoUrl().toString();
        this.rating = 5.0;
        this.number = firebaseUser.getPhoneNumber();
    }

    public static FirebaseUser getCurrentUser(){

        if (firebaseUser==null){
            firebaseUser = auth.getCurrentUser();
        }
        return firebaseUser;
    }

    public String getUsername() {
        return displayname;
    }

    public void setDisplayname(String username) {
        this.displayname = username;
    }

    public String getProfile_pic(ImageView iv) {

        new DownloadImageTask(iv).execute(profile_pic);
        return profile_pic;

    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
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

    public static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
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

