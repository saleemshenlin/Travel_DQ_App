package com.travelapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.esri.android.map.GraphicsLayer;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.MultiPath;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.Polyline;
import com.esri.core.map.FeatureSet;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleLineSymbol;

/**
 * 查询类
 * 
 * @author saleemshenlin<br>
 *         包含应用所有有关查询的方法类集合<br>
 * 
 */
public class Query {

	// /**
	// * 用于设置查询的排序条件<br>
	// * 按Event发生时间的升序排序 <br>
	// *
	// * @return String 排序条件
	// */
	// public String getSortOrder(String mString) {
	// String strSQL = null;
	// strSQL = mString + " ASC";
	// return strSQL;
	// }

	/**
	 * // * 用于Event根据查询类型设置查询条件<br>
	 * // * 类型 0 学术讲座 1 电影演出 2 精品课程 3 我关注的<br>
	 * // * 时间设置为一周,从查询当天开始算<br>
	 * // * // * @param intIndex // * 查询类型 // * @return String 查询条件 //
	 */
	// public String getSectionViaType(int intIndex) {
	// String strSQL;
	// switch (intIndex) {
	// case 0:
	// strSQL = "";
	// return strSQL;
	// case 1:
	// strSQL = PoiDB.C_C_ID + " = '01'";
	// return strSQL;
	// case 2:
	// strSQL = PoiDB.C_C_ID + " = '02'";
	// return strSQL;
	// case 3:
	// strSQL = PoiDB.C_C_ID + " = '03'";
	// return strSQL;
	// case 4:
	// strSQL = PoiDB.C_C_ID + " = '04'";
	// return strSQL;
	// default:
	// return null;
	// }
	// }
	//
	/**
	 * 将WKT形式的空间信息转换成Esri的Geometry
	 * 
	 * @param wkt
	 *            传入WKT形式的空间信息
	 * @return 返回该Geometry形式的空间信息
	 */
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

	/**
	 * 将WKT形式的空间信息转换成Esri的Geometry的具体方法
	 * 
	 * @param multipath
	 *            经纬度坐标
	 * @param type
	 *            空间信息的类型点、线还是面
	 * @return 返回该Geometry形式的空间信息
	 */
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

