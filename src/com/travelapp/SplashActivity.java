package com.travelapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

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
				addData();
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

	/*
	 * �����״μ�������
	 */
	protected void addData() {
		// TODO Auto-generated method stub
		new AddData().execute();
	}

	/**
	 * ���ڸ���SharedPreferences���´����������ٴ�����
	 */
	private void setGuided() {
		SharedPreferences preferences = getSharedPreferences(
				SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putBoolean("isFirstIn", false);
		editor.commit();
	}

	class AddData extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			Query mQuery = new Query();
			mQuery.getPoisFromWebAPI(0);
			return null;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			Toast.makeText(SplashActivity.this, "�״μ�������...>>>>",
					Toast.LENGTH_LONG).show();
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			Toast.makeText(SplashActivity.this, "��ɼ������ݣ�", Toast.LENGTH_SHORT)
					.show();
			setGuided();
			goHome();
		}

	}

}
