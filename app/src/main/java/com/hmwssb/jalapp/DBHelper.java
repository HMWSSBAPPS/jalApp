package com.hmwssb.jalapp;

/**
 @author Santhosh Kumar B
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

	private static final int DB_VERSION = 1;

	// Database Name
	public static final String DB_NAME = "LINEMAN_DB";
	public static final String rowID = "rowID";

	// General Tablecolumns
	public static final String TABLE_GENERAL = "TABLE_GENERAL";
	public static final String GEN_MOBILE = "GEN_MOBILE";
	public static final String GEN_USER_ID = "GEN_USER_ID";
	public static final String GEN_PASS_CODE = "GEN_PASS_CODE";
	public static final String GEN_DATE = "GEN_DATE";

	// Valve Tablecolumns
	public static final String TABLE_VALVE = "TABLE_VALVE";
	public static final String VALVE_SID = "VALVE_SID";
	public static final String VALVE_LINEMANID = "VALVE_LINEMANID";
	public static final String VLAVE_LINEID = "VLAVE_LINEID";
	public static final String VALVE_VALVEID = "VALVE_VALVEID";
	public static final String VALVE_SUBVALVEID = "VALVE_SUBVALVEID";
	public static final String VALVE_LANDMARK = "VALVE_LANDMARK";
	public static final String VALVE_AREA = "VALVE_AREA";
	public static final String VALVE_VALVETYPE = "VALVE_VALVETYPE";
	public static final String VALVE_LATITUDE = "VALVE_LATITUDE";
	public static final String VALVE_LONGITUDE = "VALVE_LONGITUDE";
	public static final String VALVE_IMAGE = "VALVE_IMAGE";
	public static final String VALVE_STATUS = "VALVE_STATUS";
	public static final String VALVE_SCHEDULE = "VALVE_SCHEDULE";
	public static final String VALVE_TIME = "VALVE_TIME";
	public static final String VALVE_SUB_STATUS = "VALVE_SUB_STATUS";
	public static final String VALVE_CANS = "VALVE_CANS";
	public static final String VALVE_CENTRIC = "VALVE_CENTRIC";
	public static final String VALVE_SUPLY_AREA = "VALVE_SUPLY_AREA";

	String CREATE_VALVE_TABLE = CreateTableStringByID(TABLE_VALVE, rowID,
			new String[] { VALVE_SID, VALVE_LINEMANID, VLAVE_LINEID,
					VALVE_VALVEID, VALVE_SUBVALVEID, VALVE_LANDMARK,
					VALVE_AREA, VALVE_VALVETYPE, VALVE_LATITUDE,
					VALVE_LONGITUDE, VALVE_STATUS, VALVE_SUB_STATUS,
					VALVE_IMAGE, VALVE_SCHEDULE, VALVE_TIME, VALVE_CENTRIC,
					VALVE_CANS,VALVE_SUPLY_AREA });

	String CREATE_GENERAL_TABLE = CreateTableStringByID(TABLE_GENERAL, rowID,
			new String[] { GEN_MOBILE, GEN_USER_ID, GEN_PASS_CODE, GEN_DATE });

	public DBHelper(Context ctx) {
		super(ctx, DB_NAME, null, DB_VERSION);
		// System.out.println("DB init");
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		createTables(db);
		// System.out.println("DB oncreate");
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {

	}

	public void createTables(SQLiteDatabase database) {

		try {
			// System.out.println("DB create table");
			database.execSQL(CREATE_VALVE_TABLE);
			database.execSQL(CREATE_GENERAL_TABLE);

			Log.d("TAG", "Tables created!");
		} catch (Exception ex) {
			Log.d("TAG",
					"Error in DBHelper.createTables() : " + ex.getMessage());
		}
	}

	// create a table with all column types are TEXT
	public static String CreateTableString(String tablename,
			String[] columnnames) {
		String CREATE_TABLE = "CREATE TABLE if not exits " + tablename + " (";
		for (String data : columnnames) {
			if (data.indexOf("Photo") != -1) {
				CREATE_TABLE = CREATE_TABLE + data + " LONGTEXT,";
			} else {
				CREATE_TABLE = CREATE_TABLE + data + " TEXT,";
			}

		}
		CREATE_TABLE = CREATE_TABLE.substring(0, CREATE_TABLE.length() - 1)
				+ ")";
		return CREATE_TABLE;
	}

	// create a table with all column types are TEXT Except ID As Primary key
	// auto incrment
	public static String CreateTableStringByID(String tablename,
			String primarycolumn, String[] restcolumns) {
		String CREATE_TABLE = "CREATE TABLE " + tablename + " ("
				+ primarycolumn + " INTEGER PRIMARY KEY AUTOINCREMENT,";
		for (String data : restcolumns) {
			CREATE_TABLE = CREATE_TABLE + data + " TEXT,";
		}
		CREATE_TABLE = CREATE_TABLE.substring(0, CREATE_TABLE.length() - 1)
				+ ")";
		return CREATE_TABLE;
	}

	public static String CreateTableByIDValue(String tablename,
			HashMap<String, String> hm) {
		String CREATE_TABLE = "CREATE TABLE " + tablename + " (";
		Iterator itr = hm.entrySet().iterator();
		while (itr.hasNext()) {
			Map.Entry pairs = (Map.Entry) itr.next();
			CREATE_TABLE = CREATE_TABLE + pairs.getKey() + " "
					+ pairs.getValue() + ",";
		}
		CREATE_TABLE = CREATE_TABLE.substring(0, CREATE_TABLE.length() - 1)
				+ ")";
		return CREATE_TABLE;
	}

	// create a table with manual column name and column type using hash Map
	public static String CreateTableByIDValues(String tablename,
			String columnnames[], String columntypes[]) {
		String CREATE_TABLE = "CREATE TABLE " + tablename + " (";

		for (int i = 0; i < columnnames.length; i++)
			CREATE_TABLE = CREATE_TABLE + columnnames[i] + " " + columntypes[i]
					+ ",";

		CREATE_TABLE = CREATE_TABLE.substring(0, CREATE_TABLE.length() - 1)
				+ ")";
		return CREATE_TABLE;
	}

	// it returns values of a based on column value we pass
	public String getValueById(Context context, String tablename,
			String scolumname, String dcolumname, String scolumnvalue) {
		String countQuery = "SELECT " + dcolumname + " FROM " + tablename
				+ " WHERE " + scolumname + "='" + scolumnvalue + "'";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		String SID = "";
		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				SID = cursor.getString(0);
			} while (cursor.moveToNext());
		}

		cursor.close();
		db.close();
		return SID;
	}

	// checks the value already there int he table
	public boolean checkAlreadyExists(Context context, String tablename,
			String columnname, String columnvalue) {
		boolean value = false;
		String countQuery = "SELECT  * FROM " + tablename + " WHERE "
				+ columnname + "='" + columnvalue + "'";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		int count = cursor.getCount();
		if (count > 0)
			value = true;
		cursor.close();
		db.close();
		// return count
		return value;
	}

	// checks the value already there int he table
	public boolean checkAlreadyExistsByValue(Context context, String tablename,
			String columnname[], String columnvalue[]) {
		boolean value = false;
		String countQuery = "SELECT  * FROM " + tablename + " WHERE ";
		for (int k = 0; k < columnname.length; k++)
			countQuery = countQuery + columnname[k] + "='" + columnvalue[k]
					+ "' AND ";
		countQuery = countQuery.substring(0, countQuery.length() - 5);
		// System.out.println("countQuery:" + countQuery);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		int count = cursor.getCount();
		if (count > 0)
			value = true;
		cursor.close();
		db.close();
		// return count
		return value;
	}

	// not myne
	public static int getCount(Context context, final String tableName) {
		DBHelper dbhelper = new DBHelper(context);
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		int cnt = 0;
		Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + tableName, null);
		cursor.moveToFirst();
		cnt = Integer.parseInt(cursor.getString(0));
		cursor.close();
		db.close();
		return cnt;
	}

	// not myne
	public int getCountByValue(Context context, final String tableName,
			String colName, String colValue) {
		DBHelper dbhelper = new DBHelper(context);
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		int cnt = 0;
		Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + tableName
				+ " WHERE " + colName + "='" + colValue + "'", null);
		cursor.moveToFirst();
		cnt = Integer.parseInt(cursor.getString(0));
		cursor.close();
		db.close();
		return cnt;
	}

	public List<String[]> getTableData(Context context, String TableName) {
		DBHelper dbhelper = new DBHelper(context);
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		Cursor cur = db.rawQuery("SELECT * FROM " + TableName, null);
		List<String[]> list = null;
		if (cur.getCount() > 0)
			list = cursortoListArr(cur);
		cur.close();
		db.close();
		dbhelper.close();
		return list;
	}

	public List<String[]> getTableDataByValue(Context context,
			String TableName, String columname, String columnValue) {
		DBHelper dbhelper = new DBHelper(context);
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		Cursor cur = db.rawQuery("SELECT * FROM " + TableName + " WHERE "
				+ columname + "='" + columnValue + "'", null);
		List<String[]> list = new ArrayList<String[]>();
		// System.out.println("cur:" + cur.getCount());
		if (cur.getCount() > 0)
			list = cursortoListArr(cur);
		cur.close();
		db.close();
		dbhelper.close();

		return list;
	}

	public List<String[]> getNotTableDataByValue(Context context,
			String TableName, String columname, String columnValue) {
		DBHelper dbhelper = new DBHelper(context);
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		Cursor cur = db.rawQuery("SELECT * FROM " + TableName + " WHERE "
				+ columname + "<>'" + columnValue + "'", null);
		List<String[]> list = new ArrayList<String[]>();
		if (cur.getCount() > 0)
			list = cursortoListArr(cur);
		cur.close();
		db.close();
		dbhelper.close();
		return list;
	}

	public List<String[]> getTableDataByValues(Context context,
			String TableName, String columname[], String columnValue[]) {
		DBHelper dbhelper = new DBHelper(context);
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		String query = "SELECT * FROM " + TableName + " WHERE ";
		for (int k = 0; k < columname.length; k++) {
			query = query + columname[k] + "= '" + columnValue[k] + "'"
					+ " AND ";
		}
		query = query.substring(0, query.length() - 5);
		// System.out.println("query::::::"+query);
		Cursor cur = db.rawQuery(query, null);
		List<String[]> list = new ArrayList<String[]>();
		if (cur.getCount() > 0)
			list = cursortoListArr(cur);
		cur.close();
		db.close();
		dbhelper.close();
		return list;
	}

	public List<String[]> getTableDataByValues1(Context context,
			String TableName, String dcolumnName, String columname[],
			String columnValue[]) {
		DBHelper dbhelper = new DBHelper(context);
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		String query = "SELECT " + dcolumnName + " FROM " + TableName
				+ " WHERE ";
		for (int k = 0; k < columname.length; k++)
			query = query + columname[k] + "='" + columnValue[k] + "'"
					+ " AND ";
		query = query.substring(0, query.length() - 5);
		Cursor cur = db.rawQuery(query, null);
		List<String[]> list = null;
		if (cur.getCount() > 0)
			list = cursortoListArr(cur);
		cur.close();
		db.close();
		dbhelper.close();
		return list;
	}

	public List<String[]> getQueryData(Context context, String query) {
		DBHelper dbhelper = new DBHelper(context);
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		Cursor cur = db.rawQuery(query, null);
		List<String[]> list = new ArrayList<String[]>();
		if (cur.getCount() > 0)
			list = cursortoListArr(cur);
		cur.close();
		db.close();
		dbhelper.close();
		return list;
	}

	public String[] getColumnData(Context context, String TableName,
			String columnName) {
		DBHelper dbhelper = new DBHelper(context);
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		Cursor cur = db.rawQuery("SELECT " + columnName + " FROM " + TableName,
				null);

		String[] list = new String[cur.getCount()];
		if (cur.getCount() > 0)
			list = cursortoStringArr(cur);

		cur.close();
		db.close();
		dbhelper.close();
		return list;
	}

	public String[] getColumnDataByValues(Context context, String TableName,
			String dcolumnName, String[] columnNames, String[] values) {
		DBHelper dbhelper = new DBHelper(context);
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		String query = "SELECT " + dcolumnName + " FROM " + TableName
				+ " WHERE ";
		for (int k = 0; k < columnNames.length; k++) {
			query = query + columnNames[k] + "='" + values[k] + "'" + " AND ";
		}

		query = query.substring(0, query.length() - 5);
		// System.out.println("query:" + query);
		Cursor cur = db.rawQuery(query, null);

		String[] list = new String[cur.getCount()];
		if (cur.getCount() > 0)
			list = cursortoStringArr(cur);

		cur.close();
		db.close();
		dbhelper.close();
		// System.out.println("list size:" + list.length);
		return list;
	}

	public String[] getColumnDataFromQuery(Context context, String query) {
		DBHelper dbhelper = new DBHelper(context);
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		Cursor cur = db.rawQuery(query, null);

		String[] list = null;
		if (cur.getCount() > 0)
			list = cursortoStringArr(cur);

		cur.close();
		db.close();
		dbhelper.close();
		return list;
	}

	public static long insertintoTable(Context context, final String tableName,
			String[] colNames, String[] values) {
		DBHelper dbhelper = new DBHelper(context);
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		ContentValues cv = new ContentValues();
		for (int i = 0; i < colNames.length; i++) {
			cv.put(colNames[i], values[i]);
		}
		long cnt = db.insert(tableName, null, cv);
		db.close();
		return cnt;
	}

	public static boolean deleteRows(Context context, final String tablename,
			String columnName, String value) {
		DBHelper dbhelper = new DBHelper(context);
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		boolean flag = db.delete(tablename, columnName + "='" + value + "'",
				null) > 0;
		db.close();
		return flag;

	}

	public static boolean deleteRowData(Context context,
			final String tablename, String[] columnName, String[] value) {
		DBHelper dbhelper = new DBHelper(context);
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		String whereClause = "";
		for (int k = 0; k < columnName.length; k++) {
			whereClause = whereClause + columnName[k] + "='" + value[k] + "'"
					+ " AND ";
		}
		whereClause = whereClause.substring(0, whereClause.length() - 5);

		boolean flag = db.delete(tablename, whereClause, null) > 0;
		// System.out.println("flag in database:" + flag);
		db.close();
		return flag;

	}

	public static boolean deleteAllRows(Context context, final String tablename) {
		DBHelper dbhelper = new DBHelper(context);
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		boolean flag = db.delete(tablename, null, null) > 0;
		db.close();
		return flag;

	}

	// public static boolean updateROW(Context context, final String tablename,
	// String[] columnNames, String[] columnValues, String whereColumn,
	// String whereValue) {
	// DBHelper dbhelper = new DBHelper(context);
	// SQLiteDatabase db = dbhelper.getReadableDatabase();
	// ContentValues cv = new ContentValues();
	// for (int i = 0; i < columnNames.length; i++) {
	// cv.put(columnNames[i], columnValues[i]);
	// }
	//
	// boolean flag = db.update(tablename, cv, whereColumn + "='" + whereValue
	// + "'", null) > 0;
	// db.close();
	// return flag;
	//
	// }

	public static boolean updateRowData(Context context,
			final String tablename, String[] columnNames,
			String[] columnValues, String[] whereColumn, String[] whereValue) {
		DBHelper dbhelper = new DBHelper(context);
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		ContentValues cv = new ContentValues();
		for (int i = 0; i < columnNames.length; i++) {
			cv.put(columnNames[i], columnValues[i]);
		}
		boolean flag = false;
		if (whereColumn != null) {
			String whereClause = "";
			for (int k = 0; k < whereColumn.length; k++) {
				whereClause = whereClause + whereColumn[k] + "='"
						+ whereValue[k] + "'" + " AND ";
			}
			whereClause = whereClause.substring(0, whereClause.length() - 5);
			flag = db.update(tablename, cv, whereClause, null) > 0;
		} else {
			flag = db.update(tablename, cv, null, null) > 0;
		}

		db.close();
		// System.out.println("flag.................." + flag);
		return flag;
	}

	public String[] cursortoStringArr(Cursor c) {
		String[] arr = new String[c.getCount()];
		for (int i = 0; i < arr.length; i++) {
			c.moveToNext();
			arr[i] = c.getString(0);
		}
		c.close();
		return arr;
	}

	public List<String[]> cursortoListArr(Cursor c) {
		List<String[]> rowList = new ArrayList<String[]>();
		while (c.moveToNext()) {
			String[] arr = new String[c.getColumnCount()];
			for (int i = 0; i < c.getColumnCount(); i++) {
				arr[i] = c.getString(i);
			}
			rowList.add(arr);
		}
		c.close();
		return rowList;
	}



}
