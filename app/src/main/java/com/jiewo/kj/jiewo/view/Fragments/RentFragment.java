package com.jiewo.kj.jiewo.view.Fragments;


import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jiewo.kj.jiewo.R;
import com.jiewo.kj.jiewo.ViewModel.RentViewModel;
import com.jiewo.kj.jiewo.databinding.FragmentRentBinding;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;


public class RentFragment extends DialogFragment {

    RentViewModel viewModel;
    FragmentRentBinding binding;
    ArrayAdapter<String> catAdapter;
    private int current = 0;
    private final int MAX = 6;
    LinearLayout imageButtonParent;


    public RentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(RentViewModel.class);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AppTheme);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {

        super.onStart();
        Dialog dialog = getDialog();

        if (dialog != null) {

            int width = ViewGroup.LayoutParams.MATCH_PARENT;

            int height = ViewGroup.LayoutParams.MATCH_PARENT;

            dialog.getWindow().setLayout(width, height);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//         Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_rent, container, false);
        View view = binding.getRoot();
        Toolbar toolbar = view.findViewById(R.id.toolbarDialog);
        toolbar.setTitle("Rent Item");

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(android.R.drawable.ic_menu_close_clear_cancel);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();

            }
        });

        //((MainActivity) getActivity()).hideFab();

        binding.setView(this);
        binding.setVm(viewModel);
        imageButtonParent = binding.imageButtonParent;

        catAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, viewModel.getCategoryList());
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.catSpinner.setAdapter(catAdapter);


        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.ticker) {
            //validateForm();
            return true;
        } else if (id == android.R.id.home) {
            // handle close button click here
            //dismiss(); // problem is with this call
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClickRefresh(View view) {
        Log.e("onclick", "onclick pressed");
        catAdapter.notifyDataSetChanged();


    }


    public void onClickAddImage(View view) {

        if (current < MAX) {

            generateImageButton();
            Log.e("current", String.valueOf(current) + viewModel.imageNo.get());
            if (current == 5) binding.setIsVisible(true);
            viewModel.imageNo.set(++current);
        } else {
            Toast toast = Toast.makeText(getContext(), "Maximum number of picture", Toast.LENGTH_LONG);
            toast.show();
            //return true;
        }
        ///return  false;
    }

    public void generateImageButton() {
        View view = getLayoutInflater().inflate(R.layout.layout_imagepicker, imageButtonParent, false);
        AppCompatImageButton imageButton = (AppCompatImageButton) view.findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickImageDialog.build(new PickSetup())
                        .setOnPickResult(new IPickResult() {
                            @Override
                            public void onPickResult(PickResult r) {
                                Bitmap bitmap = ThumbnailUtils.extractThumbnail(r.getBitmap(), 100, 100);
                                imageButton.setImageBitmap(bitmap);
                            }
                        }).show(getActivity().getSupportFragmentManager());
            }
        });
        imageButtonParent.addView(imageButton);
    }

    public void onClickSubmit(View view) {
        Log.e("asd", binding.catSpinner.getText().toString());
//        MaterialDialog md = new MaterialDialog.Builder(getContext())
//                .title("Submit Item?")
//                .content("By clicking submit, I confirm I've read and accepted the Terms and Condition?")
//                .positiveText("SUBMIT")
//                .show();
//
//        md.getBuilder().onPositive(new MaterialDialog.SingleButtonCallback() {
//            @Override
//            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                //submitItem();
//            }
//        });
    }


}
