package com.jiewo.kj.jiewo.view.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.jiewo.kj.jiewo.R;
import com.jiewo.kj.jiewo.ViewModel.CategoryViewModel;
import com.jiewo.kj.jiewo.ViewModel.RequestViewModel;
import com.jiewo.kj.jiewo.ViewModel.UserViewModel;
import com.jiewo.kj.jiewo.databinding.FragmentItemDetailBinding;
import com.jiewo.kj.jiewo.model.ItemModel;
import com.jiewo.kj.jiewo.model.UserModel;
import com.jiewo.kj.jiewo.view.activity.MainActivity;

import java.util.ArrayList;
import java.util.List;

import ss.com.bannerslider.banners.Banner;
import ss.com.bannerslider.banners.RemoteBanner;
import ss.com.bannerslider.views.BannerSlider;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link ItemDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ItemDetailFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    FragmentItemDetailBinding binding;
    CategoryViewModel viewModel;
    UserViewModel viewModelUser;
    RequestViewModel viewModelRequest;
    ActionBarDrawerToggle mToggle = MainActivity.result.getActionBarDrawerToggle();
    FloatingActionButton fab;
    UserModel user = UserModel.getUser();


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public ItemDetailFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ItemDetailFragment newInstance(String param1, String param2) {
        ItemDetailFragment fragment = new ItemDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        viewModel = ViewModelProviders.of(getActivity()).get(CategoryViewModel.class);
        viewModelUser = ViewModelProviders.of(getActivity()).get(UserViewModel.class);
        viewModelRequest = ViewModelProviders.of(getActivity()).get(RequestViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_item_detail, container, false);
        fab = binding.fabFav;
        View view = binding.getRoot();
        binding.setView(this);
        binding.setLogged(user);
        binding.setViewmodel(viewModelUser);


        ((MainActivity) getActivity()).hideFab();
        MainActivity.result.getActionBarDrawerToggle().setDrawerIndicatorEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        viewModel.getItemId().observe(this, (ItemModel i) -> {
            binding.setItem(i);
            getActivity().setTitle(i.getItemTitle());
            buildSlider(i.getItemImages());

            viewModelUser.getSeller(i.getOwner()).observe(this, u -> {
                binding.setSeller(u);
            });

        });

        viewModel.getDistance().observe(this, distance -> {
            String text = "Locating...";
            if (distance != null) {
                text = String.format("%.2f%s", distance / 1000, " KM");
            }
            binding.layoutItemDetail.distance.setText(text);
        });


        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    public void onClickCall(View v, String number) {
        if (number != null) {
            switch (v.getId()) {
                case R.id.phonecall:
                case R.id.phonecall1:
                    //phonecall
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", number, null));
                    startActivity(intent);
                    break;
                case R.id.whatsappmsg:
                    String uri = "https://api.whatsapp.com/send?phone=" + number;
                    try {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        startActivity(browserIntent);
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Error/n" + e.toString(), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.phonemsg:
                    //phone msg
                    Intent intentsms = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", number, null));
                    intentsms.putExtra("sms_body", "I am interested in your item.");
                    startActivity(intentsms);

            }
        } else {
            Toast.makeText(getContext(), "This user did not leave any contact details", Toast.LENGTH_SHORT).show();
        }
    }

    public void makeRequest(ItemModel item, UserModel owner) {

        MaterialDialog md = new MaterialDialog.Builder(getContext())
                .title("Send Request")
                .content("Send a rent request for this item?")
                .positiveText("Send")
                .show();

        md.getBuilder().onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                Toast.makeText(getContext(), "Posting...", Toast.LENGTH_LONG).show();
                if (viewModelRequest.request(item, owner)) {
                    Toast.makeText(getContext(), "Request sent", Toast.LENGTH_SHORT).show();
                } else {
                    Toast toast = Toast.makeText(getContext(), "This is your item", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
    }


    void buildSlider(List<Uri> itemImages) {

        BannerSlider bannerSlider = binding.itemSlider1;
        bannerSlider.setLoopSlides(false);
        bannerSlider.setMustAnimateIndicators(true);

        List<Banner> banners = new ArrayList<>();
        //add banner using image url
        for (Uri uri : itemImages) {
            banners.add(new RemoteBanner(uri.toString()));
        }
        bannerSlider.setBanners(banners);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //viewModel.getDistance().removeObservers(this);
    }
}
