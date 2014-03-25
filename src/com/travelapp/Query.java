package com.travelapp;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;

import com.esri.android.map.GraphicsLayer;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.MultiPath;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.Polyline;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;

public class Query {
	/**
	 * 用于实例化类EventProvider
	 */
	private PoisProvider mPoisProvider = new PoisProvider();
	private PoiProvider mPoiProvider = new PoiProvider();
	private RouteProvider mRouteProvider = new RouteProvider();
	Cursor mItemCursor = null;

	/**
	 * 用于设置查询的排序条件<br>
	 * 按Event发生时间的升序排序 <br>
	 * 
	 * @return String 排序条件
	 */
	public String getSortOrder(String mString) {
		String strSQL = null;
		strSQL = mString + " ASC";
		return strSQL;
	}

	/**
	 * 用于Event根据查询类型设置查询条件<br>
	 * 类型 0 学术讲座 1 电影演出 2 精品课程 3 我关注的<br>
	 * 时间设置为一周,从查询当天开始算<br>
	 * 
	 * @param intIndex
	 *            查询类型
	 * @return String 查询条件
	 */
	public String getSectionViaType(int intIndex) {
		String strSQL;
		switch (intIndex) {
		case 0:
			strSQL = "";
			return strSQL;
		case 1:
			strSQL = PoiDB.C_C_ID + " = '01'";
			return strSQL;
		case 2:
			strSQL = PoiDB.C_C_ID + " = '02'";
			return strSQL;
		case 3:
			strSQL = PoiDB.C_C_ID + " = '03'";
			return strSQL;
		case 4:
			strSQL = PoiDB.C_C_ID + " = '04'";
			return strSQL;
		default:
			return null;
		}
	}

	public static Geometry wkt2Geometry(String wkt) {
		Geometry geo = null;
		if (wkt == null || wkt == "") {
			return null;
		}
		String headStr = wkt.substring(0, wkt.indexOf("("));
		String temp = wkt.substring(wkt.indexOf("(") + 1, wkt.lastIndexOf(")"));
		if (headStr.equals("POINT")) {
			String[] values = temp.split(" ");
			geo = new Point(Double.valueOf(values[0]),
					Double.valueOf(values[1]));
		} else if (headStr.equals("POLYLINE") || headStr.equals("Polygon")) {
			geo = parseWKT(temp, headStr);
		} else if (headStr.equals("Envelope")) {
			String[] extents = temp.split(",");
			geo = new Envelope(Double.valueOf(extents[0]),
					Double.valueOf(extents[1]), Double.valueOf(extents[2]),
					Double.valueOf(extents[3]));
		} else if (headStr.equals("MultiPoint")) {
		} else {
			return null;
		}
		return geo;
	}

	private static Geometry parseWKT(String multipath, String type) {
		String subMultipath = multipath.substring(1, multipath.length() - 1);
		String[] paths;
		if (subMultipath.indexOf("),(") >= 0) {
			paths = subMultipath.split("),(");// 多个几何对象的字符串
		} else {
			paths = new String[] { subMultipath };
		}
		Point startPoint = null;
		MultiPath path = null;
		if (type.equals("POLYLINE")) {
			path = new Polyline();
		} else {
			path = new Polygon();
		}
		for (int i = 0; i < paths.length; i++) {
			String[] points = paths[i].split(",");
			startPoint = null;
			for (int j = 0; j < points.length; j++) {
				String[] pointStr = points[j].split(" ");
				if (startPoint == null) {
					startPoint = new Point(Double.valueOf(pointStr[0]),
							Double.valueOf(pointStr[1]));
					path.startPath(startPoint);
				} else {
					path.lineTo(new Point(Double.valueOf(pointStr[0]), Double
							.valueOf(pointStr[1])));
				}
			}
		}
		return path;
	}

	public GraphicsLayer getPoisByType(Context context, int type) {
		GraphicsLayer mGraphicsLayer = new GraphicsLayer();
		String queryByType = getSectionViaType(type);
		mItemCursor = mPoisProvider.query(PoisProvider.CONTENT_URI, null,
				queryByType, null, this.getSortOrder(PoiDB.C_ID));
		Map<String, Object> mMap = new HashMap<String, Object>();

		try {
			mItemCursor.moveToFirst();
			while (mItemCursor.moveToNext()) {
				String WKT = mItemCursor.getString(mItemCursor
						.getColumnIndex(PoiDB.C_SHAPE));
				String ID = mItemCursor.getString(mItemCursor
						.getColumnIndex(PoiDB.C_ID));
				String TYPE = ID.substring(0, 1);
				String NAME = mItemCursor.getString(mItemCursor
						.getColumnIndex(PoiDB.C_NAME));
				mMap.put("NAME", NAME);
				mMap.put("ID", ID);
				int imgId = context.getResources().getIdentifier("ic_" + TYPE,
						"drawable", "com.travelapp");
				Drawable mDrawable = context.getResources().getDrawable(imgId);
				PictureMarkerSymbol mPictureMarkerSymbol = new PictureMarkerSymbol(
						mDrawable);
				Point mPoint = (Point) Query.wkt2Geometry(WKT);
				Log.d("Query", mPoint.getX() + ";" + mPoint.getY());
				Graphic mGraphic = new Graphic(mPoint, mPictureMarkerSymbol,
						mMap, 0);
				mGraphicsLayer.addGraphic(mGraphic);
			}
		} catch (Exception e) {
			Log.e("Query", e.toString());
		} finally {
			if (mItemCursor != null) {
				mItemCursor.close();
			}
			TravelApplication.getPoiDB().closeDatabase();
		}
		return mGraphicsLayer;

	}

