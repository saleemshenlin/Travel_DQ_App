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
import com.esri.core.geometry.Point;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class TravelApplication extends Application {
	/**
	 * ����һ����ǩ,��LogCat�ڱ�ʾLBSApplication
	 */
	private final static String TAG = "TravelApplication";
	/**
	 * ����һ������,���ڱ�ʾ��Ļ���
	 */
	private static int SCREENWIDTH;
	/**
	 * ����һ������,���ڱ�ʾ��Ļ�߶�
	 */
	private static int SCREENHEIGHT;
	/**
	 * ����һ������,���ڱ�ʾDPI
	 */
	private static double SCREENDPI;
	/**
	 * ����һ������,���ڱ�ʾ������
	 */
	private static Context CONTEXT;
	/**
	 * �Ƿ�����ListMap
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
	 * ����LbsApplication<br>
	 * 1)��ȡ������,��ֵ��CONTEXT<br>
	 * 2)��ȡ��Ļ�ֱ���<br>
	 * 3)��ʼ��SuperMap����<br>
	 * 4)��ʼ��mPoint2d��LOCATIONACCUCRACY<br>
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
	}

	/**
	 * ����Dpת����
	 * 
	 * @param context
	 *            ������
	 * @param dp
	 *            DIP
	 * @return int PX
	 */
	public static int Dp2Px(Context context, int dp) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	/**
	 * ���ڻ�ȡ��Ļ�ֱ���
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
		mBuilder.setMessage("ȷ���˳���");
		mBuilder.setTitle("��ʾ");
		mBuilder.setPositiveButton("ȷ��", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				System.exit(0);
			}
		});
		mBuilder.setNegativeButton("ȡ��", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		mBuilder.create().show();
	}

	private void initBDLocation() {
		mLocationClient = new LocationClient(getApplicationContext()); // ����LocationClient��
		mLocationClient.registerLocationListener(mBDListener);
		// mLocationClient.setAK(getString(R.string.baiduak));
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setAddrType("all");// ���صĶ�λ���������ַ��Ϣ
		option.setCoorType("bd09ll");// ���صĶ�λ����ǰٶȾ�γ��,Ĭ��ֵgcj02
		option.setScanSpan(5000);// ���÷���λ����ļ��ʱ��Ϊ5000ms
		option.disableCache(true);// ��ֹ���û��涨λ
		option.setPoiNumber(5); // ��෵��POI����
		option.setPoiDistance(1000); // poi��ѯ����
		option.setPoiExtraInfo(true); // �Ƿ���ҪPOI�ĵ绰�͵�ַ����ϸ��Ϣ
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
}
