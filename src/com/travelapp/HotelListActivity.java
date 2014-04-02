package com.travelapp;

import java.util.ArrayList;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 住宿poi列表页
 * 
 * @author saleemshenlin <br>
 *         显示住宿poi的列表，包括poi的图片、名称、房价和简介<br>
 *         通过"~/api/pois？type={type}"查询<br>
 *         将结果通过NewQueryListAdapter绑定到ListView中<br>
 *         点击列表的item能够跳转到poi详细页
 * 
 */
public class HotelListActivity extends Activity {
	private ImageView mBackImageView;
	private TextView mTitleTextView;
	private ImageView mMapImageView;
	private SmoothProgressBar mProgressBar;
	private ArrayList<POI> mList = new ArrayList<POI>();
	/**
	 * 定义一个标签,在LogCat内表示EventListFragment
	 */
	private static final String TAG = "ScenicListActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		initView();
		new PoiQuery().execute();
		// new AddMap().execute();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			TravelApplication.buildExitDialog(HotelListActivity.this);
		}
		return false;
	}

	private void initView() {
		mBackImageView = (ImageView) findViewById(R.id.imgListBack);
		mMapImageView = (ImageView) findViewById(R.id.imgListMap);
		mTitleTextView = (TextView) findViewById(R.id.txtListTitle);
		mProgressBar = (SmoothProgressBar) findViewById(R.id.smoothBar);
		mTitleTextView.setText("住宿列表");
		mBackImageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(HotelListActivity.this,
						HomeActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
						| Intent.FLAG_ACTIVITY_NEW_TASK);
				HotelListActivity.this.startActivity(intent);
				HotelListActivity.this.finish();
				HotelListActivity.this.overridePendingTransition(
						R.anim.anim_in_left2right, R.anim.anim_out_left2right);
			}
		});
		mMapImageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HotelListActivity.this,
						MapActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
						| Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra("FUNCTION", "POIS");
				intent.putExtra("TYPE", 2);
				HotelListActivity.this.startActivity(intent);
				HotelListActivity.this.finish();
				HotelListActivity.this.overridePendingTransition(
						R.anim.anim_in_right2left, R.anim.anim_out_right2left);
			}
		});
	}

	/**
	 * 后台线程查询poi列表
	 * 
	 * @author saleemshenlin <br>
	 *         建立后台线程通过web api获取poi列表信息<br>
	 *         将结果ArrayList通过NewQueryListAdapter绑定到ListView中
	 * 
	 */
	class PoiQuery extends AsyncTask<String[], String, ArrayList<POI>> {

		/**
		 * ListView实例,用于显示Event列表
		 */
		private ListView mPoiListView = (ListView) findViewById(R.id.listPoi);;

		@Override
		protected ArrayList<POI> doInBackground(String[]... params) {
			Query mQuery = new Query();
			try {
				mList = mQuery.getPoisFromWebAPI(2);
				int num = mList.size();
				Log.i(TAG, "ActivityProvider cursor" + num);
			} catch (Exception e) {
				Log.e(TAG, e.toString());
			}
			return mList;
		}

		@Override
		protected void onPostExecute(ArrayList<POI> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (result != null) {
				mProgressBar.setVisibility(View.GONE);
				Toast.makeText(HotelListActivity.this, "成功获取数据",
						Toast.LENGTH_LONG).show();
				NewQueryListAdapter mQueryListAdapter = new NewQueryListAdapter(
						HotelListActivity.this, R.layout.row, result,
						mPoiListView, 2);
				mPoiListView.setAdapter(mQueryListAdapter);
				mPoiListView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						Intent intent = new Intent(HotelListActivity.this,
								HotelDetailActivity.class);
						int poiId = mList.get(position).Id;
						intent.putExtra("ID", poiId);
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
								| Intent.FLAG_ACTIVITY_NEW_TASK);
						HotelListActivity.this.startActivity(intent);
						HotelListActivity.this.finish();
						HotelListActivity.this.overridePendingTransition(
								R.anim.anim_in_right2left,
								R.anim.anim_out_right2left);
					}
				});
			} else {
				Toast.makeText(HotelListActivity.this, "获取数据失败",
						Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			Toast.makeText(HotelListActivity.this, "正在获取数据....>>>",
					Toast.LENGTH_SHORT).show();
		}

	}

}
