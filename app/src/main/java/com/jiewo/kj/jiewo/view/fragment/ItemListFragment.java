package com.jiewo.kj.jiewo.view.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.LocationCallback;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.jiewo.kj.jiewo.R;
import com.jiewo.kj.jiewo.ViewModel.CategoryViewModel;
import com.jiewo.kj.jiewo.databinding.FragmentItemListBinding;
import com.jiewo.kj.jiewo.model.ItemModel;
import com.jiewo.kj.jiewo.model.UserModel;
import com.jiewo.kj.jiewo.view.activity.MainActivity;
import com.jiewo.kj.jiewo.view.adapter.ItemViewHolder;

import java.util.ArrayList;
import java.util.List;

import static com.jiewo.kj.jiewo.util.Constants.DATABASE_REF;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ItemListFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    FragmentItemListBinding binding;
    CategoryViewModel viewModel;
    FirebaseRecyclerAdapter adapter;
    RecyclerView recyclerView;
    Query keyQuery;
    List<String> data =new ArrayList<>();
    // TODO: Customize parameters
    private int mColumnCount = 2;
    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ItemListFragment newInstance(int columnCount) {
        ItemListFragment fragment = new ItemListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(CategoryViewModel.class);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Set the adapter
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_item_list, container, false);
        View view = binding.getRoot();
        recyclerView = binding.CategoryItemList;

        ((MainActivity) getActivity()).hideFab();
        MainActivity.result.getActionBarDrawerToggle().setDrawerIndicatorEnabled(false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setNestedScrollingEnabled(false);
//        recyclerView.setHasFixedSize(true);

        viewModel.getCategoryId().observe(this, s -> {
            getActivity().setTitle(s.getName());
            keyQuery = DATABASE_REF.child("Category/" + s.getId() + "/items");
            buildItemList();
            recyclerView.setAdapter(adapter);

        });


        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    void buildItemList() {
        Query dataRef = DATABASE_REF.child("Item");

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
                    GeoFire geoFire = new GeoFire(DATABASE_REF.child("Item-Location").getRef());
                    geoFire.getLocation(model.getItemId(), new LocationCallback() {
                        @Override
                        public void onLocationResult(String key, GeoLocation location) {

                            if (location != null) {
                                Location loc = new Location("provider");
                                loc.setLatitude(location.latitude);
                                loc.setLongitude(location.longitude);
                                viewModel.setItemLocation(loc);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e("item", "retrieve item location failed");
                        }
                    });

                    viewModel.selectItem(model);

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

                return new ItemViewHolder(view,getContext());
            }
        };


    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(ItemModel item);
    }
}
