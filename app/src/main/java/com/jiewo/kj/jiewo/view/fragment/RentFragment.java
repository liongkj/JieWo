package com.jiewo.kj.jiewo.view.fragment;


import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.RuntimeRemoteException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.jiewo.kj.jiewo.R;
import com.jiewo.kj.jiewo.ViewModel.RentViewModel;
import com.jiewo.kj.jiewo.databinding.FragmentRentBinding;
import com.jiewo.kj.jiewo.view.adapter.PlaceAutoCompleteAdapter;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import java.util.ArrayList;
import java.util.List;


public class RentFragment extends DialogFragment {

    private static final LatLngBounds BOUNDS_MALAYSIA = new LatLngBounds(
            new LatLng(0.360349, 99.228516), new LatLng(7.327599, 105.952148));
    private final int MAX = 6;
    protected GeoDataClient mGeoDataClient;
    RentViewModel viewModel;
    FragmentRentBinding binding;
    LinearLayout imageButtonParent;
    private int current = 0;
    private PlaceAutoCompleteAdapter mAdapter;
    private AutoCompleteTextView mAutocompleteView;
    private String TAG = "google api location";
    private List<Uri> imageList = new ArrayList<>();
    private OnCompleteListener<PlaceBufferResponse> mUpdatePlaceDetailsCallback
            = new OnCompleteListener<PlaceBufferResponse>() {
        @Override
        public void onComplete(Task<PlaceBufferResponse> task) {
            try {
                PlaceBufferResponse places = task.getResult();
                // Get the Place object from the buffer.
                final Place place = places.get(0).freeze();
                // Format details of the place for display and show it in a TextView.
                // Display the third party attributions if set.

                viewModel.setSelectedPlace(place);
                places.release();
                Log.i(TAG, "Place details received: " + place.getName());
                places.release();
            } catch (RuntimeRemoteException e) {
                // Request did not complete successfully
                Log.e(TAG, "Place keyQuery did not complete.", e);
                return;
            }
        }
    };
    private AdapterView.OnItemClickListener mAutoCompleteClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final AutocompletePrediction item = mAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);
            Log.i(TAG, "Autocomplete item selected: " + primaryText);
            /*
             Issue a request to the Places Geo Data Client to retrieve a Place object with
             additional details about the place.
              */
            Task<PlaceBufferResponse> placeResult = mGeoDataClient.getPlaceById(placeId);
            placeResult.addOnCompleteListener(mUpdatePlaceDetailsCallback);

            Log.i(TAG, "Called getPlaceById to get Place details for " + placeId);
        }
    };

    public RentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(RentViewModel.class);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AppTheme);
        setHasOptionsMenu(true);
        mGeoDataClient = Places.getGeoDataClient(getContext(), null);
        mAdapter = new PlaceAutoCompleteAdapter(getContext(), mGeoDataClient, BOUNDS_MALAYSIA, null);

    }

    @Override
    public void onStart() {

        super.onStart();
        Dialog dialog = getDialog();

        if (dialog != null) {

            int width = ViewGroup.LayoutParams.MATCH_PARENT;

            int height = ViewGroup.LayoutParams.MATCH_PARENT;

            dialog.getWindow().setLayout(width, height);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//         Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_rent, container, false);
        View view = binding.getRoot();
        Toolbar toolbar = view.findViewById(R.id.toolbarDialog);
        toolbar.setTitle("Rent Item");

        //((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_close_cancel);
        toolbar.setNavigationOnClickListener(v -> {
//            MainActivity.
//            ((AppCompatActivity) getActivity()).setSupportActionBar(current);
            getDialog().dismiss();

        });

        //((MainActivity) getActivity()).hideFab();

        binding.setView(this);
        binding.setVm(viewModel);
        imageButtonParent = binding.imageButtonParent;

        mAutocompleteView = binding.txtAutoCompleteLoc;
        mAutocompleteView.setOnItemClickListener(mAutoCompleteClickListener);
        mAutocompleteView.setAdapter(mAdapter);

        ArrayAdapter<String> catAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, viewModel.getCategoryList());
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.catSpinner.setAdapter(catAdapter);


        return view;
    }

    public void onClickAddImage(View view) {

        if (current < MAX) {

            generateImageButton();
            Log.e("current", String.valueOf(current) + viewModel.imageNo.get());
            if (current == 5) binding.setIsVisible(true);
            viewModel.imageNo.set(++current);
        } else {
            Toast toast = Toast.makeText(getContext(), "Maximum number of picture", Toast.LENGTH_LONG);
            toast.show();
            //return true;
        }
        ///return  false;
    }

    public void generateImageButton() {
        View view = getLayoutInflater().inflate(R.layout.layout_imagepicker, imageButtonParent, false);
        AppCompatImageButton imageButton = view.findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickImageDialog.build(new PickSetup())
                        .setOnPickResult(new IPickResult() {
                            @Override
                            public void onPickResult(PickResult r) {
                                imageList.add(r.getUri());
                                Bitmap bitmap = ThumbnailUtils.extractThumbnail(r.getBitmap(), 100, 100);
                                imageButton.setImageBitmap(bitmap);
                            }
                        }).show(getActivity().getSupportFragmentManager());
            }
        });
        imageButtonParent.addView(imageButton);
    }

    public void onClickSubmit(View view) {

        MaterialDialog md = new MaterialDialog.Builder(getContext())
                .title("Submit Item?")
                .content("By clicking submit, I confirm I've read and accepted the Terms and Condition?")
                .positiveText("SUBMIT")
                .show();

        md.getBuilder().onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                Toast.makeText(getContext(), "Posting...", Toast.LENGTH_LONG).show();
                getDialog().dismiss();
                if (viewModel.submit(imageList)) {
                    Toast toast = Toast.makeText(getContext(), "Item saved successfully", Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    Toast toast = Toast.makeText(getContext(), "There is an error in uploading your item", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
    }


}
