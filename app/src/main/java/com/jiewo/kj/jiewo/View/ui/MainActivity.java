package com.jiewo.kj.jiewo.View.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jiewo.kj.jiewo.Model.UserModel;
import com.jiewo.kj.jiewo.R;
import com.jiewo.kj.jiewo.View.ui.Fragments.HomeFragment;
import com.jiewo.kj.jiewo.View.ui.Fragments.RentItemFragment;
import com.jiewo.kj.jiewo.View.ui.Fragments.SettingFragment;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;


public class MainActivity extends AppCompatActivity {


    TextView txtUsername;
    ImageView imageView;
    UserModel user = UserModel.getUser();
    private Toolbar toolbar;

    private Drawer result = null;
    private AccountHeader headerResult = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Main");

        //drawer header
        buildHeader(false, savedInstanceState);

        buildDrawer();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
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

    private void buildDrawer() {
        PrimaryDrawerItem home = new PrimaryDrawerItem().withIdentifier(1).withName("Home").withIcon(GoogleMaterial.Icon.gmd_home);
        PrimaryDrawerItem search = new PrimaryDrawerItem().withIdentifier(3).withName("Find Items").withIcon(GoogleMaterial.Icon.gmd_search);
        PrimaryDrawerItem rentItem = new PrimaryDrawerItem().withIdentifier(2).withName("Rent My Item").withIcon(GoogleMaterial.Icon.gmd_add);
        PrimaryDrawerItem nearby = new PrimaryDrawerItem().withIdentifier(4).withName("What's Nearby").withIcon(GoogleMaterial.Icon.gmd_location_city);
        SecondaryDrawerItem settings = new SecondaryDrawerItem().withIdentifier(5).withName("Settings").withIcon(GoogleMaterial.Icon.gmd_settings);
        SecondaryDrawerItem items = new SecondaryDrawerItem().withIdentifier(6).withName("My Items").withIcon(GoogleMaterial.Icon.gmd_check_box);


        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        home,
                        search,
                        rentItem,
                        nearby,
                        new DividerDrawerItem(),
                        items,
                        settings
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        selectDrawerItem((int) drawerItem.getIdentifier());

                        return false;
                    }
                })
                .build();


    }

    private void selectDrawerItem(int identifier) {
        Fragment fragment = null;
        Class fragmentClass = null;
        switch (identifier) {
            case 1:
                fragmentClass = HomeFragment.class;
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                result.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);
                break;
            case 2:
                fragmentClass = RentItemFragment.class;
                result.getActionBarDrawerToggle().setDrawerIndicatorEnabled(false);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                break;
            case 3:
                fragmentClass = HomeFragment.class;
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                fragmentClass = HomeFragment.class;
                break;
            case 5:
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                result.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);
                fragmentClass = SettingFragment.class;
                break;
            default:
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                result.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);
                fragmentClass = HomeFragment.class;
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
}