	//
	// public GraphicsLayer getPoisByType(Context context, int type) {
	// GraphicsLayer mGraphicsLayer = new GraphicsLayer();
	// String queryByType = getSectionViaType(type);
	// mItemCursor = mPoisProvider.query(PoisProvider.CONTENT_URI, null,
	// queryByType, null, this.getSortOrder(PoiDB.C_ID));
	// Map<String, Object> mMap = new HashMap<String, Object>();
	//
	// try {
	// mItemCursor.moveToFirst();
	// while (mItemCursor.moveToNext()) {
	// String WKT = mItemCursor.getString(mItemCursor
	// .getColumnIndex(PoiDB.C_SHAPE));
	// String ID = mItemCursor.getString(mItemCursor
	// .getColumnIndex(PoiDB.C_ID));
	// String TYPE = ID.substring(0, 1);
	// String NAME = mItemCursor.getString(mItemCursor
	// .getColumnIndex(PoiDB.C_NAME));
	// mMap.put("NAME", NAME);
	// mMap.put("ID", ID);
	// int imgId = context.getResources().getIdentifier("ic_" + TYPE,
	// "drawable", "com.travelapp");
	// Drawable mDrawable = context.getResources().getDrawable(imgId);
	// PictureMarkerSymbol mPictureMarkerSymbol = new PictureMarkerSymbol(
	// mDrawable);
	// Point mPoint = (Point) Query.wkt2Geometry(WKT);
	// Log.d("Query", mPoint.getX() + ";" + mPoint.getY());
	// Graphic mGraphic = new Graphic(mPoint, mPictureMarkerSymbol,
	// mMap, 0);
	// mGraphicsLayer.addGraphic(mGraphic);
	// }
	// } catch (Exception e) {
	// Log.e("Query", e.toString());
	// } finally {
	// if (mItemCursor != null) {
	// mItemCursor.close();
	// }
	// TravelApplication.getPoiDB().closeDatabase();
	// }
	// return mGraphicsLayer;
	//
	// }
	//
	// public GraphicsLayer getPoisById(Context context, String id) {
	// GraphicsLayer mGraphicsLayer = new GraphicsLayer();
	// final Uri queryUri = Uri.parse(PoiProvider.CONTENT_URI.toString() + "/"
	// + id);
	// mItemCursor = mPoisProvider.query(queryUri, null, null, null, null);
	// Map<String, Object> mMap = new HashMap<String, Object>();
	// try {
	// if (mItemCursor.moveToFirst()) {
	// String WKT = mItemCursor.getString(mItemCursor
	// .getColumnIndex(PoiDB.C_SHAPE));
	// String ID = mItemCursor.getString(mItemCursor
	// .getColumnIndex(PoiDB.C_ID));
	// String TYPE = ID.substring(0, 1);
	// String NAME = mItemCursor.getString(mItemCursor
	// .getColumnIndex(PoiDB.C_NAME));
	// mMap.put("NAME", NAME);
	// mMap.put("ID", ID);
	// int imgId = context.getResources().getIdentifier("ic_" + TYPE,
	// "drawable", "com.travelapp");
	// Drawable mDrawable = context.getResources().getDrawable(imgId);
	// PictureMarkerSymbol mPictureMarkerSymbol = new PictureMarkerSymbol(
	// mDrawable);
	// Point mPoint = (Point) Query.wkt2Geometry(WKT);
	// Log.d("Query", mPoint.getX() + ";" + mPoint.getY());
	// Point mNewPoint = new Point(mPoint.getX(),
	// mPoint.getY() - 0.001);
	// Graphic mGraphic = new Graphic(mNewPoint, mPictureMarkerSymbol,
	// mMap, 0);
	// mGraphicsLayer.addGraphic(mGraphic);
	// }
	// } catch (Exception e) {
	// Log.e("Query", e.toString());
	// } finally {
	// if (mItemCursor != null) {
	// mItemCursor.close();
	// }
	// TravelApplication.getPoiDB().closeDatabase();
	// }
	// return mGraphicsLayer;
	// }
	//
	// public Graphic getPoisByIdInRoute(Context context, String id, int
	// postion) {
	// Graphic mGraphic = null;
	// final Uri queryUri = Uri.parse(PoiProvider.CONTENT_URI.toString() + "/"
	// + id);
	// mItemCursor = mPoisProvider.query(queryUri, null, null, null, null);
	// Map<String, Object> mMap = new HashMap<String, Object>();
	// try {
	// if (mItemCursor.moveToFirst()) {
	// String WKT = mItemCursor.getString(mItemCursor
	// .getColumnIndex(PoiDB.C_SHAPE));
	// String NAME = mItemCursor.getString(mItemCursor
	// .getColumnIndex(PoiDB.C_NAME));
	// mMap.put("NAME", NAME);
	// mMap.put("POSTION", "num_" + postion);
	// int imgId = context.getResources().getIdentifier(
	// "num_" + postion, "drawable", "com.travelapp");
	// Drawable mDrawable = context.getResources().getDrawable(imgId);
	// PictureMarkerSymbol mPictureMarkerSymbol = new PictureMarkerSymbol(
	// mDrawable);
	// Point mPoint = (Point) Query.wkt2Geometry(WKT);
	// Log.d("Query", mPoint.getX() + ";" + mPoint.getY());
	// Point mNewPoint = new Point(mPoint.getX(), mPoint.getY() + 200);
	// mGraphic = new Graphic(mNewPoint, mPictureMarkerSymbol, mMap, 0);
	// }
	// } catch (Exception e) {
	// Log.e("Query", e.toString());
	// } finally {
	// if (mItemCursor != null) {
	// mItemCursor.close();
	// }
	// TravelApplication.getPoiDB().closeDatabase();
	// }
	// return mGraphic;
	// }
	//
	// public Cursor getPoiByType(int type) {
	// try {
	// mItemCursor = mPoiProvider.query(PoiProvider.CONTENT_URI, null,
	// getSectionViaType(type), null, getSortOrder(PoiDB.C_ID));
	// } catch (Exception e) {
	// Log.e("Query", e.toString());
	// } finally {
	// if (mItemCursor.isClosed()) {
	// mItemCursor.close();
	// }
	// TravelApplication.getPoiDB().closeDatabase();
	// }
	// return mItemCursor;
	// }
	//
	// public Cursor getPoiById(String id) {
	// final Uri queryUri = Uri.parse(PoiProvider.CONTENT_URI.toString() + "/"
	// + id);
	// try {
	// mItemCursor = mPoiProvider.query(queryUri, null, null, null, null);
	// } catch (Exception e) {
	// Log.e("Query", e.toString());
	// } finally {
	// if (mItemCursor.isClosed()) {
	// mItemCursor.close();
	// }
	// TravelApplication.getPoiDB().closeDatabase();
	// }
	// return mItemCursor;
	// }
	//
	// public Cursor getRouteById(int id) {
	// final Uri queryUri = Uri.parse(PoiProvider.CONTENT_URI.toString() + "/"
	// + id);
	// try {
	// mItemCursor = mRouteProvider
	// .query(queryUri, null, null, null, null);
	// } catch (Exception e) {
	// Log.e("Query", e.toString());
	// } finally {
	// if (mItemCursor.isClosed()) {
	// mItemCursor.close();
	// }
	// TravelApplication.getPoiDB().closeDatabase();
	// }
	// return mItemCursor;
	// }
	//
	// public Cursor queryAroundByType(String type) {
	// final Uri queryUri = Uri.parse(PoiProvider.CONTENT_URI.toString() + "/"
	// + type);
	// mPoiProvider.query(queryUri, null, null, null, null);
	// mPoisProvider.query(queryUri, null, null, null, null);
	// return mItemCursor;
	// }

