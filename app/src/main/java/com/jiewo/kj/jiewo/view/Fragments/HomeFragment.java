package com.jiewo.kj.jiewo.view.Fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jiewo.kj.jiewo.R;
import com.jiewo.kj.jiewo.databinding.FragmentHomeBinding;
import com.jiewo.kj.jiewo.view.activity.MainActivity;

import java.util.ArrayList;
import java.util.List;

import ss.com.bannerslider.banners.Banner;
import ss.com.bannerslider.banners.DrawableBanner;
import ss.com.bannerslider.views.BannerSlider;


public class HomeFragment extends Fragment {

    private boolean doubleBackToExitPressedOnce;
    private Handler mHandler = new Handler();
    ActionBarDrawerToggle mToggle = MainActivity.result.getActionBarDrawerToggle();
    FragmentHomeBinding binding;

    public HomeFragment() {
        // Required empty public constructor
    }

    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            doubleBackToExitPressedOnce = false;
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        View view = binding.getRoot();
        getActivity().setTitle("Home");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        mToggle.setDrawerIndicatorEnabled(true);
        ((MainActivity) getActivity()).showFab();
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

    void buildSlider() {

        BannerSlider bannerSlider = binding.bannerSlider1;
        List<Banner> banners = new ArrayList<>();
        //add banner using image url
        banners.add(new DrawableBanner(R.drawable.banner_ad1));
        //add banner using resource drawable
        banners.add(new DrawableBanner(R.drawable.banner_rent));
        bannerSlider.setBanners(banners);
    }


}
