package com.android.gridapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ImageAdapter extends BaseAdapter {
	private Activity mContext;

	public ImageAdapter(Activity c) {
		mContext = c;
	}

	public int getCount() {
		return mThumbIds.length;
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

	// create a new ImageView for each item referenced by the Adapter
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout v;
		if (convertView == null) { 
			// if it's not recycled, initialize some attributes
			LayoutInflater li = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);		
			// "false" in metod call is needed. Without I get an  
			// java.lang.UnsupportedOperationException: addView(View, 
			//               LayoutParams) is not supported in AdapterView
			v = (LinearLayout) li.inflate(R.layout.gridapp_item, parent,false);
			//
			ImageView imageView = (ImageView) v.findViewById(R.id.gridapp_image);
			imageView = new ImageView(mContext);
			imageView.setLayoutParams(new GridView.LayoutParams(60, 60));
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			imageView.setPadding(8, 8, 8, 8);
		} else {
			v = (LinearLayout) convertView;
		}
		TextView tv = (TextView) v.findViewById(R.id.gridapp_text);
		tv.setText("Great");

		ImageView imageView = (ImageView) v.findViewById(R.id.gridapp_image);
		imageView.setImageResource(mThumbIds[position]);
		return v;
	}

	public View getViewOrg(int position, View convertView, ViewGroup parent) {
		ImageView imageView;
		if (convertView == null) { // if it's not recycled, initialize some
																// attributes
			imageView = new ImageView(mContext);
			imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			imageView.setPadding(8, 8, 8, 8);
		} else {
			imageView = (ImageView) convertView;
		}

		imageView.setImageResource(mThumbIds[position]);
		return imageView;
	}

	// references to our images
	private Integer[] mThumbIds = { R.drawable.two_persons, R.drawable.book,
			R.drawable.box, R.drawable.clipboard, R.drawable.cwr_mailbox,
			R.drawable.cwr_new, R.drawable.helpbooks,
			R.drawable.megaphone, R.drawable.peer, R.drawable.pencil,
			R.drawable.people_04b, R.drawable.people_05b, R.drawable.people_07a,
			R.drawable.people_09a, R.drawable.phonebox, };
}