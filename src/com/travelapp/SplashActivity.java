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
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

public class SplashActivity extends Activity {
	/**
	 * 定义一个常量,用来纪录时候是否是首次加载,
	 */
	boolean isFirstIn = false;
	/**
	 * 定义一个常数,用于表示进入HomeActivity
	 */
	private static final int Add_DATA = 1000;
	/**
	 * 定义一个常数,用于表示进入GuideActivity
	 */
	private static final int GO_HOME = 1001;
	/**
	 * 定义一个常数,用于表示等待时间,值为1s
	 */
	private static final long SPLASH_DELAY_MILLIS = 1000;
	/**
	 * 定义一个常量,用于表示SharedPreferences的名称
	 */
	private static final String SHAREDPREFERENCES_NAME = "first_pref";
	/**
	 * 实例一个进度条,用来表示正在加载数据
	 */
	private ProgressBar prbLoad;
	/**
	 * 定义一个Handler,用来表示跳转到不同界面
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
		prbLoad = (ProgressBar) findViewById(R.id.prbLoadData);
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
	 * 用于跳转进入HomeActivity
	 */
	private void goHome() {
		Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
		SplashActivity.this.startActivity(intent);
		SplashActivity.this.finish();
		SplashActivity.this.overridePendingTransition(
				R.anim.anim_in_right2left, R.anim.anim_out_left2right);
	}

	/**
	 * 用于跳转j进入GuideActivity
	 */
	private void addData() {
		new InitDataBaseData().execute();
	}

	/**
	 * 类InitDataBaseData<br>
	 * 用于在首次加载时调用FileIO.getDateFromXML()<br>
	 * 采用多线程导入数据、更新課程日期，<br>
	 * 因导入数据时间比导入地图数据时间长,在结束之后调用init();
	 */
	class InitDataBaseData extends AsyncTask<String, Integer, String> {

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			prbLoad.setVisibility(View.GONE);
			Toast.makeText(SplashActivity.this, result, Toast.LENGTH_SHORT)
					.show();
			setGuided();
			goHome();
		}

		@Override
		protected String doInBackground(String... params) {
			FileIO fileIO = new FileIO();
			fileIO.getDateFromXML();
			fileIO.copyJSON(TravelApplication.getContext());
			fileIO.jSON2WKT(TravelApplication.getContext(), "poi.json");
			fileIO.jSON2WKT(TravelApplication.getContext(), "route.json");
			return "加载完毕";
		}

	}

	/**
	 * 用于更新SharedPreferences，下次启动不用再次引导
	 */
	private void setGuided() {
		SharedPreferences preferences = getSharedPreferences(
				SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putBoolean("isFirstIn", false);
		editor.commit();
	}
}
