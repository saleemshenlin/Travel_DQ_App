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
	 * 定义一个数据库版本
	 */
	static final int VERSION = 1;
	/**
	 * 用于表示一个数据库名
	 */
	static final String DATABASE = "data.db";
	/**
	 * 用于表示表名
	 */
	static final String TABLE = "poi";
	static final String TABLE_POIS = "pois";
	static final String TABLE_ROUTE = "routes";
	/**
	 * 字段名
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
	 * 类DbHelper<br>
	 * 针对sqlite3数据库操作
	 */
	class DbHelper extends SQLiteOpenHelper {
		/**
		 * 用于通过数据名称和版本构造一个DbHelp类
		 * 
		 * @param context
		 *            上下文
		 */
		public DbHelper(Context context) {
			super(context, DATABASE, null, VERSION);
		}

		/**
		 * 用于创建一个新数据库和一个新表
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
		 * 用于在更新数据库时,直接删除旧数据库
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
	 * 在初始化EventData时,同时初始化一个DbHelper
	 * 
	 * @param context
	 *            上下文
	 */
	public PoiDB(Context context) {
		this.dbHelper = new DbHelper(context);
		Log.i("POIDB", "initialized data");
	}

	/**
	 * 用于关闭数据库
	 */
	public void closeDatabase() {
		this.dbHelper.close();
	}

	/**
	 * 用于打开应用时插入数据<br>
	 * 根据tabIsExist()和tableIsNull()的结果决定是否执行<br>
	 * 具体方法如下:<br>
	 * 使用insertWithOnConflict(),让数据库自己解决冲突
	 * 
	 * @param values
	 *            数据匹配对
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
	 * 用于判断表是否为空 <br>
	 * 具体方法如下:<br>
	 * 1)执行sql语句select * from event<br>
	 * 2)根据结果Cursor来判断
	 * 
	 * @return boolean 是否存在
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
	 * 用于获取数据库DbHelper类
	 * 
	 * @return DbHelper DbHelper类
	 */
	public DbHelper getDbHelper() {
		return this.dbHelper;
	}

}
