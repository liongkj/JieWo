package com.jiewo.kj.jiewo.util;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jiewo.kj.jiewo.model.ItemModel;
import com.jiewo.kj.jiewo.model.UserModel;

import static com.jiewo.kj.jiewo.util.Constants.DATABASE_REF;

/**
 * Created by khaij on 28/02/2018.
 */

public class Callback extends ItemTouchHelper.SimpleCallback {
    View view;
    private FirebaseRecyclerAdapter adapter; // this will be your recycler adapter
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference();

    public Callback(int dragDirs, int swipeDirs, FirebaseRecyclerAdapter adapter, View view) {
        super(dragDirs, swipeDirs);
        this.adapter = adapter;
        this.view = view;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition(); // this is how you can get the position
        ItemModel itemModel = (ItemModel) adapter.getItem(position); // You will have your own class ofcourse.
        final String[] catId = {null};
        // showing snack bar with Undo option
        Snackbar snackbar = Snackbar
                .make(view, itemModel.getItemTitle() + " deleted", Snackbar.LENGTH_LONG);
        snackbar.setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // undo is selected, restore the deleted item
                root.child("User").child(UserModel.getUser().getId()).child("items").child(itemModel.getItemId()).setValue(true);
                root.child("Category").child(catId[0]).child("items").child(itemModel.getItemId()).setValue(true);
            }
        });
        snackbar.setActionTextColor(Color.YELLOW);
        snackbar.show();

        root.child("User").child(UserModel.getUser().getId()).child("items").child(itemModel.getItemId()).setValue(null);
        DATABASE_REF.child("Category").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.child("name").exists()) {
                        if ((ds.child("name").getValue().toString()).equals(itemModel.getItemCategory())) {
                            catId[0] = ds.getKey();
                        }
                    }
                }

                root.child("Category").child(catId[0]).child("items").child(itemModel.getItemId()).setValue(null);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}
