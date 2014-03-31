package com.travelapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class SplashActivity extends Activity {
	/**
	 * ����һ������,������¼ʱ���Ƿ����״μ���,
	 */
	boolean isFirstIn = false;
	/**
	 * ����һ������,���ڱ�ʾ����HomeActivity
	 */
	private static final int Add_DATA = 1000;
	/**
	 * ����һ������,���ڱ�ʾ����GuideActivity
	 */
	private static final int GO_HOME = 1001;
	/**
	 * ����һ������,���ڱ�ʾ�ȴ�ʱ��,ֵΪ1s
	 */
	private static final long SPLASH_DELAY_MILLIS = 1000;
	/**
	 * ����һ������,���ڱ�ʾSharedPreferences������
	 */
	private static final String SHAREDPREFERENCES_NAME = "first_pref";
	/**
	 * ����һ��Handler,������ʾ��ת����ͬ����
	 */
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Add_DATA:
				goHome();
				break;
			case GO_HOME:
				goHome();
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		init();
	}

	private void init() {
		SharedPreferences preferences = getSharedPreferences(
				SHAREDPREFERENCES_NAME, MODE_PRIVATE);
		isFirstIn = preferences.getBoolean("isFirstIn", true);
		if (!isFirstIn) {
			mHandler.sendEmptyMessageDelayed(GO_HOME, SPLASH_DELAY_MILLIS);
		} else {
			mHandler.sendEmptyMessageDelayed(Add_DATA, SPLASH_DELAY_MILLIS);
		}

	}

	/**
	 * ������ת����HomeActivity
	 */
	private void goHome() {
		Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
		SplashActivity.this.startActivity(intent);
		SplashActivity.this.finish();
		SplashActivity.this.overridePendingTransition(
				R.anim.anim_in_right2left, R.anim.anim_out_left2right);
	}

}
