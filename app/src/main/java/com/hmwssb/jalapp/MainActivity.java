package com.hmwssb.jalapp;

import java.io.File;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

    LinearLayout ll_logout, ll_report, ll_gps;
    Button btn_logout, btn_report, btn_gps;
    ProgressDialog prog;

    File mediaStorageDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ll_gps = (LinearLayout) findViewById(R.id.ll_gps);
        ll_gps.setOnClickListener(this);
        ll_logout = (LinearLayout) findViewById(R.id.ll_logout);
        ll_logout.setOnClickListener(this);
        ll_report = (LinearLayout) findViewById(R.id.ll_report);
        ll_report.setOnClickListener(this);

        btn_logout = (Button) findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(this);
        btn_report = (Button) findViewById(R.id.btn_report);
        btn_report.setOnClickListener(this);
        btn_gps = (Button) findViewById(R.id.btn_gps);
        btn_gps.setOnClickListener(this);
        Fragment featured = new Refresh_GPS();

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_layout, featured).commit();
    }

    @Override
    public void onClick(View v) {
        if (v == ll_logout || v == btn_logout) {
            Logout();
        } else if (v == ll_gps || v == btn_gps) {
            Fragment featured = new Refresh_GPS();

            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_layout, featured).commit();

        } else if (v == ll_report || v == btn_report) {
            Fragment featured = new Report();

            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_layout, featured).commit();
        }

    }

    public void Logout() {
        final Dialog dl = new Dialog(MainActivity.this);
        dl.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dl.setContentView(R.layout.show_logout);

        Button b_yes = (Button) dl.findViewById(R.id.logout_yes);
        b_yes.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
                dl.cancel();

            }
        });
        Button b_no = (Button) dl.findViewById(R.id.logout_no);
        b_no.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dl.cancel();
            }
        });
        dl.show();

    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        Intent i = new Intent(MainActivity.this, HomePage.class);
        startActivity(i);
        finish();
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add(1, 1, 0, "Update Valves");

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {

            case 1:
                if (Helper.isNetworkAvailable(MainActivity.this) == true) {
                    new DownloadData().execute("mobileNo^passcode");
                } else {
                    Helper.showShortToast(MainActivity.this,
                            "please check your internet connection...");
                }

                break;

        }

        return super.onOptionsItemSelected(item);

    }

    public class DownloadData extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {

            prog = new ProgressDialog(MainActivity.this);
            prog.setTitle("Please Wait...!");
            prog.setMessage("Updating Valves...");
            prog.show();

        }

        @Override
        protected String doInBackground(String... KEY) {

            String responsestring = "";

            String SOAP_ACTION = "";
            SoapObject request = null;
            String[] VALUE = null;
            int TIMEOUT_WAIT_TO_CONNECT = 60 * 60 * 60 * 60 * 1000;

            VALUE = new String[]{
                    Helper.getMobileNumFromDB(MainActivity.this,
                            DBHelper.TABLE_GENERAL, DBHelper.GEN_MOBILE),
                    Helper.getMobileNumFromDB(MainActivity.this,
                            DBHelper.TABLE_GENERAL, DBHelper.GEN_PASS_CODE)};
            SOAP_ACTION = Helper.NAMESPACE + Helper.VALVE_DATA_METHOD_NAME;
            request = new SoapObject(Helper.NAMESPACE,
                    Helper.VALVE_DATA_METHOD_NAME);

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
                    Helper.URL, TIMEOUT_WAIT_TO_CONNECT);
            try {

                androidHttpTransport.call(SOAP_ACTION, envelope);

                // SoapObject response = (SoapObject) envelope.getResponse();
                responsestring = envelope.getResponse().toString();

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
                    Toast.makeText(MainActivity.this,
                            response.substring(response.indexOf("-") + 1),
                            Toast.LENGTH_SHORT).show();
                } else if (response.trim().startsWith("$101")) {
                    prog.dismiss();
                    Toast.makeText(MainActivity.this,
                            response.substring(response.indexOf("-") + 1),
                            Toast.LENGTH_SHORT).show();
                } else {

                    LoadData(response);

                }

            } catch (Exception e) {
                e.printStackTrace();
                prog.dismiss();
                Toast.makeText(MainActivity.this, "Please Try Again...",
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
            mediaStorageDir = new File(MainActivity.this.getFilesDir()
                    .getPath() + File.separator + Helper.IMAGE_DIRECTORY_NAME);

        }

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {

                return null;
            }
        }

        File mediaFile = null;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + imgname + ".jpg");

        return mediaFile;
    }

    public void LoadData(String data) {

        if (data.indexOf("Table=anyType{") != -1) {
            DBHelper.deleteAllRows(MainActivity.this, DBHelper.TABLE_VALVE);
            data = data.replace("Table=anyType{", "@");
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
                    //
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
                        MainActivity.this,
                        DBHelper.TABLE_VALVE,
                        new String[]{DBHelper.VALVE_SID,
                                DBHelper.VALVE_LINEMANID,
                                DBHelper.VLAVE_LINEID, DBHelper.VALVE_VALVEID,
                                DBHelper.VALVE_SUBVALVEID,
                                DBHelper.VALVE_LANDMARK, DBHelper.VALVE_AREA,
                                DBHelper.VALVE_VALVETYPE,
                                DBHelper.VALVE_LATITUDE,
                                DBHelper.VALVE_LONGITUDE,
                                DBHelper.VALVE_STATUS,
                                DBHelper.VALVE_SUB_STATUS,
                                DBHelper.VALVE_IMAGE, DBHelper.VALVE_SCHEDULE,
                                DBHelper.VALVE_TIME, DBHelper.VALVE_CANS,
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
                                        .indexOf("=") + 1)});

            }
            prog.dismiss();
            Intent i = new Intent(MainActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        } else {
            prog.dismiss();
            Toast.makeText(
                    MainActivity.this,
                    "No Valves Found For this Phone Number.....Please Try Again...",
                    Toast.LENGTH_SHORT).show();
        }

    }

}