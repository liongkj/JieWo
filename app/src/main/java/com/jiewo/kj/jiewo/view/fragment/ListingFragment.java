package com.jiewo.kj.jiewo.view.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Query;
import com.jiewo.kj.jiewo.R;
import com.jiewo.kj.jiewo.ViewModel.CategoryViewModel;
import com.jiewo.kj.jiewo.ViewModel.UserViewModel;
import com.jiewo.kj.jiewo.databinding.FragmentListingBinding;
import com.jiewo.kj.jiewo.model.ItemModel;
import com.jiewo.kj.jiewo.model.UserModel;
import com.jiewo.kj.jiewo.view.adapter.ItemViewHolder;

import java.util.ArrayList;
import java.util.List;

import static com.jiewo.kj.jiewo.util.Constants.DATABASE_REF;


public class ListingFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "userid";
    private String mParam1;
    Query keyQuery;
    UserModel user = UserModel.getUser();
    private RecyclerView recyclerView;
    UserViewModel viewModel;
    CategoryViewModel categoryViewModel;
    FragmentListingBinding binding;
    private FirebaseRecyclerAdapter<ItemModel, ItemViewHolder> adapter;

    public ListingFragment() {
        // Required empty public constructor
    }

    public static ListingFragment newInstance(String param1) {
        ListingFragment fragment = new ListingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(UserViewModel.class);
        categoryViewModel = ViewModelProviders.of(getActivity()).get(CategoryViewModel.class);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_listing, container, false);
        View view = binding.getRoot();
        recyclerView = binding.UserItemList;
        binding.setView(this);
        buildItemList();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setNestedScrollingEnabled(false);
//        recyclerView.setHasFixedSize(true);


        Log.e("d", mParam1);
//        buildItemList();
        recyclerView.setAdapter(adapter);
        return view;

    }


    void buildItemList() {
        Query dataRef = DATABASE_REF.child("Item");
        keyQuery = DATABASE_REF.child("User/" + mParam1 + "/items");
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<ItemModel>()
                .setLifecycleOwner(this)
                .setIndexedQuery(keyQuery.getRef(), dataRef.getRef(), snapshot -> {
                    ItemModel im = new ItemModel();
                    UserModel um = new UserModel();
                    List<Uri> images = new ArrayList<>();
                    String id = snapshot.getKey();
                    String title = snapshot.child("title").getValue().toString();
                    String description = snapshot.child("description").getValue().toString();
                    Double cost = Double.valueOf(snapshot.child("price").getValue().toString());
                    um.setId(snapshot.child("owner").getValue().toString());
                    String category = snapshot.child("category").getValue().toString();
                    for (DataSnapshot ds : snapshot.child("picture").getChildren()) {
                        images.add(Uri.parse(ds.getValue().toString()));
                    }
                    im.setItemId(id);
                    im.setItemTitle(title);
                    im.setItemDescription(description);
                    im.setItemPrice(cost);
                    im.setImages(images);
                    im.setOwner(um);
                    im.setItemCategory(category);
                    return im;
                })
                .build();

        adapter = new FirebaseRecyclerAdapter<ItemModel, ItemViewHolder>(options) {
            @Override
            protected void onBindViewHolder(ItemViewHolder holder, int position, ItemModel model) {
                holder.bindView(model);
                holder.setOnClickListener((view, position1) -> {
                    final ItemModel item = model;

//                    viewModel.selectItem(item);

                    Fragment fragment = new ItemDetailFragment();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    fragmentTransaction.replace(R.id.fragment_placeholder, fragment, "tag")
                            .addToBackStack(null)
                            .commit();
//
                });

            }

            @Override
            public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_fragment_item, parent, false);

                return new ItemViewHolder(view, getContext());
            }
        };


    }

}
