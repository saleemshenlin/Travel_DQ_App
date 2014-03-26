package com.travelapp;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class NewQueryListAdapter extends BaseAdapter {
	private ArrayList<POI> poiList;
	private LayoutInflater inflater;
	private Context mContext;
	private int mLayoutRes;
	private ListView lv;
	private int scrollStauts = 0;
	private int type;

	public NewQueryListAdapter(Context context, int layoutRes,
			ArrayList<POI> poiList, ListView lv, int type) {
		// TODO Auto-generated constructor stub
		this.poiList = poiList;
		this.inflater = LayoutInflater.from(context);
		this.mContext = context;
		this.mLayoutRes = layoutRes;
		this.type = type;
		this.lv = lv;
		this.lv.setOnScrollListener(new AbsListView.OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				switch (scrollState) {
				case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:// 停止 0
					scrollStauts = 0;
					updateUI();
					// System.out.println("停止");
					break;
				case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:// 触摸滑动
																			// 1
					scrollStauts = 1;

					// System.out.println("触摸滑动");
					break;
				case AbsListView.OnScrollListener.SCROLL_STATE_FLING:// 快速滑动 2
					scrollStauts = 2;
					// System.out.println("快速滑动");
					break;
				default:
					break;
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub

			}
		});

	}

	private void updateUI() {
		// TODO Auto-generated method stub
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return poiList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return poiList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return poiList.get(position).hashCode();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(mLayoutRes, null);
			holder.title = (TextView) convertView
					.findViewById(R.id.txtRowTitle);
			holder.price = (TextView) convertView
					.findViewById(R.id.txtRowPrice);
			holder.abstracttext = (TextView) convertView
					.findViewById(R.id.txtRowAbstract);
			holder.image = (ImageView) convertView.findViewById(R.id.imgPalce);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		POI mPoi = poiList.get(position);
		holder.title.setText(mPoi.Name);
		switch (type) {
		case 1:
			holder.price.setText("门票：" + mPoi.Ticket);
			break;
		case 2:
			holder.price.setText("房价：" + mPoi.Ticket);
			break;
		case 3:
			holder.price.setText("人均消费：" + mPoi.Ticket);
			break;
		case 4:
			holder.price.setText("营业时间：" + mPoi.Time);
			break;
		default:
			break;
		}
		holder.abstracttext.setText(mPoi.Abstract);
		if (mPoi.Image != null) {
			holder.image.setImageBitmap(mPoi.Image);
		} else if (mPoi.ImgUrl != null) {
			if (scrollStauts == 0) {
				String filePath = mContext.getExternalFilesDir("cache") + "/"
						+ mPoi.ImgUrl;
				mPoi.Image = Query.readBitmapFromFile(mContext, filePath, 4);
				holder.image.setImageBitmap(mPoi.Image);
			} else {
				holder.image.setImageResource(R.drawable.img_missing);
			}
		} else {
			holder.image.setImageResource(R.drawable.img_missing);
		}
		return convertView;
	}

	private class ViewHolder {
		ImageView image;
		TextView title;
		TextView price;
		TextView abstracttext;
	}

}
