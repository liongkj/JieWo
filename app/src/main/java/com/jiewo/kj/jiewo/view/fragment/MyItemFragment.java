package com.jiewo.kj.jiewo.view.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jiewo.kj.jiewo.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyItemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyItemFragment extends Fragment {

    public MyItemFragment() {
        // Required empty public constructor
    }


    public static MyItemFragment newInstance() {

        return new MyItemFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_item, container, false);
    }

}
