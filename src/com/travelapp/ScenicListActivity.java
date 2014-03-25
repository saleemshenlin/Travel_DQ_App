package com.travelapp;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ScenicListActivity extends Activity {
	private ImageView mBackImageView;
	private TextView mTitleTextView;
	private ImageView mMapImageView;
	private static Resources mResources;
	private ArrayList<POI> mList = new ArrayList<POI>();
	/**
	 * 定义一个标签,在LogCat内表示EventListFragment
	 */
	private static final String TAG = "ScenicListActivity";
	/**
	 * ListView实例,用于显示Event列表
	 */
	private ListView mPoiListView;
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
			TravelApplication.buildExitDialog(ScenicListActivity.this);
		}
		return false;
	}

	private void initView() {
		mBackImageView = (ImageView) findViewById(R.id.imgListBack);
		mMapImageView = (ImageView) findViewById(R.id.imgListMap);
		mTitleTextView = (TextView) findViewById(R.id.txtListTitle);
		mPoiListView = (ListView) findViewById(R.id.listPoi);
		mTitleTextView.setText("景点列表");
		mBackImageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(ScenicListActivity.this,
						HomeActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
						| Intent.FLAG_ACTIVITY_NEW_TASK);
				ScenicListActivity.this.startActivity(intent);
				ScenicListActivity.this.finish();
				ScenicListActivity.this.overridePendingTransition(
						R.anim.anim_in_left2right, R.anim.anim_out_left2right);
			}
		});
		mMapImageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ScenicListActivity.this,
						MapActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
						| Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra("FUNCTION", "POIS");
				intent.putExtra("TYPE", 1);
				ScenicListActivity.this.startActivity(intent);
				ScenicListActivity.this.finish();
				ScenicListActivity.this.overridePendingTransition(
						R.anim.anim_in_right2left, R.anim.anim_out_right2left);
			}
		});
	}

	class PoiQuery extends AsyncTask<String[], String, ArrayList<POI>> {

		@Override
		protected ArrayList<POI> doInBackground(String[]... params) {
			mQuery = new Query();
			try {
				mList = mQuery.getPoisFromWebAPI(1);
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
				Toast.makeText(ScenicListActivity.this, "成功获取数据",
						Toast.LENGTH_LONG).show();
				QueryListAdapter mQueryListAdapter = new QueryListAdapter(
						ScenicListActivity.this, R.layout.row, result);
				mPoiListView.setAdapter(mQueryListAdapter);
				mPoiListView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						Intent intent = new Intent(ScenicListActivity.this,
								ScenicDetailActivity.class);
						int poiId = mList.get(position).Id;
						intent.putExtra("ID", poiId);
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
								| Intent.FLAG_ACTIVITY_NEW_TASK);
						ScenicListActivity.this.startActivity(intent);
						ScenicListActivity.this.finish();
						ScenicListActivity.this.overridePendingTransition(
								R.anim.anim_in_right2left,
								R.anim.anim_out_right2left);
					}
				});
			} else {
				Toast.makeText(ScenicListActivity.this, "获取数据失败",
						Toast.LENGTH_SHORT).show();
			}
		}

	}

	class QueryListAdapter extends ArrayAdapter<POI> {
		private int resourceId;

		public QueryListAdapter(Context context, int textViewResourceId,
				List<POI> objects) {
			super(context, textViewResourceId, objects);
			this.resourceId = textViewResourceId;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			POI mPoi = getItem(position);
			LinearLayout queryListLayout = new LinearLayout(getContext());
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
					inflater);
			vi.inflate(resourceId, queryListLayout, true);
			TextView mItemTitle = (TextView) queryListLayout
					.findViewById(R.id.txtRowTitle);
			TextView mItemPrice = (TextView) queryListLayout
					.findViewById(R.id.txtRowPrice);
			TextView mItemAbstract = (TextView) queryListLayout
					.findViewById(R.id.txtRowAbstract);
			ImageView mItemImage = (ImageView) queryListLayout
					.findViewById(R.id.imgPalce);
			mItemTitle.setText(mPoi.Name);
			mItemPrice.setText(mPoi.Ticket);
			mItemAbstract.setText(mPoi.Abstract);
			if (mPoi.ImgUrl == null || mPoi.ImgUrl.equals("null")) {
				int id = mResources.getIdentifier("img_missing", "drawable",
						"com.travelapp");
				Drawable mDrawable = mResources.getDrawable(id);
				mItemImage.setImageDrawable(mDrawable);
			} else {
				String imgUrl = mPoi.ImgUrl + "_mini.jpg";
				mItemImage.setImageBitmap(mQuery.returnBitMap(imgUrl));
			}
			return queryListLayout;
		}
	}

}
