package com.hmwssb.jalapp;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class ZipprGPSService extends Service implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static final String TAG = "ZipprGPSService";

    public static String BROADCAST_ACTION = "DATA";

    private static final boolean DEBUG = true;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10 * 1000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 0 * 1000;

    private static final int REQUEST_PERMISSION_LOCATION_SETTING = 100;
    private static final int PERMISSION_REQUEST_FINE_LOCATION = 200;

    private Location mCurrentLocation;
    private boolean mIsActivityDestroyed;

    private boolean mIsResolving = false;
    private static final String DIALOG_ERROR = "dialog_error";
    /**
     * Request code for resolutions involving location fetch
     */
    private static final int RC_LOCATION_FETCH = 1;

    public ZipprGPSService() {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
        buildGoogleApiClient();
        mIsActivityDestroyed = false;


    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        createLocationRequest();


    }


    /**
     * Check if we have the ACCESS_FINE_LOCATION permission and request it if we do not.
     *
     * @return true if we have the permission, false if we do not.
     */
    private boolean checkIfLocationPermissionsGranted() {

        // No explanation needed, we can request the permission.
        //requestLocationPermission();

        return false;
    }


//    private void requestLocationPermission() {
//        ActivityCompat.requestPermissions(getApplicationContext(),
//                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                PERMISSION_REQUEST_FINE_LOCATION);
//    }


    protected void onLocationPermissionsGranted() {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (DEBUG) Log.d(TAG, "onConnected() called with: bundle = [" + bundle + "]");
        Location mLastLocation = getLastLocation();
        if (mLastLocation != null) {
            onLocationChanged(mLastLocation);
        }
        requestLocationUpdate();
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    protected Location getLastLocation() {
        return LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    private void requestLocationUpdate() {
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (DEBUG)
            Log.d(TAG, "onConnectionFailed() called with: connectionResult = [" + connectionResult + "]");
        // Could not connect to Google Play Services.  The user needs to select an account,
        // grant permissions or resolve an error in order to sign in. Refer to the javadoc for
        // ConnectionResult to see possible error codes.

        if (mIsResolving) {
            // Already attempting to resolve an error.
            return;
        } else if (connectionResult.hasResolution()) {
//            try {
//                mIsResolving = true;
//               // connectionResult.startResolutionForResult(getApplicationContext(), RC_LOCATION_FETCH);
//            } catch (IntentSender.SendIntentException e) {
//                // There was an error with the resolution intent. Try again.
//                mGoogleApiClient.connect();
//            }
        } else {
            // Show dialog using GoogleApiAvailability.getErrorDialog()

            mIsResolving = true;
        }
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        onLocationPermissionsGranted();
        startLocationUpdates();
    }


    @Override
    public void onConnectionSuspended(int i) {
        if (DEBUG) Log.d(TAG, "onConnectionSuspended() called with: i = [" + i + "]");
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (DEBUG) Log.d(TAG, "onLocationChanged() called with: location = [" + location + "]");
        mCurrentLocation = location;

        onGoogleClientLocationChanged(mCurrentLocation);
    }

    /**
     * Returns the current location of the user.
     *
     * @return the current location
     */
    @Nullable
    final protected Location getCurrentLocation() {
        return mCurrentLocation;
    }

    /**
     * Called when the users current location is changed.
     *
     * @param location the changed location.
     */
    protected void onGoogleClientLocationChanged(Location location) {
        if (DEBUG)
            Log.d(TAG, "onGoogleClientLocationChanged() called with: location = [" + location + "]");
        if (location != null) {
            Intent i = new Intent();
            i.setAction(ZipprGPSService.BROADCAST_ACTION);
            i.putExtra("Lat", "" + location.getLatitude());
            i.putExtra("Lon", "" + location.getLongitude());
            i.putExtra("Accuracy", "" + location.getAccuracy());
            sendBroadcast(i);
        }
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        mIsActivityDestroyed = true;
        stopLocationUpdates();
        disconnectLocationServices();
        super.onDestroy();

    }

    final protected void startLocationUpdates() {
        if (DEBUG) Log.d(TAG, "startLocationUpdates() called");
        connectToLocationServices();
    }

    final protected void stopLocationUpdates() {
        if (DEBUG) Log.d(TAG, "stopLocationUpdates() called");
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
        disconnectLocationServices();
    }

    final protected void disconnectLocationServices() {
        if (DEBUG) Log.d(TAG, "disconnectLocationServices() called");
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            if (DEBUG) Log.d(TAG, "disconnecting mGoogleApiClient");
            mGoogleApiClient.disconnect();
        }
    }

    final protected void connectToLocationServices() {
        if (DEBUG) Log.d(TAG, "connectToLocationServices() called");
        if (mGoogleApiClient != null && !mGoogleApiClient.isConnected()) {
            if (DEBUG) Log.d(TAG, "connecting mGoogleApiClient");
            mGoogleApiClient.connect();
        }
    }

}