package com.travelapp;

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
	private static PoiDB mPoiDB;

	public static Context getContext() {
		return CONTEXT;
	}

	public static PoiDB getPoiDB() {
		mPoiDB = new PoiDB(getContext());
		return mPoiDB;
	}

	/**
	 * 创建LbsApplication<br>
	 * 1)获取上下文,赋值个CONTEXT<br>
	 * 2)获取屏幕分辨率<br>
	 * 3)初始化SuperMap环境<br>
	 * 4)初始化mPoint2d和LOCATIONACCUCRACY<br>
	 * 
	 */
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

	public static int getScreenWidth() {
		return SCREENWIDTH;
	}

	public static void setScreenWidth(float xdpi) {
		TravelApplication.SCREENWIDTH = (int) xdpi;
	}

	public static int getScreenHeight() {
		return SCREENHEIGHT;
	}

	public static void setScreenHeight(float ydpi) {
		TravelApplication.SCREENHEIGHT = (int) ydpi;
	}

	public static double getScreenDPI() {
		return SCREENDPI;
	}

	public static void setScreenDPI(double screenDPI) {
		TravelApplication.SCREENDPI = screenDPI;
	}

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

	private void initWB() {
		mWeiboAuth = new WeiboAuth(getApplicationContext(),
				getApplicationContext().getString(R.string.wb_app_id),
				getApplicationContext().getString(R.string.redirect_url), null);
	}
	
	private void initEsriSDK(){
		String myClientID = this.getString(R.string.esri_client_id);
		ArcGISRuntime.setClientId(myClientID); 
	}
}
