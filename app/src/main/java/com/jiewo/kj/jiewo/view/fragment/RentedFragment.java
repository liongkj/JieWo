package com.jiewo.kj.jiewo.view.fragment;


import android.arch.lifecycle.ViewModelProviders;
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
import com.jiewo.kj.jiewo.ViewModel.RequestViewModel;
import com.jiewo.kj.jiewo.ViewModel.UserViewModel;
import com.jiewo.kj.jiewo.databinding.FragmentRentedBinding;
import com.jiewo.kj.jiewo.model.ItemModel;
import com.jiewo.kj.jiewo.model.UserModel;
import com.jiewo.kj.jiewo.util.Callback;
import com.jiewo.kj.jiewo.view.adapter.ItemViewHolder;
import com.stepstone.apprating.AppRatingDialog;

import java.util.ArrayList;
import java.util.Arrays;
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
    RecyclerView recyclerView, recyclerView1;
    Query keyQuery;
    RequestViewModel viewModel;
    UserViewModel userViewModel;
    private String mParam1, mParam2;
    private FirebaseRecyclerAdapter<ItemModel, ItemViewHolder> adapter, adapter1;

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

    public void test(View view) {
        showDialog();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_rented, container, false);
        View view = binding.getRoot();
        binding.setView(this);
        viewModel = ViewModelProviders.of(getActivity()).get(RequestViewModel.class);
        userViewModel = ViewModelProviders.of(getActivity()).get(UserViewModel.class);

        buildItemList();
        buildItemList1();

        recyclerView = binding.UserItemList;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setNestedScrollingEnabled(false);


        recyclerView1 = binding.UserReturnList;
        recyclerView1.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView1.setNestedScrollingEnabled(false);

//        recyclerView.setHasFixedSize(true);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new Callback(0, ItemTouchHelper.LEFT, adapter, getParentFragment().getView());
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);


        recyclerView.setAdapter(adapter);
        recyclerView1.setAdapter(adapter1);

        return view;
    }

    //to return
    void buildItemList() {
        Query dataRef = DATABASE_REF.child("Request");
        keyQuery = DATABASE_REF.child("User/" + mParam1 + "/rented");
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
                            .title("Return item?")
                            .content("Click YES if you returned the item. A confirmation will be sent to the owner")
                            .positiveText("Yes")
                            .negativeText("Dismiss")
                            .show();

                    md.getBuilder().onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            Toast toast = Toast.makeText(getContext(), "Item returned", Toast.LENGTH_LONG);
                            viewModel.returnRequest(model);
                            showDialog();
                            userViewModel.setRateTarget(model.getItemDescription());
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


    //to receive
    void buildItemList1() {
        Query dataRef = DATABASE_REF.child("Request");
        keyQuery = DATABASE_REF.child("User/" + mParam1 + "/receive");
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

        adapter1 = new FirebaseRecyclerAdapter<ItemModel, ItemViewHolder>(options) {
            @Override
            protected void onBindViewHolder(ItemViewHolder holder, int position, ItemModel model) {

                holder.bindView(model, true);
                holder.setOnClickListener((view, position1) -> {

                    MaterialDialog md = new MaterialDialog.Builder(getContext())
                            .title("Confirm item returned?")
                            .content("Only press CONFIRM if you collected your item.")
                            .positiveText("Confirm")
                            .negativeText("Dismiss")
                            .show();

                    md.getBuilder().onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            Toast toast = Toast.makeText(getContext(), "Item received", Toast.LENGTH_LONG);
                            viewModel.acceptReturnRequest(model);
                            showDialog();
                            userViewModel.setRateTarget(model.getOwner().getId());
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


    private void showDialog() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
                .setNoteDescriptions(Arrays.asList("Very Bad", "Not good", "Quite ok", "Very Good", "Excellent !!!"))
                .setDefaultRating(2)
                .setTitle("Rate this JieWo experience")
                .setDescription("Please rate and give feedback for the renting experience")
                .setStarColor(R.color.md_yellow_500)
                .setNoteDescriptionTextColor(R.color.md_white_1000)
                .setTitleTextColor(R.color.md_white_1000)
                .setDescriptionTextColor(R.color.md_white_1000)
                .setHint("Please write your comment here ...")
                .setHintTextColor(R.color.md_white_1000)
                .setCommentTextColor(R.color.md_black_1000)
                .setCommentBackgroundColor(R.color.md_white_1000)
                .setWindowAnimation(R.style.MyDialogFadeAnimation)
                .create(getActivity())
                .show();
    }


}
