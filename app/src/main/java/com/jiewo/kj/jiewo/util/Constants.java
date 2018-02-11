package com.jiewo.kj.jiewo.util;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by khaij on 25/01/2018.
 */

public final class Constants {
    public static final String FIREBASE_ROOT = "searchedLocation";
    public static final String CATEGORY = "Category";
    public static final String ITEM = "Item";
    public static final String GEOLOCATION = "Item-Location";
    public static final DatabaseReference DATABASE_REF = FirebaseDatabase.getInstance().getReference();

    public static final String USER = "User";

    public static final String DEFAULTPIC = "https://firebasestorage.googleapis.com/v0/b/jiewo-a02c5.appspot.com/o/profile_image%2Fdefault.png?alt=media&token=0fa0a1ce-b3ac-47af-b3c3-5e0cc91b140f";


}