	public GraphicsLayer getPoisById(Context context, String id) {
		GraphicsLayer mGraphicsLayer = new GraphicsLayer();
		final Uri queryUri = Uri.parse(PoiProvider.CONTENT_URI.toString() + "/"
				+ id);
		mItemCursor = mPoisProvider.query(queryUri, null, null, null, null);
		Map<String, Object> mMap = new HashMap<String, Object>();
		try {
			if (mItemCursor.moveToFirst()) {
				String WKT = mItemCursor.getString(mItemCursor
						.getColumnIndex(PoiDB.C_SHAPE));
				String ID = mItemCursor.getString(mItemCursor
						.getColumnIndex(PoiDB.C_ID));
				String TYPE = ID.substring(0, 1);
				String NAME = mItemCursor.getString(mItemCursor
						.getColumnIndex(PoiDB.C_NAME));
				mMap.put("NAME", NAME);
				mMap.put("ID", ID);
				int imgId = context.getResources().getIdentifier("ic_" + TYPE,
						"drawable", "com.travelapp");
				Drawable mDrawable = context.getResources().getDrawable(imgId);
				PictureMarkerSymbol mPictureMarkerSymbol = new PictureMarkerSymbol(
						mDrawable);
				Point mPoint = (Point) Query.wkt2Geometry(WKT);
				Log.d("Query", mPoint.getX() + ";" + mPoint.getY());
				Point mNewPoint = new Point(mPoint.getX(),
						mPoint.getY() - 0.001);
				Graphic mGraphic = new Graphic(mPoint, mPictureMarkerSymbol,
						mMap, 0);
				mGraphicsLayer.addGraphic(mGraphic);
			}
		} catch (Exception e) {
			Log.e("Query", e.toString());
		} finally {
			if (mItemCursor != null) {
				mItemCursor.close();
			}
			TravelApplication.getPoiDB().closeDatabase();
		}
		return mGraphicsLayer;
	}

	public Graphic getPoisByIdInRoute(Context context, String id, int postion) {
		Graphic mGraphic = null;
		final Uri queryUri = Uri.parse(PoiProvider.CONTENT_URI.toString() + "/"
				+ id);
		mItemCursor = mPoisProvider.query(queryUri, null, null, null, null);
		Map<String, Object> mMap = new HashMap<String, Object>();
		try {
			if (mItemCursor.moveToFirst()) {
				String WKT = mItemCursor.getString(mItemCursor
						.getColumnIndex(PoiDB.C_SHAPE));
				String NAME = mItemCursor.getString(mItemCursor
						.getColumnIndex(PoiDB.C_NAME));
				mMap.put("NAME", NAME);
				mMap.put("POSTION", "num_" + postion);
				int imgId = context.getResources().getIdentifier(
						"num_" + postion, "drawable", "com.travelapp");
				Drawable mDrawable = context.getResources().getDrawable(imgId);
				PictureMarkerSymbol mPictureMarkerSymbol = new PictureMarkerSymbol(
						mDrawable);
				Point mPoint = (Point) Query.wkt2Geometry(WKT);
				Log.d("Query", mPoint.getX() + ";" + mPoint.getY());
				Point mNewPoint = new Point(mPoint.getX(), mPoint.getY() + 200);
				mGraphic = new Graphic(mPoint, mPictureMarkerSymbol, mMap, 0);
			}
		} catch (Exception e) {
			Log.e("Query", e.toString());
		} finally {
			if (mItemCursor != null) {
				mItemCursor.close();
			}
			TravelApplication.getPoiDB().closeDatabase();
		}
		return mGraphic;
	}

	public Cursor getPoiByType(int type) {
		try {
			mItemCursor = mPoiProvider.query(PoiProvider.CONTENT_URI, null,
					getSectionViaType(type), null, getSortOrder(PoiDB.C_ID));
		} catch (Exception e) {
			Log.e("Query", e.toString());
		} finally {
			if (mItemCursor.isClosed()) {
				mItemCursor.close();
			}
			TravelApplication.getPoiDB().closeDatabase();
		}
		return mItemCursor;
	}

	public Cursor getPoiById(String id) {
		final Uri queryUri = Uri.parse(PoiProvider.CONTENT_URI.toString() + "/"
				+ id);
		try {
			mItemCursor = mPoiProvider.query(queryUri, null, null, null, null);
		} catch (Exception e) {
			Log.e("Query", e.toString());
		} finally {
			if (mItemCursor.isClosed()) {
				mItemCursor.close();
			}
			TravelApplication.getPoiDB().closeDatabase();
		}
		return mItemCursor;
	}

	public Cursor getRouteById(int id) {
		final Uri queryUri = Uri.parse(PoiProvider.CONTENT_URI.toString() + "/"
				+ id);
		try {
			mItemCursor = mRouteProvider
					.query(queryUri, null, null, null, null);
		} catch (Exception e) {
			Log.e("Query", e.toString());
		} finally {
			if (mItemCursor.isClosed()) {
				mItemCursor.close();
			}
			TravelApplication.getPoiDB().closeDatabase();
		}
		return mItemCursor;
	}

	public Cursor queryAroundByType(String type) {
		final Uri queryUri = Uri.parse(PoiProvider.CONTENT_URI.toString() + "/"
				+ type);
		mPoiProvider.query(queryUri, null, null, null, null);
		mPoisProvider.query(queryUri, null, null, null, null);
		return mItemCursor;
	}
}
