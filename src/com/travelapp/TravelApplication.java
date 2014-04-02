package com.travelapp;

import java.io.File;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.util.DisplayMetrics;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.esri.android.runtime.ArcGISRuntime;
import com.esri.core.geometry.Point;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * 应用的Application
 * 
 * @author saleemshenlin<br>
 *         用于初始化第三方SDK，Baidu定位，微信，微博，ArcGIS Runtime
 * 
 */
public class TravelApplication extends Application {
	/**
	 * 定义一个标签,在LogCat内表示LBSApplication
	 */
	private final static String TAG = "TravelApplication";
	/**
	 * 定义一个常数,用于表示屏幕宽度
	 */
	private static int SCREENWIDTH;
	/**
	 * 定义一个常数,用于表示屏幕高度
	 */
	private static int SCREENHEIGHT;
	/**
	 * 定义一个常数,用于表示DPI
	 */
	private static double SCREENDPI;
	/**
	 * 定义一个常量,用于表示上下文
	 */
	private static Context CONTEXT;
	/**
	 * 是否来自ListMap
	 */
	public static boolean isFromMap = false;

	public static LocationClient mLocationClient = null;
	public static Point mLocationPoint;
	public static IWXAPI mIwxapi;
	public static WeiboAuth mWeiboAuth;

	private BDLocationListener mBDListener = (BDLocationListener) new MyLocationListener();
	public static File imgCache;

	public static Context getContext() {
		return CONTEXT;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		CONTEXT = getApplicationContext();
		mLocationPoint = new Point(121.423656, 31.170015);
		Log.i(TAG, "LBSApplication onCreate!");
		getScreenDesplay();
		Log.i(TAG, "LBSApplication getScreenDisplay height:" + SCREENHEIGHT);
		initBDLocation();
		initWX();
		initWB();
		initEsriSDK();
		// 建立图片缓存
		imgCache = new File(TravelApplication.getContext().getExternalFilesDir(
				""), "cache");
		if (!imgCache.exists()) {
			imgCache.mkdirs();
		}
	}

	/**
	 * 用于Dp转像素
	 * 
	 * @param context
	 *            上下文
	 * @param dp
	 *            DIP
	 * @return int PX
	 */
	public static int Dp2Px(Context context, int dp) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	/**
	 * 用于获取屏幕分别率
	 */
	private void getScreenDesplay() {
		DisplayMetrics dm = new DisplayMetrics();
		dm = getResources().getDisplayMetrics();
		setScreenWidth(dm.widthPixels);
		setScreenHeight(dm.heightPixels);
		setScreenDPI(dm.densityDpi);
	}

	/**
	 * 用于获取屏幕宽度
	 */
	public static int getScreenWidth() {
		return SCREENWIDTH;
	}

	/**
	 * 用于设置屏幕宽度
	 */
	public static void setScreenWidth(float xdpi) {
		TravelApplication.SCREENWIDTH = (int) xdpi;
	}

	/**
	 * 用于获取屏幕高度
	 */
	public static int getScreenHeight() {
		return SCREENHEIGHT;
	}

	/**
	 * 用于设置屏幕高度
	 */
	public static void setScreenHeight(float ydpi) {
		TravelApplication.SCREENHEIGHT = (int) ydpi;
	}

	/**
	 * 用于获取屏幕DPI
	 */
	public static double getScreenDPI() {
		return SCREENDPI;
	}

	/**
	 * 用于设置屏幕DPI
	 */
	public static void setScreenDPI(double screenDPI) {
		TravelApplication.SCREENDPI = screenDPI;
	}

