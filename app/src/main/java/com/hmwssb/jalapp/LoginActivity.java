package com.hmwssb.jalapp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

import static android.Manifest.permission.READ_PHONE_STATE;

public class LoginActivity extends Activity implements OnClickListener, SMSReceiver.OTPReceiveListener {

    EditText et_reg_mobile;
    Button btn_register;
    Dialog prog;
    int hit_index = 0;
    String passCode = "";
    File mediaStorageDir;
    AppSignatureHelper appSignatureHashHelper;
    private static final int PERMISSION_REQUEST_CODE = 200;
    SMSReceiver otpReceiver;
    String finalresponse = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login);
        et_reg_mobile = (EditText) findViewById(R.id.et_reg_mobile);
        appSignatureHashHelper = new AppSignatureHelper(this);
        Log.d("Hashvalue", "Apps Hash Key: " + appSignatureHashHelper.getAppSignatures().get(0));
        btn_register = (Button) findViewById(R.id.btn_register);
        btn_register.setOnClickListener(this);
        et_reg_mobile.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (et_reg_mobile.getText().toString().length() > 0) {
                    et_reg_mobile.setError(null);
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    public void showProgressDialog() {

        try {
            prog = new Dialog(LoginActivity.this, R.style.AppTheme1);
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

        if (v == btn_register) {
            if (et_reg_mobile.getText().toString().length() == 0) {
                et_reg_mobile.setError("Please enter mobile number...",
                        Helper.showIcon(R.drawable.error, LoginActivity.this));
                et_reg_mobile.requestFocus();
                et_reg_mobile.requestFocusFromTouch();
            } else if (Helper.getcheckPhoneno(et_reg_mobile.getText()
                    .toString())) {
                et_reg_mobile.setError("Please enter valid mobile number...",
                        Helper.showIcon(R.drawable.error, LoginActivity.this));
                et_reg_mobile.requestFocus();
                et_reg_mobile.requestFocusFromTouch();
            } else {
                if (Helper.isNetworkAvailable(LoginActivity.this) == true) {


                    DBHelper dbh = new DBHelper(LoginActivity.this);
                    SQLiteDatabase db = dbh.getWritableDatabase();
                    String Query = "Select * From " + DBHelper.TABLE_GENERAL
                            + ";";
                    Cursor c = db.rawQuery(Query, null);
                    if (c.getCount() == 0) {
                        hit_index = 1;
//                        new DownloadData().execute("mobileNo^IMEIno^passcode");

                        // hit_index = 2;
                        // DBHelper.insertintoTable(
                        // LoginActivity.this,
                        // DBHelper.TABLE_GENERAL,
                        // new String[] { DBHelper.GEN_MOBILE,
                        // DBHelper.GEN_USER_ID,
                        // DBHelper.GEN_PASS_CODE, DBHelper.GEN_DATE },
                        // new String[] { et_reg_mobile.getText().toString(),
                        // "1234", "1234", Helper.getDate() });
                        // passCode = "1234";
                        // new DownloadData().execute("mobileNo^passcode");

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (checkPermission()) {
                                //If permission is already having then showing the toast
//                                Toast.makeText(LoginActivity.this, "You already have the both permission", Toast.LENGTH_LONG).show();
                                new DownloadData().execute("mobileNo^IMEIno^passcode");
                                //Existing the method with return
                                return;
                            } else {
                                //If the app has not the permission then asking for the permission
                                requestPermission();
                            }
                        } else {
                            new DownloadData().execute("mobileNo^IMEIno^passcode");
                        }

                    } else {
                        while (c.moveToNext()) {
                            passCode = c.getString(c
                                    .getColumnIndex(DBHelper.GEN_PASS_CODE));
                        }
                        hit_index = 3;
//                        new DownloadData().execute("mobileNo^passcode");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (checkPermission()) {
                                //If permission is already having then showing the toast
//                                Toast.makeText(LoginActivity.this, "You already have the both permission", Toast.LENGTH_LONG).show();
                                new DownloadData().execute("mobileNo^passcode");
                                //Existing the method with return
                                return;
                            } else {
                                //If the app has not the permission then asking for the permission
                                requestPermission();
                            }
                        } else {
                            new DownloadData().execute("mobileNo^passcode");
                        }
                    }
                    c.close();
                    db.close();

                } else {
                    Helper.showShortToast(LoginActivity.this,
                            "Please check your internet connection...");
                }

            }

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // startActivity(new Intent(LoginActivity.this, splashActivity.class));
        finish();
    }

    public class DownloadData extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            if (hit_index == 2 || hit_index == 1) {
                showProgressDialog();
            } else if (hit_index == 3) {
                showProgressDialog();
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
                if (hit_index == 1) {
                    VALUE = new String[]{et_reg_mobile.getText().toString(),
                            Helper.getIMEI(LoginActivity.this),
                            "1234"};
                    SOAP_ACTION = Helper.NAMESPACE + Helper.AUTH_METHOD_NAME;
                    request = new SoapObject(Helper.NAMESPACE,
                            Helper.AUTH_METHOD_NAME);
                    String KEY_VALUE[] = KEY[0].split("\\^");
                    if (KEY_VALUE != null) {
                        for (int i = 0; i < KEY_VALUE.length; i++) {
                            System.out.println("key[" + i + "]....."
                                    + KEY_VALUE[i] + "..............VALVE[" + i
                                    + "]......." + VALUE[i]);
                            request.addProperty(KEY_VALUE[i], VALUE[i]);
                        }
                    }
                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                            SoapEnvelope.VER11);

                    envelope.dotNet = true;
                    envelope.setOutputSoapObject(request);
                    HttpTransportSE androidHttpTransport = new HttpTransportSE(
                            Helper.URL, TIMEOUT_WAIT_TO_CONNECT);
                    androidHttpTransport.call(SOAP_ACTION, envelope);

                    // SoapObject response = (SoapObject)
                    // envelope.getResponse();
                    responsestring = envelope.getResponse().toString();

                } else if (hit_index == 2 || hit_index == 3) {
                    VALUE = new String[]{et_reg_mobile.getText().toString(),
                            passCode};
                    SOAP_ACTION = Helper.NAMESPACE
                            + Helper.VALVE_DATA_METHOD_NAME;
                    request = new SoapObject(Helper.NAMESPACE,
                            Helper.VALVE_DATA_METHOD_NAME);
                    String KEY_VALUE[] = KEY[0].split("\\^");
                    if (KEY_VALUE != null) {
                        for (int i = 0; i < KEY_VALUE.length; i++) {
                            System.out.println("key[" + i + "]....."
                                    + KEY_VALUE[i] + "..............VALVE[" + i
                                    + "]......." + VALUE[i]);

                            request.addProperty(KEY_VALUE[i], VALUE[i]);
                        }
                    }
                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                            SoapEnvelope.VER11);

                    envelope.dotNet = true;
                    envelope.setOutputSoapObject(request);
                    HttpTransportSE androidHttpTransport = new HttpTransportSE(
                            Helper.URL, TIMEOUT_WAIT_TO_CONNECT);
                    androidHttpTransport.call(SOAP_ACTION, envelope);

                    // SoapObject response = (SoapObject)
                    // envelope.getResponse();
                    responsestring = envelope.getResponse().toString();

                }

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
                    Toast.makeText(LoginActivity.this,
                            response.substring(response.indexOf("-") + 1),
                            Toast.LENGTH_SHORT).show();
                } else if (response.trim().startsWith("$101")) {
                    prog.dismiss();
                    Toast.makeText(LoginActivity.this,
                            response.substring(response.indexOf("-") + 1),
                            Toast.LENGTH_SHORT).show();
                } else {
                    if (hit_index == 1) {
                        if (response.trim().toLowerCase().indexOf("fail") != -1) {
                            prog.dismiss();
                            // DBHelper.insertintoTable(LoginActivity.this,
                            // DBHelper.TABLE_GENERAL, new String[] {
                            // DBHelper.GEN_MOBILE,
                            // DBHelper.GEN_USER_ID,
                            // DBHelper.GEN_PASS_CODE,
                            // DBHelper.GEN_DATE }, new String[] {
                            // et_reg_mobile.getText().toString(),
                            // "", "1234", Helper.getDate() });
                            // passCode = "1234";
                            // hit_index = 2;
                            // new DownloadData().execute("mobileNo^passcode");
                            Toast.makeText(
                                    LoginActivity.this,
                                    response.substring(response.indexOf(",") + 1),
                                    Toast.LENGTH_SHORT).show();
                        } else if (response.trim().toLowerCase()
                                .indexOf("success") != -1) {
                            finalresponse = response;
                            startSMSListener();
//                            ReadSMS(response);
                        } else {
                            prog.dismiss();
                            Toast.makeText(LoginActivity.this, response,
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else if (hit_index == 2 || hit_index == 3) {
                        // if (response.trim().toLowerCase().indexOf("fail") !=
                        // -1) {
                        // prog.dismiss();
                        // Toast.makeText(
                        // LoginActivity.this,
                        // response.substring(response.indexOf(",") + 1),
                        // Toast.LENGTH_SHORT).show();
                        // } else if (response.trim().toLowerCase()
                        // .indexOf("success") != -1) {
                        LoadData(response);
                        // } else {
                        // prog.dismiss();
                        // Toast.makeText(LoginActivity.this, response,
                        // Toast.LENGTH_SHORT).show();
                        // }
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
                prog.dismiss();
                Toast.makeText(LoginActivity.this, "Please Try Again...",
                        Toast.LENGTH_SHORT).show();
            }

        }

    }

    public File getOutputMediaFile(String imgname) {

        // External sdcard location

        Boolean isSDPresent = android.os.Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED);

        if (isSDPresent) {
            mediaStorageDir = new File(Environment
                    .getExternalStorageDirectory().getPath()
                    + File.separator
                    + Helper.IMAGE_DIRECTORY_NAME);
        } else {
            mediaStorageDir = new File(LoginActivity.this.getFilesDir()
                    .getPath() + File.separator + Helper.IMAGE_DIRECTORY_NAME);

        }

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {

                return null;
            }
        }

        // Create a media file name
        File mediaFile = null;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + imgname + ".jpg");

        return mediaFile;
    }

    public void LoadData(String data) {

        if (data.indexOf("Table1=anyType{") != -1) {
            data = data.replace("Table1=anyType{", "@");
            String arr[] = data.split("\\@");
            for (int i = 1; i < arr.length; i++) {
                System.out.println("arr[" + i + "].........." + arr[i]);
                String subSPlit[] = arr[i].split("\\;");
                System.out.println("sub len........" + subSPlit.length);
                String img = "0";
                if (subSPlit[15].substring(subSPlit[15].indexOf("=") + 1)
                        .trim().length() > 0) {
                    img = subSPlit[15].substring(subSPlit[15].indexOf("=") + 1)
                            .trim();
                    // try {
                    // StrictMode.ThreadPolicy policy = new
                    // StrictMode.ThreadPolicy.Builder()
                    // .permitAll().build();
                    // StrictMode.setThreadPolicy(policy);
                    // img = subSPlit[16].substring(
                    // subSPlit[16].indexOf("=") + 1).trim();
                    // URL aURL = new URL(img);
                    // URLConnection conn = aURL.openConnection();
                    // conn.connect();
                    // InputStream is = conn.getInputStream();
                    // BufferedInputStream bis = new BufferedInputStream(is);
                    // Bitmap bm = BitmapFactory.decodeStream(bis);
                    // File f = getOutputMediaFile(subSPlit[2].substring(
                    // subSPlit[2].indexOf("=") + 1).trim()
                    // + "-"
                    // + subSPlit[3].substring(
                    // subSPlit[3].indexOf("=") + 1).trim());
                    // FileOutputStream fos = null;
                    // fos = new FileOutputStream(f);
                    // bm.compress(Bitmap.CompressFormat.PNG, 70, fos);
                    // bis.close();
                    // fos.close();
                    // is.close();
                    // img = f.getPath();
                    // } catch (Exception e) {
                    // img = "0";
                    // e.printStackTrace();
                    // }
                }
                DBHelper.insertintoTable(
                        LoginActivity.this,
                        DBHelper.TABLE_VALVE,
                        new String[]{
                                DBHelper.VALVE_SID,
                                DBHelper.VALVE_LINEMANID,
                                DBHelper.VLAVE_LINEID,
                                DBHelper.VALVE_VALVEID,
                                DBHelper.VALVE_SUBVALVEID,
                                DBHelper.VALVE_LANDMARK,
                                DBHelper.VALVE_AREA,
                                DBHelper.VALVE_VALVETYPE,
                                DBHelper.VALVE_LATITUDE,
                                DBHelper.VALVE_LONGITUDE,
                                DBHelper.VALVE_STATUS,
                                DBHelper.VALVE_SUB_STATUS,
                                DBHelper.VALVE_IMAGE,
                                DBHelper.VALVE_SCHEDULE,
                                DBHelper.VALVE_TIME,
                                DBHelper.VALVE_CANS,
                                DBHelper.VALVE_CENTRIC,
                                DBHelper.VALVE_SUPLY_AREA},
                        new String[]{
                                subSPlit[0].substring(
                                        subSPlit[0].indexOf("=") + 1).trim(),
                                subSPlit[1].substring(
                                        subSPlit[1].indexOf("=") + 1).trim(),
                                subSPlit[2].substring(
                                        subSPlit[2].indexOf("=") + 1).trim(),
                                subSPlit[3].substring(
                                        subSPlit[3].indexOf("=") + 1).trim(),
                                subSPlit[4].substring(
                                        subSPlit[4].indexOf("=") + 1).trim(),
                                subSPlit[5].substring(
                                        subSPlit[5].indexOf("=") + 1).trim(),
                                subSPlit[6].substring(
                                        subSPlit[6].indexOf("=") + 1).trim(),
                                subSPlit[7].substring(
                                        subSPlit[7].indexOf("=") + 1).trim(),
                                subSPlit[13].substring(
                                        subSPlit[13].indexOf("=") + 1).trim(),
                                subSPlit[14].substring(
                                        subSPlit[14].indexOf("=") + 1).trim(),
                                "0",
                                "0",
                                img,
                                subSPlit[10].substring(
                                        subSPlit[10].indexOf("=") + 1).trim()
                                        + " "
                                        + subSPlit[11].substring(
                                        subSPlit[11].indexOf("=") + 1)
                                        .trim(),
                                "0-0",
                                subSPlit[18].substring(subSPlit[18]
                                        .indexOf("=") + 1),
                                subSPlit[17].substring(subSPlit[17]
                                        .indexOf("=") + 1),
                                subSPlit[19].substring(subSPlit[19]
                                        .indexOf("=") + 1)
                        });

            }
            prog.dismiss();
            Intent i = new Intent(LoginActivity.this, HomePage.class);
            startActivity(i);
            finish();
//            Intent intent = new Intent(LoginActivity.this, SubMenuPage.class);
//            startActivity(intent);
//            finish();
        } else {
            prog.dismiss();
            Toast.makeText(
                    LoginActivity.this,
                    "No Valves Found For this Phone Number.....Please Try Again...",
                    Toast.LENGTH_SHORT).show();
        }

    }

    public void ReadSMS(final String data) {
//        showToast(data);
//		 prog.dismiss();
//		 String split[] = data.split("\\,");
//		 hit_index = 2;
//		 DBHelper.insertintoTable(LoginActivity.this, DBHelper.TABLE_GENERAL,
//		 new String[] { DBHelper.GEN_MOBILE, DBHelper.GEN_USER_ID,
//		 DBHelper.GEN_PASS_CODE, DBHelper.GEN_DATE },
//		 new String[] { et_reg_mobile.getText().toString(), split[1],
//		 split[2], Helper.getDate() });
//		 passCode = split[2];
//		 new DownloadData().execute("mobileNo^passcode");

        Handler h = new Handler();
        h.postDelayed(new Runnable() {

            @Override
            public void run() {
                String split[] = data.split("\\,");
                Uri uri = Uri.parse("content://sms/inbox");
                Cursor c = getContentResolver().query(uri, null, null, null,
                        null);
                // Read the sms data and store it in the list
                int count = 0;
                while (c.moveToNext()) {
                    String body = c.getString(c.getColumnIndexOrThrow("body"))
                            .toString();

                    String address = c.getString(
                            c.getColumnIndexOrThrow("address")).toString();
                    Log.d("body", "run: " + body + "address" + address);
                    if (body.indexOf(split[2]) != -1 && address.length() < 10) {
                        count++;
                        break;
                    }
                }
                c.close();
                if (count > 0) {
                    hit_index = 2;
                    DBHelper.insertintoTable(
                            LoginActivity.this,
                            DBHelper.TABLE_GENERAL,
                            new String[]{DBHelper.GEN_MOBILE,
                                    DBHelper.GEN_USER_ID,
                                    DBHelper.GEN_PASS_CODE, DBHelper.GEN_DATE},
                            new String[]{et_reg_mobile.getText().toString(),
                                    split[1], split[2], Helper.getDate()});
                    passCode = split[2];
                    new DownloadData().execute("mobileNo^passcode");
                } else {
                    prog.dismiss();
                    Helper.showShortToast(LoginActivity.this,
                            "Your mobile number is not registered...Please Try Again...");
                }
            }
        }, 30000);
    }

    private void startSMSListener() {
        try {
            otpReceiver = new SMSReceiver();
            otpReceiver.setOTPListener(this);

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);
            this.registerReceiver(otpReceiver, intentFilter);

            SmsRetrieverClient client = SmsRetriever.getClient(this);

            Task<Void> task = client.startSmsRetriever();
            task.addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // API successfully started
                }
            });

            task.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Fail to start API
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onOTPReceived(String otp) {
        showToast("OTP Received: " + otp);
        String split[] = finalresponse.split("\\,");
        // Read the sms data and store it in the list
        int count = 0;
        if (otp.indexOf(split[2]) != -1) {
            count++;
        }

        if (count > 0) {
            hit_index = 2;
            DBHelper.insertintoTable(
                    LoginActivity.this,
                    DBHelper.TABLE_GENERAL,
                    new String[]{DBHelper.GEN_MOBILE,
                            DBHelper.GEN_USER_ID,
                            DBHelper.GEN_PASS_CODE, DBHelper.GEN_DATE},
                    new String[]{et_reg_mobile.getText().toString(),
                            split[1], split[2], Helper.getDate()});
            passCode = split[2];
            new DownloadData().execute("mobileNo^passcode");
        } else {
            prog.dismiss();
            Helper.showShortToast(LoginActivity.this,
                    "Your mobile number is not registered...Please Try Again...");
        }
        if (otpReceiver != null) {
            unregisterReceiver(otpReceiver);
//            LocalBroadcastManager.getInstance(this).unregisterReceiver(otpReceiver);
        }
    }

    @Override
    public void onOTPTimeOut() {
        showToast("OTP Time out");
    }

    @Override
    public void onOTPReceivedError(String error) {
        showToast(error);
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        try {
            if (otpReceiver != null) {
                unregisterReceiver(otpReceiver);
//                LocalBroadcastManager.getInstance(this).unregisterReceiver(otpReceiver);
            }
        } catch (Exception e) {
        }
        //  unregisterReceiver(deliveryBroadcastReceiver);
        super.onStop();
    }

    @Override
    public void onDestroy() {

        try {
            if (otpReceiver != null) {
                unregisterReceiver(otpReceiver);
//                LocalBroadcastManager.getInstance(this).unregisterReceiver(otpReceiver);
            }
        } catch (Exception e) {
        }

        super.onDestroy();
    }

    /*Method used to check the runtime permissions for device id and read sms*/
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), READ_PHONE_STATE);

        return result == PackageManager.PERMISSION_GRANTED;
    }

    /*Requesting for the required permissions*/
    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{READ_PHONE_STATE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean readPhoneStateAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (readPhoneStateAccepted) {
//                        Toast.makeText(this, "Both Permission granted", Toast.LENGTH_LONG).show();
                        new DownloadData().execute("mobileNo^IMEIno^passcode");
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(READ_PHONE_STATE)) {
                            showMessageOKCancel("You need to allow access the permissions",
                                    new DialogInterface.OnClickListener() {
                                        @TargetApi(Build.VERSION_CODES.M)
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            requestPermissions(new String[]{READ_PHONE_STATE},
                                                    PERMISSION_REQUEST_CODE);
                                        }
                                    });
                            return;
                        }
                    }

                }
                break;
        }

    }

    /*showing alert message to accept the permissions*/
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(LoginActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

}
