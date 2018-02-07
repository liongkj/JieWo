package com.jiewo.kj.jiewo.view.Fragments;

import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jiewo.kj.jiewo.R;
import com.jiewo.kj.jiewo.databinding.FragmentHomeBinding;
import com.jiewo.kj.jiewo.model.CategoryModel;
import com.jiewo.kj.jiewo.util.CategoryAdapter;
import com.jiewo.kj.jiewo.util.GridSpacingItemDecoration;
import com.jiewo.kj.jiewo.view.activity.MainActivity;

import java.util.ArrayList;
import java.util.List;

import ss.com.bannerslider.banners.Banner;
import ss.com.bannerslider.banners.DrawableBanner;
import ss.com.bannerslider.views.BannerSlider;


public class HomeFragment extends Fragment {

    private boolean doubleBackToExitPressedOnce;
    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private List<CategoryModel> albumList;
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

        setHasOptionsMenu(true);

        buildSlider();
        buildCategory();


        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_options_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
        //TODO search function
    }

    void buildCategory() {
        recyclerView = binding.recyclerView;

        albumList = new ArrayList<>();
        adapter = new CategoryAdapter(getContext(), albumList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        prepareAlbums();

        try {
            //Glide.with(this).load(R.drawable.banner_ad1).into((ImageView) findViewById(R.id.backdrop));
        } catch (Exception e) {
            e.printStackTrace();
        }

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

    private void prepareAlbums() {
        int[] covers = new int[]{
                R.drawable.ic_menu_camera,
                R.drawable.ic_menu_items,
        };
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

}
