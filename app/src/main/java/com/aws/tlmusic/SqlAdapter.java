package com.aws.tlmusic;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * <b>列表配置器</b>
 * 
 * @author 席有芳
 * @QQ QQ:951868171
 * @version 1.0
 * @email xi_yf_001@126.com
 * */
public class SqlAdapter extends BaseAdapter {
	private Activity activity;
	private String[] data;
	private int[] id;

	public SqlAdapter(Activity activity, String[] data, int[] id) {
		this.activity = activity;
		this.data = data;
		this.id = id;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		FileListView flv = new FileListView(activity);
		flv.getImg().setBackgroundDrawable(activity.getResources().getDrawable(
				R.drawable.format_music));
		flv.getTitle().setText(data[position].substring(
				data[position].lastIndexOf("/") + 1, data[position].length()));
		flv.getInfo().setText(data[position]);
		flv.setId(id[position]);
		return flv;
	}

	public String[] getData() {
		return data;
	}

	public void setData(String[] data) {
		this.data = data;
	}

	public int[] getId() {
		return id;
	}

	public void setId(int[] id) {
		this.id = id;
	}

}
