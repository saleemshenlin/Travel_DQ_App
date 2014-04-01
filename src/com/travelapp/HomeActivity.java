package com.travelapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * 
 * @author saleemshenlin<br>
 *         应用主页，显示导航按钮<br>
 *         地图、景点、住宿、餐饮、购物、周边、线路、分析        
 * 
 */
public class HomeActivity extends Activity {

	private ImageView mMapImageView;
	private ImageView mScenicImageView;
	private ImageView mHotelImageView;
	private ImageView mRestImageView;
	private ImageView mFunImageView;
	private ImageView mRouteImageView;
	private ImageView mSocialImageView;
	private ImageView mAroundImageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		initView();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			TravelApplication.buildExitDialog(HomeActivity.this);
		}
		return false;
	}

	private void initView() {
		mMapImageView = (ImageView) findViewById(R.id.imgItemMap);
		mScenicImageView = (ImageView) findViewById(R.id.imgItemScenic);
		mHotelImageView = (ImageView) findViewById(R.id.imgItemHotel);
		mFunImageView = (ImageView) findViewById(R.id.imgItemFun);
		mRestImageView = (ImageView) findViewById(R.id.imgItemRest);
		mRouteImageView = (ImageView) findViewById(R.id.imgItemRoute);
		mSocialImageView = (ImageView) findViewById(R.id.imgItemSocial);
		mAroundImageView = (ImageView) findViewById(R.id.imgItemAround);
		mMapImageView.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(HomeActivity.this, MapActivity.class);
				HomeActivity.this.startActivity(intent);
				HomeActivity.this.finish();
				HomeActivity.this.overridePendingTransition(
						R.anim.anim_in_right2left, R.anim.anim_out_right2left);
			}
		});
		mScenicImageView.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(HomeActivity.this,
						ScenicListActivity.class);
				HomeActivity.this.startActivity(intent);
				HomeActivity.this.finish();
				HomeActivity.this.overridePendingTransition(
						R.anim.anim_in_right2left, R.anim.anim_out_right2left);
			}
		});
		mHotelImageView.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(HomeActivity.this,
						HotelListActivity.class);
				HomeActivity.this.startActivity(intent);
				HomeActivity.this.finish();
				HomeActivity.this.overridePendingTransition(
						R.anim.anim_in_right2left, R.anim.anim_out_right2left);
			}
		});
		mRestImageView.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(HomeActivity.this,
						RestListActivity.class);
				HomeActivity.this.startActivity(intent);
				HomeActivity.this.finish();
				HomeActivity.this.overridePendingTransition(
						R.anim.anim_in_right2left, R.anim.anim_out_right2left);
			}
		});
		mFunImageView.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(HomeActivity.this,
						FunListActivity.class);
				HomeActivity.this.startActivity(intent);
				HomeActivity.this.finish();
				HomeActivity.this.overridePendingTransition(
						R.anim.anim_in_right2left, R.anim.anim_out_right2left);
			}
		});
		mAroundImageView.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(HomeActivity.this,
						AroundListActivity.class);
				HomeActivity.this.startActivity(intent);
				HomeActivity.this.finish();
				HomeActivity.this.overridePendingTransition(
						R.anim.anim_in_right2left, R.anim.anim_out_right2left);
			}
		});
		mRouteImageView.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(HomeActivity.this,
						RouteListActivity.class);
				HomeActivity.this.startActivity(intent);
				HomeActivity.this.finish();
				HomeActivity.this.overridePendingTransition(
						R.anim.anim_in_right2left, R.anim.anim_out_right2left);
			}
		});
		mSocialImageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeActivity.this,
						SocialActivity.class);
				HomeActivity.this.startActivity(intent);
				HomeActivity.this.finish();
				HomeActivity.this.overridePendingTransition(
						R.anim.anim_in_right2left, R.anim.anim_out_right2left);
			}
		});
	}

}
