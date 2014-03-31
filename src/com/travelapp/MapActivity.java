package com.travelapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.android.map.Callout;
import com.esri.android.map.CalloutStyle;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISLocalTiledLayer;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.android.map.event.OnZoomListener;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Point;
import com.esri.core.map.Graphic;

public class MapActivity extends Activity {
	private ImageView mBackImageView;
	private MapView mMap = null;
	private Query mQuery;
	private Intent mIntent;
	private Bundle mBundle;
	private GraphicsLayer mGraphicsLayer;
	private ArcGISLocalTiledLayer mLocalTiledLayer;
	private String mFunction = "POIS";
	private int mType = 0;
	private int mPoiId = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		initView();
		new AddMap().execute();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			TravelApplication.buildExitDialog(MapActivity.this);
		}
		return false;
	}

	private void initView() {
		mBackImageView = (ImageView) findViewById(R.id.imgListBack);
		mMap = (MapView) findViewById(R.id.map);
		mGraphicsLayer = new GraphicsLayer();
		mIntent = getIntent();
		mBundle = mIntent.getExtras();
		if (mBundle != null) {
			mFunction = mBundle.getString("FUNCTION");
			mType = mBundle.getInt("TYPE");
			if (mFunction.equals("POIS")) {
				switch (mType) {
				case 1:
					mBackImageView
							.setOnClickListener(new View.OnClickListener() {

								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									Intent intent = new Intent(
											MapActivity.this,
											ScenicListActivity.class);
									intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
											| Intent.FLAG_ACTIVITY_NEW_TASK);
									MapActivity.this.startActivity(intent);
									MapActivity.this.finish();
									MapActivity.this.overridePendingTransition(
											R.anim.anim_in_left2right,
											R.anim.anim_out_left2right);
								}
							});
					break;
				case 2:
					mBackImageView
							.setOnClickListener(new View.OnClickListener() {

								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									Intent intent = new Intent(
											MapActivity.this,
											HotelListActivity.class);
									intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
											| Intent.FLAG_ACTIVITY_NEW_TASK);
									MapActivity.this.startActivity(intent);
									MapActivity.this.finish();
									MapActivity.this.overridePendingTransition(
											R.anim.anim_in_left2right,
											R.anim.anim_out_left2right);
								}
							});
					break;
				case 3:
					mBackImageView
							.setOnClickListener(new View.OnClickListener() {

								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									Intent intent = new Intent(
											MapActivity.this,
											RestListActivity.class);
									intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
											| Intent.FLAG_ACTIVITY_NEW_TASK);
									MapActivity.this.startActivity(intent);
									MapActivity.this.finish();
									MapActivity.this.overridePendingTransition(
											R.anim.anim_in_left2right,
											R.anim.anim_out_left2right);
								}
							});
					break;
				case 4:
					mBackImageView
							.setOnClickListener(new View.OnClickListener() {

								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									Intent intent = new Intent(
											MapActivity.this,
											FunListActivity.class);
									intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
											| Intent.FLAG_ACTIVITY_NEW_TASK);
									MapActivity.this.startActivity(intent);
									MapActivity.this.finish();
									MapActivity.this.overridePendingTransition(
											R.anim.anim_in_left2right,
											R.anim.anim_out_left2right);
								}
							});
					break;
				}
			} else {
				mPoiId = mBundle.getInt("ID");
				switch (Integer.parseInt(String.valueOf(mType).substring(0, 1))) {
				case 1:
					mBackImageView
							.setOnClickListener(new View.OnClickListener() {

								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									Intent intent = new Intent(
											MapActivity.this,
											ScenicDetailActivity.class);
									intent.putExtra("ID", mPoiId);
									intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
											| Intent.FLAG_ACTIVITY_NEW_TASK);
									MapActivity.this.startActivity(intent);
									MapActivity.this.finish();
									MapActivity.this.overridePendingTransition(
											R.anim.anim_in_left2right,
											R.anim.anim_out_left2right);
								}
							});
					break;
				case 2:
					mBackImageView
							.setOnClickListener(new View.OnClickListener() {

								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									Intent intent = new Intent(
											MapActivity.this,
											HotelDetailActivity.class);
									intent.putExtra("ID", mPoiId);
									intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
											| Intent.FLAG_ACTIVITY_NEW_TASK);
									MapActivity.this.startActivity(intent);
									MapActivity.this.finish();
									MapActivity.this.overridePendingTransition(
											R.anim.anim_in_left2right,
											R.anim.anim_out_left2right);
								}
							});
					break;
				case 3:
					mBackImageView
							.setOnClickListener(new View.OnClickListener() {

								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									Intent intent = new Intent(
											MapActivity.this,
											RestDetailActivity.class);
									intent.putExtra("ID", mPoiId);
									intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
											| Intent.FLAG_ACTIVITY_NEW_TASK);
									MapActivity.this.startActivity(intent);
									MapActivity.this.finish();
									MapActivity.this.overridePendingTransition(
											R.anim.anim_in_left2right,
											R.anim.anim_out_left2right);
								}
							});
					break;
				case 4:
					mBackImageView
							.setOnClickListener(new View.OnClickListener() {

								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									Intent intent = new Intent(
											MapActivity.this,
											FunDetailActivity.class);
									intent.putExtra("ID", mPoiId);
									intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
											| Intent.FLAG_ACTIVITY_NEW_TASK);
									MapActivity.this.startActivity(intent);
									MapActivity.this.finish();
									MapActivity.this.overridePendingTransition(
											R.anim.anim_in_left2right,
											R.anim.anim_out_left2right);
								}
							});
					break;
				}
			}
		} else {
			mBackImageView.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(MapActivity.this,
							HomeActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
							| Intent.FLAG_ACTIVITY_NEW_TASK);
					MapActivity.this.startActivity(intent);
					MapActivity.this.finish();
					MapActivity.this.overridePendingTransition(
							R.anim.anim_in_left2right,
							R.anim.anim_out_left2right);
				}
			});
		}

	}

	public GraphicsLayer addGraphicToLayer(String function, int type) {
		GraphicsLayer mLayer = new GraphicsLayer();
		// create a simple marker symbol to be used by our graphic
		mQuery = new Query();
		if (function.equals("POIS")) {
			mLayer = mQuery.getPoisLocation(TravelApplication.getContext(),
					type);
		} else {
			mLayer = mQuery.getPoiLocation(TravelApplication.getContext(),
					String.valueOf(mPoiId));
		}
		return mLayer;

	}

	@Override
	protected void onPause() {
		super.onPause();
		mMap.pause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mMap.unpause();

	}

	class AddMap extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			mMap.setMapBackground(0xffffffff, Color.WHITE, 0, 0);
			mLocalTiledLayer = new ArcGISLocalTiledLayer(
					getString(R.string.map_address));
			mMap.setExtent(new Envelope(13570407.0434979, 5681967.05272005,
					14203165.9874021, 6017039.55107995), 0);
			mMap.setMinScale(4622324.434309);
			mMap.setMaxScale(72223.819286);
			mMap.setOnZoomListener(new OnZoomListener() {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void preAction(float pivotX, float pivotY, double factor) {
					// TODO Auto-generated method stub

				}

				@Override
				public void postAction(float pivotX, float pivotY, double factor) {
					// TODO Auto-generated method stub
					double mapscale = mMap.getScale();
					if (mapscale < 1155581.108577) {
						mMap.setMapBackground(0xffffffff, Color.WHITE, 0, 0);
						mMap.addLayer(mLocalTiledLayer);
						mMap.addLayer(mGraphicsLayer);
					}
				}
			});
			mMap.addLayer(mLocalTiledLayer);
			mGraphicsLayer = addGraphicToLayer(mFunction, mType);
			mMap.addLayer(mGraphicsLayer);
			mMap.setOnSingleTapListener(new OnSingleTapListener() {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void onSingleTap(float x, float y) {
					int[] graphicIDs = mGraphicsLayer.getGraphicIDs(x, y, 25);
					if (graphicIDs != null && graphicIDs.length > 0) {
						Callout mCallout = mMap.getCallout();
						CalloutStyle mStyle = new CalloutStyle();
						Intent mIntent = null;
						int imgId = 1;
						mStyle.setAnchor(5);
						mStyle.setCornerCurve(10);
						mStyle.setMaxHeight(TravelApplication.Dp2Px(
								MapActivity.this, 56));
						mStyle.setMaxWidth(TravelApplication.Dp2Px(
								MapActivity.this, 200));
						LayoutInflater mInflater = LayoutInflater
								.from(MapActivity.this);
						View mView = mInflater.inflate(R.layout.callout, null);
						TextView mTextView = (TextView) mView
								.findViewById(R.id.txtCallout);
						ImageView mImageIconView = (ImageView) mView
								.findViewById(R.id.imgCallout);
						ImageView mImageMoreView = (ImageView) mView
								.findViewById(R.id.imgCalloutMore);
						if (mFunction.equals("POI")) {
							mImageMoreView.setVisibility(View.GONE);
						}
						Graphic mGraphic = mGraphicsLayer
								.getGraphic(graphicIDs[0]);
						String poiName = (String) mGraphic
								.getAttributeValue("NAME");

						if (poiName.length() > 7) {
							String name = poiName.substring(0, 7);
							mTextView.setText(name + "...");
						} else {
							String name = poiName;
							mTextView.setText(name);
						}
						final int poiId = Integer.parseInt((String) mGraphic
								.getAttributeValue("ID"));
						final int poiType = Integer.parseInt((String) mGraphic
								.getAttributeValue("TYPE"));
						switch (poiType) {
						case 1:
							imgId = MapActivity.this.getResources()
									.getIdentifier("ic_scenic", "drawable",
											"com.travelapp");
							mStyle.setBackgroundColor(0xff9933CC);
							mStyle.setFrameColor(0xff9933CC);
							mIntent = new Intent(MapActivity.this,
									ScenicDetailActivity.class);
							break;
						case 2:
							imgId = MapActivity.this.getResources()
									.getIdentifier("ic_hotel", "drawable",
											"com.travelapp");
							mStyle.setBackgroundColor(0xff0099CC);
							mStyle.setFrameColor(0xff0099CC);
							mIntent = new Intent(MapActivity.this,
									HotelDetailActivity.class);
							break;
						case 3:
							imgId = MapActivity.this.getResources()
									.getIdentifier("ic_rest", "drawable",
											"com.travelapp");
							mStyle.setBackgroundColor(0xff669900);
							mStyle.setFrameColor(0xff669900);
							mIntent = new Intent(MapActivity.this,
									RestDetailActivity.class);
							break;
						case 4:
							imgId = MapActivity.this.getResources()
									.getIdentifier("ic_fun", "drawable",
											"com.travelapp");
							mStyle.setBackgroundColor(0xffFF8800);
							mStyle.setFrameColor(0xffFF8800);
							mIntent = new Intent(MapActivity.this,
									FunDetailActivity.class);
							break;
						}
						if (mType == 0) {
							final Intent newIntent = mIntent;
							mImageMoreView
									.setOnClickListener(new View.OnClickListener() {

										@Override
										public void onClick(View v) {
											TravelApplication.isFromMap = true;
											newIntent.putExtra("ID", poiId);
											newIntent.putExtra("FROM",
													"MapActivity");
											newIntent
													.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
															| Intent.FLAG_ACTIVITY_NEW_TASK);
											MapActivity.this
													.startActivity(newIntent);
											MapActivity.this.finish();
											MapActivity.this
													.overridePendingTransition(
															R.anim.anim_in_right2left,
															R.anim.anim_out_right2left);
										}
									});
						} else if (mType == 1 || mType == 2 || mType == 3
								|| mType == 4) {
							final Intent newIntent = mIntent;
							mImageMoreView
									.setOnClickListener(new View.OnClickListener() {

										@Override
										public void onClick(View v) {
											TravelApplication.isFromMap = true;
											newIntent.putExtra("ID", poiId);
											newIntent.putExtra("FROM",
													"ListActivity");
											newIntent
													.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
															| Intent.FLAG_ACTIVITY_NEW_TASK);
											MapActivity.this
													.startActivity(newIntent);
											MapActivity.this.finish();
											MapActivity.this
													.overridePendingTransition(
															R.anim.anim_in_right2left,
															R.anim.anim_out_right2left);
										}
									});
						}
						Drawable mDrawable = MapActivity.this.getResources()
								.getDrawable(imgId);
						mImageIconView.setImageDrawable(mDrawable);
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
				Toast.makeText(MapActivity.this, "成功获取地图", Toast.LENGTH_LONG)
						.show();

			}

		}
	}

}