package com.jiewo.kj.jiewo.View.Fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jiewo.kj.jiewo.Model.UserModel;
import com.jiewo.kj.jiewo.R;
import com.jiewo.kj.jiewo.Util.Constants;
import com.jiewo.kj.jiewo.ViewModel.ItemViewModel;
import com.jiewo.kj.jiewo.ViewModel.UserViewModel;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnFocusChange;


public class RentItemFragment extends Fragment {
    @BindView(R.id.imageButtonParent)
    LinearLayout linearLayout;
    @BindView(R.id.category_spinner)
    MaterialBetterSpinner catSpinner;
    @BindView(R.id.location_spinner)
    MaterialBetterSpinner locSpinner;
    @BindView(R.id.txtTitle)
    MaterialEditText txtTitle;
    @BindView(R.id.txtDescription)
    MaterialEditText txtDescription;
    @BindView(R.id.txtPrice)
    MaterialEditText txtPrice;

    MenuItem btndone;

    boolean isValid = true;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Constants.ITEM_USER);
    UserModel user = UserModel.getUser();
    private UserViewModel userViewModel;

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    public RentItemFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_rent_item, container, false);
        ButterKnife.bind(this, view);
        getActivity().setTitle("Rent Item");


        //set image uploader
        for (int i = 0; i < 6; i++) {
            final ImageButton ib = (ImageButton) linearLayout.getChildAt(i);
            ib.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PickImageDialog.build(new PickSetup())
                            .setOnPickResult(new IPickResult() {
                                @Override
                                public void onPickResult(PickResult r) {
                                    Bitmap bitmap = ThumbnailUtils.extractThumbnail(r.getBitmap(), 100, 100);
                                    ib.setImageBitmap(bitmap);
                                }
                            }).show(getActivity().getSupportFragmentManager());
                }
            });
        }

        ArrayAdapter<CharSequence> locAdapter = ArrayAdapter.createFromResource(getContext(), R.array.location_array, android.R.layout.simple_spinner_dropdown_item);
        locAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        locSpinner.setAdapter(locAdapter);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        ItemViewModel itemViewModel = ViewModelProviders.of(this).get(ItemViewModel.class);
//        itemViewModel.init();
        itemViewModel.getCategoryList().observe(this, categoryList -> {
            ArrayAdapter<String> catAdapter =
                    new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, categoryList);
            catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            catSpinner.setAdapter(catAdapter);
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu, menu);
        btndone = menu.findItem(R.id.button_done);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.button_done:
                // if (isValid()){
                confirmSubmit();
                break;
            // }
            default:
                return super.onOptionsItemSelected(item);
        }

        return false;
    }

    @OnFocusChange(R.id.txtPrice)
    void clear(boolean focus) {
        if (focus) {
            txtPrice.setText("");
        } else {
            isFree();
        }
    }


    public boolean isValid() {

        Log.e("validation", "run");
        if (txtPrice.getText().toString().isEmpty()) {
            txtPrice.setText("0.0");
        }

        if (!txtTitle.isCharactersCountValid()) {
            txtTitle.setError("Minimum 2 characters");
            isValid = false;
        }
        if (!txtDescription.isCharactersCountValid()) {
            txtDescription.setError("Item Description is Required");
            isValid = false;
        }
        return isValid;
    }

    private void isFree() {
        if (txtPrice.getText().toString().isEmpty() || Double.compare(Double.parseDouble(txtPrice.getText().toString()), 0.0) == 0) {
            txtPrice.setText("0");
            MaterialDialog md = new MaterialDialog.Builder(getContext())
                    .title("Confirm?")
                    .content("You are renting this item for free. Are you sure?")
                    .positiveText("Ok")
                    .show();

            md.getBuilder().onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    Log.e("yes", "d");

                }
            })
            ;
        }

    }

    private void confirmSubmit() {

        MaterialDialog md = new MaterialDialog.Builder(getContext())
                .title("Submit Item?")
                .content("By clicking submit, I confirm I've read and accepted the Terms and Condition?")
                .positiveText("SUBMIT")
                .show();

        md.getBuilder().onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                submitItem();
            }
        });
    }

    private void submitItem() {
        final String title = txtTitle.getText().toString();
        final String des = txtDescription.getText().toString();
        final Double price = Double.valueOf(txtPrice.getText().toString());
        final String category = catSpinner.getText().toString();
        Toast.makeText(getContext(), "Posting...", Toast.LENGTH_SHORT).show();

        //writeNewItem(user.getId(), user.getName(), title, des, price, category);
    }

    private void writeNewItem(String userId, String username, String title, String des, Double price, String category) {
        //Log.e("validation", "validation done");
        // String key = mDatabase.child("items").push().getKey();
        //ItemModel item = new ItemModel(userId, username, title, des, price, category);
        //Map<String, Object> itemValues = item.toMap();

//        Map<String, Object> childUpdates = new HashMap<>();
//        childUpdates.put("/items/" + key, itemValues);
//        childUpdates.put("/user-items/" + userId + "/" + key, itemValues);
//
//        mDatabase.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
//            @Override
//            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//                Snackbar bar = Snackbar.make(getActivity().findViewById(android.R.id.content), "Item added.", Snackbar.LENGTH_LONG)
//                        .setAction("Dismiss", new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                // Handle user action
//                            }
//                        });
//                bar.show();
//            }
//
//        });
//
//        onPostSubmit();

    }

    void onPostSubmit() {
        Log.e("firebase", "success");
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        Class fragmentClass = HomeFragment.class;
        Fragment fragment = null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();

        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }


        fragmentManager.beginTransaction()
                .replace(R.id.fragment_placeholder, fragment)
                .commit();
    }

}



