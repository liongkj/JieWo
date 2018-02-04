package com.jiewo.kj.jiewo.util;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by khaij on 25/01/2018.
 */

public class Constants {
    public static final String FIREBASE_ROOT = "searchedLocation";
    public static final String CATOGORY = "Category";
    public static final String ITEM = "Item";
    public static final String GEOLOCATION = "Item-Location";
    public static final DatabaseReference DATABASE_REF = FirebaseDatabase.getInstance().getReference();
    public static final String USER = "User";
}
