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

public class RestListActivity extends Activity {
	private ImageView mBackImageView;
	private TextView mTitleTextView;
	private ImageView mMapImageView;
	private SmoothProgressBar mProgressBar;
	/**
	 * 定义一个标签,在LogCat内表示EventListFragment
	 */
	private static final String TAG = "ScenicListActivity";
	private ArrayList<POI> mList = new ArrayList<POI>();

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
			TravelApplication.buildExitDialog(RestListActivity.this);
		}
		return false;
	}

	private void initView() {
		mBackImageView = (ImageView) findViewById(R.id.imgListBack);
		mMapImageView = (ImageView) findViewById(R.id.imgListMap);
		mTitleTextView = (TextView) findViewById(R.id.txtListTitle);
		mProgressBar = (SmoothProgressBar) findViewById(R.id.smoothBar);
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

	class PoiQuery extends AsyncTask<String[], String, ArrayList<POI>> {

		/**
		 * ListView实例,用于显示Event列表
		 */
		private ListView mPoiListView = (ListView) findViewById(R.id.listPoi);;

		@Override
		protected ArrayList<POI> doInBackground(String[]... params) {
			Query mQuery = new Query();
			try {
				mList = mQuery.getPoisFromWebAPI(3);
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
				Toast.makeText(RestListActivity.this, "成功获取数据",
						Toast.LENGTH_LONG).show();
				NewQueryListAdapter mQueryListAdapter = new NewQueryListAdapter(
						RestListActivity.this, R.layout.row, result,
						mPoiListView, 3);
				mPoiListView.setAdapter(mQueryListAdapter);
				mPoiListView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						Intent intent = new Intent(RestListActivity.this,
								RestDetailActivity.class);
						int poiId = mList.get(position).Id;
						intent.putExtra("ID", poiId);
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
						Toast.LENGTH_SHORT).show();
			}
		}
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			Toast.makeText(RestListActivity.this, "正在获取数据....>>>",
					Toast.LENGTH_SHORT).show();
		}

	}

}
