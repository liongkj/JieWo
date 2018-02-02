package com.jiewo.kj.jiewo.View.Fragments;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.jiewo.kj.jiewo.R;
import com.jiewo.kj.jiewo.ViewModel.RentViewModel;
import com.jiewo.kj.jiewo.databinding.FragmentRentBinding;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import java.util.List;


public class RentFragment extends Fragment {

    RentViewModel viewModel;
    FragmentRentBinding binding;
    ArrayAdapter<String> catAdapter;
    private int current = 0;
    private final int MAX = 6;
    LinearLayout imageButtonParent;
    private String category;

    public RentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(RentViewModel.class);


        LiveData<DataSnapshot> itemCategory = viewModel.getDataSnapshotLiveData();
        final Observer<List<String>> catObserver = new Observer<List<String>>() {
            @Override
            public void onChanged(@Nullable final List<String> categoryList) {
                // Update the UI, in this case, a TextView.
                if (categoryList != null) {
                    catAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, categoryList);
                }
                catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.catSpinner.setAdapter(catAdapter);
                catAdapter.notifyDataSetChanged();
            }
        };
        viewModel.getCategoryList().observe(this, catObserver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//         Inflate the layout for this fragment
        getActivity().setTitle("Rent Item");
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_rent, container, false);
        View view = binding.getRoot();
        binding.setView(this);
        binding.setVm(viewModel);
        imageButtonParent = binding.imageButtonParent;
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

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
