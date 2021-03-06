package com.jiewo.kj.jiewo.ViewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.util.Log;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.LocationCallback;
import com.google.android.gms.location.places.Place;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.jiewo.kj.jiewo.model.UserModel;

import java.util.List;
import java.util.Locale;

import static com.jiewo.kj.jiewo.util.Constants.DATABASE_REF;

/**
 * Created by khaij on 25/01/2018.
 */

public class UserViewModel extends ViewModel {

    private String userId;
    private GeoFire geoFire;
    private MutableLiveData<UserModel> seller;
    private MutableLiveData<UserModel> userData;
    private MutableLiveData<String> locationData;
    private MutableLiveData<String> rateTarget;


    public UserViewModel() {

        geoFire = new GeoFire(DATABASE_REF);
        seller = new MutableLiveData<>();
        rateTarget = new MutableLiveData<>();

    }

    public LiveData<UserModel> getLoggedUser(UserModel user) {
        if (userData == null) {
            userData = new MutableLiveData<>();
            loadUser(user, userData);
        }
        return userData;
    }

    public LiveData<UserModel> getSeller(UserModel user) {
        this.seller = new MutableLiveData<>();
        loadUser(user, seller);
        return seller;
    }

    public LiveData<String> getLocation(UserModel user, Context context) {
        if (locationData == null) {
            locationData = new MutableLiveData<>();
            loadFavoritePlace(user, context);
        }
        return locationData;
    }

    private void loadUser(UserModel user, MutableLiveData<UserModel> holder) {
        DATABASE_REF.child("User/" + user.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    UserModel user = new UserModel();

                    String name = dataSnapshot.child("Name").getValue().toString();
                    String number = dataSnapshot.child("Number").getValue().toString();
                    Uri uri = Uri.parse(dataSnapshot.child("Profile").getValue().toString());
                    double rating = Double.valueOf(dataSnapshot.child("Rating").getValue().toString());
                    user.setId(dataSnapshot.getKey());
                    user.setName(name);
                    user.setNumber(number);
                    user.setPhotoURI(uri);
                    user.setRating(rating);

                    holder.setValue(user);
                } catch (Exception e) {
                    Log.e("Error", e.toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void updateFavoritePlace(UserModel user, Place place) {
        GeoLocation geoLocation = new GeoLocation(place.getLatLng().latitude, place.getLatLng().longitude);
        geoFire.setLocation("User/" + user.getId() + "/favorite", geoLocation);
    }

    private void loadFavoritePlace(UserModel user, Context context) {
        geoFire.getLocation("User/" + user.getId() + "/favorite", new LocationCallback() {
            @Override
            public void onLocationResult(String key, GeoLocation location) {
                if (location != null) {
                    Log.e("geofire", key + " found");
                    locationData.setValue(getCompleteAddressString(location.latitude, location.longitude, context));
                } else {
                    Log.e("geofire", key + "  is not valid");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE, Context context) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("My Current loction address", strReturnedAddress.toString());
            } else {
                Log.w("My Current loction address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current loction address", "Canont get Address!");
        }
        return strAdd;
    }


    public void setRate(int i, String s) {
        if (rateTarget != null) {
            DATABASE_REF.child("User").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String id = rateTarget.getValue();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {

                        if (ds.getKey().equals(id)) {
                            Double rating = Double.valueOf(dataSnapshot.child(id).child("Rating").getValue().toString());
                            rating = (rating + i) / 2;
                            DATABASE_REF.child("User").child(id).child("Rating").setValue(rating);
                            Log.e("rate", id + i + s);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }

    public void setRateTarget(String rateTarget) {
        this.rateTarget.setValue(rateTarget);
    }
}
