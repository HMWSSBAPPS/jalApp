package com.hmwssb.jalapp;

import java.util.Vector;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends Activity implements OnMapReadyCallback {

    // Google Map
    private GoogleMap googleMap;
    Marker marker;
    Vector<String> data_Vec = new Vector<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_page);
        MapFragment mapFragment = (MapFragment)
                getFragmentManager()
                        .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        String str = getIntent().getStringExtra("DATA");
        String split[] = str.split("\\@");
        for (int i = 0; i < split.length; i++) {
            data_Vec.addElement(split[i]);
        }


    }

    /**
     * function to load map. If map is not created it will create it for you
     */
//	private void initilizeMap() {
//		if (googleMap == null) {
//			googleMap = ((MapFragment) getFragmentManager().findFragmentById(
//					R.id.map)).getMapAsync(this);
//			LatLng lg = null;
//			for (int i = 0; i < data_Vec.size(); i++) {
//				String split[] = data_Vec.elementAt(i).toString().split("\\^");
//
//				lg = new LatLng(Double.parseDouble(split[3]),
//						Double.parseDouble(split[4]));
//				if (split[2].trim().equalsIgnoreCase("1")) {
//					googleMap
//							.addMarker(new MarkerOptions()
//									.position(lg)
//									.title(split[0])
//									.icon(BitmapDescriptorFactory
//											.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
//				} else if (split[2].trim().equalsIgnoreCase("0")) {
//					googleMap
//							.addMarker(new MarkerOptions()
//									.position(lg)
//									.title(split[0])
//									.icon(BitmapDescriptorFactory
//											.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
//				} else if (split[2].trim().equalsIgnoreCase("2")) {
//					googleMap
//							.addMarker(new MarkerOptions()
//									.position(lg)
//									.title(split[0])
//									.icon(BitmapDescriptorFactory
//											.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
//				}
//
//			}
//
//			googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lg, 4));
//			googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
//
//			// check if map is created successfully or not
//			if (googleMap == null) {
//				Toast.makeText(getApplicationContext(),
//						"Sorry! unable to create maps", Toast.LENGTH_SHORT)
//						.show();
//			}
//		}
//	}
    @Override
    protected void onResume() {
        super.onResume();
//		initilizeMap();
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        finish();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        LatLng lg = null;
        for (int i = 0; i < data_Vec.size(); i++) {
            String split[] = data_Vec.elementAt(i).toString().split("\\^");

            lg = new LatLng(Double.parseDouble(split[3]),
                    Double.parseDouble(split[4]));
            if (split[2].trim().equalsIgnoreCase("1")) {
                googleMap
                        .addMarker(new MarkerOptions()
                                .position(lg)
                                .title(split[0])
                                .icon(BitmapDescriptorFactory
                                        .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            } else if (split[2].trim().equalsIgnoreCase("0")) {
                googleMap
                        .addMarker(new MarkerOptions()
                                .position(lg)
                                .title(split[0])
                                .icon(BitmapDescriptorFactory
                                        .defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            } else if (split[2].trim().equalsIgnoreCase("2")) {
                googleMap
                        .addMarker(new MarkerOptions()
                                .position(lg)
                                .title(split[0])
                                .icon(BitmapDescriptorFactory
                                        .defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
            }

        }

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lg, 4));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);

        // check if map is created successfully or not
        if (googleMap == null) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                    .show();
        }
    }

}
