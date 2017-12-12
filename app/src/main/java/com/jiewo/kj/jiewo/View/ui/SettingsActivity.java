package com.jiewo.kj.jiewo.View.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.jiewo.kj.jiewo.Model.UserModel;
import com.jiewo.kj.jiewo.R;

public class SettingsActivity extends AppCompatActivity {
    TextView txtUsername,txtEmail,txtNumber;
    ImageView imageView;
    Uri uri;
    String username,email,number;
    UserModel user = UserModel.getUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        txtUsername = (TextView) findViewById(R.id.txtUsername);
        txtEmail = (TextView) findViewById(R.id.txtEmail);
        txtNumber = (TextView)findViewById(R.id.txtNumber);
        imageView = (ImageView)findViewById(R.id.img_profile);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtUsername.setText(user.getName());
        txtEmail.setText(user.getEmail());
        txtNumber.setText(user.getNumber());
        user.setProfilePic(imageView);
        }

//    @Override
//    public void onResume() {
//        super.onResume();
//
//        Intent intent = getIntent();
//
//        String frag = intent.getExtras().getString("frag");
//
//        switch (frag) {
//
//            case "fragmentB":
//                //here you can set Fragment B to your activity as usual;
//                fragmentManager.beginTransaction().replace(R.id.fragment_placeholder, new FragmentB()).commit();
//                break;
//        }
//    }

    public void signOut(View view) {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        user.logout();
                        startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
                        finish();
                    }
                });
    }

    public void deleteAccount(final View view) {
        new MaterialDialog.Builder(this)
                .title("Confirm delete?")
                .content("All data associated with your account would be deleted permanently")
                .positiveText("DELETE")
                .negativeText("Cancel")
                .iconRes(R.drawable.ic_emoticon_sad)
                .show();

        new MaterialDialog.Builder(this)
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

}
