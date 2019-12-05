package com.hmwssb.jalapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.graphics.BitmapFactory.decodeStream;

public class Helper {
    //        public static int reqWidth = 320, reqHeight = 240;
    public static int reqWidth = 500, reqHeight = 400;

    public static Dialog dialogbox;

    public static String version = "2.2";

    public static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;

    public static final String IMAGE_DIRECTORY_NAME = "LINE_MAN/CAPTURED_IMAGES";
    public static final String Get_WorkMonitoring = "Get_WorkMonitoring";

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static String lan_str = "english";

    public static double lat = 0.0, lon = 0.0;
    public static String NAMESPACE = "http://tempuri.org/",

    AUTH_METHOD_NAME = "user_auth",
            VALVE_DATA_METHOD_NAME = "valve_data",
            VALVE_UPDATE_METHOD_NAME = "valve_update",
            Valve_mobile_version_control = "Valve_mobile_version_control",
    // URL = "http://117.239.133.135/val/mainservice.asmx?WSDL",
    // URL =
    // "http://bms.hyderabadwater.gov.in/val/mainservice.asmx?WSDL",
    URL = "https://bms.hyderabadwater.gov.in/val2/mainservice.asmx?WSDL";

    public static String URL1 = "http://65.19.149.210/RMCTrade/RMC_Trade.asmx?WSDL";

    // Testing url
//       public static String SUBMIT_URL = "https://test.hyderabadwater.gov.in:9235/CTL/ERP/EIF/CommonService/IL/ILineManAppCodeTreeUC?WSDL";
//       public static String SUBMIT_URL = "https://test3.hyderabadwater.gov.in:8235/CTL/ERP/EIF/CommonService/IL/ILineManAppCodeTreeUC?WSDL";

    // Live url
    public static String SUBMIT_URL = "https://erp.hyderabadwater.gov.in:91/CTL/ERP/EIF/CommonService/IL/ILineManAppCodeTreeUC?wsdl";

    public static String SaveChlorinationLineManAppWithCan = "SaveChlorinationLineManAppWithCAN",
            SaveChlorinationLineManApp = "SaveChlorinationLineManApp",
            SaveValveLeakagesLineManApp = "SaveValveLeakagesLineManApp",
            SavePipeLineLeakagesLineManApp = "SavePipeLineLeakagesLineManApp",
            SavePollutedWaterLineManApp = "SavePollutedWaterLineManApp",
            SaveSewerageOverflowLineManApp = "SaveSewerageOverflowLineManApp",
            SaveMissingManholeCoverLineManApp = "SaveMissingManholeCoverLineManApp",
            SaveNonReceiptOfBillsLineManApp = "SaveNonReceiptOfBillsLineManApp",
            SaveRequestMeterLineManApp = "SaveRequestMeterLineManApp",
            SaveIllegalConnectionInfo = "SaveIllegalConnectionInfo",
            SaveLowWaterPressureForLineManMobileApp = "SaveLowWaterPressureForLineManMobileApp";

    public static String SECTION_CODE_URL = "http://bms.hyderabadwater.gov.in/val/mainservice.asmx?WSDL",
            getsection = "getsection", illegal_section = "illegal_section";

    public static void showLongToast(Activity activity, String str) {
        Toast.makeText(activity, str, Toast.LENGTH_LONG).show();
    }

    public static void showShortToast(Activity activity, String str) {
        Toast.makeText(activity, str, Toast.LENGTH_SHORT).show();
    }

    public static Drawable showIcon(int id, Context a) {
        Drawable dr_show = a.getResources().getDrawable(id);
        dr_show.setBounds(0, 0, dr_show.getIntrinsicWidth(),
                dr_show.getIntrinsicHeight());
        return dr_show;

    }

