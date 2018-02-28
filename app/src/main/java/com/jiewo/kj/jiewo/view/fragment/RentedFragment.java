package com.jiewo.kj.jiewo.view.fragment;


import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
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
import com.jiewo.kj.jiewo.databinding.FragmentRentedBinding;
import com.jiewo.kj.jiewo.model.ItemModel;
import com.jiewo.kj.jiewo.model.UserModel;
import com.jiewo.kj.jiewo.util.Callback;
import com.jiewo.kj.jiewo.view.adapter.ItemViewHolder;

import java.util.ArrayList;
import java.util.List;

import static com.jiewo.kj.jiewo.util.Constants.DATABASE_REF;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RentedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RentedFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    FragmentRentedBinding binding;
    RecyclerView recyclerView;
    Query keyQuery;
    private String mParam1;
    private FirebaseRecyclerAdapter<ItemModel, ItemViewHolder> adapter;

    public RentedFragment() {
        // Required empty public constructor
    }

    public static RentedFragment newInstance(String param1) {
        RentedFragment fragment = new RentedFragment();
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
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_request, container, false);
        View view = binding.getRoot();
        binding.setView(this);
        recyclerView = binding.UserItemList;
        buildItemList();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setNestedScrollingEnabled(false);
//        recyclerView.setHasFixedSize(true);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new Callback(0, ItemTouchHelper.LEFT, adapter, getParentFragment().getView());
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);


//        buildItemList();
        recyclerView.setAdapter(adapter);
        return view;
    }

    void buildItemList() {
        Query dataRef = DATABASE_REF.child("Rented");
        keyQuery = DATABASE_REF.child("User/" + mParam1 + "/rented");
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<ItemModel>()
                .setLifecycleOwner(this)
                .setIndexedQuery(keyQuery.getRef(), dataRef.getRef(), snapshot -> {
                    ItemModel im = new ItemModel();
                    UserModel um = new UserModel();
                    List<Uri> images = new ArrayList<>();
                    String item = snapshot.child("Item").getValue().toString();
                    String itemname = snapshot.child("Itemname").getValue().toString();
                    String id = snapshot.getKey();
                    if (snapshot.child("From").exists()) {
                        String requester = snapshot.child("From").getValue().toString();
                        um.setId(requester);
                    }
                    if (snapshot.child("Rating").exists()) {
                        String requester = snapshot.child("Rating").getValue().toString();
                        um.setRating(Double.valueOf(requester));
                    }
                    if (snapshot.child("ItemPic").exists()) {
                        String pic = snapshot.child("ItemPic").getValue().toString();
                        images.add(Uri.parse(pic));
                    }

                    String requesterName = snapshot.child("FromName").getValue().toString();
                    String ownerid = snapshot.child("To").getValue().toString();
                    um.setName(requesterName);
                    um.setId(ownerid);
                    im.setItemId(item);
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
                            .content("User rating: " + model.getOwner().getRating())
                            .positiveText("Yes")
                            .negativeText("No")
                            .show();

                    md.getBuilder().onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            Toast toast = Toast.makeText(getContext(), "Item is rented", Toast.LENGTH_LONG);
                            DATABASE_REF.child("User").child(model.getOwner().getId()).child("rented/" + model.getItemId()).setValue(true);
                            DATABASE_REF.child("User").child(UserModel.getUser().getId()).child("request").child(model.getItemCategory()).setValue(null);
                            DATABASE_REF.child("Request").child(model.getItemCategory()).setValue(null);
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
