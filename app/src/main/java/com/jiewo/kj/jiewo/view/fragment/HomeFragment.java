package com.jiewo.kj.jiewo.view.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.jiewo.kj.jiewo.R;
import com.jiewo.kj.jiewo.ViewModel.HomeViewModel;
import com.jiewo.kj.jiewo.databinding.FragmentHomeBinding;
import com.jiewo.kj.jiewo.model.CategoryModel;
import com.jiewo.kj.jiewo.model.ItemModel;
import com.jiewo.kj.jiewo.util.CategoryViewHolder;
import com.jiewo.kj.jiewo.view.activity.MainActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ss.com.bannerslider.banners.Banner;
import ss.com.bannerslider.banners.DrawableBanner;
import ss.com.bannerslider.views.BannerSlider;

import static com.jiewo.kj.jiewo.util.Constants.CATEGORY;
import static com.jiewo.kj.jiewo.util.Constants.DATABASE_REF;


public class HomeFragment extends Fragment {

    private boolean doubleBackToExitPressedOnce;
    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter<CategoryModel, CategoryViewHolder> adapter;

    private Handler mHandler = new Handler();
    ActionBarDrawerToggle mToggle = MainActivity.result.getActionBarDrawerToggle();
    FragmentHomeBinding binding;
    HomeViewModel viewModel;

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
        viewModel = ViewModelProviders.of(getActivity()).get(HomeViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        View view = binding.getRoot();
        recyclerView = binding.recyclerView;

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));

        getActivity().setTitle("JieWo");
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

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    void buildCategory() {
        Query query = DATABASE_REF.child(CATEGORY).orderByChild("name");

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions
                .Builder<CategoryModel>()
                .setQuery(query, new SnapshotParser<CategoryModel>() {
                    CategoryModel cm;
                    List<String> items;
                    @Override
                    public CategoryModel parseSnapshot(DataSnapshot snapshot) {
                        items = new ArrayList<>();
                        String id = snapshot.getKey();
                        String name = snapshot.child("name").getValue().toString();
                        for (DataSnapshot ds : snapshot.child("items").getChildren()) {
                            items.add(ds.getValue().toString());
                        }
                        int count = items.size();
                        cm = new CategoryModel(id ,name, items, count);
                        return cm;
                    }
                })
                .build();

        adapter = new FirebaseRecyclerAdapter<CategoryModel, CategoryViewHolder>(options) {
            @Override
            protected void onBindViewHolder(CategoryViewHolder holder, int position, CategoryModel model) {
                holder.bindView(model);
                holder.setOnClickListener((view, position1) -> {
                    final CategoryModel cat = model;

                    viewModel.selectCategory(cat);
                    Fragment fragment = new ItemListFragment();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    fragmentTransaction.replace(R.id.fragment_placeholder, fragment, "tag")
                            .addToBackStack(null)
                            .commit();
                });

            }

            @Override
            public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_grid_category, parent, false);

                return new CategoryViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
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
