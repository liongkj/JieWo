package com.jiewo.kj.jiewo.view.activity;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuth.AuthStateListener;
import com.google.firebase.auth.FirebaseUser;
import com.jiewo.kj.jiewo.R;
import com.jiewo.kj.jiewo.ViewModel.RentViewModel;
import com.jiewo.kj.jiewo.ViewModel.UserViewModel;
import com.jiewo.kj.jiewo.model.ItemModel;
import com.jiewo.kj.jiewo.model.UserModel;
import com.jiewo.kj.jiewo.view.fragment.CategoryFragment;
import com.jiewo.kj.jiewo.view.fragment.HomeFragment;
import com.jiewo.kj.jiewo.view.fragment.ItemListFragment;
import com.jiewo.kj.jiewo.view.fragment.MyItemFragment;
import com.jiewo.kj.jiewo.view.fragment.RentFragment;
import com.jiewo.kj.jiewo.view.fragment.SettingFragment;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements
        ItemListFragment.OnListFragmentInteractionListener,
        AuthStateListener {

    static public Drawer result = null;
    UserModel user = UserModel.getUser();
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fabAdd)
    FloatingActionButton fab;
    UserViewModel viewModel;
    private AccountHeader headerResult = null;
    private FirebaseAuth.AuthStateListener authListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
//        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Main");

        authListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user == null) {
                // user auth state is changed - user is null
                // launch login activity
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        };

        viewModel = ViewModelProviders.of(this).get(UserViewModel.class);

        viewModel.getUser(user).observe(this, new Observer<UserModel>() {
            @Override
            public void onChanged(@Nullable UserModel userModel) {
                user = userModel;
            }
        });
        //drawer header
        buildHeader(false, savedInstanceState);

        buildDrawer();

        HomeFragment HomeFragment = new HomeFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_placeholder, HomeFragment)
                .addToBackStack(null)
                .commit();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                RentFragment rentFragment = new RentFragment();

                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//                transaction.add(android.R.id.content, rentFragment).addToBackStack(null).commit();
                rentFragment.show(transaction, "tag");


            }
        });
        //TODO https://github.com/konifar/fab-transformation
        RentViewModel rentViewModel = new RentViewModel();


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_placeholder);
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.e("fragment", currentFragment.toString());
                if (!(currentFragment instanceof RentFragment))
                    onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity

        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    public void showFab() {
        fab.show();
    }

    public void hideFab() {
        fab.hide();
    }

    private void buildDrawer() {
        PrimaryDrawerItem home = new PrimaryDrawerItem().withIdentifier(1).withName("Home").withIcon(GoogleMaterial.Icon.gmd_home);
        //PrimaryDrawerItem rentItem = new PrimaryDrawerItem().withIdentifier(2).withName("Rent My Item").withIcon(GoogleMaterial.Icon.gmd_add);
        PrimaryDrawerItem search = new PrimaryDrawerItem().withIdentifier(3).withName("Category").withIcon(GoogleMaterial.Icon.gmd_dashboard);
        //PrimaryDrawerItem nearby = new PrimaryDrawerItem().withIdentifier(4).withName("What's Nearby").withIcon(GoogleMaterial.Icon.gmd_location_on);
        SecondaryDrawerItem settings = new SecondaryDrawerItem().withIdentifier(5).withName("Settings").withIcon(GoogleMaterial.Icon.gmd_settings);
        SecondaryDrawerItem items = new SecondaryDrawerItem().withIdentifier(6).withName("My Items").withIcon(GoogleMaterial.Icon.gmd_check_box);


        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .withActionBarDrawerToggleAnimated(true)
                .addDrawerItems(
                        home,
                        //rentItem,
                        search,
                        // nearby,
                        new DividerDrawerItem(),
                        items,
                        settings
                )
                .withOnDrawerItemClickListener((view, position, drawerItem) -> {
                    // do something with the clicked item :D
                    selectDrawerItem((int) drawerItem.getIdentifier());
                    return false;
                })
                .withOnDrawerNavigationListener(clickedView -> {
                    //this method is only called if the Arrow icon is shown. The hamburger is automatically managed by the MaterialDrawer
                    //if the back arrow is shown. close the activity
//                        RentFragment.class.this.finish();
                    getSupportFragmentManager().popBackStack();
                    return true;
                })
                .build();
    }

    private void selectDrawerItem(int identifier) {
        Fragment fragment = null;
        Class fragmentClass;
        switch (identifier) {
            case 1:
                fragmentClass = HomeFragment.class;
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                result.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);
                break;
//            case 2:
//                result.getActionBarDrawerToggle().setDrawerIndicatorEnabled(false);
//                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//                fragmentClass = RentFragment.class;
//                break;
            case 3:
                fragmentClass = CategoryFragment.class;
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                result.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);
                break;
            case 5:
                fragmentClass = SettingFragment.class;
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                result.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);
                break;
            case 6:
                fragmentClass = MyItemFragment.class;
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                result.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);
                break;
//            0
            default:
                fragmentClass = HomeFragment.class;
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                result.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);

                break;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_placeholder, fragment)
                .addToBackStack(null)
                .commit();


    }

    private void buildHeader(boolean compact, Bundle savedInstanceState) {
        // Create the AccountHeader
        ProfileDrawerItem profile = new ProfileDrawerItem();
        profile.withName(user.getName()).withEmail(user.getEmail()).withIcon(user.getPhotoURI());
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .withCompactStyle(compact)
                .addProfiles(
                        profile
                        //don't ask but google uses 14dp for the add account icon in gmail but 20dp for the normal icons (like manage account)
                )

                .withSavedInstance(savedInstanceState)
                .build();
    }

    @Override
    public void onListFragmentInteraction(ItemModel item) {

    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

    }
}
