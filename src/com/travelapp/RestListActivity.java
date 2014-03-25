package com.travelapp;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class RestListActivity extends Activity {
	private ImageView mBackImageView;
	private TextView mTitleTextView;
	private ImageView mMapImageView;
	private static Resources mResources;
	private Cursor mItemCursor = null;
	/**
	 * 定义一个标签,在LogCat内表示EventListFragment
	 */
	private static final String TAG = "ScenicListActivity";
	/**
	 * ListView实例,用于显示Event列表
	 */
	private ListView mPoiListView;
	/**
	 * 实例一个SimpleCursorAdapter,用与给listEvent绑定数据
	 */
	private SimpleCursorAdapter mSimpleCursorAdapter;
	/**
	 * 定义一个String[],用于指明绑定数据的哪些字段<br>
	 * C_NAME,C_LOCATION,C_DATE
	 */
	private final String[] FROM = { PoiDB.C_NAME, PoiDB.C_PRICE,
			PoiDB.C_ABSTRACT, PoiDB.C_ID };
	/**
	 * 定义一个int[],对应row.xml中的控件id,分别映射FROM中的元素
	 */
	private final int[] TO = { R.id.txtRowTitle, R.id.txtRowPrice,
			R.id.txtRowAbstract, R.id.imgPalce };

	/**
	 * 实例一个Query
	 */
	private Query mQuery;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		mResources = this.getResources();
		initView();
		new PoiQuery().execute();
		// new AddMap().execute();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			TravelApplication.buildExitDialog(RestListActivity.this);
		}
		return false;
	}

	private void initView() {
		mBackImageView = (ImageView) findViewById(R.id.imgListBack);
		mMapImageView = (ImageView) findViewById(R.id.imgListMap);
		mTitleTextView = (TextView) findViewById(R.id.txtListTitle);
		mPoiListView = (ListView) findViewById(R.id.listPoi);
		mTitleTextView.setText("餐饮列表");
		mBackImageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(RestListActivity.this,
						HomeActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
						| Intent.FLAG_ACTIVITY_NEW_TASK);
				RestListActivity.this.startActivity(intent);
				RestListActivity.this.finish();
				RestListActivity.this.overridePendingTransition(
						R.anim.anim_in_left2right, R.anim.anim_out_left2right);
			}
		});
		mMapImageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(RestListActivity.this,
						MapActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
						| Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra("FUNCTION", "POIS");
				intent.putExtra("TYPE", 3);
				RestListActivity.this.startActivity(intent);
				RestListActivity.this.finish();
				RestListActivity.this.overridePendingTransition(
						R.anim.anim_in_right2left, R.anim.anim_out_right2left);
			}
		});
	}

	/**
	 * 定义一个常量,用于按给定的格式绑Event的时间和地点数据<br>
	 * 具体方法如下:<br>
	 * 1)当给控件txtDateTime绑数据时,按格式"时间：" + C_DATE + " " + C_TIME<br>
	 * 2)当给控件txtLocation绑数据时,按格式"地点：" +C_LOCATION
	 */
	private static final ViewBinder LIST_VIEW_BINDER = new ViewBinder() {

		@Override
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			if (view.getId() == R.id.txtRowPrice) {
				String date = cursor.getString(columnIndex);
				((TextView) view).setText("人均消费：" + date);
				return true;
			} else if (view.getId() == R.id.txtRowTitle) {
				String title = cursor.getString(columnIndex);
				if (title.length() > 10) {
					String name = title.substring(0, 10);
					((TextView) view).setText(name + "...");
				} else {
					String name = title;
					((TextView) view).setText(name);
				}
				return true;
			} else if (view.getId() == R.id.txtRowAbstract) {
				String place = cursor.getString(columnIndex).substring(0, 30);
				((TextView) view).setText(place + "...");
				return true;
			} else if (view.getId() == R.id.imgPalce) {
				String name = "img_0" + cursor.getString(columnIndex);

				int id = mResources.getIdentifier(name, "drawable",
						"com.travelapp");
				Drawable mDrawable = mResources.getDrawable(id);
				((ImageView) view).setImageDrawable(mDrawable);
				return true;
			} else {
				return false;
			}
		}

	};

	class PoiQuery extends AsyncTask<String[], String, String> {

		@Override
		protected String doInBackground(String[]... params) {
			mQuery = new Query();
			try {
				mItemCursor = mQuery.getPoiByType(3);
				int num = mItemCursor.getCount();
				Log.i(TAG, "ActivityProvider cursor" + num);
			} catch (Exception e) {
				Log.e(TAG, e.toString());
			} finally {
				if (mItemCursor.isClosed()) {
					mItemCursor.close();
				}
				TravelApplication.getPoiDB().closeDatabase();
			}
			return "ok";
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (result != null) {
				Toast.makeText(RestListActivity.this, "成功获取数据",
						Toast.LENGTH_LONG).show();
				mSimpleCursorAdapter = new SimpleCursorAdapter(
						TravelApplication.getContext(), R.layout.row,
						mItemCursor, FROM, TO,
						CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
				mSimpleCursorAdapter.setViewBinder(LIST_VIEW_BINDER);
				mPoiListView.setAdapter(mSimpleCursorAdapter);
				mPoiListView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						Intent intent = new Intent(RestListActivity.this,
								RestDetailActivity.class);
						intent.putExtra("ID", id);
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
								| Intent.FLAG_ACTIVITY_NEW_TASK);
						RestListActivity.this.startActivity(intent);
						RestListActivity.this.finish();
						RestListActivity.this.overridePendingTransition(
								R.anim.anim_in_right2left,
								R.anim.anim_out_right2left);
					}
				});
			} else {
				Toast.makeText(RestListActivity.this, "获取数据失败",
						Toast.LENGTH_LONG).show();
			}
		}

	}

}
