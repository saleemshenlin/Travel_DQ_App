package com.travelapp;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.esri.android.map.Callout;
import com.esri.android.map.CalloutStyle;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISLocalTiledLayer;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polyline;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleLineSymbol;

public class RouteDetailActivity extends Activity {
	ImageView mBackImageView;
	TextView mTitleTextView;
	TextView mRouteTitle;
	Intent mIntent;
	Bundle mBundle;
	Cursor mItemCursor;
	ListView mListView;
	Resources mResources;
	MapView mMap;
	Geometry mGeometry;
	GraphicsLayer mPoiGraphicsLayer;
	GraphicsLayer mRouteGraphicsLayer;
	ArcGISLocalTiledLayer mLocalTiledLayer;
	ArcGISTiledMapServiceLayer mTiledMapServiceLayer;
	List<Route> mList;
	Query mQuery;
	String mPOIs;
	int mRouteID;
	static String TAG = "RouteDetailActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_routedetail);
		mIntent = getIntent();
		mBundle = mIntent.getExtras();
		mRouteID = mBundle.getInt("ID");
		mResources = this.getResources();
		initView();
		mList = new ArrayList<Route>();
		mPOIs = getRoute(mRouteID);
		getRoutePoi(mPOIs);
		initData();
		new AddMap().execute();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			TravelApplication.buildExitDialog(RouteDetailActivity.this);
		}
		return false;
	}

	private void initView() {
		// TODO Auto-generated method stub
		mBackImageView = (ImageView) findViewById(R.id.imgListBack);
		mTitleTextView = (TextView) findViewById(R.id.txtListTitle);
		mRouteTitle = (TextView) findViewById(R.id.txtRouteTitle);
		mListView = (ListView) findViewById(R.id.listRoute);
		mMap = (MapView) findViewById(R.id.map);
		mPoiGraphicsLayer = new GraphicsLayer();
		mRouteGraphicsLayer = new GraphicsLayer();
		mQuery = new Query();
		mTitleTextView.setText("·������");
		mBackImageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(RouteDetailActivity.this,
						RouteListActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
						| Intent.FLAG_ACTIVITY_NEW_TASK);
				RouteDetailActivity.this.startActivity(intent);
				RouteDetailActivity.this.finish();
				RouteDetailActivity.this.overridePendingTransition(
						R.anim.anim_in_left2right, R.anim.anim_out_left2right);
			}
		});
	}

	private String getRoute(int routeid) {
		String mRoutePOi = null;
		mItemCursor = mQuery.getRouteById(routeid);
		try {
			if (mItemCursor.moveToFirst()) {
				mRouteTitle.setText(mItemCursor.getString(mItemCursor
						.getColumnIndex(PoiDB.C_NAME)));
				mRoutePOi = mItemCursor.getString(mItemCursor
						.getColumnIndex(PoiDB.C_ABSTRACT));
				String WKT = mItemCursor.getString(mItemCursor
						.getColumnIndex(PoiDB.C_SHAPE));
				SimpleLineSymbol sls = new SimpleLineSymbol(Color.rgb(0, 153,
						204), 4);
				Polyline mPolyline = (Polyline) Query.wkt2Geometry(WKT);
				mGeometry = mPolyline;
				Graphic mGraphic = new Graphic(mPolyline, sls);
				mRouteGraphicsLayer.addGraphic(mGraphic);
			}
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		} finally {
			if (mItemCursor.isClosed()) {
				mItemCursor.close();
			}
			TravelApplication.getPoiDB().closeDatabase();
		}
		return mRoutePOi;
	}

	private void getRoutePoi(String pois) {
		String[] mPois = pois.split(";");
		for (int i = 1; i <= mPois.length; i++) {
			Log.e("lengh", String.valueOf(mPois.length));
			final Uri queryUri = Uri.parse(RouteProvider.CONTENT_URI.toString()
					+ "/" + mPois[i - 1].substring(1));
			PoiProvider mPoiProvider = new PoiProvider();
			mItemCursor = mPoiProvider.query(queryUri, null, null, null, null);
			try {
				if (mItemCursor.moveToFirst()) {
					String mImg = "num_" + i;
					String mName = mItemCursor.getString(mItemCursor
							.getColumnIndex(PoiDB.C_NAME));
					String mAbstract = mItemCursor.getString(mItemCursor
							.getColumnIndex(PoiDB.C_ABSTRACT));
					Route mRoute = new Route();
					mRoute.IMG = mImg;
					mRoute.NAME = mName;
					mRoute.ABSTRACT = mAbstract;
					mPoiGraphicsLayer.addGraphic(mQuery.getPoisByIdInRoute(
							this, mPois[i - 1].substring(1), i));
					mList.add(mRoute);
				}
			} catch (Exception e) {
				Log.e(TAG, e.toString());
			} finally {
				if (mItemCursor.isClosed()) {
					mItemCursor.close();
				}
				TravelApplication.getPoiDB().closeDatabase();
			}
		}
	}

	private void initData() {
		RouteListAdapter mAdapter = new RouteListAdapter(this,
				R.layout.route_row, mList);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(RouteDetailActivity.this,
						ScenicDetailActivity.class);
				intent.putExtra(
						"ID",
						Long.parseLong(mPOIs.split(";")[Integer.parseInt(id
								+ "")]));
				intent.putExtra("FROM", "RouteDetailActivity");
				intent.putExtra("ROUTEID", mRouteID);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
						| Intent.FLAG_ACTIVITY_NEW_TASK);
				RouteDetailActivity.this.startActivity(intent);
				RouteDetailActivity.this.finish();
				RouteDetailActivity.this.overridePendingTransition(
						R.anim.anim_in_right2left, R.anim.anim_out_right2left);
			}
		});
	}

	public class Route {
		String NAME;
		String IMG;
		String ABSTRACT;
	}

	class RouteListAdapter extends ArrayAdapter<Route> {
		private int resourceId;

		public RouteListAdapter(Context context, int textViewResourceId,
				List<Route> objects) {
			super(context, textViewResourceId, objects);
			this.resourceId = textViewResourceId;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Route mRoute = getItem(position);
			LinearLayout routeListLayout = new LinearLayout(getContext());
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
					inflater);
			vi.inflate(resourceId, routeListLayout, true);
			TextView mRouteTitle = (TextView) routeListLayout
					.findViewById(R.id.txtRouteRowTitle);
			// TextView mRouteAbastract = (TextView) routeListItem
			// .findViewById(R.id.txtRouteRow);
			ImageView mRouteImage = (ImageView) routeListLayout
					.findViewById(R.id.imgRouteRow);
			mRouteTitle.setText(mRoute.NAME);
			// mRouteAbastract.setText(mRoute.ABSTRACT);
			int imgId = mResources.getIdentifier(mRoute.IMG, "drawable",
					"com.travelapp");
			Drawable mDrawable = mResources.getDrawable(imgId);
			mRouteImage.setImageDrawable(mDrawable);
			return routeListLayout;
		}
	}

	class AddMap extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			mLocalTiledLayer = new ArcGISLocalTiledLayer(
					getString(R.string.map_address));
			mMap.setExtent(mGeometry);
			mMap.setMaxResolution(611.49622628138);
			mMap.setMinResolution(9.55462853563415);
			mMap.setMapBackground(0xffffffff, Color.WHITE, 0, 0);
			mMap.addLayer(mLocalTiledLayer);
			mMap.addLayer(mRouteGraphicsLayer);
			mMap.addLayer(mPoiGraphicsLayer);
			mMap.setOnSingleTapListener(new OnSingleTapListener() {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void onSingleTap(float x, float y) {
					int[] graphicIDs = mPoiGraphicsLayer
							.getGraphicIDs(x, y, 25);
					if (graphicIDs != null && graphicIDs.length > 0) {
						Callout mCallout = mMap.getCallout();
						CalloutStyle mStyle = new CalloutStyle();
						mStyle.setAnchor(5);
						mStyle.setCornerCurve(10);
						mStyle.setMaxHeight(TravelApplication.Dp2Px(
								RouteDetailActivity.this, 56));
						mStyle.setMaxWidth(TravelApplication.Dp2Px(
								RouteDetailActivity.this, 300));
						mStyle.setBackgroundColor(0xff0099CC);
						mStyle.setFrameColor(0xff0099CC);
						LayoutInflater mInflater = LayoutInflater
								.from(RouteDetailActivity.this);
						View mView = mInflater.inflate(R.layout.callout, null);
						TextView mTextView = (TextView) mView
								.findViewById(R.id.txtCallout);
						ImageView mImageIconView = (ImageView) mView
								.findViewById(R.id.imgCallout);
						ImageView mImageMoreView = (ImageView) mView
								.findViewById(R.id.imgCalloutMore);
						mImageMoreView.setVisibility(View.GONE);
						mImageIconView.setVisibility(View.GONE);
						Graphic mGraphic = mPoiGraphicsLayer
								.getGraphic(graphicIDs[0]);
						String poiName = (String) mGraphic
								.getAttributeValue("NAME");
						mTextView.setText(poiName);
						mCallout.setStyle(mStyle);
						mCallout.setOffset(0, -15);
						mCallout.show((Point) mGraphic.getGeometry(), mView);
					}
					Log.v("MapActivity", "OnSingleTapLinstener is running !");
				}
			});
			return "ok";
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (result != null) {
				// Toast.makeText(RouteDetailActivity.this, "�ɹ���ȡ��ͼ",
				// Toast.LENGTH_LONG).show();

			}

		}
	}
}
