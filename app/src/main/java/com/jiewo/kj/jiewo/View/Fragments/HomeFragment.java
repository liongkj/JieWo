package com.jiewo.kj.jiewo.View.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jiewo.kj.jiewo.R;
import com.jiewo.kj.jiewo.View.Activities.MainActivity;


public class HomeFragment extends Fragment {

    private boolean doubleBackToExitPressedOnce;
    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            doubleBackToExitPressedOnce = false;
        }
    };

    private Handler mHandler = new Handler();

    ActionBarDrawerToggle mToggle = MainActivity.result.getActionBarDrawerToggle();

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        getActivity().setTitle("Home");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        mToggle.setDrawerIndicatorEnabled(true);

        view.setFocusableInTouchMode(true);

        view.requestFocus();

        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP
                        && keyCode == KeyEvent.KEYCODE_BACK) {
                    if (doubleBackToExitPressedOnce) {
                        // super.onBackPressed();
                        if (mHandler != null) {
                            mHandler.removeCallbacks(mRunnable);
                        }
                        getActivity().finish();
                        return true;
                    }
                    doubleBackToExitPressedOnce = true;
                    Toast.makeText(getActivity(),
                            "Please click BACK again to exit",
                            Toast.LENGTH_SHORT).show();
                    mHandler.postDelayed(mRunnable, 2000);
                }
                return true;
            }
        });
        return view;
    }

}
