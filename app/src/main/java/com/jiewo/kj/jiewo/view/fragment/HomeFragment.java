package com.jiewo.kj.jiewo.view.fragment;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.jiewo.kj.jiewo.R;
import com.jiewo.kj.jiewo.databinding.FragmentHomeBinding;
import com.jiewo.kj.jiewo.util.MapFragment;
import com.jiewo.kj.jiewo.view.activity.MainActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    FragmentHomeBinding binding;
    private GoogleMap mMap;
    MapFragment mapFragment;
    private static final int LOCATION_REQUEST_CODE = 1;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((MainActivity) getActivity()).hideFab();
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        View v = binding.getRoot();

        binding.setView(this);

        mapFragment = new MapFragment(getActivity());

        //TODO https://github.com/arimorty/floatingsearchview


        // Inflate the layout for this fragment
        return v;

    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == MapFragment.MY_PERMISSIONS_REQUEST_LOCATION) {
            mapFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


}
