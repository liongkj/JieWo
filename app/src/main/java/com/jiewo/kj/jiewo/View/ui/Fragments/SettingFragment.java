package com.jiewo.kj.jiewo.View.ui.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.jiewo.kj.jiewo.Model.UserModel;
import com.jiewo.kj.jiewo.R;
import com.jiewo.kj.jiewo.View.ui.Activities.LoginActivity;
import butterknife.BindView;
import butterknife.ButterKnife;


public class SettingFragment extends Fragment
        implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    @BindView(R.id.txtUsername)
    TextView txtUsername;
    @BindView(R.id.txtEmail)
    TextView txtEmail;
    @BindView(R.id.txtNumber)
    TextView txtNumber;
    @BindView(R.id.img_profile)
    ImageView imageView;
    @BindView(R.id.btn_signout)
    AppCompatButton btnsignOut;
    @BindView(R.id.btn_acc_delete)
    AppCompatButton btndelAccount;
    @BindView(R.id.txt_add_address)
    TextView btnAddress;

    String username, email, number;
    UserModel user = UserModel.getUser();

    public SettingFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        ButterKnife.bind(this, view);
        // Inflate the layout for this fragment
        getActivity().setTitle("Settings");
        btnAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent myIntent = new Intent(getActivity(),AddressActivity.class);
                //startActivity(myIntent);
            }
        });

        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        btnsignOut.setOnClickListener(this);
        btndelAccount.setOnClickListener(this);
        txtUsername.setText(user.getName());
        txtEmail.setText(user.getEmail());
        txtNumber.setText(user.getNumber());
        user.setProfilePic(imageView);
    }


    public void deleteAccout(View view) {

    }

    private void signOut() {
        AuthUI.getInstance()
                .signOut(getActivity())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // user is now signed out
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
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Log.i("del123", "delete prompt");
//                        confirmDelete(view);
                    }
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
