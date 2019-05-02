package com.hmwssb.jalapp;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class LocationAddress {
    private static final String TAG = "LocationAddress";

    public static void getAddressFromLocation(final double latitude,
                                              final double longitude, final Context context, final Handler handler) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                System.out.println("1");
                String result = "";
                try {
                    System.out.println("2");
                    List<Address> addressList = geocoder.getFromLocation(
                            latitude, longitude, 1);
                    System.out.println("3...." + addressList.size());
                    if (addressList != null && addressList.size() > 0) {
                        Address address = addressList.get(0);
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                            sb.append(address.getAddressLine(i)).append(",");
                        }
                        // sb.append(address.get).append(",");
                        // sb.append(address.getLocality()).append(",");
                        // sb.append(address.getPostalCode()).append(",");
                        // sb.append(address.getCountryName());
                        // System.out.println("location......" + sb.toString());
                        // result = sb.toString();
                        // if (addressList.get(0).getSubLocality() != null)
                        // sb.append(addressList.get(0).getSubLocality())
                        // .append(",");
                        // if (addressList.get(0).getLocality() != null)
                        // sb.append(addressList.get(0).getLocality()).append(
                        // ",");
                        // if (addressList.get(0).getAdminArea() != null)
                        // sb.append(addressList.get(0).getAdminArea())
                        // .append(",");
                        // if (addressList.get(0).getCountryName() != null)
                        // sb.append(addressList.get(0).getCountryName())
                        // .append(",");
                        // if (addressList.get(0).getPostalCode() != null)
                        // sb.append(addressList.get(0).getPostalCode());
                        System.out.println("location......" + sb.toString());
                        result = sb.toString();
                        if (result.length() > 0) {
                            result = result.substring(0,
                                    result.lastIndexOf(','));
                        }
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Unable connect to Geocoder", e);
                } finally {
                    Message message = Message.obtain();
                    message.setTarget(handler);
                    if (result.length() > 0) {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        bundle.putString("address", result);
                        message.setData(bundle);
                    } else {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        bundle.putString("address", "1");
                        message.setData(bundle);
                    }
                    message.sendToTarget();
                }
            }
        };
        thread.start();
    }
}
