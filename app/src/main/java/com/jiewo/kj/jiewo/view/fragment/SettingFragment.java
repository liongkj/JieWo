package com.jiewo.kj.jiewo.view.fragment;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jiewo.kj.jiewo.R;
import com.jiewo.kj.jiewo.ViewModel.UserViewModel;
import com.jiewo.kj.jiewo.model.UserModel;
import com.jiewo.kj.jiewo.view.activity.LoginActivity;
import com.jiewo.kj.jiewo.view.activity.MainActivity;
import com.squareup.picasso.Picasso;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;
import static com.jiewo.kj.jiewo.util.Constants.DATABASE_REF;


public class SettingFragment extends Fragment
        implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {


    private static final int RESULT_CANCEL = 0;
    @BindView(R.id.txtUsername)
    TextView txtUsername;
    @BindView(R.id.txtEmail)
    TextView txtEmail;
    @BindView(R.id.txtNumber)
    TextView txtNumber;
    @BindView(R.id.img_profile)
    ImageView profileImg;
    @BindView(R.id.btn_signout)
    AppCompatButton btnsignOut;
    @BindView(R.id.btn_acc_delete)
    AppCompatButton btndelAccount;
    @BindView(R.id.txt_add_address)
    TextView btnAddress;
    @BindView(R.id.txtSavedLocation)
    TextView txtAddress;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    UserViewModel viewModel;
    UserModel user = UserModel.getUser();
    private StorageReference mStorage;

    public SettingFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        mStorage = storage.getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        ButterKnife.bind(this, view);
        // Inflate the layout for this fragment
        getActivity().setTitle("Settings");
        ((MainActivity) getActivity()).hideFab();

        viewModel = ViewModelProviders.of(getActivity()).get(UserViewModel.class);

        btnAddress.setOnClickListener(v -> findPlace(v));

        viewModel.getLocation(user, getContext())
                .observe(getActivity(), s -> {
                    txtAddress.setText(s);
                });

        viewModel.getLoggedUser(user)
                .observe(getActivity(), i -> {
                            txtEmail.setText(i.getEmail());
                            txtNumber.setText(i.getNumber());
                            txtUsername.setText(i.getName());
                            Picasso.with(getContext()).load(i.getPhotoURI()).into(profileImg);
                        }
                );

        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeProfilePicture();
            }
        });

        return view;

    }

    private void changeProfilePicture() {
        PickImageDialog.build(new PickSetup())
                .setOnPickResult(new IPickResult() {
                    @Override
                    public void onPickResult(PickResult r) {
                        Uri uri = r.getUri();
                        //profileImg.setImageURI(uri);
                        StorageReference filepath = mStorage.child("profile_image").child(uri.getLastPathSegment());
                        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Uri downloadUri = taskSnapshot.getDownloadUrl();
                                DATABASE_REF.child("User").child(user.getId()).child("Profile").setValue(downloadUri.toString());
                                Toast.makeText(getContext(), "Profile Picture changed successfully", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).show(getActivity().getSupportFragmentManager());
    }

    public void findPlace(View view) {
        try {
            AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder()
                    .setCountry("MY")
                    .build();

            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .setFilter(autocompleteFilter)
                            .build(getActivity());


            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(getContext(), data);
                txtAddress.setText(place.getAddress().toString());
                viewModel.updateFavoritePlace(user, place);
                Toast.makeText(getContext(), "Location Updated", Toast.LENGTH_SHORT).show();

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getContext(), data);
                Snackbar snackbar = Snackbar
                        .make(getView(), status.toString(), Snackbar.LENGTH_LONG);
                snackbar.show();
            } else if (resultCode == RESULT_CANCEL) {
                Status status = PlaceAutocomplete.getStatus(getContext(), data);
                Snackbar snackbar = Snackbar
                        .make(getView(), "An error occured,", Snackbar.LENGTH_LONG);
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        btnsignOut.setOnClickListener(this);
        btndelAccount.setOnClickListener(this);
    }


    public void deleteAccout(View view) {

    }

    private void signOut() {
        AuthUI.getInstance()
                .signOut(getActivity())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // user is now signed out
                        user.logout();
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                        getActivity().finish();
                    }
                });
    }

    public void deleteAccount() {
        new MaterialDialog.Builder(getContext())
                .title("Confirm delete?")
                .content("All data associated with your account would be deleted permanently")
                .positiveText("DELETE")
                .negativeText("Cancel")
                .iconRes(R.drawable.ic_emoticon_sad)
                .show();

        new MaterialDialog.Builder(getContext())
                .onPositive((dialog, which) -> dialog.dismiss())
                .onNegative((dialog, which) -> {
                    Log.i("del123", "delete prompt");
//                        confirmDelete(view);
                })
        ;
    }

    @Override
    public void onClick(View view) {
        if (view == view.findViewById(R.id.btn_signout)) {
            signOut();
        } else
            deleteAccount();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
