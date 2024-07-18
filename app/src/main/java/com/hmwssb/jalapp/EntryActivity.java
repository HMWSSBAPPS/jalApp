package com.hmwssb.jalapp;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.jetbrains.annotations.Nullable;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
//import android.support.v4.content.FileProvider;
//import android.support.v4.content.res.ResourcesCompat;
//changes
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;
/////
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.hmwssb.jalapp.Helper.decodeSampledBitmapFromFile;
import static com.hmwssb.jalapp.Helper.reqHeight;
import static com.hmwssb.jalapp.Helper.reqWidth;
import static com.hmwssb.jalapp.ZipprGPSService.BROADCAST_ACTION;

public class EntryActivity extends Activity implements OnClickListener {

    Dialog prog;
    TextView tv_title;

    LinearLayout ll_meter;
    EditText et_meter_can_no;

    LinearLayout ll_billing;
    EditText et_billing_can_no;

    Button btn_image, btn_submit;

    ImageView iv_img;
    String name = "", section_code = "", index = "";

    protected LocationManager locationManager;
    LocationListener mlocListener;
    Location loc = null;
    String gps_data = "0.0-0.0";

    private Uri imageuri;
    File mediaStorageDir;
    int hit_index = 0;
    String locationAddress = "";
    Spinner rcvalue;
    EditText canno;
    private IntentFilter mIntentFilter;
    private static final int PERMISSION_REQUEST_CODE = 200;
    public static final int MEDIA_TYPE_IMAGE = 1;
    String imagetemp, mImageStr, selectedItem;
    static final int REQUEST_TAKE_PHOTO = 100;
    int phototype = 0;
    String mCurrentPhotoPath = "", picCncoded = "";
    private static final int REQUEST_ENABLE_GPS = 1001;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry_page);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(BROADCAST_ACTION);
        name = getIntent().getStringExtra("NAME");
        index = getIntent().getStringExtra("INDEX");
        System.out.println("index in entry....." + index);

        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        initViews();
    }


    private void initViews() {
        try {
            mIntentFilter = new IntentFilter();
            mIntentFilter.addAction(BROADCAST_ACTION);

            tv_title = findViewById(R.id.tv_title);
            rcvalue = findViewById(R.id.rcvalue);
            canno = findViewById(R.id.canno);

            ll_meter = findViewById(R.id.ll_meter);
            et_meter_can_no = findViewById(R.id.et_meter_can_no);
            et_meter_can_no.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (et_meter_can_no.getText().toString().length() > 0) {
                        et_meter_can_no.setError(null);
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void afterTextChanged(Editable s) {}
            });

            ll_billing = findViewById(R.id.ll_billing);
            et_billing_can_no = findViewById(R.id.et_billing_can_no);
            et_billing_can_no.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (et_billing_can_no.getText().toString().length() > 0) {
                        et_billing_can_no.setError(null);
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void afterTextChanged(Editable s) {}
            });

            btn_image = findViewById(R.id.btn_image);
            btn_image.setOnClickListener(this);
            btn_submit = findViewById(R.id.btn_submit);
            btn_submit.setOnClickListener(this);
            iv_img = findViewById(R.id.iv_img);

            if (Helper.lan_str.trim().equalsIgnoreCase("english")) {
                btn_image.setText("CAPTURE IMAGE");
                btn_submit.setText("SUBMIT");
            } else {
                btn_image.setText("ఫోటో తీయండి");
                btn_submit.setText("పంపండి");
            }

            setViews();
        } catch (Exception e) {
            Log.e("EntryActivity", "Error in initViews", e);
        }
    }



    public void setViews() {
        try {
            if (name != null) {
                tv_title.setText(name);
            } else {
                Log.e("EntryActivity", "Name is null");
            }

            String colorString = getIntent().getStringExtra("COLOR");
            if (colorString != null) {
                try {
                    tv_title.setTextColor(Color.parseColor(colorString));
                } catch (IllegalArgumentException e) {
                    Log.e("EntryActivity", "Invalid color format: " + colorString, e);
                }
            } else {
                Log.e("EntryActivity", "Color is null");
            }

            if (index != null) {
                index = index.trim();
                switch (index) {
                    case "0":
                    case "1":
                    case "2":
                    case "3":
                    case "4":
                    case "5":
                    case "8":
                        btn_image.setVisibility(View.VISIBLE);
                        break;
                    case "6":
                        ll_billing.setVisibility(View.VISIBLE);
                        break;
                    case "7":
                        ll_meter.setVisibility(View.VISIBLE);
                        break;
                    default:
                        Log.e("EntryActivity", "Invalid index value: " + index);
                        break;
                }
            } else {
                Log.e("EntryActivity", "Index is null");
            }
        } catch (Exception e) {
            Log.e("EntryActivity", "Error in setViews", e);
        }
    }


    private void showProgressDialog() {
        if (progressDialog != null && !progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == btn_submit) {
            handleSubmitClick();
        } else if (v == btn_image) {
            handleImageClick();
        }
    }
    private void handleImageClick() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermission()) {
                // Start GPS service if permission granted
                showProgressDialog();
                startService(new Intent(this, ZipprGPSService.class));
                captureImage();
            } else {
                requestPermission();
            }
        } else {
            showProgressDialog();
            startService(new Intent(this, ZipprGPSService.class));
            captureImage();
        }
    }
    private void handleSubmitClick() {
        switch (index.trim()) {
            case "0":
            case "1":
            case "2":
            case "3":
            case "4":
            case "5":
            case "8":
                handleCommonImageSubmit();
                break;
            case "6":
                handleBillingCanSubmit();
                break;
            case "7":
                handleMeterCanSubmit();
                break;
            default:
                Helper.showShortToast(EntryActivity.this, "Invalid index");
                break;
        }
    }
    private void handleCommonImageSubmit() {
        Log.d("EntryActivity", "handleCommonImageSubmit called");
        if (iv_img.getVisibility() == View.GONE) {
            Helper.showShortToast(EntryActivity.this, "Please capture image");
            btn_image.requestFocus();
            btn_image.requestFocusFromTouch();
        } else {
            if (Helper.isNetworkAvailable(EntryActivity.this)) {
                new GetSection().execute("Lat^Long");
            } else {
                Helper.showShortToast(EntryActivity.this, "Please check your internet connection...");
            }
        }
    }



    private void handleMeterCanSubmit() {
        Log.d("EntryActivity", "handleMeterCanSubmit called");
        if (et_meter_can_no.getText().toString().length() == 0) {
            et_meter_can_no.setError("Please enter can number", Helper.showIcon(R.drawable.error, EntryActivity.this));
            et_meter_can_no.requestFocus();
            et_meter_can_no.requestFocusFromTouch();
        } else if (et_meter_can_no.getText().toString().length() < 9) {
            et_meter_can_no.setError("CAN number should be 9 digits", Helper.showIcon(R.drawable.error, EntryActivity.this));
            et_meter_can_no.requestFocus();
            et_meter_can_no.requestFocusFromTouch();
        } else {
            if (Helper.isNetworkAvailable(EntryActivity.this)) {
                new GetSection().execute("Lat^Long");
            } else {
                Helper.showShortToast(EntryActivity.this, "Please check your internet connection...");
            }
        }
    }

    private void handleBillingCanSubmit() {
        Log.d("EntryActivity", "handleBillingCanSubmit called");
        if (et_billing_can_no.getText().toString().length() == 0) {
            et_billing_can_no.setError("Please enter can number", Helper.showIcon(R.drawable.error, EntryActivity.this));
            et_billing_can_no.requestFocus();
            et_billing_can_no.requestFocusFromTouch();
        } else if (et_billing_can_no.getText().toString().length() < 9) {
            et_billing_can_no.setError("CAN number should be 9 digits", Helper.showIcon(R.drawable.error, EntryActivity.this));
            et_billing_can_no.requestFocus();
            et_billing_can_no.requestFocusFromTouch();
        } else {
            if (Helper.isNetworkAvailable(EntryActivity.this)) {
                new GetSection().execute("Lat^Long");
            } else {
                Helper.showShortToast(EntryActivity.this, "Please check your internet connection...");
            }
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(EntryActivity.this, SubMenuPage.class);
        i.putExtra("INDEX", getIntent().getStringExtra("INDEX"));
        startActivity(i);
        finish();
    }

    public class SubmitData extends AsyncTask<String, Void, String> {

        ProgressDialog prog;
        Context context;

        public SubmitData(Context context) {
            this.context = context;
            prog = new ProgressDialog(context);
            prog.setMessage("Loading...");
            prog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!prog.isShowing()) {
                prog.show();
            }

            // Validate picCncoded
            if (picCncoded == null || picCncoded.isEmpty()) {
                Log.e("SubmitData", "Encoded image data is null or empty");
                Toast.makeText(context, "Please capture the image.", Toast.LENGTH_SHORT).show();
                cancel(true);  // Cancel the AsyncTask
            } else {
                Log.d("SubmitData", "Encoded image data length: " + picCncoded.length());
            }
        }

        @Override
        protected String doInBackground(String... KEY) {
            String responsestring = "";

            String SOAP_ACTION = "";
            SoapObject request = null;
            String[] VALUE = null;
            try {
                int TIMEOUT_WAIT_TO_CONNECT = 60 * 60 * 60 * 60 * 1000;

                String linemanID = Helper.getMobileNumFromDB(
                        context, DBHelper.TABLE_VALVE,
                        DBHelper.VALVE_LINEMANID);
                Log.d("SubmitData", "Lineman ID: " + linemanID);

                if (hit_index == 1) { // no chlorine
                    String imageSTr = picCncoded;
//                            (Build.VERSION.SDK_INT >= 24) ? picCncoded : imagetemp;
                    Log.d("ssssssssdd:",picCncoded);
                    VALUE = new String[]{
                            section_code,
                            Helper.getMobileNumFromDB(context,
                                    DBHelper.TABLE_GENERAL, DBHelper.GEN_MOBILE),
                            imageSTr,
                            linemanID,
                            gps_data.substring(0, gps_data.indexOf("-")),
                            gps_data.substring(gps_data.indexOf("-") + 1),
                            locationAddress};
                    SOAP_ACTION = Helper.NAMESPACE + "ILineManAppCodeTreeUC/"
                            + Helper.SaveChlorinationLineManApp;

                    request = new SoapObject(Helper.NAMESPACE,
                            Helper.SaveChlorinationLineManApp);
                    Log.d("SubmitData", "SOAP Action: " + SOAP_ACTION);
                }
                // Add conditions for other hit_index values as per your application logic...

                String KEY_VALUE[] = KEY[0].split("\\^");
                if (KEY_VALUE != null) {
                    for (int i = 0; i < KEY_VALUE.length; i++) {
                        System.out.println("ssssssssss");
                        Log.d("SubmitData", "key[" + i + "]:" + KEY_VALUE[i] + " VALVE[" + i + "]:" + VALUE[i]);
                        request.addProperty(KEY_VALUE[i], VALUE[i]);
                    }
                }
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                        SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(
                        Helper.SUBMIT_URL, TIMEOUT_WAIT_TO_CONNECT);
                androidHttpTransport.call(SOAP_ACTION, envelope);

                responsestring = envelope.getResponse().toString();
                Log.d("SubmitData", "Response======: " + responsestring);

            } catch (ConnectException e) {
                Log.e("SubmitData", "ConnectException", e);
                responsestring = "$101-No Network Found. Please make sure your mobile internet is enabled";
            } catch (XmlPullParserException e) {
                Log.e("SubmitData", "XmlPullParserException", e);
                responsestring = "$102-WebService Not Found. Please contact the developer";
            } catch (SocketTimeoutException e) {
                Log.e("SubmitData", "SocketTimeoutException", e);
                responsestring = "$101-Your internet connection seems very poor. Please try again";
            } catch (Exception e) {
                Log.e("SubmitData", "Exception", e);
                responsestring = "$102-Exception++++++. Please contact the developer";
            }

            return responsestring;
        }

        @Override
        protected void onPostExecute(String response) {
            try {
                Log.d("SubmitData", "Post Execute=====: " + response);
                if (prog != null && prog.isShowing()) {
                    prog.dismiss();
                }

                if (response.trim().startsWith("$102") || response.trim().startsWith("$101")) {
                    Toast.makeText(context,
                            response.substring(response.indexOf("-") + 1),
                            Toast.LENGTH_SHORT).show();
                } else {
                    if (hit_index == 1 || hit_index == 2 || hit_index == 3
                            || hit_index == 4 || hit_index == 5
                            || hit_index == 6 || hit_index == 7
                            || hit_index == 8 || hit_index == 9) {
                        if (response.trim().startsWith("0")) {
                            Toast.makeText(context,
                                    response.substring(response.indexOf("|") + 1),
                                    Toast.LENGTH_SHORT).show();
                        } else if (response.trim().startsWith("1")) {
                            Toast.makeText(context,
                                    response.substring(response.indexOf("|") + 1),
                                    Toast.LENGTH_SHORT).show();
                            context.startActivity(new Intent(context,
                                    SubMenuPage.class));
                            ((Activity) context).finish();
                        } else {
                            Toast.makeText(context,
                                    response.substring(response.indexOf("|") + 1),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("SubmitData", "Exception in onPostExecute", e);
                if (prog != null && prog.isShowing()) {
                    prog.dismiss();
                }
                Toast.makeText(context, "Please Try Again...",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class GetSection extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected String doInBackground(String... KEY) {

            String responsestring = "";

            String SOAP_ACTION = "";
            SoapObject request = null;
            String[] VALUE = null;
            try {
                int TIMEOUT_WAIT_TO_CONNECT = 60 * 60 * 60 * 60 * 1000;

                VALUE = new String[]{
                        gps_data.substring(0, gps_data.indexOf("-")),
                        gps_data.substring(gps_data.indexOf("-") + 1)};

                SOAP_ACTION = Helper.NAMESPACE + Helper.illegal_section;

                request = new SoapObject(Helper.NAMESPACE,
                        Helper.illegal_section);
                System.out.println("soap action......." + SOAP_ACTION);

                String KEY_VALUE[] = KEY[0].split("\\^");
                if (KEY_VALUE != null)
                    for (int i = 0; i < KEY_VALUE.length; i++) {
                        System.out.println("key[" + i + "]....." + KEY_VALUE[i]
                                + "..............VALVE[" + i + "]......."
                                + VALUE[i]);

                        request.addProperty(KEY_VALUE[i], VALUE[i]);
                    }
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                        SoapEnvelope.VER11);

                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(
                        Helper.SECTION_CODE_URL, TIMEOUT_WAIT_TO_CONNECT);
                androidHttpTransport.call(SOAP_ACTION, envelope);

                // SoapObject response = (SoapObject)
                // envelope.getResponse();
                responsestring = envelope.getResponse().toString();
                Log.e("GetSection resp", responsestring);

            } catch (ConnectException e) {
                e.printStackTrace();
                responsestring = "$101-No Network Found. Please make sure your mobile internet enable";
            } catch (XmlPullParserException e) {
                e.printStackTrace();
                System.out.println("yyyyyyyy");
                responsestring = "$102-WebService Not Found. Please contact developer";
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                responsestring = "$101-Your internet connection seems very poor. Please try again";
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("zzzzzzzzz");
                responsestring = "$102-Exception. Please contact developer";
            }

            return responsestring;
        }

        @Override
        protected void onPostExecute(String response) {
            try {

                System.out.println("in post....sssssssssss" + response);
                if (response.trim().startsWith("$102")) {
                    gps_data = "";
                    locationAddress = "";
                    prog.dismiss();
                    Toast.makeText(EntryActivity.this,
                            response.substring(response.indexOf("-") + 1),
                            Toast.LENGTH_SHORT).show();
                } else if (response.trim().startsWith("$101")) {
                    prog.dismiss();
                    gps_data = "";
                    locationAddress = "";
                    Toast.makeText(EntryActivity.this,
                            response.substring(response.indexOf("-") + 1),
                            Toast.LENGTH_SHORT).show();
                } else {
                    LoadData(response);
                }

            } catch (Exception e) {
                e.printStackTrace();
                prog.dismiss();
                gps_data = "";
                locationAddress = "";
                Toast.makeText(EntryActivity.this, "Please Try Again...",
                        Toast.LENGTH_SHORT).show();
            }

        }
    }

    public void LoadData(String data) {
        section_code = "";
        if (data.indexOf("Sectioncode=anyType{") != -1) {
            data = data.replace("Sectioncode=anyType{", "@");
            String arr[] = data.split("\\@");

            // for (int i = 1; i < arr.length; i++) {
            System.out.println("arr[" + 1 + "].........." + arr[1]);
            String subSPlit[] = arr[1].split("\\;");
            System.out.println("sub len........" + subSPlit.length);
            section_code = subSPlit[0].substring(subSPlit[0].indexOf("=") + 1);

            // }
            LocationAddress
                    .getAddressFromLocation(Double.parseDouble(gps_data
                                    .substring(0, gps_data.indexOf("-"))), Double
                                    .parseDouble(gps_data.substring(gps_data
                                            .indexOf("-") + 1)), EntryActivity.this,
                            new GeocoderHandler());

        } else {
            prog.dismiss();
            gps_data = "";
            locationAddress = "";
            Toast.makeText(
                    EntryActivity.this,
                    "No Section Found For Your Location.....Please Try Again...",
                    Toast.LENGTH_SHORT).show();
        }

    }

    // GPS Finding

    public void gpsFinding() {
        try {
            loc = null;
            turnGPSOn();
            showProgressDialog();
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            mlocListener = new MyLocationListener();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 1000, 10, mlocListener);// network_provider,GPS_PROVIDER
        } catch (Exception ex) {
        }
    }

    private void turnGPSOn() {
        try {

//            String provider = Settings.Secure.getString(getContentResolver(),
//                    Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
//            if (!provider.contains("gps")) { // if gps is disabled
//                // final Intent poke = new Intent();
//                // poke.setClassName("com.android.settings",
//                // "com.android.settings.widget.SettingsAppWidgetProvider");
//                // poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
//                // poke.setData(Uri.parse("3"));
//                // sendBroadcast(poke);
//
//                startActivity(new Intent(
//                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//            }


            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

//            if (!isGPSEnabled) {
//                new AlertDialog.Builder(this)
//                        .setMessage("GPS is not enabled. Do you want to go to settings menu?")
//                        .setCancelable(false)
//                        .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                                startActivity(intent);
//                            }
//                        })
//                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                dialog.cancel();
//                            }
//                        })
//                        .create()
//                        .show();
//            }

        } catch (Exception ex) {

        }
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, Helper.CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
        } else {
            Toast.makeText(this, "No camera app available", Toast.LENGTH_SHORT).show();
        }
    }

    private void startGPSService() {
        Intent serviceIntent = new Intent(this, ZipprGPSService.class);
        ContextCompat.startForegroundService(this, serviceIntent);
    }

    private void turnGPSOff() {
        try {

            String provider = Settings.Secure.getString(getContentResolver(),
                    Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

            if (provider.contains("gps")) { // if gps is enabled
                final Intent poke = new Intent();
                poke.setClassName("com.android.settings",
                        "com.android.settings.widget.SettingsAppWidgetProvider");
                poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
                poke.setData(Uri.parse("3"));
                sendBroadcast(poke);
            }
        } catch (Exception ex) {

        }
    }

    private class MyLocationListener implements LocationListener {

        public void onLocationChanged(Location location) {

            if (location != null) {
                loc = location;
                gps_data = loc.getLatitude() + "-" + loc.getLongitude();
                locationManager.removeUpdates(mlocListener);
                location = null;
                if (Helper.isNetworkAvailable(EntryActivity.this) == true) {
                    new GetSection().execute("Lat^Long");
                } else {
                    gps_data = "";
                    Helper.showShortToast(EntryActivity.this,
                            "please check your internet connection...");
                }

            } else {
            }
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }


    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        getPackageName() + ".provider",
                        photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                startActivityForResult(intent, 100);
            }
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }



    private static File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "CameraDemo");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg");
    }

    public Uri getOutputMediaFileUri(int type) {
//        return Uri.fromFile(getOutputMediaFile());
        /*return FileProvider.getUriForFile(
                this,
                getApplicationContext()
                        .getPackageName() + ".provider", getOutputMediaFile(type));*/

        if (Build.VERSION.SDK_INT >= 24) {
            return FileProvider.getUriForFile(
                    this,
                    getApplicationContext()
                            .getPackageName() + ".provider", getOutputMediaFile(type));
        } else {
            return Uri.fromFile(getOutputMediaFile(type));
        }
    }

//    public Uri getOutputMediaFileUri(int type) {
//        return Uri.fromFile(getOutputMediaFile(type));
//    }

    public File getOutputMediaFile(int type) {

        // External sdcard location

        Boolean isSDPresent = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);

        if (isSDPresent) {
            mediaStorageDir = new File(Environment
                    .getExternalStorageDirectory().getPath()
                    + File.separator
                    + Helper.IMAGE_DIRECTORY_NAME);
        } else {
            mediaStorageDir = new File(EntryActivity.this.getFilesDir()
                    .getPath() + File.separator + Helper.IMAGE_DIRECTORY_NAME);

        }

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {

                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd-HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile = null;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putParcelable("file_image_uri", imageuri);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        imageuri = savedInstanceState.getParcelable("file_image_uri");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            iv_img.setVisibility(View.VISIBLE);
            iv_img.setImageURI(Uri.parse(mCurrentPhotoPath)); // Update iv_img with the captured image URI
            Toast.makeText(this, "Image Captured", Toast.LENGTH_SHORT).show();
            hideProgressDialog(); // Hide the loader
        } else {
            Toast.makeText(this, "Image Capture Failed", Toast.LENGTH_SHORT).show();
            hideProgressDialog(); // Hide the loader even if image capture fails
        }
    }

    private void dealTakePhoto() {


//        int targetW = iv_img.getWidth();
//        int targetH = iv_img.getHeight();
        int targetW = 413;
        int targetH = 413;

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        //BitmapFactory.decodeFile(file, bmOptions);


        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        if (photoW != 0) {
            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            //Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            //Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            //Bitmap bitmap = ((BitmapDrawable) iv_img.getDrawable()).getBitmap();
            String encodeImage = getStringImage(bitmap);
            if (phototype == 0) {
                picCncoded = encodeImage;
            }
            if (phototype == 0) {
                iv_img.setImageBitmap(bitmap);

            }

            Log.d("DEBUG", "dealTakePhoto: " + encodeImage + "end");
            if (phototype == 0) {
                iv_img.setImageBitmap(bitmap);
            }

        }
    }

    public Drawable getDrawbleImage(int res) {

        Drawable drawable = ResourcesCompat.getDrawable(getResources(), res, null);

        return drawable;
    }

    public String getStringImage(Bitmap bmp) {
        Bitmap bm = BitmapFactory.decodeFile(mCurrentPhotoPath);
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 60, bao);
        byte[] ba = bao.toByteArray();

        return Base64.encodeToString(ba, Base64.DEFAULT);
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            locationAddress = "";

            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = "";
            }

            if (locationAddress.trim().equalsIgnoreCase("1")) {
                locationAddress = "";
            }

            if (index.trim().equalsIgnoreCase("0")) {
                handleNetworkAvailability(1);
            } else if (index.trim().equalsIgnoreCase("1")) {
                handleNetworkAvailability(2);
            } else if (index.trim().equalsIgnoreCase("2")) {
                handleNetworkAvailability(3);
            } else if (index.trim().equalsIgnoreCase("3")) {
                handleNetworkAvailability(4);
            } else if (index.trim().equalsIgnoreCase("4")) {
                handleNetworkAvailability(5);
            } else if (index.trim().equalsIgnoreCase("5")) {
                handleNetworkAvailability(6);
            } else if (index.trim().equalsIgnoreCase("6")) {
                handleNetworkAvailability(7);
            } else if (index.trim().equalsIgnoreCase("7")) {
                handleNetworkAvailability(8);
            } else if (index.trim().equalsIgnoreCase("8")) {
                handleNetworkAvailability(9);
            }
        }

        private void handleNetworkAvailability(int hitIndex) {
            if (Helper.isNetworkAvailable(EntryActivity.this)) {
                hit_index = hitIndex;
                new SubmitData(EntryActivity.this)
                        .execute("sectionCode^mobileNo^image^lineManID^latitute^longitude^addressLocation");
            } else {
                Helper.showShortToast(EntryActivity.this,
                        "Please check your internet connection...");
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (broadcastReceiver != null) {
                unregisterReceiver(broadcastReceiver);
                Log.d("EntryActivity", "BroadcastReceiver unregistered");
            }
        } catch (Exception e) {
            Log.e("EntryActivity", "Error unregistering broadcastReceiver", e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (broadcastReceiver != null) {
                registerReceiver(broadcastReceiver, mIntentFilter, Context.RECEIVER_NOT_EXPORTED);
                Log.d("EntryActivity", "BroadcastReceiver registered");
            }
        } catch (Exception e) {
            Log.e("EntryActivity", "Error registering broadcastReceiver", e);
        }
    }


    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String action = intent.getAction();
                if (action != null && action.trim().equalsIgnoreCase(BROADCAST_ACTION)) {
                    Bundle b = intent.getExtras();
                    if (b != null) {
                        String lat = b.getString("Lat");
                        String lon = b.getString("Lon");
                        String gps_data = lat + "-" + lon;
                        stopService(new Intent(EntryActivity.this, ZipprGPSService.class));
                        if (Helper.isNetworkAvailable(EntryActivity.this)) {
                            // Assuming 'prog' is a ProgressDialog instance
                            prog.dismiss();
                            captureImage();
                        } else {
                            gps_data = "";
                            Helper.showShortToast(EntryActivity.this, "Please check your internet connection...");
                        }
                        Log.d("EntryActivity", "Latitude: " + lat + ", Longitude: " + lon);
                    } else {
                        Log.e("EntryActivity", "Bundle is null in broadcast receiver");
                    }
                }
            } catch (Exception e) {
                Log.e("EntryActivity", "Error in onReceive", e);
            }
        }
    };


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager
                .getRunningServices(Integer.MAX_VALUE)) {

            if (serviceClass.getName().equals(service.service.getClassName())) {
                System.out.println("serviceClass.getName()........"
                        + serviceClass.getName());
                return true;
            }
        }
        return false;
    }


    /*Method used to check the runtime permissions for location,camera and storage*/
//    private boolean checkPermission() {
//        int result = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
//        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
//        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
//        int result3 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
//        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED && result3 == PackageManager.PERMISSION_GRANTED;
//    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }

    /*Requesting for the required permissions*/
//    private void requestPermission() {
//        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION, CAMERA, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
//    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.CAMERA,
                            Manifest.permission.READ_MEDIA_IMAGES,
                            Manifest.permission.READ_MEDIA_VIDEO,
                            Manifest.permission.READ_MEDIA_AUDIO
                    },
                    PERMISSION_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.CAMERA,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },
                    PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) {
                handleImageClick();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                hideProgressDialog(); // Hide the loader if permission is denied
            }
        }
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
    /*showing alert message to accept the permissions*/
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(EntryActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


}
