package com.travelapp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.util.Log;

import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.MultiPath;
import com.esri.core.geometry.Point;
import com.esri.core.map.FeatureSet;
import com.esri.core.map.Graphic;

public class FileIO {
	/**
	 * 从Data.xml获取数据存入sqlite3<br>
	 * 具体方法如下:<br>
	 * 1)判断时候需要导入数据 <br>
	 * 2)使用pull方式解析XML<br>
	 * 3)遍历每一条数据,然后调用EventData.insertOrIgnore()存入数据库
	 */
	public void getDateFromXML() {
		Context mContext = TravelApplication.getContext();
		Resources mResources = mContext.getResources();

		XmlResourceParser mXmlResourceParser = mResources.getXml(R.xml.data);
		int intEventType;
		StringBuffer mStringBuffer = new StringBuffer();
		ContentValues mContentValues = new ContentValues();
		String strRowName = "";
		try {
			intEventType = mXmlResourceParser.getEventType();
			while (intEventType != XmlResourceParser.END_DOCUMENT) {
				if (intEventType == XmlResourceParser.START_TAG) {
					String tagName = mXmlResourceParser.getName().toString()
							.trim();
					if (!tagName.equals("Alldata")) {
						mStringBuffer.append(mXmlResourceParser.getName());
						if (tagName.equals("poi")) {
							mStringBuffer.append("(");
						} else {
							mStringBuffer.append(":");
							strRowName = tagName;
						}
					}
				} else if (intEventType == XmlResourceParser.END_TAG) {
					String tagName = mXmlResourceParser.getName().toString()
							.trim();
					if (tagName.equals("poi")) {
						mStringBuffer.append(")");
						Log.d("FileIO", mStringBuffer.toString());
						TravelApplication.getPoiDB().insertOrIgnore(
								mContentValues, PoiDB.TABLE);
						mStringBuffer.delete(0, mStringBuffer.length() - 1);
					} else if (tagName.equals("Alldata")) {
						Log.d("FileIO", "end");
					} else {
						mStringBuffer.append(", ");
					}
				} else if (intEventType == XmlResourceParser.TEXT) {
					String tagText = mXmlResourceParser.getText().toString()
							.trim();
					mStringBuffer.append(mXmlResourceParser.getText()
							.toString().trim());
					if (strRowName.equals("id")) {
						mContentValues.put(PoiDB.C_ID, tagText);
					} else if (strRowName.equals("name")) {
						mContentValues.put(PoiDB.C_NAME, tagText);
					} else if (strRowName.equals("d_name")) {
						mContentValues.put(PoiDB.C_D_NAME, tagText);
					} else if (strRowName.equals("address")) {
						mContentValues.put(PoiDB.C_ADDRESS, tagText);
					} else if (strRowName.equals("time")) {
						mContentValues.put(PoiDB.C_TIME, tagText);
					} else if (strRowName.equals("ticket")) {
						mContentValues.put(PoiDB.C_PRICE, tagText);
					} else if (strRowName.equals("type")) {
						mContentValues.put(PoiDB.C_TYPE, tagText);
					} else if (strRowName.equals("tele")) {
						mContentValues.put(PoiDB.C_TELE, tagText);
					} else if (strRowName.equals("abstract")) {
						mContentValues.put(PoiDB.C_ABSTRACT, tagText);
					} else if (strRowName.equals("c_id")) {
						mContentValues.put(PoiDB.C_C_ID, tagText);
					}
				}
				intEventType = mXmlResourceParser.next();
			}
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void copyJSON(Context context) {
		createPath(context, "data");
		AssetManager mAssetManager = context.getAssets();
		String[] files = null;
		try {
			files = mAssetManager.list("");
		} catch (IOException e) {
			Log.e("FileIO", e.toString());
		}
		for (String filename : files) {

			InputStream in = null;
			OutputStream out = null;
			try {
				if (filename.equals("poi.json")
						|| filename.equals("route.json")) {
					in = mAssetManager.open(filename);
					File outFile = null;
					outFile = new File(context.getExternalFilesDir("data"),
							filename);
					if (!outFile.exists()) {
						out = new FileOutputStream(outFile);
						copyFile(in, out);
						in.close();
						in = null;
						out.flush();
						out.close();
						out = null;
					}
				}
			} catch (IOException e) {
				Log.e("FileIO", e.toString());
			}
		}

	}

	public void jSON2WKT(Context context, String filename) {
		File poiJson = new File(context.getExternalFilesDir("data"), filename);
		JsonFactory mJsonFactory = new JsonFactory();
		try {
			System.out.println("Done!");
			JsonParser mjJsonParser = mJsonFactory.createJsonParser(poiJson);
			mjJsonParser.nextToken();
			FeatureSet mFeatureSet = FeatureSet.fromJson(mjJsonParser);
			Graphic[] mGraphics = mFeatureSet.getGraphics();
			for (Graphic mGraphic : mGraphics) {
				ContentValues values = new ContentValues();
				String mID = (String) mGraphic.getAttributeValue("NewID");
				values.put(PoiDB.C_ID, mID);
				String mName = (String) mGraphic.getAttributeValue("Name");
				values.put(PoiDB.C_NAME, mName);
				Geometry mGeometry = mGraphic.getGeometry();
				String mShape = GeometryToWKT(mGeometry);
				values.put(PoiDB.C_SHAPE, mShape);
				if (filename.equals("poi.json")) {
					String mC_ID = (String) mGraphic.getAttributeValue("C_ID");
					values.put(PoiDB.C_C_ID, mC_ID);
					TravelApplication.getPoiDB().insertOrIgnore(values,
							PoiDB.TABLE_POIS);
				} else {
					String mABSTRACT = (String) mGraphic
							.getAttributeValue("Detail");
					values.put(PoiDB.C_ABSTRACT, mABSTRACT);
					TravelApplication.getPoiDB().insertOrIgnore(values,
							PoiDB.TABLE_ROUTE);
				}

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 用于文件的复制
	 * 
	 * @param in
	 *            复制源
	 * @param out
	 *            复制目的地
	 * @throws IOException
	 */
	private void copyFile(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int intRead;
		while ((intRead = in.read(buffer)) != -1) {
			out.write(buffer, 0, intRead);
		}
	}

	/**
	 * 用于根据路径是否存在,创建存储路径
	 * 
	 * @param context
	 *            上下文
	 * @param path
	 *            创建路径
	 */
	private static void createPath(Context context, String path) {
		try {
			File mFile = new File(context.getExternalFilesDir(null), path);
			if (!mFile.exists()) {
				mFile.mkdir();
			}
		} catch (Exception e) {
			Log.e("FileIO", e.toString());
		}
	}

	public static String GeometryToWKT(Geometry geometry) {
		if (geometry == null) {
			return null;
		}
		String geoStr = "";
		Geometry.Type type = geometry.getType();
		if ("POINT".equals(type.name())) {
			Point pt = (Point) geometry;
			geoStr = type.name() + "(" + pt.getX() + " " + pt.getY() + ")";
		} else if ("Polygon".equals(type.name())
				|| "POLYLINE".equals(type.name())) {
			MultiPath pg = (MultiPath) geometry;
			geoStr = type.name() + "(" + "";
			int pathSize = pg.getPathCount();
			for (int j = 0; j < pathSize; j++) {
				String temp = "(";
				int size = pg.getPathSize(j);
				for (int i = 0; i < size; i++) {
					Point pt = pg.getPoint(i);
					temp += pt.getX() + " " + pt.getY() + ",";
				}
				temp = temp.substring(0, temp.length() - 1) + ")";
				geoStr += temp + ",";
			}
			geoStr = geoStr.substring(0, geoStr.length() - 1) + ")";
		} else if ("Envelope".equals(type.name())) {
			Envelope env = (Envelope) geometry;
			geoStr = type.name() + "(" + env.getXMin() + "," + env.getYMin()
					+ "," + env.getXMax() + "," + env.getYMax() + ")";
		} else if ("MultiPoint".equals(type.name())) {
		} else {
			geoStr = null;
		}
		return geoStr;
	}
}
