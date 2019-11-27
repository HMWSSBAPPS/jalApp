package com.hmwssb.jalapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

public class HomePage extends Activity implements OnClickListener {

    LinearLayout ll_complaints, ll_valve_operations, refresh_lines;
    ImageView iv_logout;
    RelativeLayout rl_lang;
    Dialog prog;
    TextView tv_lang, tv_complaints, tv_valve_oper;
    int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.home_page);
        ll_valve_operations = (LinearLayout) findViewById(R.id.ll_valve_operations);
        refresh_lines = (LinearLayout) findViewById(R.id.refresh_lines);
//		Helper.getMobileNumFromDB(MainActivity.this,
//				DBHelper.TABLE_GENERAL, DBHelper.GEN_MOBILE),
        ll_valve_operations.setOnClickListener(this);
        refresh_lines.setOnClickListener(this);

        tv_complaints = (TextView) findViewById(R.id.tv_complaints);
        tv_valve_oper = (TextView) findViewById(R.id.tv_valve_oper);

        ll_complaints = (LinearLayout) findViewById(R.id.ll_complaints);
        ll_complaints.setOnClickListener(this);

        iv_logout = (ImageView) findViewById(R.id.iv_logout);
        iv_logout.setOnClickListener(this);
        tv_lang = (TextView) findViewById(R.id.tv_lang);
        rl_lang = (RelativeLayout) findViewById(R.id.rl_lang);
        rl_lang.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if (v == rl_lang) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (index == 0) {
                            index = 1;
                            Helper.lan_str = "telugu";
                            tv_lang.setText(" En ");
                            tv_complaints.setText("ఓ & ఎమ్ పర్యవేక్షణ");
                            tv_valve_oper.setText("వాల్వ్ కార్యకలాపాలు");
                        } else if (index == 1) {
                            Helper.lan_str = "english";
                            index = 0;
                            tv_lang.setText(" తె ");
                            tv_complaints.setText("O & M Monitoring");
                            tv_valve_oper.setText("VALVE OPERATIONS");
                        }
                    }
                }
                return true;
            }
        });
        if (Helper.lan_str.trim().equalsIgnoreCase("english")) {
            index = 0;
            tv_lang.setText(" తె ");
            tv_complaints.setText("O & M Monitoring");
            tv_valve_oper.setText("VALVE OPERATIONS");
        } else {
            index = 1;
            tv_lang.setText(" En ");
            tv_complaints.setText("ఓ & ఎమ్ పర్యవేక్షణ");
            tv_valve_oper.setText("వాల్వ్ కార్యకలాపాలు");
        }

    }

    public void Logout() {
        final Dialog dl = new Dialog(HomePage.this);
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
    public void onClick(View v) {

        if (v == ll_valve_operations) {
            Intent i = new Intent(HomePage.this, MainActivity.class);

            startActivity(i);
            finish();
        } else if (v == ll_complaints) {
            Intent i = new Intent(HomePage.this, SubMenuPage.class);

            startActivity(i);
            finish();
        } else if (v == refresh_lines) {
            Helper.getMobileNumFromDB(HomePage.this,
                    DBHelper.TABLE_GENERAL, DBHelper.GEN_MOBILE);
            Log.d("MobileNumber", "onClick: " + Helper.getMobileNumFromDB(HomePage.this,
                    DBHelper.TABLE_GENERAL, DBHelper.GEN_PASS_CODE));
            new HomePage.DownloadData().execute("mobileNo^passcode");
        } else if (v == iv_logout) {
            Logout();
        }

    }

    public class DownloadData extends AsyncTask<String, Void, String> {

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
                VALUE = new String[]{Helper.getMobileNumFromDB(HomePage.this,
                        DBHelper.TABLE_GENERAL, DBHelper.GEN_MOBILE),
                        Helper.getMobileNumFromDB(HomePage.this,
                                DBHelper.TABLE_GENERAL, DBHelper.GEN_PASS_CODE)};
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
                    Toast.makeText(HomePage.this,
                            response.substring(response.indexOf("-") + 1),
                            Toast.LENGTH_SHORT).show();
                } else if (response.trim().startsWith("$101")) {
                    prog.dismiss();
                    Toast.makeText(HomePage.this,
                            response.substring(response.indexOf("-") + 1),
                            Toast.LENGTH_SHORT).show();
                } else {
                    LoadData(response);
                }


            } catch (Exception e) {
                e.printStackTrace();
                prog.dismiss();
                Toast.makeText(HomePage.this, "Please Try Again...",
                        Toast.LENGTH_SHORT).show();
            }

        }

    }

    public void showProgressDialog() {

        try {
            prog = new Dialog(HomePage.this, R.style.AppTheme1);
            prog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            prog.setContentView(R.layout.progress_layout);
            prog.setCancelable(false);
            prog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void LoadData(String data) {

        if (data.indexOf("Table1=anyType{") != -1) {
            data = data.replace("Table1=anyType{", "@");
            String arr[] = data.split("\\@");
            DBHelper.deleteAllRows(HomePage.this, DBHelper.TABLE_VALVE);
            for (int i = 1; i < arr.length; i++) {
                System.out.println("arr[" + i + "].........." + arr[i]);
                String subSPlit[] = arr[i].split("\\;");
                System.out.println("sub len........" + subSPlit.length);
                String img = "0";
                if (subSPlit[15].substring(subSPlit[15].indexOf("=") + 1)
                        .trim().length() > 0) {
                    img = subSPlit[15].substring(subSPlit[15].indexOf("=") + 1)
                            .trim();
                }

                DBHelper.insertintoTable(
                        HomePage.this,
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
//            Intent intent = new Intent(HomePage.this, SubMenuPage.class);
//            startActivity(intent);
//            finish();
        } else {
            prog.dismiss();
            Toast.makeText(
                    HomePage.this,
                    "No Valves Found For this Phone Number.....Please Try Again...",
                    Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // startActivity(new Intent(HomePage.this, splashActivity.class));
        finish();
    }


}
