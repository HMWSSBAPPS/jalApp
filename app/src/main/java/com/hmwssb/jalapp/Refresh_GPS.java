package com.hmwssb.jalapp;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class Refresh_GPS extends Fragment {

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    HashMap<String, List<String>> valve_images;

    HashMap<String, List<String>> valve_lineman;
    HashMap<String, List<String>> valve_lineid;
    HashMap<String, List<String>> valve_valveid;

    HashMap<String, List<Integer>> listDataChildImage;
    HashMap<String, List<String>> listDataChildSchedule;
    HashMap<String, List<String>> listDataChildTime;
    List<String> water_leakage_id = new ArrayList<String>();
    List<String> sewage_id = new ArrayList<String>();
    Dialog prog;
    Vector<String> dataVec = new Vector<String>(),
            matched_vec = new Vector<String>(),
            line_vec = new Vector<String>(),
            line_dataVe = new Vector<String>(),
            valve_Vec = new Vector<String>();
    Vector<String[]> alert_Vec = new Vector<String[]>();

    protected LocationManager locationManager;
    LocationListener mlocListener;
    Location loc = null;
    String gps_data = "";
    String child_subSplit[] = null, child_split[] = null;
    Dialog dg;
    LinearLayout ll_report_main;
    int hit_index = 0, selPos = 0;
    TextView tv_gps;

    private IntentFilter mIntentFilter;

    private static final int PERMISSION_REQUEST_CODE = 200;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        View view = inflater.inflate(R.layout.refresh_gps, container, false);

        expListView = (ExpandableListView) view.findViewById(R.id.lvExp);
        tv_gps = (TextView) view.findViewById(R.id.tv_gps);

        // preparing list data
		/*turnGPSOn();
		showProgressDialog();
		getActivity().startService(new Intent(getActivity(), ZipprGPSService.class));*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermission()) {
                //If permission is already having then showing the toast
//                Toast.makeText(EntryActivity.this, "You already have the both permissions", Toast.LENGTH_LONG).show();
                turnGPSOn();
                showProgressDialog();
                getActivity().startService(new Intent(getActivity(), ZipprGPSService.class));
                //Existing the method with return
            } else {
                //If the app has not the permission then asking for the permission
//                            requestStoragePermission();
                requestPermission();
            }
        } else {
            turnGPSOn();
            showProgressDialog();
            getActivity().startService(new Intent(getActivity(), ZipprGPSService.class));
        }

        //gpsFinding();

        // gps_data = "17.478924047284277,78.48647871748868";
        // tv_gps.setText("GPS : " + gps_data);
        // Helper.lat = 17.478924047284277;
        // Helper.lon = 78.48647871748868;
        // Helper.showShortToast(getActivity(), "GPS Coordinates Received....");
        // new LoadData().execute();
        expListView.setOnChildClickListener(new OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                System.out.println("valve vec......." + valve_Vec);
                String str = valve_Vec.elementAt(groupPosition).toString();
                child_split = str.split("\\@");
                System.out.println("child_split[childPosition].........."
                        + child_split[childPosition]);
                child_subSplit = child_split[childPosition].split("\\^");
                showAlert();

                // new PostToServer()
                // .execute("linemenid^lineid^valveid^valvestatus^mobileNo^lat^lon");

                return false;
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(ZipprGPSService.BROADCAST_ACTION);
        getActivity().registerReceiver(broadcastReceiver, mIntentFilter);
    }

    public void showProgressDialog() {

        try {
            prog = new Dialog(getActivity(), R.style.AppTheme1);
            prog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            prog.setContentView(R.layout.progress_layout);
            prog.setCancelable(false);
            prog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String getTime() {
        String retStr = "";
        Calendar c = Calendar.getInstance();
        String hh = "" + c.get(Calendar.HOUR_OF_DAY);
        if (hh.length() == 1) {
            hh = "0" + hh;
        }
        String mm = "" + c.get(Calendar.MINUTE);
        if (mm.length() == 1) {
            mm = "0" + mm;
        }
        retStr = hh + ":" + mm;
        return retStr;
    }

    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        listDataChildImage = new HashMap<String, List<Integer>>();
        listDataChildSchedule = new HashMap<String, List<String>>();

        valve_images = new HashMap<String, List<String>>();
        valve_lineman = new HashMap<String, List<String>>();
        valve_lineid = new HashMap<String, List<String>>();
        valve_valveid = new HashMap<String, List<String>>();

        listDataChildTime = new HashMap<String, List<String>>();

        for (int i = 0; i < line_dataVe.size(); i++) {
            listDataHeader.add(line_dataVe.elementAt(i).toString());
            String split[] = valve_Vec.elementAt(i).toString().split("\\@");
            List<String> sewage = new ArrayList<String>();
            List<Integer> image = new ArrayList<Integer>();
            List<String> Valve_image = new ArrayList<String>();
            List<String> schedule = new ArrayList<String>();
            List<String> time = new ArrayList<String>();
            List<String> lineman = new ArrayList<String>();
            List<String> line = new ArrayList<String>();
            List<String> valvid = new ArrayList<String>();
            for (int j = 0; j < split.length; j++) {
                String subplsit[] = split[j].split("\\^");
                sewage.add(subplsit[1] + "(" + subplsit[2] + ")");
                image.add(Integer.parseInt(subplsit[3]));
                Valve_image.add(subplsit[6]);
                schedule.add(subplsit[7]);
                time.add(subplsit[8]);
                lineman.add(subplsit[4]);
                line.add(subplsit[5]);
                valvid.add(subplsit[0]);
            }
            listDataChild.put(listDataHeader.get(i), sewage);
            listDataChildImage.put(listDataHeader.get(i), image);

            valve_images.put(listDataHeader.get(i), Valve_image);

            valve_lineman.put(listDataHeader.get(i), lineman);
            valve_lineid.put(listDataHeader.get(i), line);
            valve_valveid.put(listDataHeader.get(i), valvid);

            listDataChildSchedule.put(listDataHeader.get(i), schedule);
            listDataChildTime.put(listDataHeader.get(i), time);
        }

    }

    public class LoadData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... params) {

            DBHelper dbh = new DBHelper(getActivity());
            SQLiteDatabase db = dbh.getWritableDatabase();
            String Query = "SELECT * FROM " + DBHelper.TABLE_VALVE;
            Cursor c = db.rawQuery(Query, null);
            dataVec.removeAllElements();
            matched_vec.removeAllElements();
            line_vec.removeAllElements();
            line_dataVe.removeAllElements();
            valve_Vec.removeAllElements();
            while (c.moveToNext()) {
                dataVec.addElement(c.getString(c
                        .getColumnIndex(DBHelper.VALVE_SID))
                        + "^"
                        + c.getString(c
                        .getColumnIndex(DBHelper.VALVE_LINEMANID))
                        + "^"
                        + c.getString(c.getColumnIndex(DBHelper.VLAVE_LINEID))
                        + "^"
                        + c.getString(c.getColumnIndex(DBHelper.VALVE_VALVEID))
                        + "^"
                        + c.getString(c.getColumnIndex(DBHelper.VALVE_LANDMARK))
                        + "^"
                        + c.getString(c.getColumnIndex(DBHelper.VALVE_AREA))
                        + "^"
                        + c.getString(c
                        .getColumnIndex(DBHelper.VALVE_VALVETYPE))
                        + "^"
                        + c.getString(c.getColumnIndex(DBHelper.VALVE_LATITUDE))
                        + "^"
                        + c.getString(c
                        .getColumnIndex(DBHelper.VALVE_LONGITUDE))
                        + "^"
                        + c.getString(c.getColumnIndex(DBHelper.VALVE_STATUS))
                        + "^"
                        + c.getString(c.getColumnIndex(DBHelper.VALVE_IMAGE))
                        + "^"
                        + c.getString(c.getColumnIndex(DBHelper.VALVE_SCHEDULE))
                        + "^"
                        + c.getString(c.getColumnIndex(DBHelper.VALVE_TIME))
                        + "^"
                        + c.getString(c.getColumnIndex(DBHelper.VALVE_CANS))
                        + "^"
                        + c.getString(c.getColumnIndex(DBHelper.VALVE_CENTRIC))
                        + "^"
                        + c.getString(c
                        .getColumnIndex(DBHelper.VALVE_SUPLY_AREA)));
            }
            c.close();
            db.close();
            System.out.println("dataVEc......" + dataVec);
            for (int i = 0; i < dataVec.size(); i++) {

                String split[] = dataVec.elementAt(i).toString().split("\\^");
                Location l1 = new Location("");
                l1.setLatitude(Helper.lat);
                l1.setLongitude(Helper.lon);
                Location l2 = new Location("");

                l2.setLatitude(Double.parseDouble(split[7]));
                l2.setLongitude(Double.parseDouble(split[8]));
                float distanceInMeters = l1.distanceTo(l2);
                if (distanceInMeters <= 100) {
                    matched_vec.addElement(dataVec.elementAt(i).toString());
                    if (!line_vec.contains(split[2])) {
                        line_vec.addElement(split[2]);
                        line_dataVe
                                .addElement(split[15] + "(" + split[2] + ")");
                        valve_Vec.addElement(split[3] + "^" + split[4] + "^"
                                + split[6] + "^" + split[9] + "^" + split[1]
                                + "^" + split[2] + "^" + split[10] + "^"
                                + split[11] + "^" + split[12] + "^" + split[13]
                                + "^" + split[14]);
                    } else {
                        valve_Vec
                                .setElementAt(
                                        valve_Vec.elementAt(line_vec
                                                .indexOf(split[2]))
                                                + "@"
                                                + split[3]
                                                + "^"
                                                + split[4]
                                                + "^"
                                                + split[6]
                                                + "^"
                                                + split[9]
                                                + "^"
                                                + split[1]
                                                + "^"
                                                + split[2]
                                                + "^"
                                                + split[10]
                                                + "^"
                                                + split[11]
                                                + "^"
                                                + split[12]
                                                + "^"
                                                + split[13] + "^" + split[14],
                                        line_vec.indexOf(split[2]));

                    }

                }
            }

            System.out.println("vec............." + matched_vec);
            System.out.println("line vec............." + line_vec);
            System.out.println("line data vec............." + line_dataVe);
            System.out.println("valve vec............." + valve_Vec);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (matched_vec.isEmpty()) {
                Helper.showShortToast(getActivity(),
                        "No Valves Found for Your Current Location...");
                prog.dismiss();
            } else {
                prepareListData();
                listAdapter = new ExpandableListAdapter(getActivity(),
                        listDataHeader, listDataChild, listDataChildImage,
                        valve_images, listDataChildSchedule, listDataChildTime,
                        valve_lineman, valve_lineid, valve_valveid);
                expListView.setAdapter(listAdapter);
                prog.dismiss();
            }

        }

    }

    public void gpsFinding() {
        try {
            loc = null;
            turnGPSOn();
            showProgressDialog();
            locationManager = (LocationManager) getActivity().getSystemService(
                    getActivity().LOCATION_SERVICE);
            mlocListener = new MyLocationListener();
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,// network
                    // provider,GPS_PROVIDER
                    1000, 20, mlocListener);
        } catch (Exception ex) {
        }
    }

    private void turnGPSOn() {
        try {

            String provider = Settings.Secure.getString(getActivity()
                            .getContentResolver(),
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

            String provider = Settings.Secure.getString(getActivity()
                            .getContentResolver(),
                    Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

            if (provider.contains("gps")) { // if gps is enabled
                final Intent poke = new Intent();
                poke.setClassName("com.android.settings",
                        "com.android.settings.widget.SettingsAppWidgetProvider");
                poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
                poke.setData(Uri.parse("3"));
                getActivity().sendBroadcast(poke);
            }
        } catch (Exception ex) {

        }
    }

    private class MyLocationListener implements LocationListener {

        public void onLocationChanged(Location location) {

            if (location != null) {
                loc = location;
                Helper.lat = loc.getLatitude();
                Helper.lon = loc.getLongitude();
                gps_data = loc.getLatitude() + "-" + loc.getLongitude();
                tv_gps.setText("GPS : " + gps_data);
                Helper.showShortToast(getActivity(),
                        "GPS Coordinates Received....");
                locationManager.removeUpdates(mlocListener);
                location = null;
                turnGPSOff();
                new LoadData().execute();
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

    public class PostToServer extends AsyncTask<String, Void, String> {

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
            String sttaus = "";
            int TIMEOUT_WAIT_TO_CONNECT = 60 * 60 * 60 * 60 * 1000;
            if (hit_index == 1) {
                if (child_subSplit[3].trim().equals("0")
                        || child_subSplit[3].trim().equals("2")) {
                    sttaus = "OPEN";
                } else if (child_subSplit[3].trim().equals("1")) {
                    sttaus = "CLOSE";
                }

                VALUE = new String[]{
                        child_subSplit[4],
                        child_subSplit[5],
                        child_subSplit[0],
                        sttaus,
                        Helper.getMobileNumFromDB(getActivity(),
                                DBHelper.TABLE_GENERAL, DBHelper.GEN_MOBILE),
                        "" + Helper.lat, "" + Helper.lon};
            } else if (hit_index == 2) {
                String arr[] = alert_Vec.elementAt(selPos);
                if (arr[9].trim().equals("0") || arr[9].trim().equals("2")) {
                    sttaus = "OPEN";
                } else if (arr[9].trim().equals("1")) {
                    sttaus = "CLOSE";
                }
                VALUE = new String[]{
                        arr[1],
                        arr[2],
                        arr[3],
                        sttaus,
                        Helper.getMobileNumFromDB(getActivity(),
                                DBHelper.TABLE_GENERAL, DBHelper.GEN_MOBILE),
                        "" + Helper.lat, "" + Helper.lon};
            }

            SOAP_ACTION = Helper.NAMESPACE + Helper.VALVE_UPDATE_METHOD_NAME;
            request = new SoapObject(Helper.NAMESPACE,
                    Helper.VALVE_UPDATE_METHOD_NAME);

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
                    Toast.makeText(getActivity(),
                            response.substring(response.indexOf("-") + 1),
                            Toast.LENGTH_SHORT).show();
                } else if (response.trim().startsWith("$101")) {
                    prog.dismiss();
                    Toast.makeText(getActivity(),
                            response.substring(response.indexOf("-") + 1),
                            Toast.LENGTH_SHORT).show();
                } else {
                    prog.dismiss();
                    if (response.trim().toLowerCase().indexOf("success") != -1) {
                        String sttaus = "";

                        if (hit_index == 1) {
                            if (child_subSplit[3].trim().equals("0")
                                    || child_subSplit[3].trim().equals("2")) {
                                sttaus = "1";
                            } else if (child_subSplit[3].trim().equals("1")) {
                                sttaus = "2";
                            }
                            String split[] = child_subSplit[8].split("\\-");
                            split[0] = getTime();
                            if (sttaus.trim().equalsIgnoreCase("2")) {
                                split[1] = getTime();
                            }
                            String time = split[0] + "-" + split[1];
                            DBHelper.updateRowData(getActivity(),
                                    DBHelper.TABLE_VALVE, new String[]{
                                            DBHelper.VALVE_STATUS,
                                            DBHelper.VALVE_TIME},
                                    new String[]{sttaus, time},
                                    new String[]{DBHelper.VALVE_LINEMANID,
                                            DBHelper.VLAVE_LINEID,
                                            DBHelper.VALVE_VALVEID},
                                    new String[]{child_subSplit[4],
                                            child_subSplit[5],
                                            child_subSplit[0]});
                            DBHelper.updateRowData(getActivity(),
                                    DBHelper.TABLE_VALVE,
                                    new String[]{DBHelper.VALVE_SUB_STATUS},
                                    new String[]{"1"}, new String[]{
                                            DBHelper.VALVE_LINEMANID,
                                            DBHelper.VLAVE_LINEID},
                                    new String[]{child_subSplit[4],
                                            child_subSplit[5]});
                            dg.cancel();
                        } else if (hit_index == 2) {
                            String arr[] = alert_Vec.elementAt(selPos);
                            if (arr[9].trim().equals("0")
                                    || arr[9].trim().equals("2")) {
                                sttaus = "1";
                            } else if (arr[9].trim().equals("1")) {
                                sttaus = "2";
                            }
                            String split[] = child_subSplit[12].split("\\-");
                            split[0] = getTime();
                            if (sttaus.trim().equalsIgnoreCase("2")) {
                                split[1] = getTime();
                            }
                            String time = split[0] + "-" + split[1];
                            DBHelper.updateRowData(getActivity(),
                                    DBHelper.TABLE_VALVE, new String[]{
                                            DBHelper.VALVE_STATUS,
                                            DBHelper.VALVE_TIME},
                                    new String[]{sttaus, time},
                                    new String[]{DBHelper.VALVE_LINEMANID,
                                            DBHelper.VLAVE_LINEID,
                                            DBHelper.VALVE_VALVEID},
                                    new String[]{arr[1], arr[2], arr[3]});
                            DBHelper.updateRowData(getActivity(),
                                    DBHelper.TABLE_VALVE,
                                    new String[]{DBHelper.VALVE_SUB_STATUS},
                                    new String[]{"1"}, new String[]{
                                            DBHelper.VALVE_LINEMANID,
                                            DBHelper.VLAVE_LINEID},
                                    new String[]{arr[1], arr[2]});
                            dg.cancel();
                        }

                        new LoadData().execute();
                    } else if (response.trim().toLowerCase().indexOf("fail") != -1) {
                        Toast.makeText(getActivity(), response,
                                Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getActivity(), response,
                                Toast.LENGTH_SHORT).show();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                prog.dismiss();
                Toast.makeText(getActivity(), "Please Try Again...",
                        Toast.LENGTH_SHORT).show();
            }

        }
    }

    public void showAlert() {
        dg = new Dialog(getActivity());
        dg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dg.setContentView(R.layout.show_alert);
        TextView tv_selected_title = (TextView) dg
                .findViewById(R.id.tv_selected_title);
        tv_selected_title.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        TextView tv_list_title = (TextView) dg.findViewById(R.id.tv_list_title);
        tv_list_title.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        TextView tv_sel_valve_name = (TextView) dg
                .findViewById(R.id.tv_sel_valve_name);
        tv_sel_valve_name.setText(child_subSplit[1] + "(" + child_subSplit[2]
                + ")");

        TextView tv_sel_schedule = (TextView) dg
                .findViewById(R.id.tv_sel_schedule);
        tv_sel_schedule.setText("Schedule : " + child_subSplit[7]);

        TextView tv_cans = (TextView) dg.findViewById(R.id.tv_cans);
        if (child_subSplit[9].trim().equalsIgnoreCase("null")) {
            tv_cans.setText("Total Cans : 0");
        } else {
            tv_cans.setText("Total Cans : " + child_subSplit[9]);
        }

        ImageView img_sel_val_stat = (ImageView) dg
                .findViewById(R.id.img_sel_val_stat);
        ProgressBar pb = (ProgressBar) dg.findViewById(R.id.pb_image);
        ImageView img_valve = (ImageView) dg.findViewById(R.id.img_valve);

        if (child_subSplit[6].trim().equals("0")
                || child_subSplit[6].length() == 0) {
            pb.setVisibility(View.INVISIBLE);
            img_valve.setVisibility(View.VISIBLE);
        } else {
            pb.setVisibility(View.INVISIBLE);
            img_valve.setVisibility(View.VISIBLE);
            img_valve.setImageURI(Uri.parse(child_subSplit[6]));
        }

        if (child_subSplit[3].trim().equals("0")) {
            img_sel_val_stat.setImageResource(R.drawable.valve_close_1);
        } else if (child_subSplit[3].trim().equals("1")) {
            img_sel_val_stat.setImageResource(R.drawable.valve_open_1);
        } else if (child_subSplit[3].trim().equals("2")) {
            img_sel_val_stat.setImageResource(R.drawable.valve_open_yellow_1);
        }
        img_sel_val_stat.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (Helper.isNetworkAvailable(getActivity()) == true) {
                    hit_index = 1;
                    new PostToServer()
                            .execute("linemenid^lineid^valveid^valvestatus^mobileNo^lat^lon");
                } else {
                    Helper.showShortToast(getActivity(),
                            "please check your internet connection...");
                }

            }
        });
        ll_report_main = (LinearLayout) dg.findViewById(R.id.ll_report_main);
        createCentricLayout();

        Button btn_close = (Button) dg.findViewById(R.id.btn_close);
        btn_close.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dg.cancel();

            }
        });
        dg.show();

    }

    public void createCentricLayout() {
        ll_report_main.removeAllViews();
        alert_Vec.removeAllElements();
        System.out.println("child subsplit[10]......" + child_subSplit[10]);
        System.out.println("child subsplit[5]......" + child_subSplit[5]);
        System.out.println("child subsplit[0]......" + child_subSplit[0]);
        if (child_subSplit[10].trim().length() != 0
                && !child_subSplit[10].trim().equalsIgnoreCase("null")) {

            for (int i = 0; i < dataVec.size(); i++) {
                System.out.println("childSpli[" + i + "]......"
                        + dataVec.elementAt(i).toString());
                String split[] = dataVec.elementAt(i).toString().split("\\^");
                if (split[2].trim().equalsIgnoreCase(child_subSplit[5])
                        && split[14].trim()
                        .equalsIgnoreCase(child_subSplit[10])) {
                    if (!split[3].trim().equalsIgnoreCase(child_subSplit[0])) {
                        alert_Vec.addElement(split);
                    }

                }
            }

            for (int i = 0; i < alert_Vec.size(); i++) {
                String arr[] = alert_Vec.elementAt(i);
                View v = getActivity().getLayoutInflater().inflate(
                        R.layout.alert_adapter, null);
                TextView tv_sel_valve_name = (TextView) v
                        .findViewById(R.id.tv_sel_valve_name);
                TextView tv_sel_schedule = (TextView) v
                        .findViewById(R.id.tv_sel_schedule);

                TextView tv_sel_cans = (TextView) v
                        .findViewById(R.id.tv_sel_cans);
                if (arr[13].trim().equalsIgnoreCase("null")) {
                    tv_sel_cans.setText("Linked Cans : 0");
                } else {
                    tv_sel_cans.setText("Linked Cans : " + arr[13]);
                }

                tv_sel_valve_name.setText(arr[4] + "(" + arr[6] + ")");
                tv_sel_schedule.setText("Schedule : " + arr[11]);

                ProgressBar pb = (ProgressBar) v.findViewById(R.id.pb_image);
                ImageView img_valve = (ImageView) v
                        .findViewById(R.id.img_valve);

                if (arr[10].trim().equals("0") || arr[10].length() == 0) {
                    pb.setVisibility(View.INVISIBLE);
                    img_valve.setVisibility(View.VISIBLE);
                } else {
                    pb.setVisibility(View.INVISIBLE);
                    img_valve.setVisibility(View.VISIBLE);
                    img_valve.setImageURI(Uri.parse(arr[10]));
                }
                ImageView img_sel_val_stat = (ImageView) v
                        .findViewById(R.id.img_sel_val_stat);
                if (arr[9].trim().equals("0")) {
                    img_sel_val_stat.setImageResource(R.drawable.valve_close_1);
                } else if (arr[9].trim().equals("1")) {
                    img_sel_val_stat.setImageResource(R.drawable.valve_open_1);
                } else if (arr[9].trim().equals("2")) {
                    img_sel_val_stat
                            .setImageResource(R.drawable.valve_open_yellow_1);
                }
                final int pos = i;
                img_sel_val_stat.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (Helper.isNetworkAvailable(getActivity()) == true) {
                            selPos = pos;
                            hit_index = 2;
                            new PostToServer()
                                    .execute("linemenid^lineid^valveid^valvestatus^mobileNo^lat^lon");
                        } else {
                            Helper.showShortToast(getActivity(),
                                    "please check your internet connection...");
                        }

                    }
                });
                ll_report_main.addView(v);
            }

        }

    }

    private View addVerticalSpace() {
        View v = new View(getActivity());
        LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT, 5);
        v.setBackgroundColor(Color.parseColor("#0F5390"));
        v.setLayoutParams(layoutParams);
        return v;
    }


    @Override
    public void onPause() {
        getActivity().unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        getActivity().registerReceiver(broadcastReceiver, mIntentFilter);
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
                getActivity().stopService(new Intent(getActivity(), ZipprGPSService.class));

                System.out.println("lat........" + lat);
                System.out.println("lon........" + lon);
                Helper.lat = Double.parseDouble(lat);
                Helper.lon = Double.parseDouble(lon);
                tv_gps.setText("GPS : " + gps_data);
                Helper.showShortToast(getActivity(),
                        "GPS Coordinates Received....");

                turnGPSOff();
                new LoadData().execute();


            }
        }

    };

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
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

    /*Method used to check the runtime permissions for location*/
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getActivity(), ACCESS_FINE_LOCATION);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    /*Requesting for the required permissions*/
    private void requestPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (locationAccepted) {
                        turnGPSOn();
                        showProgressDialog();
                        getActivity().startService(new Intent(getActivity(), ZipprGPSService.class));
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                                showMessageOKCancel("You need to allow access the permission",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{ACCESS_FINE_LOCATION},
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
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


}
