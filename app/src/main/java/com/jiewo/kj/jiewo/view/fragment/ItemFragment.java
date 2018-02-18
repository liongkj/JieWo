package com.jiewo.kj.jiewo.view.fragment;


import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jiewo.kj.jiewo.R;
import com.jiewo.kj.jiewo.ViewModel.UserViewModel;
import com.jiewo.kj.jiewo.databinding.FragmentItemBinding;
import com.jiewo.kj.jiewo.model.UserModel;
import com.jiewo.kj.jiewo.view.activity.MainActivity;
import com.jiewo.kj.jiewo.view.adapter.TabFragmentAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ItemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ItemFragment extends Fragment {

    FragmentItemBinding binding;
    UserViewModel viewModel;
    ActionBarDrawerToggle mToggle = MainActivity.result.getActionBarDrawerToggle();
    UserModel user = UserModel.getUser();
//    AppBarLayout appBarLayout = getActivity().findViewById(R.id.app_bar);

    private int mReplace = 0;
    private List<Fragment> mFragments;
    private List<String> mFragmentTitles;
    String userid;

    public ItemFragment() {
        // Required empty public constructor
    }


    public static ItemFragment newInstance() {

        return new ItemFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_item, container, false);
        binding.setView(this);
        View view = binding.getRoot();

        getActivity().setTitle("My Items");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        mToggle.setDrawerIndicatorEnabled(true);
        ((MainActivity) getActivity()).showFab();
//        appBarLayout.setElevation(0);

        viewModel = ViewModelProviders.of(getActivity()).get(UserViewModel.class);
        userid = user.getId();
        initViewPager();

        return view;
    }

    private void initViewPager() {

        if (mFragments == null) {
            mFragments = new ArrayList<>();
        }

        if (mFragmentTitles == null) {
            mFragmentTitles = new ArrayList<>();
        }
        //ViewPager findFragmentByTag，tag= "android:switcher:" + R.id.viewpager + position
        ListingFragment listingFragment = (ListingFragment) getActivity().getSupportFragmentManager()
                .findFragmentByTag("android:switcher:" + R.id.item_pager + ":" + 0);
        WishlistFragment wishlistFragment = (WishlistFragment) getActivity().getSupportFragmentManager()
                .findFragmentByTag("android:switcher:" + R.id.item_pager + ":" + 1);
        if (listingFragment == null) {
            listingFragment = ListingFragment.newInstance(userid);
            Log.e("fragment", "new instance");
        }
        if (wishlistFragment == null) {
            wishlistFragment = WishlistFragment.newInstance(userid);
        }
        mFragments.add(listingFragment);
        mFragments.add(wishlistFragment);
        mFragmentTitles.add("Listing");
        mFragmentTitles.add("Favourite Items");
        //Setup ViewPager
        TabFragmentAdapter adapter = new TabFragmentAdapter(getChildFragmentManager(), mFragments, mFragmentTitles);
        binding.itemPager.setAdapter(adapter);
        binding.tabLayout.setupWithViewPager(binding.itemPager);

        binding.itemPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mReplace = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.mFragments = null;
        this.mFragmentTitles = null;
    }

}
