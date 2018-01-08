package com.jiewo.kj.jiewo.View.ui.Fragments;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Geocoder;
import android.location.Location;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jiewo.kj.jiewo.Model.ItemModel;
import com.jiewo.kj.jiewo.Model.UserModel;
import com.jiewo.kj.jiewo.R;
import com.jiewo.kj.jiewo.Util.Constants;
import com.jiewo.kj.jiewo.Util.FetchAddressIntentService;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;


public class RentItemFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    @BindView(R.id.imageButtonParent)
    LinearLayout linearLayout;
    @BindView(R.id.category_spinner)
    MaterialBetterSpinner catSpinner;
    @BindView(R.id.location_spinner)
    MaterialBetterSpinner locSpinner;
    @BindView(R.id.txtTitle)
    MaterialEditText txtTitle;
    @BindView(R.id.txtDescription)
    MaterialEditText txtDescription;
    @BindView(R.id.txtPrice)
    MaterialEditText txtPrice;


    MenuItem btndone;

    boolean isValid = true;
    private static final String[] ITEMS = {"Bags & Wallets", "Shoes", "Clothes", "Item 4", "Item 5", "Item 6"};
    private DatabaseReference mDatabase;
    UserModel user = UserModel.getUser();
    protected Location mLastLocation;
    private AddressResultReceiver mResultReceiver;
    private FusedLocationProviderClient mFusedLocationClient;
    boolean mLocationPermissionGranted;
    protected Location mLastKnownLocation;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    public RentItemFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_rent_item, container, false);
        ButterKnife.bind(this, view);
        getActivity().setTitle("Rent Item");

        //set image uploader
        for (int i = 0; i < 6; i++) {
            final ImageButton ib = (ImageButton) linearLayout.getChildAt(i);
            ib.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PickImageDialog.build(new PickSetup())
                            .setOnPickResult(new IPickResult() {
                                @Override
                                public void onPickResult(PickResult r) {
                                    Bitmap bitmap = ThumbnailUtils.extractThumbnail(r.getBitmap(), 100, 100);
                                    ib.setImageBitmap(bitmap);
                                }
                            }).show(getActivity().getSupportFragmentManager());
                }
            });
        }

        ArrayAdapter<CharSequence> catAdapter = ArrayAdapter.createFromResource(getContext(), R.array.category_array, android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> locAdapter = ArrayAdapter.createFromResource(getContext(), R.array.location_array, android.R.layout.simple_spinner_dropdown_item);
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        catSpinner.setAdapter(catAdapter);
        locSpinner.setAdapter(locAdapter);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        mResultReceiver = new AddressResultReceiver(new Handler());
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu, menu);
        btndone = menu.findItem(R.id.button_done);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.button_done:
                if (isValid()) confirmSubmit();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return false;
    }

    @OnFocusChange(R.id.txtPrice)
    void clear(boolean focus) {
        if (focus) {
            txtPrice.setText("");
        } else {
            isFree();
        }
    }


    public boolean isValid() {

        Log.e("validation", "run");
        if (txtPrice.getText().toString().isEmpty()) {
            txtPrice.setText("0.0");
        }

        if (!txtTitle.isCharactersCountValid()) {
            txtTitle.setError("Minimum 2 characters");
            isValid = false;
        }
        if (!txtDescription.isCharactersCountValid()) {
            txtDescription.setError("Item Description is Required");
            isValid = false;
        }
        return isValid;
    }

    private void isFree() {
        if (txtPrice.getText().toString().isEmpty() || Double.compare(Double.parseDouble(txtPrice.getText().toString()), 0.0) == 0) {
            txtPrice.setText("0");
            MaterialDialog md = new MaterialDialog.Builder(getContext())
                    .title("Confirm?")
                    .content("You are renting this item for free. Are you sure?")
                    .positiveText("Ok")
                    .show();

            md.getBuilder().onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    Log.e("yes", "d");

                }
            })
            ;
        }

    }

    private void confirmSubmit() {

        MaterialDialog md = new MaterialDialog.Builder(getContext())
                .title("Submit Item?")
                .content("By clicking submit, I confirm I've read and accepted the Terms and Condition?")
                .positiveText("SUBMIT")
                .show();

        md.getBuilder().onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                submitItem();
            }
        })
        ;
    }

    private void submitItem() {
        final String title = txtTitle.getText().toString();
        final String des = txtDescription.getText().toString();
        final Double price = Double.valueOf(txtPrice.getText().toString());
        final String category = catSpinner.getText().toString();
        Toast.makeText(getContext(), "Posting...", Toast.LENGTH_SHORT).show();

        writeNewItem(user.getId(), user.getName(), title, des, price, category);
    }

    private void writeNewItem(String userId, String username, String title, String des, Double price, String category) {
        Log.e("validation", "validation done");
        String key = mDatabase.child("items").push().getKey();
        ItemModel item = new ItemModel(userId, username, title, des, price, category);
        Map<String, Object> itemValues = item.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/items/" + key, itemValues);
        childUpdates.put("/user-items/" + userId + "/" + key, itemValues);

        mDatabase.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                Snackbar bar = Snackbar.make(getActivity().findViewById(android.R.id.content), "Item added.", Snackbar.LENGTH_LONG)
                        .setAction("Dismiss", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Handle user action
                            }
                        });
                bar.show();
            }

        });

        onPostSubmit();

    }

    void onPostSubmit() {
        Log.e("firebase", "success");
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        Class fragmentClass = HomeFragment.class;
        Fragment fragment = null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();

        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }


        fragmentManager.beginTransaction()
                .replace(R.id.fragment_placeholder, fragment)
                .commit();
    }

    private void startIntentService() {
        Intent intent = new Intent(getContext(), FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);
        getContext().startService(intent);
    }

    private void fetchAddressButtonHander(View view) {
        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    mLastKnownLocation = location;

                    // In some rare cases the location returned can be null
                    if (mLastKnownLocation == null) {
                        return;
                    }

                    if (!Geocoder.isPresent()) {
                        Toast.makeText(getContext(),
                                "No geocoder",
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    // Start service and update UI to reflect new location
                    startIntentService();
//                        updateUI();
                }
            });

        } else

        {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

    }

    @OnClick(R.id.btn_location)
    public void getLocation() {
        startIntentService();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
//        updateLocationUI();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    class AddressResultReceiver extends ResultReceiver {
        /**
         * Create a new ResultReceive to receive results.  Your
         * {@link #onReceiveResult} method will be called from the thread running
         * <var>handler</var> if given, or from an arbitrary thread if null.
         *
         * @param handler
         */
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string
            // or an error message sent from the intent service.
            String mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            Log.e("location getting", mAddressOutput);

            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                Log.e("location", getString(R.string.address_found));
            }

        }
    }

}



