package com.jiewo.kj.jiewo.view.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.jiewo.kj.jiewo.R;
import com.jiewo.kj.jiewo.ViewModel.CategoryViewModel;
import com.jiewo.kj.jiewo.databinding.FragmentCategoryBinding;
import com.jiewo.kj.jiewo.model.CategoryModel;
import com.jiewo.kj.jiewo.view.activity.MainActivity;
import com.jiewo.kj.jiewo.view.adapter.CategoryViewHolder;

import java.util.ArrayList;
import java.util.List;

import ss.com.bannerslider.banners.Banner;
import ss.com.bannerslider.banners.DrawableBanner;
import ss.com.bannerslider.views.BannerSlider;

import static com.jiewo.kj.jiewo.util.Constants.CATEGORY;
import static com.jiewo.kj.jiewo.util.Constants.DATABASE_REF;


public class CategoryFragment extends Fragment {

    FragmentCategoryBinding binding;
    CategoryViewModel viewModel;
    ActionBarDrawerToggle mToggle = MainActivity.result.getActionBarDrawerToggle();
    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter<CategoryModel, CategoryViewHolder> adapter;


    public CategoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(CategoryViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_category, container, false);
        View view = binding.getRoot();


//        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
//        recyclerView.addItemDecoration(dividerItemDecoration);

        getActivity().setTitle("Category".toUpperCase());
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        mToggle.setDrawerIndicatorEnabled(true);
        ((MainActivity) getActivity()).showFab();

        setHasOptionsMenu(true);

        buildSlider();

        recyclerView = view.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(layoutManager);

        buildCategory();
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
                        cm = new CategoryModel(id, name, items, count);
                        Log.e("id", id);
                        return cm;
                    }
                })
                .build();

        adapter = new FirebaseRecyclerAdapter<CategoryModel, CategoryViewHolder>(options) {

            @Override
            public void onDataChanged() {
                // Called each time there is a new data snapshot. You may want to use this method
                // to hide a loading spinner or check for the "no documents" state and update your UI.
                // ...

            }

            @Override
            public void onError(DatabaseError e) {
                // Called when there is an error getting data. You may want to update
                // your UI to display an error message to the user.
                // ...
            }//TODO

            @Override
            protected void onBindViewHolder(@NonNull CategoryViewHolder holder, int position, @NonNull CategoryModel model) {
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