	/**
	 * 从Web API获取数据多个poi(poi列表),通过json转换成arraylist
	 * 
	 * @param type
	 *            传入要查询的类型<br>
	 *            0:所有;1:景点;2:住宿;3:餐饮;4:购物;
	 * @return ArrayList的poi列表
	 */
	public ArrayList<POI> getPoisFromWebAPI(int type) {
		ArrayList<POI> mQueryList = new ArrayList<POI>();
		try {
			String result = getJsonFromWebAPI(TravelApplication.getContext()
					.getString(R.string.request_url) + getPoiType(type));
			JsonFactory mJsonFactory = new JsonFactory();
			JsonParser mjJsonParser = mJsonFactory.createJsonParser(result);
			mjJsonParser.nextToken();
			mjJsonParser.nextToken();
			mQueryList = json2ArrayList(mjJsonParser);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.e("getPoiFromWebAPI", mQueryList.toString());

		return mQueryList;
	}

	/**
	 * 从Web API获取数据单个poi(poi详细),返回POI类
	 * 
	 * @param id
	 *            poi的id
	 * @return POI类的poi信息
	 */
	public POI getPoiFromAPI(String id) {
		POI mPoi = new POI();
		try {
			String result = getJsonFromWebAPI(TravelApplication.getContext()
					.getString(R.string.request_url) + "/" + id);
			JsonFactory mJsonFactory = new JsonFactory();
			JsonParser jsonParser = mJsonFactory.createJsonParser(result);
			jsonParser.nextToken();
			jsonParser.nextToken();
			while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
				String nameString = jsonParser.getCurrentName();
				if ("Id".equals(nameString)) {
					while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
						String fieldname = jsonParser.getCurrentName();
						if ("Id".equals(fieldname)) {
							Log.e("Id", jsonParser.getText());
							mPoi.Id = Integer.parseInt(jsonParser.getText());
						} else if ("Name".equals(fieldname)) {
							Log.e("Name", jsonParser.getText());
							mPoi.Name = jsonParser.getText();
						} else if ("C_ID".equals(fieldname)) {
							Log.e("C_ID", jsonParser.getText());
							if (!jsonParser.getText().equals(fieldname)) {
								mPoi.C_ID = Integer.parseInt(jsonParser
										.getText());
							}
						} else if ("D_Name".equals(fieldname)) {
							Log.e("D_Name", jsonParser.getText());
							mPoi.D_Name = jsonParser.getText();
						} else if ("Time".equals(fieldname)) {
							Log.e("Time", jsonParser.getText());
							mPoi.Time = jsonParser.getText();
						} else if ("Tele".equals(fieldname)) {
							Log.e("Tele", jsonParser.getText());
							mPoi.Tele = jsonParser.getText();
						} else if ("Abstract".equals(fieldname)) {
							Log.e("Abstract", jsonParser.getText());
							mPoi.Abstract = jsonParser.getText();
						} else if ("Ticket".equals(fieldname)) {
							Log.e("Ticket", jsonParser.getText());
							mPoi.Ticket = jsonParser.getText();
						} else if ("Type".equals(fieldname)) {
							Log.e("Type", jsonParser.getText());
							mPoi.Type = jsonParser.getText();
						} else if ("Address".equals(fieldname)) {
							Log.e("Address", jsonParser.getText());
							mPoi.Address = jsonParser.getText();
						} else if ("Geometry".equals(fieldname)) {
							Log.e("Geometry", jsonParser.getText());
							mPoi.Geometry = jsonParser.getText();
						} else if ("ImgUrl".equals(fieldname)) {
							Log.e("ImgUrl", jsonParser.getText());
							mPoi.ImgUrl = jsonParser.getText();
						}
					}
					Log.e("finish", "finish");
					break;
				}
			}
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mPoi;
	}

