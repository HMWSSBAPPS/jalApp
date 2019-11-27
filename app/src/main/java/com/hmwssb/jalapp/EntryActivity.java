package com.hmwssb.jalapp;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Method;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.entry_page);
        name = getIntent().getStringExtra("NAME");
        index = getIntent().getStringExtra("INDEX");
        System.out.println("index in entry....." + index);
        initViews();
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void initViews() {
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(ZipprGPSService.BROADCAST_ACTION);
        registerReceiver(broadcastReceiver, mIntentFilter);
        tv_title = (TextView) findViewById(R.id.tv_title);
        rcvalue = findViewById(R.id.rcvalue);
        canno = findViewById(R.id.canno);

        ll_meter = (LinearLayout) findViewById(R.id.ll_meter);
        et_meter_can_no = (EditText) findViewById(R.id.et_meter_can_no);
        et_meter_can_no.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (et_meter_can_no.getText().toString().length() > 0) {
                    et_meter_can_no.setError(null);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });

        ll_billing = (LinearLayout) findViewById(R.id.ll_billing);
        et_billing_can_no = (EditText) findViewById(R.id.et_billing_can_no);
        et_billing_can_no.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (et_billing_can_no.getText().toString().length() > 0) {
                    et_billing_can_no.setError(null);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });

        btn_image = (Button) findViewById(R.id.btn_image);
        btn_image.setOnClickListener(this);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(this);
        iv_img = (ImageView) findViewById(R.id.iv_img);
        if (Helper.lan_str.trim().equalsIgnoreCase("english")) {
            btn_image.setText("CAPTURE IMAGE");
            btn_submit.setText("SUBMIT");
        } else {
            btn_image.setText("ఫోటో తీయండి");
            btn_submit.setText("పంపండి");
        }
        setViews();

       /* Log.e("VALVE_LINEMANID ", Helper.getMobileNumFromDB(EntryActivity.this,
                DBHelper.TABLE_VALVE, DBHelper.VALVE_LINEMANID));*/

        /*DBHelper dbh = new DBHelper(this);
        SQLiteDatabase db = dbh.getWritableDatabase();
        SQLiteDatabase db1 = dbh.getWritableDatabase();
        String Query1 = "SELECT * FROM " + DBHelper.TABLE_GENERAL;
        Cursor c1 = db.rawQuery(Query1, null);
        String date = "";
        while (c1.moveToNext()) {
            date = c1.getString(c1.getColumnIndex(DBHelper.GEN_DATE));
            Log.e("Splash GEN_MOBILE", c1.getString(c1.getColumnIndex(DBHelper.GEN_MOBILE)));
        }
        c1.close();
        db1.close();*/
    }

    public void setViews() {
        tv_title.setText(name);
        tv_title.setTextColor(Color.parseColor(getIntent().getStringExtra(
                "COLOR")));

        if (index.trim().equalsIgnoreCase("0")) {
            btn_image.setVisibility(View.VISIBLE);
            rcvalue.setVisibility(View.VISIBLE);
            canno.setVisibility(View.VISIBLE);
            rcvalue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedItem = parent.getItemAtPosition(position).toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        } else if (index.trim().equalsIgnoreCase("1")) {
            btn_image.setVisibility(View.VISIBLE);

        } else if (index.trim().equalsIgnoreCase("2")) {

            btn_image.setVisibility(View.VISIBLE);

        } else if (index.trim().equalsIgnoreCase("3")) {
            btn_image.setVisibility(View.VISIBLE);
        } else if (index.trim().equalsIgnoreCase("4")) {
            btn_image.setVisibility(View.VISIBLE);
        } else if (index.trim().equalsIgnoreCase("5")) {
            btn_image.setVisibility(View.VISIBLE);

        } else if (index.trim().equalsIgnoreCase("6")) {
            ll_billing.setVisibility(View.VISIBLE);
        } else if (index.trim().equalsIgnoreCase("7")) {
            ll_meter.setVisibility(View.VISIBLE);
        } else if (index.trim().equalsIgnoreCase("8")) {

            btn_image.setVisibility(View.VISIBLE);
        }
    }

    public void showProgressDialog() {

        try {
            prog = new Dialog(EntryActivity.this, R.style.AppTheme1);
            prog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            prog.setContentView(R.layout.progress_layout);
            prog.setCancelable(false);
            prog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {

        if (v == btn_submit) {
            if (index.trim().equalsIgnoreCase("0")) {
//                Helper.showShortToast(EntryActivity.this,
//                        "This is Chlorine...");
                if (iv_img.getVisibility() == View.GONE) {
                    Helper.showShortToast(EntryActivity.this,
                            "Please capture image");
                    btn_image.requestFocus();
                    btn_image.requestFocusFromTouch();
                } else {
                    if (Helper.isNetworkAvailable(EntryActivity.this) == true) {
                        if (canno.getText().toString() != null && !canno.getText().toString().trim().equalsIgnoreCase("")) {
                            new GetSection().execute("Lat^Long");
                        } else {
                            Helper.showShortToast(EntryActivity.this,
                                    "Please Enter Can Number...");
                        }

                    } else {
                        Helper.showShortToast(EntryActivity.this,
                                "Please check your internet connection...");
                    }
                }
            } else if (index.trim().equalsIgnoreCase("1")) {
                if (iv_img.getVisibility() == View.GONE) {
                    Helper.showShortToast(EntryActivity.this,
                            "Please capture image");
                    btn_image.requestFocus();
                    btn_image.requestFocusFromTouch();
                } else {
                    if (Helper.isNetworkAvailable(EntryActivity.this) == true) {
                        new GetSection().execute("Lat^Long");
                    } else {
                        Helper.showShortToast(EntryActivity.this,
                                "Please check your internet connection...");
                    }
                }

            } else if (index.trim().equalsIgnoreCase("2")) {

                if (iv_img.getVisibility() == View.GONE) {
                    Helper.showShortToast(EntryActivity.this,
                            "Please capture image");
                    btn_image.requestFocus();
                    btn_image.requestFocusFromTouch();
                } else {
                    if (Helper.isNetworkAvailable(EntryActivity.this) == true) {
                        new GetSection().execute("Lat^Long");
                    } else {
                        Helper.showShortToast(EntryActivity.this,
                                "Please check your internet connection...");
                    }
                }

            } else if (index.trim().equalsIgnoreCase("3")) {
                if (iv_img.getVisibility() == View.GONE) {
                    Helper.showShortToast(EntryActivity.this,
                            "Please capture image");
                    btn_image.requestFocus();
                    btn_image.requestFocusFromTouch();
                } else {
                    if (Helper.isNetworkAvailable(EntryActivity.this) == true) {
                        new GetSection().execute("Lat^Long");
                    } else {
                        Helper.showShortToast(EntryActivity.this,
                                "Please check your internet connection...");
                    }
                }
            } else if (index.trim().equalsIgnoreCase("4")) {
                if (iv_img.getVisibility() == View.GONE) {
                    Helper.showShortToast(EntryActivity.this,
                            "Please capture image");
                    btn_image.requestFocus();
                    btn_image.requestFocusFromTouch();
                } else {
                    if (Helper.isNetworkAvailable(EntryActivity.this) == true) {
                        new GetSection().execute("Lat^Long");
                    } else {
                        Helper.showShortToast(EntryActivity.this,
                                "Please check your internet connection...");
                    }
                }
            } else if (index.trim().equalsIgnoreCase("5")) {
                if (iv_img.getVisibility() == View.GONE) {
                    Helper.showShortToast(EntryActivity.this,
                            "Please capture image");
                    btn_image.requestFocus();
                    btn_image.requestFocusFromTouch();
                } else {
                    if (Helper.isNetworkAvailable(EntryActivity.this) == true) {
                        new GetSection().execute("Lat^Long");
                    } else {
                        Helper.showShortToast(EntryActivity.this,
                                "Please check your internet connection...");
                    }
                }
            } else if (index.trim().equalsIgnoreCase("6")) {
                if (et_billing_can_no.getText().toString().length() == 0) {

                    et_billing_can_no.setError("Please enter can number",
                            Helper.showIcon(R.drawable.error,
                                    EntryActivity.this));
                    et_billing_can_no.requestFocus();
                    et_billing_can_no.requestFocusFromTouch();
                } else if (et_billing_can_no.getText().toString().length() < 9) {

                    et_billing_can_no.setError("CAN number should be 9 digit",
                            Helper.showIcon(R.drawable.error,
                                    EntryActivity.this));
                    et_billing_can_no.requestFocus();
                    et_billing_can_no.requestFocusFromTouch();
                } else {
                    if (Helper.isNetworkAvailable(EntryActivity.this) == true) {
                        new GetSection().execute("Lat^Long");
                    } else {
                        Helper.showShortToast(EntryActivity.this,
                                "Please check your internet connection...");
                    }
                }
            } else if (index.trim().equalsIgnoreCase("7")) {
                if (et_meter_can_no.getText().toString().length() == 0) {

                    et_meter_can_no.setError("Please enter can number", Helper
                            .showIcon(R.drawable.error, EntryActivity.this));
                    et_meter_can_no.requestFocus();
                    et_meter_can_no.requestFocusFromTouch();
                } else if (et_meter_can_no.getText().toString().length() < 9) {

                    et_meter_can_no.setError("CAN number should be 9 digit",
                            Helper.showIcon(R.drawable.error,
                                    EntryActivity.this));
                    et_meter_can_no.requestFocus();
                    et_meter_can_no.requestFocusFromTouch();
                } else {
                    if (Helper.isNetworkAvailable(EntryActivity.this) == true) {
                        new GetSection().execute("Lat^Long");
                    } else {
                        Helper.showShortToast(EntryActivity.this,
                                "Please check your internet connection...");
                    }
                }
            } else if (index.trim().equalsIgnoreCase("8")) {

                if (iv_img.getVisibility() == View.GONE) {
                    Helper.showShortToast(EntryActivity.this,
                            "Please capture image");
                    btn_image.requestFocus();
                    btn_image.requestFocusFromTouch();
                } else {
                    if (Helper.isNetworkAvailable(EntryActivity.this) == true) {
                        new GetSection().execute("Lat^Long");
                    } else {
                        Helper.showShortToast(EntryActivity.this,
                                "Please check your internet connection...");
                    }
                }
            }

        } else if (v == btn_image) {
            // gps_data = "17.4743731-78.485867";
            // showProgressDialog();
            // new GetSection().execute("Lat^Long");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkPermission()) {
                    //If permission is already having then showing the toast
                    turnGPSOn();
                    showProgressDialog();
                    startService(new Intent(this, ZipprGPSService.class));
                } else {
                    //If the app has not the permission then asking for the permission
//                            requestStoragePermission();
                    requestPermission();
                }
            } else {
                turnGPSOn();
                showProgressDialog();
                startService(new Intent(this, ZipprGPSService.class));
            }
            //gpsFinding();
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

        @Override
        protected void onPreExecute() {

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
                        EntryActivity.this, DBHelper.TABLE_VALVE,
                        DBHelper.VALVE_LINEMANID);
                System.out.println("lineman id........." + linemanID);

                if (hit_index == 1) {// no chlorine
                    Bitmap bp = Helper.getImage(EntryActivity.this, imageuri.getPath(), imageuri);
                    String imageSTr = Helper.BitMapToString(bp);
                    if (Build.VERSION.SDK_INT >= 24) {
                        VALUE = new String[]{
                                section_code,
                                Helper.getMobileNumFromDB(EntryActivity.this,
                                        DBHelper.TABLE_GENERAL, DBHelper.GEN_MOBILE),
//                                imagetemp,
                                imageSTr,
                                linemanID,
                                gps_data.substring(0, gps_data.indexOf("-")),
                                gps_data.substring(gps_data.indexOf("-") + 1),
                                locationAddress, canno.getText().toString(), selectedItem};
                    } else {
                        VALUE = new String[]{
                                section_code,
                                Helper.getMobileNumFromDB(EntryActivity.this,
                                        DBHelper.TABLE_GENERAL, DBHelper.GEN_MOBILE),
                                imagetemp,
//                            imageSTr,
                                linemanID,
                                gps_data.substring(0, gps_data.indexOf("-")),
                                gps_data.substring(gps_data.indexOf("-") + 1),
                                locationAddress, canno.getText().toString(), selectedItem};
                    }
                    SOAP_ACTION = Helper.NAMESPACE + "ILineManAppCodeTreeUC/"
                            + Helper.SaveChlorinationLineManApp;

                    request = new SoapObject(Helper.NAMESPACE,
                            Helper.SaveChlorinationLineManApp);
                    System.out.println("soap action......." + SOAP_ACTION);
                } else if (hit_index == 2) {// valve leakage
                    Bitmap bp = Helper.getImage(EntryActivity.this, imageuri.getPath(), imageuri);
                    String imageSTr = Helper.BitMapToString(bp);
                    if (Build.VERSION.SDK_INT >= 24) {
                        VALUE = new String[]{
                                section_code,
                                Helper.getMobileNumFromDB(EntryActivity.this,
                                        DBHelper.TABLE_GENERAL, DBHelper.GEN_MOBILE),
//                                imagetemp,
                                imageSTr,
                                linemanID,
                                gps_data.substring(0, gps_data.indexOf("-")),
                                gps_data.substring(gps_data.indexOf("-") + 1),
                                locationAddress};
                    } else {
                        VALUE = new String[]{
                                section_code,
                                Helper.getMobileNumFromDB(EntryActivity.this,
                                        DBHelper.TABLE_GENERAL, DBHelper.GEN_MOBILE),
                                imagetemp,
//                            imageSTr,
                                linemanID,
                                gps_data.substring(0, gps_data.indexOf("-")),
                                gps_data.substring(gps_data.indexOf("-") + 1),
                                locationAddress};
                    }

                    SOAP_ACTION = Helper.NAMESPACE + "ILineManAppCodeTreeUC/"
                            + Helper.SaveValveLeakagesLineManApp;

                    request = new SoapObject(Helper.NAMESPACE,
                            Helper.SaveValveLeakagesLineManApp);
                    System.out.println("soap action......." + SOAP_ACTION);
                } else if (hit_index == 3) {// water leakage
                    Bitmap bp = Helper.getImage(EntryActivity.this, imageuri.getPath(), imageuri);
                    String imageSTr = Helper.BitMapToString(bp);
                    if (Build.VERSION.SDK_INT >= 24) {
                        VALUE = new String[]{
                                section_code,
                                Helper.getMobileNumFromDB(EntryActivity.this,
                                        DBHelper.TABLE_GENERAL, DBHelper.GEN_MOBILE),
//                                imagetemp,
                                imageSTr,
                                linemanID,
                                gps_data.substring(0, gps_data.indexOf("-")),
                                gps_data.substring(gps_data.indexOf("-") + 1),
                                locationAddress};
                    } else {
                        VALUE = new String[]{
                                section_code,
                                Helper.getMobileNumFromDB(EntryActivity.this,
                                        DBHelper.TABLE_GENERAL, DBHelper.GEN_MOBILE),
                                imagetemp,
//                            imageSTr,
                                linemanID,
                                gps_data.substring(0, gps_data.indexOf("-")),
                                gps_data.substring(gps_data.indexOf("-") + 1),
                                locationAddress};
                    }
                    SOAP_ACTION = Helper.NAMESPACE + "ILineManAppCodeTreeUC/"
                            + Helper.SavePipeLineLeakagesLineManApp;
                    request = new SoapObject(Helper.NAMESPACE,
                            Helper.SavePipeLineLeakagesLineManApp);
                    System.out.println("soap action......." + SOAP_ACTION);
                } else if (hit_index == 4) {// polluted water
                    Bitmap bp = Helper.getImage(EntryActivity.this, imageuri.getPath(), imageuri);
                    String imageSTr = Helper.BitMapToString(bp);

                    if (Build.VERSION.SDK_INT >= 24) {
                        VALUE = new String[]{
                                section_code,
                                Helper.getMobileNumFromDB(EntryActivity.this,
                                        DBHelper.TABLE_GENERAL, DBHelper.GEN_MOBILE),
//                                imagetemp,
                                imageSTr,
                                linemanID,
                                gps_data.substring(0, gps_data.indexOf("-")),
                                gps_data.substring(gps_data.indexOf("-") + 1),
                                locationAddress};

                    } else {
                        VALUE = new String[]{
                                section_code,
                                Helper.getMobileNumFromDB(EntryActivity.this,
                                        DBHelper.TABLE_GENERAL, DBHelper.GEN_MOBILE),
                                imagetemp,
//                            imageSTr,
                                linemanID,
                                gps_data.substring(0, gps_data.indexOf("-")),
                                gps_data.substring(gps_data.indexOf("-") + 1),
                                locationAddress};
                    }

                    SOAP_ACTION = Helper.NAMESPACE + "ILineManAppCodeTreeUC/"
                            + Helper.SavePollutedWaterLineManApp;

                    request = new SoapObject(Helper.NAMESPACE,
                            Helper.SavePollutedWaterLineManApp);
                    System.out.println("soap action......." + SOAP_ACTION);
                } else if (hit_index == 5) {// Sewerage Overflow
                    Bitmap bp = Helper.getImage(EntryActivity.this, imageuri.getPath(), imageuri);
                    String imageSTr = Helper.BitMapToString(bp);

                    if (Build.VERSION.SDK_INT >= 24) {
                        VALUE = new String[]{
                                section_code,
                                Helper.getMobileNumFromDB(EntryActivity.this,
                                        DBHelper.TABLE_GENERAL, DBHelper.GEN_MOBILE),
//                                imagetemp,
                                imageSTr,
                                linemanID,
                                gps_data.substring(0, gps_data.indexOf("-")),
                                gps_data.substring(gps_data.indexOf("-") + 1),
                                locationAddress};

                    } else {
                        VALUE = new String[]{
                                section_code,
                                Helper.getMobileNumFromDB(EntryActivity.this,
                                        DBHelper.TABLE_GENERAL, DBHelper.GEN_MOBILE),
                                imagetemp,
//                            imageSTr,
                                linemanID,
                                gps_data.substring(0, gps_data.indexOf("-")),
                                gps_data.substring(gps_data.indexOf("-") + 1),
                                locationAddress};

                    }

                    SOAP_ACTION = Helper.NAMESPACE + "ILineManAppCodeTreeUC/"
                            + Helper.SaveSewerageOverflowLineManApp;

                    request = new SoapObject(Helper.NAMESPACE,
                            Helper.SaveSewerageOverflowLineManApp);
                    System.out.println("soap action......." + SOAP_ACTION);
                } else if (hit_index == 6) {// Missing manhole
                    Bitmap bp = Helper.getImage(EntryActivity.this, imageuri.getPath(), imageuri);
                    String imageSTr = Helper.BitMapToString(bp);
                    if (Build.VERSION.SDK_INT >= 24) {
                        VALUE = new String[]{
                                section_code,
                                Helper.getMobileNumFromDB(EntryActivity.this,
                                        DBHelper.TABLE_GENERAL, DBHelper.GEN_MOBILE),
//                                imagetemp,
                                imageSTr,
                                linemanID,
                                gps_data.substring(0, gps_data.indexOf("-")),
                                gps_data.substring(gps_data.indexOf("-") + 1),
                                locationAddress};
                    } else {
                        VALUE = new String[]{
                                section_code,
                                Helper.getMobileNumFromDB(EntryActivity.this,
                                        DBHelper.TABLE_GENERAL, DBHelper.GEN_MOBILE),
                                imagetemp,
//                            imageSTr,
                                linemanID,
                                gps_data.substring(0, gps_data.indexOf("-")),
                                gps_data.substring(gps_data.indexOf("-") + 1),
                                locationAddress};

                    }

                    SOAP_ACTION = Helper.NAMESPACE + "ILineManAppCodeTreeUC/"
                            + Helper.SaveMissingManholeCoverLineManApp;

                    request = new SoapObject(Helper.NAMESPACE,
                            Helper.SaveMissingManholeCoverLineManApp);
                    System.out.println("soap action......." + SOAP_ACTION);
                } else if (hit_index == 7) {// Low water pressure
                    VALUE = new String[]{
                            et_billing_can_no.getText().toString(),
                            section_code,
                            Helper.getMobileNumFromDB(EntryActivity.this,
                                    DBHelper.TABLE_GENERAL, DBHelper.GEN_MOBILE),
                            linemanID};

                    SOAP_ACTION = Helper.NAMESPACE + "ILineManAppCodeTreeUC/"
                            + Helper.SaveLowWaterPressureForLineManMobileApp;

                    request = new SoapObject(Helper.NAMESPACE,
                            Helper.SaveLowWaterPressureForLineManMobileApp);
                    System.out.println("soap action......." + SOAP_ACTION);
                } else if (hit_index == 8) {// Request meter
                    VALUE = new String[]{
                            et_meter_can_no.getText().toString(),
                            section_code,
                            Helper.getMobileNumFromDB(EntryActivity.this,
                                    DBHelper.TABLE_GENERAL, DBHelper.GEN_MOBILE),
                            linemanID,
                    };

                    SOAP_ACTION = Helper.NAMESPACE + "ILineManAppCodeTreeUC/"
                            + Helper.SaveRequestMeterLineManApp;

                    request = new SoapObject(Helper.NAMESPACE,
                            Helper.SaveRequestMeterLineManApp);
                    System.out.println("soap action......." + SOAP_ACTION);
                } else if (hit_index == 9) {// illegal water
                    Bitmap bp = Helper.getImage(EntryActivity.this, imageuri.getPath(), imageuri);
                    String imageSTr = Helper.BitMapToString(bp);
                    if (Build.VERSION.SDK_INT >= 24) {
                        VALUE = new String[]{
                                section_code,
                                Helper.getMobileNumFromDB(EntryActivity.this,
                                        DBHelper.TABLE_GENERAL, DBHelper.GEN_MOBILE),
//                                imagetemp,
                                imageSTr,
                                linemanID,
                                gps_data.substring(0, gps_data.indexOf("-")),
                                gps_data.substring(gps_data.indexOf("-") + 1),
                                locationAddress};
                    } else {
                        VALUE = new String[]{
                                section_code,
                                Helper.getMobileNumFromDB(EntryActivity.this,
                                        DBHelper.TABLE_GENERAL, DBHelper.GEN_MOBILE),
                                imagetemp,
//                            imageSTr,
                                linemanID,
                                gps_data.substring(0, gps_data.indexOf("-")),
                                gps_data.substring(gps_data.indexOf("-") + 1),
                                locationAddress};
                    }

                    SOAP_ACTION = Helper.NAMESPACE + "ILineManAppCodeTreeUC/"
                            + Helper.SaveIllegalConnectionInfo;

                    request = new SoapObject(Helper.NAMESPACE,
                            Helper.SaveIllegalConnectionInfo);
                    System.out.println("soap action......." + SOAP_ACTION);
                }

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
                        Helper.SUBMIT_URL, TIMEOUT_WAIT_TO_CONNECT);
                androidHttpTransport.call(SOAP_ACTION, envelope);

                // SoapObject response = (SoapObject)
                // envelope.getResponse();
                responsestring = envelope.getResponse().toString();
                Log.e("SubmitData resp", responsestring);

            } catch (ConnectException e) {
                e.printStackTrace();
                responsestring = "$101-No Network Found. Please make sure your mobile internet enable";
            } catch (XmlPullParserException e) {
                e.printStackTrace();
                responsestring = "$102-WebService Not Found. Please contact developer";
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                responsestring = "$101-Your internet connection seems very poor. Please try again";
            } catch (Exception e) {
                e.printStackTrace();
                responsestring = "$102-Exception. Please contact developer";
            }

            return responsestring;
        }

        @Override
        protected void onPostExecute(String response) {
            try {

                System.out.println("in post...." + response);
                if (response.trim().startsWith("$102")) {
                    prog.dismiss();
                    Toast.makeText(EntryActivity.this,
                            response.substring(response.indexOf("-") + 1),
                            Toast.LENGTH_SHORT).show();
                } else if (response.trim().startsWith("$101")) {
                    prog.dismiss();
                    Toast.makeText(EntryActivity.this,
                            response.substring(response.indexOf("-") + 1),
                            Toast.LENGTH_SHORT).show();
                } else {
                    prog.dismiss();
                    if (hit_index == 1 || hit_index == 2 || hit_index == 3
                            || hit_index == 4 || hit_index == 5
                            || hit_index == 6 || hit_index == 7
                            || hit_index == 8 || hit_index == 9) {
                        if (response.trim().startsWith("0")) {
//                            Toast.makeText(
//                                    EntryActivity.this,
//                                    "this is right",
//                                    Toast.LENGTH_SHORT).show();
//                            Log.d("hitinfdded", "onPostExecute: " + );
                            Toast.makeText(
                                    EntryActivity.this,
                                    response.substring(response.indexOf("|") + 1),
                                    Toast.LENGTH_SHORT).show();

                        } else if (response.trim().startsWith("1")) {
                            Toast.makeText(
                                    EntryActivity.this,
                                    response.substring(response.indexOf("|") + 1),
                                    Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(EntryActivity.this,
                                    SubMenuPage.class));
                            finish();
                        } else {
                            Toast.makeText(
                                    EntryActivity.this,
                                    response.substring(response.indexOf("|") + 1),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
                prog.dismiss();
                Toast.makeText(EntryActivity.this, "Please Try Again...",
                        Toast.LENGTH_SHORT).show();
            }

        }
    }

    public class GetSection extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
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
                responsestring = "$102-WebService Not Found. Please contact developer";
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                responsestring = "$101-Your internet connection seems very poor. Please try again";
            } catch (Exception e) {
                e.printStackTrace();
                responsestring = "$102-Exception. Please contact developer";
            }

            return responsestring;
        }

        @Override
        protected void onPostExecute(String response) {
            try {

                System.out.println("in post...." + response);
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
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 1000, 10, mlocListener);// network_provider,GPS_PROVIDER
        } catch (Exception ex) {
        }
    }

    private void turnGPSOn() {
        try {

            String provider = Settings.Secure.getString(getContentResolver(),
                    Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            if (!provider.contains("gps")) { // if gps is disabled
                // final Intent poke = new Intent();
                // poke.setClassName("com.android.settings",
                // "com.android.settings.widget.SettingsAppWidgetProvider");
                // poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
                // poke.setData(Uri.parse("3"));
                // sendBroadcast(poke);

                startActivity(new Intent(
                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        } catch (Exception ex) {

        }
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

        imageuri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageuri);

        // start the image capture Intent
        startActivityForResult(intent, Helper.CAMERA_CAPTURE_IMAGE_REQUEST_CODE);

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        System.out.println("requestCode...." + requestCode);
        if (requestCode == Helper.CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {
//                    Bitmap bp = Helper.getImage(imageuri.getPath());
                    /*Bitmap bp = Helper.getImage(EntryActivity.this, imageuri.getPath(), imageuri);
                    // just display image in imageview
                    iv_img.setImageBitmap(bp);
                    iv_img.setVisibility(View.VISIBLE);*/
                    //	Helper.showLongToast(EntryActivity.this, "Address :"+locationAddress);


                    if (Build.VERSION.SDK_INT >= 24) {
                        Bitmap bp = Helper.getImage(EntryActivity.this, imageuri.getPath(), imageuri);
                        // just display image in imageview
                        iv_img.setImageBitmap(bp);
                        iv_img.setVisibility(View.VISIBLE);
                    } else {
                        File file = new File(imageuri.getPath());
                        iv_img.setImageBitmap(decodeSampledBitmapFromFile(
                                file.getAbsolutePath(), reqWidth, reqHeight));
                        iv_img.setVisibility(View.VISIBLE);
                        BitmapDrawable drawable = (BitmapDrawable) iv_img
                                .getDrawable();
                        Bitmap bitmap = drawable.getBitmap();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] b = baos.toByteArray();
                        imagetemp = Base64.encodeToString(b, Base64.DEFAULT);
                    }
                } catch (Exception e) {
                    iv_img.setVisibility(View.GONE);
                    imageuri = null;
                    e.printStackTrace();
                }
            } else {
                imageuri = null;

            }
        }
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
            // if (locationAddress.length() > 0) {
            if (locationAddress.trim().equalsIgnoreCase("1")) {
                locationAddress = "";
            }
            if (index.trim().equalsIgnoreCase("0")) {

                if (Helper.isNetworkAvailable(EntryActivity.this) == true) {
                    hit_index = 1;
                    new SubmitData()
                            .execute("sectionCode^mobileNo^image^lineManID^latitute^longitude^addressLocation^Can^RCValue");
                } else {
                    Helper.showShortToast(EntryActivity.this,
                            "Please check your internet connection...");
                }

            } else if (index.trim().equalsIgnoreCase("1")) {

                if (Helper.isNetworkAvailable(EntryActivity.this) == true) {
                    hit_index = 2;
                    new SubmitData()
                            .execute("sectionCode^mobileNo^image^lineManID^latitute^longitude^address");
                } else {
                    Helper.showShortToast(EntryActivity.this,
                            "Please check your internet connection...");
                }

            } else if (index.trim().equalsIgnoreCase("2")) {

                if (Helper.isNetworkAvailable(EntryActivity.this) == true) {
                    hit_index = 3;
                    new SubmitData()
                            .execute("sectionCode^mobileNo^image^lineManID^latitute^longitude^address");
                } else {
                    Helper.showShortToast(EntryActivity.this,
                            "Please check your internet connection...");
                }


            } else if (index.trim().equalsIgnoreCase("3")) {

                if (Helper.isNetworkAvailable(EntryActivity.this) == true) {
                    hit_index = 4;
                    new SubmitData()
                            .execute("sectionCode^mobileNo^image^lineManID^latitute^longitude^address");
                } else {
                    Helper.showShortToast(EntryActivity.this,
                            "Please check your internet connection...");
                }

            } else if (index.trim().equalsIgnoreCase("4")) {

                if (Helper.isNetworkAvailable(EntryActivity.this) == true) {
                    hit_index = 5;
                    new SubmitData()
                            .execute("sectionCode^mobileNo^image^lineManID^latitute^longitude^address");
                } else {
                    Helper.showShortToast(EntryActivity.this,
                            "Please check your internet connection...");
                }

            } else if (index.trim().equalsIgnoreCase("5")) {

                if (Helper.isNetworkAvailable(EntryActivity.this) == true) {
                    hit_index = 6;
                    new SubmitData()
                            .execute("sectionCode^mobileNo^image^lineManID^latitute^longitude^address");
                } else {
                    Helper.showShortToast(EntryActivity.this,
                            "Please check your internet connection...");
                }

            } else if (index.trim().equalsIgnoreCase("6")) {

                if (Helper.isNetworkAvailable(EntryActivity.this) == true) {
                    hit_index = 7;
                    new SubmitData()
                            .execute("can^sectionCode^mobileNo^lineManID");
                } else {
                    Helper.showShortToast(EntryActivity.this,
                            "Please check your internet connection...");
                }

            } else if (index.trim().equalsIgnoreCase("7")) {

                if (Helper.isNetworkAvailable(EntryActivity.this) == true) {
                    hit_index = 8;
                    new SubmitData()
                            .execute("can^sectionCode^mobileNo^lineManID");
                } else {
                    Helper.showShortToast(EntryActivity.this,
                            "Please check your internet connection...");
                }

            } else if (index.trim().equalsIgnoreCase("8")) {


                if (Helper.isNetworkAvailable(EntryActivity.this) == true) {
                    hit_index = 9;
                    new SubmitData()
                            .execute("sectionCode^mobileNo^image^lineManID^latitute^longitude^address");
                } else {
                    Helper.showShortToast(EntryActivity.this,
                            "Please check your internet connection...");
                }

            }


            // }
        }
    }

    @Override
    public void onPause() {
        unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        registerReceiver(broadcastReceiver, mIntentFilter);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.trim().equalsIgnoreCase(ZipprGPSService.BROADCAST_ACTION)) {
                Bundle b = intent.getExtras();
                String lat = b.getString("Lat");
                String lon = b.getString("Lon");
                gps_data = lat + "-" + lon;
                stopService(new Intent(EntryActivity.this, ZipprGPSService.class));
                if (Helper.isNetworkAvailable(EntryActivity.this) == true) {
                    prog.dismiss();
                    captureImage();
                } else {
                    gps_data = "";
                    Helper.showShortToast(EntryActivity.this,
                            "please check your internet connection...");
                }
                System.out.println("lat........" + lat);
                System.out.println("lon........" + lon);


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
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        int result3 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED && result3 == PackageManager.PERMISSION_GRANTED;
    }

    /*Requesting for the required permissions*/
    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION, CAMERA, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean writestorageAccepted = grantResults[3] == PackageManager.PERMISSION_GRANTED;
                    if (locationAccepted && cameraAccepted && storageAccepted && writestorageAccepted) {
                        turnGPSOn();
                        showProgressDialog();
                        startService(new Intent(this, ZipprGPSService.class));
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                                showMessageOKCancel("You need to allow access the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{ACCESS_FINE_LOCATION, CAMERA, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE},
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }

                    }
                }


                break;
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
