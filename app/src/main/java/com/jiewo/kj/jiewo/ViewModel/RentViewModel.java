package com.jiewo.kj.jiewo.ViewModel;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableInt;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jiewo.kj.jiewo.model.CategoryModel;
import com.jiewo.kj.jiewo.model.ItemModel;
import com.jiewo.kj.jiewo.model.UserModel;
import com.jiewo.kj.jiewo.util.Constants;
import com.jiewo.kj.jiewo.util.FirebaseQueryLiveData;
import com.mlykotom.valifi.ValiFiForm;
import com.mlykotom.valifi.fields.ValiFieldText;
import com.mlykotom.valifi.fields.number.ValiFieldDouble;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jiewo.kj.jiewo.util.Constants.DATABASE_REF;
import static com.jiewo.kj.jiewo.util.Constants.ITEM;


/**
 * Created by khaij on 10/12/2017.
 */

public class RentViewModel extends ViewModel {


    private static final DatabaseReference CATEGORY = FirebaseDatabase.getInstance().getReference(Constants.CATEGORY);
    private static final DatabaseReference GEOFIRE = FirebaseDatabase.getInstance().getReference(Constants.GEOLOCATION);

    private final FirebaseQueryLiveData liveData = new FirebaseQueryLiveData(CATEGORY);
    private List<String> categoryList;
    GeoFire mGeoFire = new GeoFire(GEOFIRE);
    private ItemModel itemModel;
    private Place selectedPlace;
    private StorageReference mStorage;


    public final ObservableInt imageNo = new ObservableInt();
    public final ValiFieldText itemTitle = new ValiFieldText();
    public final ValiFieldText itemDescription = new ValiFieldText();
    public final ValiFieldText itemCategory = new ValiFieldText();
    public final ValiFieldDouble itemCost = new ValiFieldDouble();
    public final ValiFieldText itemLocation = new ValiFieldText();
    public final ValiFiForm form = new ValiFiForm(itemTitle, itemDescription, itemCategory, itemCost, itemLocation);

    public RentViewModel() {
        loadCategory();
        mStorage = FirebaseStorage.getInstance().getReference();
        itemTitle.setEmptyAllowed(false).setErrorDelay(1000);
        itemDescription.setEmptyAllowed(false).setErrorDelay(1000);
        itemCost.setErrorDelay(1000);
        itemLocation.setEmptyAllowed(false);
    }

    public GeoLocation getGeolcation() {
        return new GeoLocation(getSelectedPlace().getLatLng().latitude, getSelectedPlace().getLatLng().longitude);
    }

    public Place getSelectedPlace() {
        return selectedPlace;
    }

    public void setSelectedPlace(Place selectedPlace) {
        this.selectedPlace = selectedPlace;
    }

    @NonNull
    public LiveData<DataSnapshot> getDataSnapshotLiveData() {
        return liveData;
    }

    public List<String> getCategoryList() {
        //loadCategory();
        return categoryList;
    }

    @SuppressLint("StaticFieldLeak")
    private void loadCategory() {
        // do async operation to fetch users

        List<String> categoryStringList = new ArrayList<>();
        new AsyncTask<Void, Void, List<String>>() {
            @Override
            protected List<String> doInBackground(Void... voids) {
                CATEGORY.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        categoryStringList.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            String data = ds.child("name").getValue().toString();
                            categoryStringList.add(data);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                categoryList = categoryStringList;
                return categoryStringList;
            }
        }.execute();
    }

    private List<Uri> uploadImage(List<Uri> imageList, DatabaseReference itemRef) {
        int no = 0;
        List<Uri> images = new ArrayList<>();
        for (Uri uri : imageList) {
            StorageReference filepath = mStorage.child("Item_Image").child(uri.getLastPathSegment());
            no++;
            int finalNo = no;
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                    itemRef.child("picture").child("pic" + finalNo).setValue(downloadUri.toString());

                }
            });

        }
        return images;
    }

    void updateCategory(String itemId) {
        DatabaseReference catItem = DATABASE_REF.child(Constants.CATEGORY);

        catItem.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String catId = null;
                boolean exist = false;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.child("name").exists()) {

                        if (ds.child("name").getValue().equals(itemCategory.getValue())) {
                            catId = ds.getKey();

                            exist = true;
                        }
                    }
                }
                if (!exist) {
                    catId = catItem.push().getKey();
                    catItem.child(catId + "/name").setValue(itemCategory.getValue());
                }
                catItem.child(catId + "/items/"+itemId).setValue(true);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public Boolean submit(List<Uri> imageList) {
        form.refreshError();
        if (form.isValid()) {


            itemModel = new ItemModel();
            itemModel.setOwner(UserModel.getUser().getId());
            itemModel.setItemTitle(itemTitle.getValue());
            itemModel.setItemCategory(itemCategory.getValue());
            itemModel.setItemDescription(itemDescription.getValue());
            itemModel.setLocation(getGeolcation());
            itemModel.setItemPrice(itemCost.getNumber());

            //generate key

            String itemId = DATABASE_REF.child(ITEM).push().getKey();
            DatabaseReference itemRef = DATABASE_REF.child(ITEM).child(itemId);

            //save into item key
            Map<String, Object> newItem = itemModel.toMap();
            Map<String, Object> update = new HashMap<>();
            update.put(ITEM + "/" + itemId, newItem);
            DATABASE_REF.updateChildren(update);
            //DATABASE_REF.updateChildren(update);
            uploadImage(imageList, itemRef);
            //save into Category
            updateCategory(itemId);
            //save into item-location key
            mGeoFire.setLocation(itemId, getGeolcation());
            //upload image & save image url


            form.destroy();

        } else {
            Log.e("form", "invalid");
            return false;
        }

        return true;
    }

}




