package com.travelapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class PoiDB {
	/**
	 * ����һ�����ݿ�汾
	 */
	static final int VERSION = 1;
	/**
	 * ���ڱ�ʾһ�����ݿ���
	 */
	static final String DATABASE = "data.db";
	/**
	 * ���ڱ�ʾ����
	 */
	static final String TABLE = "poi";
	static final String TABLE_POIS = "pois";
	static final String TABLE_ROUTE = "routes";
	/**
	 * �ֶ���
	 */
	static final String C_ID = "_id";
	static final String C_NAME = "_name";
	static final String C_D_NAME = "_d_name";
	static final String C_ADDRESS = "_address";
	static final String C_TIME = "_time";
	static final String C_PRICE = "_price";
	static final String C_TELE = "_tele";
	static final String C_TYPE = "_type";
	static final String C_ABSTRACT = "_absrtact";
	static final String C_C_ID = "_c_id";
	static final String C_SHAPE = "_shape";

	/**
	 * ��DbHelper<br>
	 * ���sqlite3���ݿ����
	 */
	class DbHelper extends SQLiteOpenHelper {
		/**
		 * ����ͨ���������ƺͰ汾����һ��DbHelp��
		 * 
		 * @param context
		 *            ������
		 */
		public DbHelper(Context context) {
			super(context, DATABASE, null, VERSION);
		}

		/**
		 * ���ڴ���һ�������ݿ��һ���±�
		 * 
		 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database
		 *      .sqlite.SQLiteDatabase)
		 */
		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			Log.i("POIDB", "Creating database: " + DATABASE);
			db.execSQL("create TABLE " + TABLE + "(" + C_ID
					+ " INTEGER PRIMARY KEY," + C_NAME + " VARCHAR(128),"
					+ C_D_NAME + " VARCHAR(128)," + C_C_ID + " VARCHAR(10),"
					+ C_ADDRESS + " VARCHAR(128)," + C_TIME + " VARCHAR(50),"
					+ C_TYPE + " VARCHAR(50)," + C_TELE + " VARCHAR(128),"
					+ C_PRICE + " VARCHAR(128)," + C_ABSTRACT + " TEXT)");
			db.execSQL("create TABLE " + TABLE_POIS + "(" + C_ID
					+ " INTEGER PRIMARY KEY," + C_NAME + " VARCHAR(128),"
					+ C_C_ID + " VARCHAR(10)," + C_SHAPE + " TEXT)");
			db.execSQL("create TABLE " + TABLE_ROUTE + "(" + C_ID
					+ " INTEGER PRIMARY KEY," + C_NAME + " VARCHAR(128),"
					+ C_ABSTRACT + " TEXT," + C_SHAPE + " TEXT)");
		}

		/**
		 * �����ڸ������ݿ�ʱ,ֱ��ɾ�������ݿ�
		 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("drop table if exists " + TABLE);
			this.onCreate(db);
		}
	}

	private final DbHelper dbHelper;

	/**
	 * �ڳ�ʼ��EventDataʱ,ͬʱ��ʼ��һ��DbHelper
	 * 
	 * @param context
	 *            ������
	 */
	public PoiDB(Context context) {
		this.dbHelper = new DbHelper(context);
		Log.i("POIDB", "initialized data");
	}

	/**
	 * ���ڹر����ݿ�
	 */
	public void closeDatabase() {
		this.dbHelper.close();
	}

	/**
	 * ���ڴ�Ӧ��ʱ��������<br>
	 * ����tabIsExist()��tableIsNull()�Ľ�������Ƿ�ִ��<br>
	 * ���巽������:<br>
	 * ʹ��insertWithOnConflict(),�����ݿ��Լ������ͻ
	 * 
	 * @param values
	 *            ����ƥ���
	 */
	public void insertOrIgnore(ContentValues values, String table) {
		Log.d("POIDB", "insert " + values);
		SQLiteDatabase db = this.dbHelper.getWritableDatabase();
		try {
			db.insertWithOnConflict(table, null, values,
					SQLiteDatabase.CONFLICT_IGNORE);
		} finally {
			db.close();
		}
	}

	/**
	 * �����жϱ��Ƿ�Ϊ�� <br>
	 * ���巽������:<br>
	 * 1)ִ��sql���select * from event<br>
	 * 2)���ݽ��Cursor���ж�
	 * 
	 * @return boolean �Ƿ����
	 */
	public boolean tableIsNull() {
		boolean isNull = false;
		SQLiteDatabase db = null;
		Cursor mCursor = null;
		try {
			db = this.dbHelper.getReadableDatabase();
			String strSQL = "select * from " + TABLE;
			mCursor = db.rawQuery(strSQL, null);
			int num = mCursor.getCount();
			if (num < 1) {
				return true;
			}
		} catch (SQLException e) {
			// TODO: handle exception
		} finally {
			if (mCursor != null) {
				mCursor.close();
			}
			db.close();
		}
		return isNull;
	}

	/**
	 * ���ڻ�ȡ���ݿ�DbHelper��
	 * 
	 * @return DbHelper DbHelper��
	 */
	public DbHelper getDbHelper() {
		return this.dbHelper;
	}

}