	/**
	 * 根据poi查询类别，生成相应的查询url
	 * 
	 * @param type
	 *            传入要查询的类型<br>
	 *            0:所有;1:景点;2:住宿;3:餐饮;4:购物;
	 * @return web api查询的部分url
	 */
	private String getPoiType(int type) {
		String strSQL = "";
		if (type > 0 && type <= 4) {
			strSQL = "?type=" + type;
		}
		return strSQL;
	}

	/**
	 * 将json格式的查询结果转换成ArrayList形式
	 * 
	 * @param jsonParser
	 *            json格式的查询结果
	 * @return ArrayList的poi列表
	 */
	private ArrayList<POI> json2ArrayList(JsonParser jsonParser) {
		ArrayList<POI> queryList = new ArrayList<POI>();
		try {
			while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
				String nameString = jsonParser.getCurrentName();
				POI mPoi = new POI();
				if ("Id".equals(nameString)) {
					while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
						String fieldname = jsonParser.getCurrentName();
						if ("Id".equals(fieldname)) {
							Log.e("Id", jsonParser.getText());
							mPoi.Id = Integer.parseInt(jsonParser.getText());
						} else if ("Name".equals(fieldname)) {
							Log.e("Name", jsonParser.getText());
							mPoi.Name = jsonParser.getText();
						} else if ("C_ID".equals(fieldname)) {
							Log.e("C_ID", jsonParser.getText());
							if (!jsonParser.getText().equals(fieldname)) {
								mPoi.C_ID = Integer.parseInt(jsonParser
										.getText());
							}
						} else if ("D_Name".equals(fieldname)) {
							Log.e("D_Name", jsonParser.getText());
							mPoi.D_Name = jsonParser.getText();
						} else if ("Time".equals(fieldname)) {
							Log.e("Time", jsonParser.getText());
							mPoi.Time = jsonParser.getText();
						} else if ("Tele".equals(fieldname)) {
							Log.e("Tele", jsonParser.getText());
							mPoi.Tele = jsonParser.getText();
						} else if ("Abstract".equals(fieldname)) {
							Log.e("Abstract", jsonParser.getText());
							mPoi.Abstract = jsonParser.getText();
						} else if ("Ticket".equals(fieldname)) {
							Log.e("Ticket", jsonParser.getText());
							mPoi.Ticket = jsonParser.getText();
						} else if ("Type".equals(fieldname)) {
							Log.e("Type", jsonParser.getText());
							mPoi.Type = jsonParser.getText();
						} else if ("Geometry".equals(fieldname)) {
							Log.e("Geometry", jsonParser.getText());
							mPoi.Geometry = jsonParser.getText();
						} else if ("ImgUrl".equals(fieldname)) {
							Log.e("ImgUrl", jsonParser.getText());
							if (!jsonParser.getText().equals(fieldname)) {
								mPoi.ImgUrl = returnBitMap(jsonParser.getText());
							}
						}
					}
					Log.e("finish", "finish");
					queryList.add(mPoi);
				}
			}
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.e("json2ArrayList", queryList.toString());
		return queryList;
	}

	/**
	 * 通过"~/api/poi"形式的web api获取json格式数据
	 * 
	 * @param url
	 *            传入查询url
	 * @return json格式数据
	 */
	private String getJsonFromWebAPI(String url) {
		String result = "";
		try {
			String requestURL = url;
			HttpGet httpRequest = new HttpGet(requestURL);
			HttpClient httpclient = new DefaultHttpClient();
			httpclient.getParams().setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, 0);
			httpclient.getParams().setParameter(
					CoreConnectionPNames.SO_TIMEOUT, 0);
			HttpResponse httpResponse = httpclient.execute(httpRequest);
			int status = httpResponse.getStatusLine().getStatusCode();
			if (status == HttpStatus.SC_OK) {
				result = EntityUtils
						.toString(httpResponse.getEntity(), "utf-8");
			}

		} catch (Exception e) {
			Log.e("WebAPI", e.toString());
		}

		return result;
	}

	/**
	 * 根据poi信息中的图片查询连接，获取图片，并缓存到的本地。<br>
	 * 文件名进行md5加密
	 * 
	 * @param url
	 *            图片查询连接
	 * @return 图片在本地的文件名,方便调用
	 */
	public static String returnBitMap(String url) {
		String name = MD5.getMD5(url) + ".jpg";
		File file = new File(TravelApplication.getContext()
				.getExternalFilesDir("cache"), name);
		if (!file.exists()) {
			URL myFileUrl = null;
			try {
				myFileUrl = new URL(TravelApplication.getContext().getString(
						R.string.request_host)
						+ url + ".jpg");
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			try {
				HttpURLConnection conn = (HttpURLConnection) myFileUrl
						.openConnection();
				conn.setDoInput(true);
				conn.connect();
				InputStream is = conn.getInputStream();
				FileOutputStream fos = new FileOutputStream(file);
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = is.read(buffer)) != -1) {
					fos.write(buffer, 0, len);
				}
				is.close();
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return name;
	}

	/**
	 * md5加密
	 * 
	 * @author saleemshenlin
	 * 
	 */
	public static class MD5 {

		public static String getMD5(String content) {
			try {
				MessageDigest digest = MessageDigest.getInstance("MD5");
				digest.update(content.getBytes());
				return getHashString(digest);

			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			return null;
		}

		private static String getHashString(MessageDigest digest) {
			StringBuilder builder = new StringBuilder();
			for (byte b : digest.digest()) {
				builder.append(Integer.toHexString((b >> 4) & 0xf));
				builder.append(Integer.toHexString(b & 0xf));
			}
			return builder.toString();
		}
	}

	/**
	 * 加载本地缓存中的图片<br>
	 * 解决Android加载图片时内存溢出的问题
	 * 
	 * @param context
	 *            上下文
	 * @param path
	 *            图片路径
	 * @param size
	 *            对于图片进行缩放的大小
	 * @return
	 */
	public static Bitmap readBitmapFromFile(Context context, String path,
			int size) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inSampleSize = size;
		opt.inInputShareable = true;
		// 获取资源图片
		FileInputStream is = null;
		try {
			is = new FileInputStream(new File(path));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return BitmapFactory.decodeStream(is, null, opt);
	}

	/**
	 * 获取多个poi的位置
	 * 
	 * @param context
	 *            上下文
	 * @param type
	 *            poi的类型
	 * @return 加载多个poi的GraphicsLayer层
	 */
	public GraphicsLayer getPoisLocation(Context context, int type) {
		GraphicsLayer mGraphicsLayer = new GraphicsLayer();
		ArrayList<POI> pois = getPoisFromWebAPI(type);
		Map<String, Object> mMap = new HashMap<String, Object>();
		try {
			for (POI poi : pois) {
				String WKT = poi.Geometry;
				String ID = String.valueOf(poi.Id);
				String TYPE = String.valueOf(poi.C_ID);
				String NAME = poi.Name;
				mMap.put("NAME", NAME);
				mMap.put("ID", ID);
				mMap.put("TYPE", TYPE);
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
		}
		return mGraphicsLayer;

	}

	/**
	 * 获取多单个poi的位置
	 * 
	 * @param context
	 *            上下文
	 * @param id
	 *            poi的id
	 * @return 加载单个poi的GraphicsLayer层
	 */
	public GraphicsLayer getPoiLocation(Context context, String id) {
		GraphicsLayer mGraphicsLayer = new GraphicsLayer();
		POI poi = getPoiFromAPI(id);
		Map<String, Object> mMap = new HashMap<String, Object>();
		try {
			if (poi != null) {
				String WKT = poi.Geometry;
				String ID = String.valueOf(poi.Id);
				String TYPE = String.valueOf(poi.C_ID);
				String NAME = poi.Name;
				mMap.put("NAME", NAME);
				mMap.put("ID", ID);
				mMap.put("TYPE", TYPE);
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
		}
		return mGraphicsLayer;

	}

	/**
	 * 根据获取路径的url获取json格式结果，然后转换成GraphicsLayer
	 * 
	 * @param id
	 *            路径id
	 * @return 加载路径的GraphicsLayer层
	 */
	public GraphicsLayer getRouteByIdFromJson(String id) {
		GraphicsLayer mGraphicsLayer = new GraphicsLayer();
		String url = TravelApplication.getContext().getString(
				R.string.request_host)
				+ TravelApplication.getContext().getString(R.string.route_url);
		String result = getJsonFromWebAPI(url);
		JsonFactory mJsonFactory = new JsonFactory();
		try {
			JsonParser mjJsonParser = mJsonFactory.createJsonParser(result);
			mjJsonParser.nextToken();
			FeatureSet mFeatureSet = FeatureSet.fromJson(mjJsonParser);
			Graphic[] mGraphics = mFeatureSet.getGraphics();
			for (Graphic mGraphic : mGraphics) {
				if (mGraphic.getAttributeValue("NewID").equals(id)) {
					SimpleLineSymbol sls = new SimpleLineSymbol(Color.rgb(0,
							153, 204), 4);
					Graphic newGraphic = new Graphic(mGraphic.getGeometry(),
							sls, mGraphic.getAttributes(), 0);
					mGraphicsLayer.addGraphic(newGraphic);
					break;
				}
			}
		} catch (Exception e) {
			Log.e("Query", e.toString());
		}
		return mGraphicsLayer;
	}
}