    public static String getDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date());
    }

    public static boolean isNetworkAvailable(Context ctx) {
        ConnectivityManager conMgr = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting())
            return true;
        else
            return false;
    }

    public static Bitmap getCircularBitmapImage(Activity a, Bitmap source) {
        // Bitmap source = BitmapFactory.decodeResource(a.getResources(),
        // drwabale_id);
        int size = Math.min(source.getWidth(), source.getHeight());
        System.out.println("size........" + size);
        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;
        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
        // if (squaredBitmap != source) {
        // source.recycle();
        // }
        Bitmap bitmap = Bitmap
                .createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(squaredBitmap,
                BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);
        float r = size / 2f;
        canvas.drawCircle(r, r, r, paint);
        // squaredBitmap.recycle();
        return bitmap;
    }

    public static String readRawFile(int file, Activity activity) {

        String msg = "";
        int ch;
        StringBuffer sb = new StringBuffer("");
        InputStream is;
        BufferedReader br;
        try {
            is = activity.getResources().openRawResource(file);

            while ((ch = is.read()) != -1) {
                sb.append((char) ch);
            }
            msg = new String(sb);
        } catch (Exception e) {
            msg = null;
        }
        return msg;
    }

    public static Bitmap getImage(/*String filePath*/Context context, String filePath, Uri uri) {

        /*BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);
        bmp = Bitmap.createScaledBitmap(bmp, bmp.getWidth(), bmp.getHeight(),
                true);
        return bmp;*/

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(filePath, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
//        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);
        Bitmap bmp = null;
        try {
//            Bitmap bitmap = BitmapFactory.decodeStream( is );
            bmp = decodeStream(context.getContentResolver().openInputStream(uri));
            //        Bitmap bmp = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));
//            bmp = Bitmap.createScaledBitmap(bmp, bmp.getWidth(), bmp.getHeight(), true);
            bmp = Bitmap.createScaledBitmap(bmp, reqWidth, reqHeight, true);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return bmp;
    }

    /*Temp copy */
  /*  public static Bitmap getImageTemp(Context context, String filePath, Uri uri) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(filePath, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
//        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);
        Bitmap bmp = null;
        try {
//            Bitmap bitmap = BitmapFactory.decodeStream( is );
            bmp = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));
            //        Bitmap bmp = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));
            bmp = Bitmap.createScaledBitmap(bmp, bmp.getWidth(), bmp.getHeight(),
                    true);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return bmp;
    }*/

    public static String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] b = baos.toByteArray();
//        String temp = com.hmwssb.jalapp.Base64.encodeBytes(b);
        String temp = android.util.Base64.encodeToString(b, Base64.NO_WRAP);
        return temp;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    public static void setAutoOrientationEnabled(ContentResolver resolver,
                                                 boolean enabled) {
        Settings.System.putInt(resolver,
                Settings.System.ACCELEROMETER_ROTATION, enabled ? 1 : 0);
    }

    public static String dateForServer(String str) {
        String arr[] = str.split("\\/");
        return arr[1] + "/" + arr[0] + "/" + arr[2];
    }

    public static String[] FileReading(Activity av, int filename) {
        StringBuffer buf = new StringBuffer();
        InputStream is = null;
        String arr[] = null;
        try {
            String str = "";
            is = av.getResources().openRawResource(filename);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is));
            if (is != null) {
                while ((str = reader.readLine()) != null) {
                    buf.append(str + "\n");
                }
            }
            arr = buf.toString().split("\\n");
        } catch (IOException e) {
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException io) {
            }
        }
        return arr;
    }

    public static boolean getcheckPhoneno(String str) {
        boolean flag = false;
        str = str.trim();
        // System.out.println("i m in phone no check");
        if (str.length() < 10) {
            flag = true;
        } else if (!str.startsWith("7") && !str.startsWith("8")
                && !str.startsWith("9")) {
            flag = true;
            // System.out.println("phone no::::" + str);
        } else {
            int cnt = 1;
            char[] chArr = new char[str.length()];
            str.getChars(0, str.length(), chArr, 0);
            for (int i = 1; i < chArr.length; i++) {
                // System.out.println(chArr[0] + "" + chArr[i]);
                if (chArr[0] == chArr[i]) {
                    cnt++;
                }
            }
            // System.out.println("cnt value:" + cnt);
            if (cnt == str.length()) {
                flag = true;
            }
        }
        return flag;
    }

    public static boolean getMemoryCardStatus() {
        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    public static String getDateStamp() {
        String timeStamp = "";
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        String year = "" + cal.get(Calendar.YEAR);
        String month = "" + (cal.get(Calendar.MONTH) + 1);
        if (month.length() == 1) {
            month = "0" + month;
        }
        String day = "" + cal.get(Calendar.DAY_OF_MONTH);
        if (day.length() == 1) {
            day = "0" + day;
        }
        timeStamp = day + "/" + month + "/" + year;
        return timeStamp;
    }

    public static void AlertBox(Context ctx, String msg) {

        new AlertDialog.Builder(ctx).setTitle("Message").setMessage(msg)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                }).show();
    }

    public static void AlertBox(Context ctx, String header, String msg) {

        if (msg.length() > 0) {
            if (Character.isLowerCase(msg.charAt(0))) {
                msg = Character.toUpperCase(msg.charAt(0)) + msg.substring(1);
            }
        }

        new AlertDialog.Builder(ctx).setTitle(header).setMessage(msg)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                }).show();
    }

    public static String getServerResString(String str) {
        if (str.contains("-")) {
            if (str.contains("<")) {
                str = str.substring((str.indexOf("-") + 1), str.indexOf("<"));
            } else {
                str = str.substring((str.indexOf("-") + 1));
            }
        }
        return str;
    }

    public static String whatToday() {
        Calendar cal = Calendar.getInstance();
        // cal.set(Calendar.YEAR, Integer.parseInt("2012"));
        // cal.set(Calendar.MONTH, (Integer.parseInt("12") - 1));
        // cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt("24"));
        cal.setTime(new Date());
        return cal.getTime().toString();
    }

    public static boolean checkIfSatSunMon(String str) {

        String arr[] = str.split("\\/");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, Integer.parseInt(arr[2]));
        cal.set(Calendar.MONTH, (Integer.parseInt(arr[1]) - 1));
        cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(arr[0]));
        // System.out.println("cal date:" + cal.getTime());
        // System.out.println("Day :" + cal.getTime().toString());

        if (cal.getTime().toString().startsWith("Sat")
                || cal.getTime().toString().startsWith("Sun")
                || cal.getTime().toString().startsWith("Mon")
                || cal.getTime().toString().startsWith("Tue")
                || cal.getTime().toString().startsWith("Wed")) {

            // System.out
            // .println(str + " date is SAT || SUN || MON || TUE || WED");

            return true;
        }
        return false;
    }

    public static boolean checkIfSunday(String date) {
        String arr[] = date.split("\\/");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, Integer.parseInt(arr[2]));
        cal.set(Calendar.MONTH, (Integer.parseInt(arr[1]) - 1));
        cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(arr[0]));
        // System.out.println("cal date:" + cal.getTime());
        if (cal.getTime().toString().startsWith("Sun")) {
            return false;
        }
        return true;
    }

    public static boolean checkIfMunday(String date) {
        // System.out.println("checkIfMunday()");
        String arr[] = date.split("\\/");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, Integer.parseInt(arr[2]));
        cal.set(Calendar.MONTH, (Integer.parseInt(arr[1]) - 1));
        cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(arr[0]));
        // System.out.println("cal date:" + cal.getTime());
        if (cal.getTime().toString().startsWith("Mon")) {
            return true;
        }
        return false;
    }

    public static boolean checkIfThursday(String date) {
        // System.out.println("checkIfThursday()");
        String arr[] = date.split("\\/");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, Integer.parseInt(arr[2]));
        cal.set(Calendar.MONTH, (Integer.parseInt(arr[1]) - 1));
        cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(arr[0]));
        // System.out.println("cal date:" + cal.getTime());
        if (cal.getTime().toString().startsWith("Thu")) {
            return true;
        }
        return false;
    }

    public static boolean checkIfSatSunMon(int y, int m, int d) {

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, y);
        cal.set(Calendar.MONTH, m);
        cal.set(Calendar.DAY_OF_MONTH, d);
        System.out.println("cal date:" + cal.getTime());
        System.out.println("Day :" + cal.getTime().toString());
        if (cal.getTime().toString().startsWith("Sat")
                || cal.getTime().toString().startsWith("Sun")
                || cal.getTime().toString().startsWith("Mon")) {
            return false;
        }
        return true;
    }

    public static boolean dateBtnDates(String beforedate, String date,
                                       String afterdate) {
        Log.e("Helper", "dateBtnDates(" + beforedate + "," + date + ","
                + afterdate + ")");

        System.out.println("beforedate :" + beforedate + " date :" + date
                + " afterdate :" + afterdate);
        if (checkPastDate(beforedate, date)) {
            if (notAcceptFutureDate(afterdate, date)) {
                return true;
            }
        }
        return false;
    }

    public static String readTxt(Activity av, int res) {
        InputStream inputStream = av.getResources().openRawResource(res);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int i;
        try {
            i = inputStream.read();
            while (i != -1) {
                byteArrayOutputStream.write(i);
                i = inputStream.read();
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArrayOutputStream.toString();
    }

    public static String getdateAfterDays(String str, int diff) {

        String arr[] = str.split("\\/");

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, Integer.parseInt(arr[2]));
        cal.set(Calendar.MONTH, Integer.parseInt(arr[1]) - 1);
        cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(arr[0]));
        System.out.println("cal date:" + cal.getTime());
        long date = cal.getTime().getTime() + (diff * 24 * 60 * 60 * 1000);
        cal.setTime(new Date(date));
        System.out.println("after date:" + cal.getTime());
        return cal.get(Calendar.DAY_OF_MONTH) + "/"
                + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
    }

    public static String dateAfter14Days(int y, int m, int d) {

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, y);
        cal.set(Calendar.MONTH, m);
        cal.set(Calendar.DAY_OF_MONTH, d);
        System.out.println("cal date:" + cal.getTime());
        long date = cal.getTime().getTime() + (13 * 24 * 60 * 60 * 1000);
        cal.setTime(new Date(date));
        System.out.println("after date:" + cal.getTime());
        return cal.get(Calendar.DAY_OF_MONTH) + "/"
                + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
    }

    public static String getDemandDate(String batch) {

        Calendar cal = Calendar.getInstance();

        if (batch.equals("1")) {
            batch = "Mon";
        } else {
            batch = "Thu";
        }

        String date = "";

        for (int i = 1; i <= 8; i++) {

            cal.setTime(new Date());
            cal.add(Calendar.DATE, i);
            System.out.println("date :" + cal.getTime());
            if (cal.getTime().toString().trim().startsWith(batch)) {
                date = cal.get(Calendar.DAY_OF_MONTH) + "/"
                        + (cal.get(Calendar.MONTH) + 1) + "/"
                        + cal.get(Calendar.YEAR);
                break;
            }
        }
        return date;
    }

    public static boolean checkIfSunday(int y, int m, int d) {

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, y);
        cal.set(Calendar.MONTH, m);
        cal.set(Calendar.DAY_OF_MONTH, d);
        System.out.println("cal date:" + cal.getTime());
        System.out.println("Day :" + cal.getTime().toString());
        if (cal.getTime().toString().startsWith("Sun")) {
            return false;
        }
        return true;
    }

    public static String getDayOfDate(String str) {
        String arr[] = str.split("\\/");
        Calendar cal = Calendar.getInstance();
        System.out.println("arr.length :" + arr.length);
        cal.set(Integer.parseInt(arr[2]), Integer.parseInt(arr[1]) - 1,
                Integer.parseInt(arr[0]));
        System.out.println("cal day :" + cal.getTime());
        return cal.getTime().toString().trim();
    }

    public static String dateAfterDays(String str, int in) {

        String arr[] = str.split("\\/");
        Calendar cal = Calendar.getInstance();
        System.out.println("arr.length :" + arr.length);
        cal.set(Integer.parseInt(arr[2]), Integer.parseInt(arr[1]) - 1,
                Integer.parseInt(arr[0]));
        System.out.println("cal date:" + cal.getTime());
        cal.add(Calendar.DATE, in); // add how many days we want

        return cal.get(Calendar.DAY_OF_MONTH) + "/"
                + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
    }

    public static String daywithDiff(String date, int diff) {
        String split[] = date.split("\\/");
        Calendar cala = Calendar.getInstance();

        cala.set(Calendar.YEAR, Integer.parseInt(split[2]));

        cala.set(Calendar.MONTH, (Integer.parseInt(split[1]) - 1));

        cala.set(Calendar.DAY_OF_MONTH, Integer.parseInt(split[0]));

        System.out.println("cal date:" + cala.getTime());
        long val = 60 * 60 * 1000;
        long time = cala.getTime().getTime() + (diff * 24 * val);
        cala.setTime(new Date(time));
        System.out.println("after date:" + cala.getTime());
        String day = "" + cala.get(Calendar.DAY_OF_MONTH);
        if (day.length() == 1) {
            day = "0" + day;
        }
        String mnth = "" + (cala.get(Calendar.MONTH) + 1);
        if (mnth.length() == 1) {
            mnth = "0" + mnth;
        }
        return day + "/" + mnth + "/" + cala.get(Calendar.YEAR);
    }

    public static String dateAfterDaysFromServer(String str, int in) {

        String arr[] = str.split("\\/");
        Calendar cal = Calendar.getInstance();
        System.out.println("arr.length :" + arr.length);
        cal.set(Integer.parseInt(arr[2]), Integer.parseInt(arr[0]) - 1,
                Integer.parseInt(arr[1]));
        System.out.println("cal date:" + cal.getTime());
        cal.add(Calendar.DATE, in); // add how many days we want

        return cal.get(Calendar.DAY_OF_MONTH) + "/"
                + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR)
                + "$" + cal.getTime();
    }

    public static String getTodayDate(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date());
    }

    public static String getTodayLongDate(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date());
    }

    public static void getWriteToFile(String fileName, String data,
                                      Activity activity) {

        FileOutputStream fos = null;
        try {
            fos = activity.openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(data.getBytes());

        } catch (FileNotFoundException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }

            } catch (IOException e) {

                e.printStackTrace();
            }

        }

    }

    public static boolean compareDates(String date1, String date2) {

        String[] arr1 = date1.split("\\/"), arr2 = date2.split("\\/");
        if (Integer.parseInt(arr1[0]) != Integer.parseInt(arr2[0]))
            return false;
        else if (Integer.parseInt(arr1[1]) != Integer.parseInt(arr2[1]))
            return false;
        else if (Integer.parseInt(arr1[2]) != Integer.parseInt(arr2[2]))
            return false;
        else
            return true;
    }

    /*
     * It returns true when new date is grater than current date False when
     */
    public static boolean checkPastDate(String currentDate, String newdate) {
        System.out.println("currentDate :" + currentDate + "   newdate :"
                + newdate);

        String[] newarr = newdate.split("\\/");
        String[] currarr = currentDate.split("\\/");
        Calendar todayCalendar = Calendar.getInstance();
        todayCalendar.setTime(new Date());

        int newDate[] = new int[newarr.length];
        int currDate[] = new int[currarr.length];
        for (int i = 0; i < newarr.length; i++) {
            newDate[i] = Integer.parseInt(newarr[i]);
            currDate[i] = Integer.parseInt(currarr[i]);
        }
        /*
         * // CUR30/1/2013 NEW 3/2/2013 if (currDate[2] > newDate[2]) { return
         * false; } else if (currDate[1] > newDate[1]) { return false; } else if
         * (currDate[0] > newDate[0]) { return false; } else { return true; }
         */

        // CUR1/2/2013 NEW 3/2/2013
        if (currDate[2] > newDate[2]) {
            return false;
        } else if (currDate[2] < newDate[2]) {
            return true;
        } else if (currDate[1] > newDate[1]) {
            return false;
        } else if (currDate[1] < newDate[1]) {
            return true;
        } else if (currDate[0] > newDate[0]) {
            return false;
        } else if (currDate[0] < newDate[0]) {
            return true;
        } else {
            return true;
        }
    }

    public static boolean notAcceptFutureDate(String currentDate, String newdate) {

        System.out.println("currentDate :" + currentDate + "   newdate :"
                + newdate);

        String[] newarr = newdate.split("\\/");
        String[] currarr = currentDate.split("\\/");
        Calendar todayCalendar = Calendar.getInstance();
        todayCalendar.setTime(new Date());

        int newDate[] = new int[newarr.length];
        int currDate[] = new int[currarr.length];
        for (int i = 0; i < newarr.length; i++) {
            newDate[i] = Integer.parseInt(newarr[i]);
            currDate[i] = Integer.parseInt(currarr[i]);
        }
        // NEW 22 12 2012 CUR 23 11 2012
        if (newDate[2] > currDate[2]) {
            return false;
        } else if (newDate[2] < currDate[2]) {
            return true;
        } else if (newDate[1] > currDate[1]) {
            return false;
        } else if (newDate[1] < currDate[1]) {
            return true;
        } else if (newDate[0] > currDate[0]) {
            return false;
        } else {
            return true;
        }

    }

    public static long diffBtnDates(int y, int m, int d) {
        Calendar calendar1 = Calendar.getInstance();

        Calendar calendar2 = Calendar.getInstance();

        calendar1.set(Calendar.getInstance().get(Calendar.YEAR), Calendar
                        .getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DATE));

        System.out.println("dd :" + calendar1.getTime());

        calendar2.set(y, m, d);

        long milsecs1 = calendar1.getTimeInMillis();

        long milsecs2 = calendar2.getTimeInMillis();

        long diff = milsecs2 - milsecs1;

        long ddays = diff / (24 * 60 * 60 * 1000);

        System.out.println("Your Day Difference=" + ddays);
        return ddays;
    }

    public static long diffBtnTodayDate(String date) {
        Calendar calendar1 = Calendar.getInstance();

        Calendar calendar2 = Calendar.getInstance();

        calendar1.setTime(new Date());

        System.out.println("dd :" + calendar1.getTime());

        int d2, m2, y2;

        String arr2[] = date.split("\\/");
        d2 = Integer.parseInt(arr2[0]);
        m2 = Integer.parseInt(arr2[1]) - 1;
        y2 = Integer.parseInt(arr2[2]);

        calendar2.set(y2, m2, d2);

        long milsecs1 = calendar1.getTimeInMillis();

        long milsecs2 = calendar2.getTimeInMillis();

        long diff = milsecs2 - milsecs1;

        long ddays = diff / (24 * 60 * 60 * 1000);

        System.out.println("Your Day Difference=" + ddays);
        return ddays;
    }

    public static long diffBtnTwoDates(String date1, String date2) {

        System.out.println("date1 :" + date1 + "  date2 :" + date2);
        int d1, d2, m1, m2, y1, y2;

        String arr[] = date1.split("\\/");
        d1 = Integer.parseInt(arr[0]);
        m1 = Integer.parseInt(arr[1]) - 1;
        y1 = Integer.parseInt(arr[2]);

        String arr2[] = date2.split("\\/");
        d2 = Integer.parseInt(arr2[0]);
        m2 = Integer.parseInt(arr2[1]) - 1;
        y2 = Integer.parseInt(arr2[2]);

        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.DAY_OF_MONTH, d1);
        calendar1.set(Calendar.MONTH, m1);
        calendar1.set(Calendar.YEAR, y1);

        Calendar calendar2 = Calendar.getInstance();

        calendar2.set(Calendar.DAY_OF_MONTH, d2);
        calendar2.set(Calendar.MONTH, m2);
        calendar2.set(Calendar.YEAR, y2);

        long milsecs1 = calendar1.getTimeInMillis();

        long milsecs2 = calendar2.getTimeInMillis();

        long diff = milsecs2 - milsecs1;

        long days = (24 * 60 * 60 * 1000);

        long ddays = diff / (days);

        System.out.println("time diff:" + diff + " Your Day Difference="
                + ddays);
        return ddays;
    }

    public static String getReadFile(String fileName, Activity activity) {
        String str = "";
        int ch;
        StringBuffer fileContent = new StringBuffer("");
        FileInputStream fis;
        try {
            fis = activity.openFileInput(fileName);
            while ((ch = fis.read()) != -1) {
                fileContent.append((char) ch);
            }
            str = new String(fileContent);
        } catch (FileNotFoundException e) {

            str = "NO";
            e.printStackTrace();
        } catch (IOException e) {
            str = "NO";

            e.printStackTrace();
        }

        return str;

    }

    public static int getDateDifference(String date) {// date=from date
        int k = 0;
        System.out.println("date in diff......" + date);
        String split[] = date.split("\\/");
        Calendar cala1 = Calendar.getInstance();

        Calendar cala = Calendar.getInstance();

        cala.set(Calendar.YEAR, Integer.parseInt(split[2]));

        cala.set(Calendar.MONTH, (Integer.parseInt(split[1]) - 1));

        cala.set(Calendar.DAY_OF_MONTH, Integer.parseInt(split[0]));

        System.out.println("current Date........" + new Date().getTime());

        System.out.println("current Date111111........"
                + cala.getTime().getTime());

        System.out.println("cala1.getTime().getTime()..."
                + cala1.getTime().getTime());
        System.out.println("cala.getTime().getTime()...."
                + cala.getTime().getTime());

        long datetime = (cala.getTime().getTime() - (cala1.getTime().getTime()));
        long val = 60 * 60 * 1000;
        if ((cala.get(Calendar.YEAR) - cala1.get(Calendar.YEAR)) == 0) {
            System.out.println("year match");
            if ((cala.get(Calendar.MONTH) - cala1.get(Calendar.MONTH)) == 0) {
                System.out.println("Month match");
                System.out.println("cala.get(Calendar.DAY_OF_MONTH).."
                        + cala.get(Calendar.DAY_OF_MONTH));
                System.out.println("cala1.get(Calendar.DAY_OF_MONTH).."
                        + cala1.get(Calendar.DAY_OF_MONTH));
                k = cala.get(Calendar.DAY_OF_MONTH)
                        - cala1.get(Calendar.DAY_OF_MONTH);
            } else {
                System.out.println("Month not match");
                int mnt = cala.get(Calendar.MONTH) - cala1.get(Calendar.MONTH);
                k = (int) (datetime / (24 * val));

            }
        } else {
            System.out.println("year Not match");
            int yer = cala.get(Calendar.YEAR) - cala1.get(Calendar.YEAR);
            k = (int) (datetime / (24 * val));

        }

        System.out.println("date time..1.." + datetime);
        System.out.println("date time...2....." + (datetime / (24 * val)));
        // k = (int) (datetime / (24 * val));
        // k = (int) (k1 / 365);
        System.out.println("k.......diff............" + k);
        return k;
    }

    public static int finyear() {
        int year;
        Calendar calendar1 = Calendar.getInstance();

        calendar1.setTime(new Date());
        int mn = calendar1.get(calendar1.MONTH);
        if ((mn + 1) >= 4) {
            year = calendar1.get(calendar1.YEAR) + 1;
        } else {
            year = calendar1.get(calendar1.YEAR);
        }
        return year;
    }

    public static String getmonthname(int no) {
        String monthname = "";
        int year;
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();

        calendar1.setTime(new Date());
        calendar2.setTime(new Date());
        int mn = (calendar1.get(calendar1.MONTH) + 1) - no;
        if (mn < 1) {
            calendar1.set(Calendar.YEAR, calendar1.get(calendar1.YEAR) - 1);
            mn = 12 + mn;
        }
        if (mn >= 7) {
            year = calendar1.get(calendar1.YEAR) + 1;
        } else {
            year = calendar1.get(calendar1.YEAR);
        }
        switch (mn) {
            case 1:
                monthname = "Jan (" + (year - 1) + "-" + year + ")";
                break;
            case 2:
                monthname = "Feb (" + (year - 1) + "-" + year + ")";
                break;
            case 3:
                monthname = "Mar (" + (year - 1) + "-" + year + ")";
                break;
            case 4:
                monthname = "Apr (" + (year - 1) + "-" + year + ")";
                break;
            case 5:
                monthname = "May (" + (year - 1) + "-" + year + ")";
                break;
            case 6:
                monthname = "June (" + (year - 1) + "-" + year + ")";
                break;
            case 7:
                monthname = "July (" + (year - 1) + "-" + year + ")";
                break;
            case 8:
                monthname = "Aug (" + (year - 1) + "-" + year + ")";
                break;
            case 9:
                monthname = "Sep (" + (year - 1) + "-" + year + ")";
                break;
            case 10:
                monthname = "Oct (" + (year - 1) + "-" + year + ")";
                break;
            case 11:
                monthname = "Nov (" + (year - 1) + "-" + year + ")";
                break;
            case 12:
                monthname = "Dec (" + (year - 1) + "-" + year + ")";
                break;
        }

        return monthname;
    }

    public static int getdays() {
        int days = 0;
        Calendar calendar1 = Calendar.getInstance();

        calendar1.setTime(new Date());
        int mn = calendar1.get(calendar1.MONTH) + 1;
        int year = calendar1.get(calendar1.YEAR);
        if (mn == 1) {
            days = 31;
        } else if (mn == 2) {
            if (year % 4 == 0) {
                days = 29;
            } else {
                days = 28;
            }
        } else if (mn == 3) {
            days = 31;
        } else if (mn == 4) {
            days = 30;
        } else if (mn == 5) {
            days = 31;
        } else if (mn == 6) {
            days = 30;
        } else if (mn == 7) {
            days = 31;
        } else if (mn == 8) {
            days = 31;
        } else if (mn == 9) {
            days = 30;
        } else if (mn == 10) {
            days = 31;
        } else if (mn == 11) {
            days = 30;
        } else if (mn == 12) {
            days = 31;
        }

        return days;
    }

    public static int checkyear(String date) {
        int k = 0;
        String split[] = date.split("\\/");

        if (Integer.parseInt(split[2]) > finyear()) {
            k = 1;
        } else {
            if (Integer.parseInt(split[2]) == finyear()) {
                if ((Integer.parseInt(split[1])) >= 4) {
                    k = 1;
                }
            }
        }
        return k;
    }

    public static boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    public static String getIMEI(Context ctx) {

        TelephonyManager mngr = (TelephonyManager) ctx
                .getSystemService(Context.TELEPHONY_SERVICE);
        return mngr.getDeviceId();
    }


    public static String getMobileNumFromDB(Context ctx, String tablename,
                                            String colName) {
        String retStr = "";
        DBHelper dbh = new DBHelper(ctx);
        SQLiteDatabase db = dbh.getWritableDatabase();
        String Query = "Select " + colName + " From " + tablename;
        Cursor c = db.rawQuery(Query, null);
        while (c.moveToNext()) {
            retStr = c.getString(0);
//            Log.e("mob No", retStr);
        }
        c.close();
        db.close();
        return retStr;
    }

    public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth,
                                                     int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        // Query bitmap without allocating memory
        options.inJustDecodeBounds = true;
        // decode file from path
        BitmapFactory.decodeFile(path, options);
        // Calculate inSampleSize
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        // decode according to configuration or according best match
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        int inSampleSize = 1;
        if (height > reqHeight) {
            inSampleSize = Math.round((float) height / (float) reqHeight);
        }
        int expectedWidth = width / inSampleSize;
        if (expectedWidth > reqWidth) {
            // if(Math.round((float)width / (float)reqWidth) > inSampleSize) //
            // If bigger SampSize..
            inSampleSize = Math.round((float) width / (float) reqWidth);
        }
        // if value is greater than 1,sub sample the original image
        options.inSampleSize = inSampleSize;
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        // return BitmapFactory.decodeFile(path, options);
        return imageOreintationValidator(BitmapFactory.decodeFile(path, options), path);
    }

    private static Bitmap imageOreintationValidator(Bitmap bitmap, String path) {

        ExifInterface ei;
        try {
            ei = new ExifInterface(path);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    bitmap = rotateImage(bitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    bitmap = rotateImage(bitmap, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    bitmap = rotateImage(bitmap, 270);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    private static Bitmap rotateImage(Bitmap source, float angle) {

        Bitmap bitmap = null;
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        try {
            bitmap = Bitmap.createBitmap(source, 0, 0, source.getWidth(),
                    source.getHeight(), matrix, true);
        } catch (OutOfMemoryError err) {
            err.printStackTrace();
        }
        return bitmap;
    }


    /*Method used to find the the target sdk version greater than Naugat OS*/
    public static boolean isTargetSdkVersionGreaterThanNaugat() {
        return Build.VERSION.SDK_INT >= 24;
    }

}
