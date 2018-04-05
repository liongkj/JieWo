package com.jiewo.kj.jiewo.view.fragment;


import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.Query;
import com.jiewo.kj.jiewo.R;
import com.jiewo.kj.jiewo.ViewModel.RequestViewModel;
import com.jiewo.kj.jiewo.ViewModel.UserViewModel;
import com.jiewo.kj.jiewo.databinding.FragmentRequestBinding;
import com.jiewo.kj.jiewo.model.ItemModel;
import com.jiewo.kj.jiewo.model.UserModel;
import com.jiewo.kj.jiewo.view.adapter.ItemViewHolder;

import java.util.ArrayList;
import java.util.List;

import static com.jiewo.kj.jiewo.util.Constants.DATABASE_REF;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RequestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RequestFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    UserViewModel userViewModel;
    FragmentRequestBinding binding;
    RecyclerView recyclerView, recyclerView1;
    Query keyQuery;
    private String mParam1;
    private FirebaseRecyclerAdapter<ItemModel, ItemViewHolder> adapter;
    RequestViewModel viewModel;
    private FirebaseRecyclerAdapter<ItemModel, ItemViewHolder> adapter1;

    public RequestFragment() {
        // Required empty public constructor
    }

    public static RequestFragment newInstance(String param1) {
        RequestFragment fragment = new RequestFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
        userViewModel = ViewModelProviders.of(getActivity()).get(UserViewModel.class);
        viewModel = ViewModelProviders.of(getActivity()).get(RequestViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_request, container, false);
        View view = binding.getRoot();
        binding.setView(this);
        recyclerView = binding.UserItemList;
        recyclerView1 = binding.UserRentedList;
        buildItemList();
        buildItemList1();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setNestedScrollingEnabled(false);
//        recyclerView.setHasFixedSize(true);

        recyclerView1.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView1.setNestedScrollingEnabled(false);


//        buildItemList();
        recyclerView.setAdapter(adapter);
        recyclerView1.setAdapter(adapter1);
        return view;
    }

    //pending
    void buildItemList() {
        Query dataRef = DATABASE_REF.child("Request");
        keyQuery = DATABASE_REF.child("User/" + mParam1 + "/request");
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<ItemModel>()
                .setLifecycleOwner(this)
                .setIndexedQuery(keyQuery.getRef(), dataRef.getRef(), snapshot -> {
                    ItemModel im = new ItemModel();
                    UserModel um = new UserModel();
                    List<Uri> images = new ArrayList<>();
                    String itemid = snapshot.child("Item").getValue().toString();
                    String itemname = snapshot.child("Itemname").getValue().toString();
                    String id = snapshot.getKey();

                    if (snapshot.child("Rating").exists()) {
                        String requester = snapshot.child("Rating").getValue().toString();
                        um.setRating(Double.valueOf(requester));
                    }
                    if (snapshot.child("ItemPic").exists()) {
                        String pic = snapshot.child("ItemPic").getValue().toString();
                        images.add(Uri.parse(pic));
                    }

                    String rentername = snapshot.child("Rentername").getValue().toString();
                    String ownerid = snapshot.child("To").getValue().toString();
                    String renter = snapshot.child("From").getValue().toString();
                    um.setId(ownerid);
                    um.setName(rentername);
                    im.setItemDescription(renter); //renter id holder
                    im.setItemId(itemid);
                    im.setItemTitle(itemname);
                    im.setImages(images);
                    im.setOwner(um);
                    im.setItemCategory(id);//request id holder

                    return im;
                })
                .build();

        adapter = new FirebaseRecyclerAdapter<ItemModel, ItemViewHolder>(options) {
            @Override
            protected void onBindViewHolder(ItemViewHolder holder, int position, ItemModel model) {

                holder.bindView(model, true);
                holder.setOnClickListener((view, position1) -> {

                    MaterialDialog md = new MaterialDialog.Builder(getContext())
                            .title("Approve request from " + model.getOwner().getName() + "?")
                            .content("User rating: " + model.getOwner().getRating() + ". Please contact renter to arrange pickup")
                            .positiveText("Yes")
                            .negativeText("No")
                            .neutralText("Later")
                            .show();

                    md.getBuilder().onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            Toast toast = Toast.makeText(getContext(), "Item is rented", Toast.LENGTH_LONG);
                            viewModel.acceptRentRequest(model);
                            toast.show();
                        }
                    });
                    md.getBuilder().onNegative(new MaterialDialog.SingleButtonCallback() { //decline request
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            Toast toast = Toast.makeText(getContext(), "Request is declined", Toast.LENGTH_LONG);
                            viewModel.declineRentRequest(model);
                            toast.show();
                        }
                    });
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

    //rented items
    void buildItemList1() {
        Query dataRef = DATABASE_REF.child("Request");
        keyQuery = DATABASE_REF.child("User/" + mParam1 + "/outgoing");
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<ItemModel>()
                .setLifecycleOwner(this)
                .setIndexedQuery(keyQuery.getRef(), dataRef.getRef(), snapshot -> {
                    ItemModel im = new ItemModel();
                    UserModel um = new UserModel();
                    List<Uri> images = new ArrayList<>();
                    String itemid = snapshot.child("Item").getValue().toString();
                    String itemname = snapshot.child("Itemname").getValue().toString();
                    String id = snapshot.getKey();

                    if (snapshot.child("Rating").exists()) {
                        String requester = snapshot.child("Rating").getValue().toString();
                        um.setRating(Double.valueOf(requester));
                    }
                    if (snapshot.child("ItemPic").exists()) {
                        String pic = snapshot.child("ItemPic").getValue().toString();
                        images.add(Uri.parse(pic));
                    }

                    String rentername = snapshot.child("Rentername").getValue().toString();
                    String ownerid = snapshot.child("To").getValue().toString();
                    String renter = snapshot.child("From").getValue().toString();
                    um.setId(ownerid);
                    um.setName(rentername);
                    im.setItemId(itemid);
                    im.setItemTitle(itemname);
                    im.setImages(images);
                    im.setOwner(um);
                    im.setItemCategory(id);//request id holder

                    return im;
                })
                .build();

        adapter1 = new FirebaseRecyclerAdapter<ItemModel, ItemViewHolder>(options) {
            @Override
            protected void onBindViewHolder(ItemViewHolder holder, int position, ItemModel model) {

                holder.bindView(model, true);
                holder.setOnClickListener((view, position1) -> {

                    MaterialDialog md = new MaterialDialog.Builder(getContext())
                            .title("Contact renter to return items")
                            .content("User rating: " + model.getOwner().getRating())
                            .positiveText("Yes")
                            .neutralText("Later")
                            .show();

                    md.getBuilder().onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            Toast toast = Toast.makeText(getContext(), "Item is rented", Toast.LENGTH_LONG);

                            toast.show();
                        }
                    });
                    md.getBuilder().onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            Toast toast = Toast.makeText(getContext(), "Request is declined", Toast.LENGTH_LONG);


                            toast.show();
                        }
                    });
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
