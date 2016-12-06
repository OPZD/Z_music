package com.aws.tlmusic;

import android.content.Context;
import android.graphics.Color;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FileListView extends LinearLayout {
	private ImageView img = null;
	private LinearLayout ll = null;
	private TextView title = null;
	private TextView info = null;

	public FileListView(Context context) {
		super(context);
		this.setOrientation(HORIZONTAL);
		img = new ImageView(context);
		img.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		ll = new LinearLayout(context);
		ll.setOrientation(VERTICAL);
		ll.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		title = new TextView(context);
		title.setTextColor(Color.WHITE);
		title.setTextSize(18);
		title.setSingleLine(true);
		title.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		info = new TextView(context);
		// info.setText("info");
		info.setTextColor(Color.WHITE);
		info.setPadding(20, 3, 3, 0);
		info.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		ll.addView(title);
		ll.addView(info);
		this.addView(img);
		this.addView(ll);
	}

	public ImageView getImg() {
		return img;
	}

	public void setImg(ImageView img) {
		this.img = img;
	}

	public TextView getTitle() {
		return title;
	}

	public void setTitle(TextView title) {
		this.title = title;
	}

	public TextView getInfo() {
		return info;
	}

	public void setInfo(TextView info) {
		this.info = info;
	}

}
