package com.jiewo.kj.jiewo.view.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.jiewo.kj.jiewo.ViewModel.UserViewModel;
import com.jiewo.kj.jiewo.model.UserModel;

import java.util.Arrays;


public class LoginActivity extends AppCompatActivity {

    // Choose an arbitrary request code value
    private static final int RC_SIGN_IN = 123;
    UserModel user = null;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            // already signed in
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();

        } else {
            // not signed in
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(
                                    Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                            .build(),
                    RC_SIGN_IN);
        }

        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        userViewModel.setLogin(auth.getCurrentUser());
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == RESULT_OK) {

                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
                return;
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    Log.e("Login", "Login canceled by User");
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Log.e("Login", "No Internet Connection");
                    Snackbar snackbar = Snackbar
                            .make(findViewById(android.R.id.content), "No Internet", Snackbar.LENGTH_LONG);

                    snackbar.show();
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Log.e("Login", "Unknown Error");
                    return;
                }
            }

            Log.e("Login", "Unknown sign in response");
        }
    }
}

