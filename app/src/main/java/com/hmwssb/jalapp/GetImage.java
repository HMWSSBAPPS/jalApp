package com.hmwssb.jalapp;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class GetImage {

	public ProgressBar prog;
	public ImageView img_view;
	public RelativeLayout ll_main;
	Boolean mrounded_falg;
	Context mtcx;
	String lineman_id = "", line_id = "", vlave_id = "";
	File mediaStorageDir;

	public GetImage(Context ctx, String url, ProgressBar pb, ImageView img,
			RelativeLayout ll, Boolean rounded_falg, String linmanId,
			String lineid, String valvid) {
		this.prog = pb;
		this.img_view = img;
		this.ll_main = ll;
		this.mrounded_falg = rounded_falg;
		this.mtcx = ctx;
		this.line_id = lineid;
		this.lineman_id = linmanId;
		this.vlave_id = valvid;

		new DownloadImage().execute(url);
		System.out.println("url.............." + url);
	}

	public class DownloadImage extends AsyncTask<String, Void, Bitmap> {

		@Override
		protected Bitmap doInBackground(String... params) {
			Bitmap bm = null;
			try {
				if (params[0].trim().startsWith("http")
						|| params[0].trim().startsWith("https")) {
					params[0] = params[0].replace(" ", "%20");
					URL aURL = new URL(params[0]);
					URLConnection conn = aURL.openConnection();
					conn.connect();
					InputStream is = conn.getInputStream();
					BufferedInputStream bis = new BufferedInputStream(is);
					bm = BitmapFactory.decodeStream(bis);
					File f = getOutputMediaFile(line_id.trim() + "-"
							+ vlave_id.trim());
					FileOutputStream fos = null;
					fos = new FileOutputStream(f);
					bm.compress(Bitmap.CompressFormat.JPEG, 70, fos);
					bis.close();
					is.close();
					fos.close();
					DBHelper.updateRowData(mtcx, DBHelper.TABLE_VALVE,
							new String[] { DBHelper.VALVE_IMAGE },
							new String[] { f.getPath() }, new String[] {
									DBHelper.VALVE_LINEMANID,
									DBHelper.VALVE_VALVEID,
									DBHelper.VLAVE_LINEID }, new String[] {
									lineman_id, vlave_id, line_id });
				} else {
					File f = new File(params[0]);
					bm = BitmapFactory.decodeFile(f.getAbsolutePath());

				}

			} catch (IOException e) {
				e.printStackTrace();

			}
			return bm;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			try {
				if (prog != null) {
					prog.setVisibility(View.GONE);
				}

				if (img_view != null) {
					if (result != null) {
						if (mrounded_falg == true) {
							Bitmap bp = Helper.getCircularBitmapImage(
									(Activity) mtcx, result);
							img_view.setImageBitmap(bp);
						} else {
							img_view.setImageBitmap(result);
						}
					}
					img_view.setVisibility(View.VISIBLE);
				} else if (ll_main != null) {
					if (result != null) {
						Drawable dr = new BitmapDrawable(result);
						ll_main.setBackgroundDrawable(dr);
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
				if (prog != null) {
					prog.setVisibility(View.GONE);
				}
				if (img_view != null) {
					img_view.setVisibility(View.VISIBLE);
				}
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
			mediaStorageDir = new File(mtcx.getFilesDir().getPath()
					+ File.separator + Helper.IMAGE_DIRECTORY_NAME);

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
				+ imgname + ".JPEG");

		return mediaFile;
	}
}