	/**
	 * 用于处理应用退出
	 * 
	 * @param context
	 *            当前上下文
	 */
	public static void buildExitDialog(Context context) {
		AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
		mBuilder.setMessage("确认退出吗？");
		mBuilder.setTitle("提示");
		mBuilder.setPositiveButton("确认", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				System.exit(0);
			}
		});
		mBuilder.setNegativeButton("取消", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		mBuilder.create().show();
	}

	/**
	 * 初始化百度定位sdk
	 */
	private void initBDLocation() {
		mLocationClient = new LocationClient(getApplicationContext()); // 声明LocationClient类
		mLocationClient.registerLocationListener(mBDListener);
		// mLocationClient.setAK(getString(R.string.baiduak));
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setAddrType("all");// 返回的定位结果包含地址信息
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
		option.setScanSpan(5000);// 设置发起定位请求的间隔时间为5000ms
		option.disableCache(true);// 禁止启用缓存定位
		option.setPoiNumber(5); // 最多返回POI个数
		option.setPoiDistance(1000); // poi查询距离
		option.setPoiExtraInfo(true); // 是否需要POI的电话和地址等详细信息
		mLocationClient.setLocOption(option);
	}

	/**
	 * 百度定位sdk的位置监听类
	 * 
	 * @author saleemshenlin<br>
	 *         监听当前获取的位置信息
	 */
	public class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;
			StringBuffer sb = new StringBuffer(256);
			sb.append("time : ");
			sb.append(location.getTime());
			sb.append("\nerror code : ");
			sb.append(location.getLocType());
			sb.append("\nlatitude : ");
			sb.append(location.getLatitude());
			sb.append("\nlontitude : ");
			sb.append(location.getLongitude());
			sb.append("\nradius : ");
			sb.append(location.getRadius());
			if (location.getLocType() == BDLocation.TypeGpsLocation) {
				sb.append("\nspeed : ");
				sb.append(location.getSpeed());
				sb.append("\nsatellite : ");
				sb.append(location.getSatelliteNumber());
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
			}
			mLocationPoint.setX(location.getLongitude());
			mLocationPoint.setY(location.getLatitude());
			Log.d("BDLocation", sb.toString());
			Log.d("LocationPoint",
					mLocationPoint.getX() + ";" + mLocationPoint.getY());
		}

		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null) {
				return;
			}
			StringBuffer sb = new StringBuffer(256);
			sb.append("Poi time : ");
			sb.append(poiLocation.getTime());
			sb.append("\nerror code : ");
			sb.append(poiLocation.getLocType());
			sb.append("\nlatitude : ");
			sb.append(poiLocation.getLatitude());
			sb.append("\nlontitude : ");
			sb.append(poiLocation.getLongitude());
			sb.append("\nradius : ");
			sb.append(poiLocation.getRadius());
			if (poiLocation.getLocType() == BDLocation.TypeNetWorkLocation) {
				sb.append("\naddr : ");
				sb.append(poiLocation.getAddrStr());
			}
			if (poiLocation.hasPoi()) {
				sb.append("\nPoi:");
				sb.append(poiLocation.getPoi());
			} else {
				sb.append("noPoi information");
			}
			Log.e("BDLocation", sb.toString());
		}
	}

	/**
	 * 初始化微信sdk
	 */
	private void initWX() {
		mIwxapi = WXAPIFactory.createWXAPI(getApplicationContext(),
				getApplicationContext().getString(R.string.wx_app_id), false);
		boolean isRegist = mIwxapi.registerApp(getApplicationContext()
				.getString(R.string.wx_app_id));
		if (isRegist) {
			Log.e("WX", "OK");
		} else {
			Log.e("WX", "Error");
		}
	}

	/**
	 * 初始化微博sdk
	 */
	private void initWB() {
		mWeiboAuth = new WeiboAuth(getApplicationContext(),
				getApplicationContext().getString(R.string.wb_app_id),
				getApplicationContext().getString(R.string.redirect_url), null);
	}

	/**
	 * 初始化ArcGIS Runtime sdk
	 */
	private void initEsriSDK() {
		String myClientID = this.getString(R.string.esri_client_id);
		ArcGISRuntime.setClientId(myClientID);
	}
}
