package com.travelapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 周边查询类别页
 * 
 * @author saleemshenlin<br>
 *         用于列表显示周边查询的4个分类，点击后跳转到AroundDetailActivity进行查询<br>
 *         并且通过百度定位api获取当前位置信息。
 * 
 */
public class AroundListActivity extends Activity {
	private ImageView mBackImageView;
	private TextView mTitleTextView;
	private LinearLayout mScenicLayout;
	private LinearLayout mHotelLayout;
	private LinearLayout mRestLayout;
	private LinearLayout mFunLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_aroundlist);
		initView();
		getBDLocation();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			TravelApplication.buildExitDialog(AroundListActivity.this);
		}
		return false;
	}

	private void initView() {
		// TODO Auto-generated method stub
		mBackImageView = (ImageView) findViewById(R.id.imgListBack);
		mTitleTextView = (TextView) findViewById(R.id.txtListTitle);
		mScenicLayout = (LinearLayout) findViewById(R.id.linScenicAround);
		mHotelLayout = (LinearLayout) findViewById(R.id.linHotelAround);
		mRestLayout = (LinearLayout) findViewById(R.id.linRestAround);
		mFunLayout = (LinearLayout) findViewById(R.id.linFunAround);
		mTitleTextView.setText("周边查询");
		mBackImageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(AroundListActivity.this,
						HomeActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
						| Intent.FLAG_ACTIVITY_NEW_TASK);
				AroundListActivity.this.startActivity(intent);
				AroundListActivity.this.finish();
				AroundListActivity.this.overridePendingTransition(
						R.anim.anim_in_left2right, R.anim.anim_out_left2right);
			}
		});
		mScenicLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(AroundListActivity.this,
						AroundDetailActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
						| Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra("FROM", 1);
				AroundListActivity.this.startActivity(intent);
				AroundListActivity.this.finish();
				AroundListActivity.this.overridePendingTransition(
						R.anim.anim_in_right2left, R.anim.anim_out_right2left);
			}
		});
		mHotelLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(AroundListActivity.this,
						AroundDetailActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
						| Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra("FROM", 2);
				AroundListActivity.this.startActivity(intent);
				AroundListActivity.this.finish();
				AroundListActivity.this.overridePendingTransition(
						R.anim.anim_in_right2left, R.anim.anim_out_right2left);
			}
		});
		mRestLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(AroundListActivity.this,
						AroundDetailActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
						| Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra("FROM", 3);
				AroundListActivity.this.startActivity(intent);
				AroundListActivity.this.finish();
				AroundListActivity.this.overridePendingTransition(
						R.anim.anim_in_right2left, R.anim.anim_out_right2left);
			}
		});
		mFunLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(AroundListActivity.this,
						AroundDetailActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
						| Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra("FROM", 4);
				AroundListActivity.this.startActivity(intent);
				AroundListActivity.this.finish();
				AroundListActivity.this.overridePendingTransition(
						R.anim.anim_in_right2left, R.anim.anim_out_right2left);
			}
		});
	}

	/**
	 * 通过百度定位api获取当前位置信息
	 */
	private void getBDLocation() {
		TravelApplication.mLocationClient.start();
		if (TravelApplication.mLocationClient != null
				&& TravelApplication.mLocationClient.isStarted())
			TravelApplication.mLocationClient.requestLocation();
		else
			Log.d("LocSDK3", "locClient is null or not started");
	}

}
