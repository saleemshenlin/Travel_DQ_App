package com.travelapp;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 购物poi详细页
 * 
 * @author saleemshenlin <br>
 *         通过"~/api/poi/{id}"进行查询<br>
 *         将查询的结果进行显示，图片，名称，开放时间，地址，电话和简介。
 * 
 */
public class FunDetailActivity extends Activity {
	private ImageView mBackImageView;
	private ImageView mItemImageView;
	private TextView mTitleTextView;
	private TextView mItemPrice;
	private LinearLayout mLinearLayout;
	private TextView mItemAddress;
	private TextView mItemTele;
	private TextView mItemAbstract;
	private ImageView mMapImageView;
	private Query mQuery;
	private Intent mIntent;
	private Bundle mBundle;
	private int mPoiId;
	private int mPoiType;
	private String mFrom;
	private Resources mResources;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		mIntent = getIntent();
		mBundle = mIntent.getExtras();
		mPoiId = mBundle.getInt("ID");
		mFrom = "Detail";
		mResources = this.getResources();
		initView();
		new getPOI().execute(String.valueOf(mPoiId));
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			TravelApplication.buildExitDialog(FunDetailActivity.this);
		}
		return false;
	}

	private void initView() {
		mBackImageView = (ImageView) findViewById(R.id.imgListBack);
		mMapImageView = (ImageView) findViewById(R.id.imgListMap);
		mItemImageView = (ImageView) findViewById(R.id.imgItem);
		mTitleTextView = (TextView) findViewById(R.id.txtListTitle);
		mItemPrice = (TextView) findViewById(R.id.txtItemPrice);
		mLinearLayout = (LinearLayout) findViewById(R.id.linTime);
		mLinearLayout.setVisibility(View.GONE);
		mItemAddress = (TextView) findViewById(R.id.txtItemAddress);
		mItemTele = (TextView) findViewById(R.id.txtItemTele);
		mItemAbstract = (TextView) findViewById(R.id.txtItemAbstract);
		if (mBundle.getString("FROM") != null) {
			mFrom = mBundle.getString("FROM");
		}
		if (mFrom.equals("MapActivity")) {
			mBackImageView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(FunDetailActivity.this,
							MapActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
							| Intent.FLAG_ACTIVITY_NEW_TASK);
					FunDetailActivity.this.startActivity(intent);
					FunDetailActivity.this.finish();
					FunDetailActivity.this.overridePendingTransition(
							R.anim.anim_in_left2right,
							R.anim.anim_out_left2right);
				}
			});
		} else if (mFrom.equals("ListActivity")) {
			mBackImageView.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(FunDetailActivity.this,
							MapActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
							| Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.putExtra("FUNCTION", "POIS");
					intent.putExtra("TYPE", 4);
					FunDetailActivity.this.startActivity(intent);
					FunDetailActivity.this.finish();
					FunDetailActivity.this.overridePendingTransition(
							R.anim.anim_in_left2right,
							R.anim.anim_out_left2right);
				}
			});
		} else {
			mBackImageView.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(FunDetailActivity.this,
							FunListActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
							| Intent.FLAG_ACTIVITY_NEW_TASK);
					FunDetailActivity.this.startActivity(intent);
					FunDetailActivity.this.finish();
					FunDetailActivity.this.overridePendingTransition(
							R.anim.anim_in_left2right,
							R.anim.anim_out_left2right);
				}
			});
		}
		mMapImageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(FunDetailActivity.this,
						MapActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
						| Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra("FUNCTION", "POI");
				intent.putExtra("TYPE", mPoiType);
				intent.putExtra("ID", mPoiId);
				FunDetailActivity.this.startActivity(intent);
				FunDetailActivity.this.finish();
				FunDetailActivity.this.overridePendingTransition(
						R.anim.anim_in_right2left, R.anim.anim_out_right2left);
			}
		});
		if (TravelApplication.isFromMap) {
			Drawable mDrawable = mResources.getDrawable(R.drawable.bg_noimg);
			mMapImageView.setImageDrawable(mDrawable);
			TravelApplication.isFromMap = false;
		}
	}

	/**
	 * 后台线程查询poi详细信息
	 * 
	 * @author saleemshenlin<br>
	 *         建立后台线程通过web api获取poi详细信息,通过POI类来更新页面，传入poi的id信息
	 * 
	 */
	class getPOI extends AsyncTask<String, String, POI> {

		@Override
		protected POI doInBackground(String... ids) {
			// TODO Auto-generated method stub
			mQuery = new Query();
			POI mPoi = mQuery.getPoiFromAPI(ids[0]);
			return mPoi;
		}

		@Override
		protected void onPostExecute(POI mPoi) {
			// TODO Auto-generated method stub
			super.onPostExecute(mPoi);
			try {
				if (mPoi != null) {
					mTitleTextView.setText(mPoi.Name);
					mItemPrice.setText("开放时间：" + mPoi.Time);
					mItemAddress.setText("地址：" + mPoi.Address);
					mItemTele.setText("电话：" + mPoi.Tele);
					mItemAbstract.setText("简介：" + mPoi.Abstract);
					mPoiType = mPoi.C_ID;
					if (mPoi.ImgUrl != null || !mPoi.ImgUrl.equals("null")) {
						String filePath = FunDetailActivity.this
								.getExternalFilesDir("cache")
								+ "/"
								+ Query.returnBitMap(mPoi.ImgUrl);
						Bitmap mBitmap = Query.readBitmapFromFile(
								getApplicationContext(), filePath, 1);
						mItemImageView.setImageBitmap(mBitmap);
					}
				}
			} catch (Exception e) {
				Log.e("FunDetail", e.toString());
			}
		}

	}

}
