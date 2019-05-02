package com.hmwssb.jalapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

public class splashActivity extends Activity {

	SQLiteDatabase db;
	DBHelper dbh = new DBHelper(splashActivity.this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		db = openOrCreateDatabase(DBHelper.DB_NAME,
				SQLiteDatabase.CREATE_IF_NECESSARY, null);
		dbh.onCreate(db);
		dbh.close();
		db.close();

		setContentView(R.layout.splash);
		if (Helper.isNetworkAvailable(splashActivity.this) == true) {
			new CheckVersion().execute("version");
		} else {
			Helper.showShortToast(splashActivity.this,
					"Please check your internet connection...");
		}

	}

	private void resultActivity() {
		DBHelper dbh = new DBHelper(this);
		SQLiteDatabase db = dbh.getWritableDatabase();
		String Query = "SELECT * FROM " + DBHelper.TABLE_VALVE;
		Cursor c = db.rawQuery(Query, null);
		if (c.getCount() == 0) {
			Intent intent = new Intent(splashActivity.this, LoginActivity.class);
			startActivity(intent);
			finish();
		} else {
			SQLiteDatabase db1 = dbh.getWritableDatabase();
			String Query1 = "SELECT * FROM " + DBHelper.TABLE_GENERAL;
			Cursor c1 = db.rawQuery(Query1, null);
			String date = "";
			while (c1.moveToNext()) {
				date = c1.getString(c1.getColumnIndex(DBHelper.GEN_DATE));
				Log.e("Splash GEN_MOBILE", c1.getString(c1.getColumnIndex(DBHelper.GEN_MOBILE)));
			}
			c1.close();
			db1.close();
			if (!date.trim().equalsIgnoreCase(Helper.getDate())) {
				DBHelper.updateRowData(splashActivity.this,
						DBHelper.TABLE_VALVE, new String[] {
								DBHelper.VALVE_STATUS,
								DBHelper.VALVE_SUB_STATUS }, new String[] {
								"0", "0" }, null, null);
				DBHelper.updateRowData(splashActivity.this,
						DBHelper.TABLE_GENERAL,
						new String[] { DBHelper.GEN_DATE },
						new String[] { Helper.getDate() }, null, null);
			}
			Intent intent = new Intent(splashActivity.this, HomePage.class);
			startActivity(intent);
			finish();

//			Intent intent = new Intent(splashActivity.this, SubMenuPage.class);
//			startActivity(intent);
//			finish();
		}
		c.close();
		db.close();

	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			Logout();
		}
		return false;
	}

	public void Logout() {
		final Dialog dl = new Dialog(splashActivity.this);
		dl.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dl.setContentView(R.layout.show_logout);
		Button b_yes = (Button) dl.findViewById(R.id.logout_yes);
		b_yes.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				dl.cancel();

			}
		});
		Button b_no = (Button) dl.findViewById(R.id.logout_no);
		b_no.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				dl.cancel();

			}
		});
		dl.show();

	}


	public class CheckVersion extends AsyncTask<String, Void, String> {

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

				VALUE = new String[] { Helper.version };
				SOAP_ACTION = Helper.NAMESPACE
						+ Helper.Valve_mobile_version_control;
				request = new SoapObject(Helper.NAMESPACE,
						Helper.Valve_mobile_version_control);
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

			System.out.println("in post...." + response);
			try {

				if (response.trim().startsWith("$102")) {

					showAlert(response.substring(response.indexOf("-") + 1),1);
				} else if (response.trim().startsWith("$101")) {
					showAlert(response.substring(response.indexOf("-") + 1),1);
				} else {
					if (response.trim().startsWith("Success")) {
					resultActivity();

					} else if (response.trim().startsWith("Fail")) {
						showAlert(response.substring(response.indexOf(",") + 1),0);
					} else {
						showAlert(response,1);
					}

				}

			} catch (Exception e) {
				e.printStackTrace();
				showAlert("Please try again...",1);

			}

		}
	}

	public void showAlert(String str,final int index) {
		final Dialog dl = new Dialog(splashActivity.this);
		dl.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dl.setContentView(R.layout.show_version_alert);
		Button b_yes = (Button) dl.findViewById(R.id.logout_yes);
		TextView tv_logout_title = (TextView) dl
				.findViewById(R.id.tv_logout_title);
		tv_logout_title.setText(str);
		if(index==1){
			b_yes.setText("OK");
		}
		b_yes.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(index==1){
					finish();
				}else {
					clearApplicationData();
					try {
						startActivity(new Intent(
								Intent.ACTION_VIEW,
								Uri.parse("https://play.google.com/store/apps/details?id=com.hmwssb.jalapp")));
					} catch (android.content.ActivityNotFoundException anfe) {

					}
				}
				dl.cancel();

			}
		});
		dl.setCancelable(false);
		dl.show();

	}
	
	public void clearApplicationData() {
		File cache = getCacheDir();
		File appDir = new File(cache.getParent());
		if (appDir.exists()) {
			String[] children = appDir.list();
			for (String s : children) {
				if (!s.equals("lib")) {
					// if (!s.trim().equalsIgnoreCase("databases")) {
					deleteDir(new File(appDir, s));
					Log.i("TAG", "File /data/data/APP_PACKAGE/" + s
							+ " DELETED");
					// }
				}
			}
		}

	}

	public boolean deleteDir(File dir) {
		if (dir != null && dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				System.out
						.println("children[" + i + "]........." + children[i]);
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		return dir.delete();
	}

	private class CheckSF extends AsyncTask<Void, Void, String> {
		@Override
		protected void onPreExecute() {

		}


		@Override
		protected String doInBackground(Void... params) {
			String responsestring = "";

			String SOAP_ACTION = "";
			SoapObject request = null;
			String[] VALUE = null;
			try {


				int TIMEOUT_WAIT_TO_CONNECT = 60 * 60 * 60 * 60 * 1000;

				VALUE = new String[] { Helper.version };
				SOAP_ACTION = Helper.NAMESPACE
						+ Helper.Get_WorkMonitoring;
				request = new SoapObject(Helper.NAMESPACE,
						Helper.Get_WorkMonitoring);

				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
						SoapEnvelope.VER11);

				envelope.dotNet = true;
				envelope.setOutputSoapObject(request);
				HttpTransportSE androidHttpTransport = new HttpTransportSE(
						Helper.URL1, TIMEOUT_WAIT_TO_CONNECT);
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

			return responsestring;		}


		@Override
		protected void onPostExecute(String responsestring) {

			System.out.println("responsestring in post..."+responsestring);
			try {
				JSONObject jsonObject = new JSONObject(responsestring);
				String response = jsonObject.getString("output");


				System.out.println("in post...." + response);
				if (response.trim().startsWith("$102")) {
					Toast.makeText(splashActivity.this, "Server error 102"+response, Toast.LENGTH_SHORT).show();

				} else if (response.trim().startsWith("$101")) {
					Toast.makeText(splashActivity.this, "Server error 101"+response, Toast.LENGTH_SHORT).show();
				} else {
					if (response.trim().startsWith("Success")) {
						resultActivity();

					}  else {
						System.out.println("in fail...." + response.toString());
//						Toast.makeText(splashActivity.this, "Un-Autherised User"+response, Toast.LENGTH_SHORT).show();

					}

				}

			} catch (JSONException e) {
				e.printStackTrace();
				Toast.makeText(splashActivity.this, "JSON error ...", Toast.LENGTH_SHORT).show();
//				showAlert("Please try JSON error again...");

			}
			catch (Exception je){je.printStackTrace();
				Toast.makeText(splashActivity.this, "Please try again...", Toast.LENGTH_SHORT).show();
//				showAlert("Please try again...");
			}


		}
	}
}