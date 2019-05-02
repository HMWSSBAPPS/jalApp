package com.hmwssb.jalapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
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

public class HomePage extends Activity implements OnClickListener {

	LinearLayout ll_complaints, ll_valve_operations;
	ImageView iv_logout;
	RelativeLayout rl_lang;
	TextView tv_lang, tv_complaints, tv_valve_oper;
	int index = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.home_page);
		ll_valve_operations = (LinearLayout) findViewById(R.id.ll_valve_operations);

		ll_valve_operations.setOnClickListener(this);

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
							Helper.lan_str="telugu";
							tv_lang.setText(" En ");
							tv_complaints.setText("ఓ & ఎమ్ పర్యవేక్షణ");
							tv_valve_oper.setText("వాల్వ్ కార్యకలాపాలు");
						} else if (index == 1) {
							Helper.lan_str="english";
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
		} else if (v == iv_logout) {
			Logout();
		}

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		// startActivity(new Intent(LoginActivity.this, splashActivity.class));
		finish();
	}


}
