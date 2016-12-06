package com.aws.tlmusic;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;

/**
*入口Main activity 
*/
public class TlMusic extends TabActivity {
	public static TabHost tabHost;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		tabHost = getTabHost();
		Intent intent = new Intent();
		intent.setClass(TlMusic.this, PlayListPanel.class);
		TabSpec playList = tabHost.newTabSpec("playList").setIndicator("播放列表",
				getResources().getDrawable(android.R.drawable.ic_menu_agenda))
				.setContent(intent);

		tabHost.addTab(playList);

		intent.setClass(TlMusic.this, PlayerPanel.class);
		TabSpec player = tabHost.newTabSpec("player").setIndicator("播放器",
				getResources().getDrawable(R.drawable.ic_music_p)).setContent(
				intent);

		tabHost.addTab(player);
		tabHost.setCurrentTab(0);
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {
				// TODO Auto-generated method stub
				System.out.println(tabId);
			}
		});
	}

}