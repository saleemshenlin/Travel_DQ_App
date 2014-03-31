package com.travelapp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class AroundDetailActivity extends Activity {
	private ImageView mBackImageView;
	private TextView mTitleTextView;
	private ImageView mMapImageView;
	private ListView mQueryListView;
	private ArrayList<QueryListItem> mQueryList = new ArrayList<QueryListItem>();
	private Intent mIntent;
	private Bundle mBundle;
	private Resources mResources;
	private ProgressBar mProgressBar;
	// private Query mQuery;
	private Cursor mItemCursor;

	int mPoiId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		mResources = this.getResources();
		initView();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			TravelApplication.buildExitDialog(AroundDetailActivity.this);
		}
		return false;
	}

	private void initView() {
		// TODO Auto-generated method stub
		mBackImageView = (ImageView) findViewById(R.id.imgListBack);
		mMapImageView = (ImageView) findViewById(R.id.imgListMap);
		mTitleTextView = (TextView) findViewById(R.id.txtListTitle);
		mQueryListView = (ListView) findViewById(R.id.listPoi);
		mProgressBar = (ProgressBar) findViewById(R.id.pbLoad);
		mProgressBar.setVisibility(View.VISIBLE);
		Drawable mDrawable = mResources.getDrawable(R.drawable.bg_noimg);
		mMapImageView.setImageDrawable(mDrawable);
		mIntent = getIntent();
		mBundle = mIntent.getExtras();
		mPoiId = mBundle.getInt("FROM");
		switch (mPoiId) {
		case 1:
			mTitleTextView.setText("周边景点");
			new QueryAround().execute("景点", "3000");
			break;
		case 2:
			mTitleTextView.setText("周边住宿");
			new QueryAround().execute("住宿", "1000");
			break;

		case 3:
			mTitleTextView.setText("周边餐饮");
			new QueryAround().execute("餐饮", "500");
			break;

		case 4:
			mTitleTextView.setText("周边购物");
			new QueryAround().execute("超市", "500");
			break;
		}
		mBackImageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(AroundDetailActivity.this,
						AroundListActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
						| Intent.FLAG_ACTIVITY_NEW_TASK);
				AroundDetailActivity.this.startActivity(intent);
				AroundDetailActivity.this.finish();
				AroundDetailActivity.this.overridePendingTransition(
						R.anim.anim_in_left2right, R.anim.anim_out_left2right);
			}
		});
		mQueryListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String url = mQueryList.get(position).getDetail_url();
				if (url != null) {
					Intent intent = new Intent();
					intent.setAction("android.intent.action.VIEW");
					Uri content_url = Uri.parse(url);
					intent.setData(content_url);
					startActivity(intent);
				} else {
					Toast.makeText(AroundDetailActivity.this, "对不起，无详情！",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		// setmItemCursor(mQuery.queryAroundByType("1"));
	}

	private ArrayList<QueryListItem> json2ArrayList(JsonParser jsonParser) {
		ArrayList<QueryListItem> queryList = new ArrayList<QueryListItem>();
		try {
			while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
				String nameString = jsonParser.getCurrentName();
				QueryListItem mQueryListItem = new QueryListItem();
				if ("name".equals(nameString)) {
					while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
						String fieldname = jsonParser.getCurrentName();
						if ("name".equals(fieldname)) {
							Log.e("name", jsonParser.getText());
							mQueryListItem.name = jsonParser.getText();
						} else if ("location".equals(fieldname)) {
							while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
								String locationname = jsonParser
										.getCurrentName();
								if ("lat".equals(locationname)) {
									jsonParser.nextToken();
									Log.e("lat", jsonParser.getText());
									mQueryListItem.lat = jsonParser.getText();
								} else if ("lng".equals(locationname)) {
									jsonParser.nextToken();
									Log.e("lng", jsonParser.getText());
									mQueryListItem.lng = jsonParser.getText();
								}
							}
						} else if ("address".equals(fieldname)) {
							jsonParser.nextToken();
							Log.e("address", jsonParser.getText());
							mQueryListItem.address = jsonParser.getText();
						} else if ("detail_info".equals(fieldname)) {
							while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
								String detailname = jsonParser.getCurrentName();
								if ("distance".equals(detailname)) {
									jsonParser.nextToken();
									Log.e("distance", jsonParser.getText());
									mQueryListItem.distance = Integer
											.parseInt(jsonParser.getText());
								} else if ("detail_url".equals(detailname)) {
									jsonParser.nextToken();
									Log.e("detail_url", jsonParser.getText());
									mQueryListItem.detail_url = jsonParser
											.getText();
								}
							}
						}

					}
					Log.e("finish", "finish");
					queryList.add(mQueryListItem);
				}
			}
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		queryList = compareArrayList(queryList);
		return queryList;
	}

	private ArrayList<QueryListItem> compareArrayList(
			ArrayList<QueryListItem> list) {
		Comparator<QueryListItem> itemComparator = new Comparator<QueryListItem>() {

			@Override
			public int compare(QueryListItem item1, QueryListItem item2) {
				// TODO Auto-generated method stub
				return item1.distance - item2.distance;
			}

		};
		Collections.sort(list, itemComparator);
		return list;
	}

	public Cursor getmItemCursor() {
		return mItemCursor;
	}

	public void setmItemCursor(Cursor mItemCursor) {
		this.mItemCursor = mItemCursor;
	}

	private class QueryAround extends
			AsyncTask<String, String, ArrayList<QueryListItem>> {

		@Override
		protected ArrayList<QueryListItem> doInBackground(String... query) {
			try {
				String requestURL = "http://api.map.baidu.com/place/v2/search?ak="
						+ getString(R.string.baiduak)
						+ "&output=json&query="
						+ query[0]
						+ "&page_size=10&page_num=0&scope=2&location="
						+ TravelApplication.mLocationPoint.getY()
						+ ","
						+ TravelApplication.mLocationPoint.getX()
						+ "&radius="
						+ query[1];
				HttpGet httpRequest = new HttpGet(requestURL);
				HttpClient httpclient = new DefaultHttpClient();
				httpclient.getParams().setParameter(
						CoreConnectionPNames.CONNECTION_TIMEOUT, 0);
				httpclient.getParams().setParameter(
						CoreConnectionPNames.SO_TIMEOUT, 0);
				HttpResponse httpResponse = httpclient.execute(httpRequest);
				int status = httpResponse.getStatusLine().getStatusCode();
				if (status == HttpStatus.SC_OK) {

					String result = EntityUtils.toString(
							httpResponse.getEntity(), "utf-8");
					JsonFactory mJsonFactory = new JsonFactory();
					JsonParser mjJsonParser = mJsonFactory
							.createJsonParser(result);
					mjJsonParser.nextToken();
					mQueryList = json2ArrayList(mjJsonParser);
				}
			} catch (Exception e) {
				Log.e("BDQuery", e.toString());
			}
			return mQueryList;
		}

		@Override
		protected void onPostExecute(ArrayList<QueryListItem> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			mProgressBar.setVisibility(View.GONE);
			QueryListAdapter mQueryListAdapter = new QueryListAdapter(
					AroundDetailActivity.this, R.layout.row, result);
			mQueryListView.setAdapter(mQueryListAdapter);
		}
	}

	class QueryListItem {
		String name;
		String lat;
		String lng;
		String address;
		int distance;
		String detail_url;

		public String getDetail_url() {
			return detail_url;
		}
	}

	class QueryListAdapter extends ArrayAdapter<QueryListItem> {
		private int resourceId;

		public QueryListAdapter(Context context, int textViewResourceId,
				List<QueryListItem> objects) {
			super(context, textViewResourceId, objects);
			this.resourceId = textViewResourceId;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			QueryListItem mQueryListItem = getItem(position);
			LinearLayout queryListLayout = new LinearLayout(getContext());
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
					inflater);
			vi.inflate(resourceId, queryListLayout, true);
			TextView mItemTitle = (TextView) queryListLayout
					.findViewById(R.id.txtRowTitle);
			TextView mItemDistance = (TextView) queryListLayout
					.findViewById(R.id.txtRowPrice);
			TextView mItemAddress = (TextView) queryListLayout
					.findViewById(R.id.txtRowAbstract);
			ImageView mItemImage = (ImageView) queryListLayout
					.findViewById(R.id.imgPalce);
			mItemImage.setVisibility(View.GONE);
			mItemTitle.setText(mQueryListItem.name);
			mItemDistance.setText(mQueryListItem.distance + "m");
			mItemAddress.setText(mQueryListItem.address);
			return queryListLayout;
		}

	}
}
