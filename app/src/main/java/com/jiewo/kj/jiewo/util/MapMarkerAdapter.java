package com.jiewo.kj.jiewo.util;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.jiewo.kj.jiewo.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * Created by khaij on 14/02/2018.
 */

public class MapMarkerAdapter implements GoogleMap.InfoWindowAdapter {

    Context context;


    public MapMarkerAdapter(Context context) {

        this.context = context;

    }

    @Override
    public View getInfoWindow(Marker marker) {
        View view = ((Activity) context).getLayoutInflater()
                .inflate(R.layout.layout_infowindow, null);

        ImageView img = view.findViewById(R.id.pic);
        TextView name = view.findViewById(R.id.name);
        TextView distance = view.findViewById(R.id.distance);


        name.setText(marker.getTitle());
        distance.setText(marker.getSnippet());
        Picasso.with(context)
                .load(Uri.parse(marker.getTag().toString()))
                .placeholder(R.drawable.empty_img)
                .resize(200, 200)
                .into(img, new MarkerCallback(marker));

        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {

        return null;
    }

    private class MarkerCallback implements Callback {
        Marker marker = null;

        public MarkerCallback(Marker marker) {
            this.marker = marker;
        }

        @Override
        public void onSuccess() {
            if (marker != null && marker.isInfoWindowShown()) {
                marker.hideInfoWindow();

                marker.showInfoWindow();
            }

        }

        @Override
        public void onError() {

        }
    }
}
