package com.jiewo.kj.jiewo.View.ui;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jiewo.kj.jiewo.Model.UserModel;
import com.jiewo.kj.jiewo.R;

public class SettingsActivity extends Fragment {
    TextView txtUsername,txtEmail,txtNumber;
    ImageView imageView;
    Uri uri;
    String username,email,number;
    UserModel user = UserModel.getUser();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setting, container, false);
//        return new View (R.layout.activity_settings);


//
//        txtUsername = (TextView) findViewById(R.id.txtUsername);
//        txtEmail = (TextView) findViewById(R.id.txtEmail);
//        txtNumber = (TextView)findViewById(R.id.txtNumber);
//        imageView = (ImageView)findViewById(R.id.img_profile);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        txtUsername.setText(user.getName());
//        txtEmail.setText(user.getEmail());
//        txtNumber.setText(user.getNumber());
//        user.setProfilePic(imageView);
    }

//    public void signOut(View view) {
//        AuthUI.getInstance()
//                .signOut(this)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    public void onComplete(@NonNull Task<Void> task) {
//                        user.logout();
//                        startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
//                        finish();
//                    }
//                });
//    }
//


}
