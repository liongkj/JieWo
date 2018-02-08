package com.jiewo.kj.jiewo.view.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Query;
import com.jiewo.kj.jiewo.R;
import com.jiewo.kj.jiewo.ViewModel.HomeViewModel;
import com.jiewo.kj.jiewo.model.CategoryModel;
import com.jiewo.kj.jiewo.model.ItemModel;
import com.jiewo.kj.jiewo.util.CategoryViewHolder;
import com.jiewo.kj.jiewo.util.ItemViewHolder;

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
    // TODO: Customize parameters
    private int mColumnCount = 2;
    private OnListFragmentInteractionListener mListener;
    HomeViewModel viewModel;
    List<ItemModel> itemList;
    FirebaseRecyclerAdapter adapter;
    List<ItemModel> items;
    RecyclerView recyclerView;

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
        viewModel = ViewModelProviders.of(getActivity()).get(HomeViewModel.class);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            buildItemList();

        }
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

    void buildItemList(){
        Query query= DATABASE_REF.child("Category/-L4o5tBb_CcMxT7TEf5e/items");
        SnapshotParser itemParser =  new SnapshotParser<ItemModel>() {
            ItemModel cm;
            List<String> items;
            @Override
            public ItemModel parseSnapshot(DataSnapshot snapshot) {
                items = new ArrayList<>();
                String id = snapshot.getValue().toString();
//                String name = snapshot.child("name").getValue().toString();
//                for (DataSnapshot ds : snapshot.child("items").getChildren()) {
//                    items.add(ds.getValue().toString());
//                }
//                int count = items.size();

                Log.e("item", id);
                return cm;
            }
        };

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions
                .Builder<ItemModel>()
                .setQuery(query, itemParser)
                .build();

        adapter = new FirebaseRecyclerAdapter<ItemModel, ItemViewHolder>(options) {
            @Override
            protected void onBindViewHolder(ItemViewHolder holder, int position, ItemModel model) {
                holder.bindView(model);
                holder.setOnClickListener((view, position1) -> {
                    final String categoryId = model.getItemTitle();
                    Log.e("catid",categoryId);
//                    Fragment fragment = new ItemListFragment();
//                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                    fragmentTransaction.replace(R.id.fragment_placeholder, fragment, "tag")
//                            .addToBackStack(null)
//                            .commit();
//                    viewModel.select(categoryId);
//
                });

            }

            @Override
            public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.fragment_item, parent, false);

                return new ItemViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
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
