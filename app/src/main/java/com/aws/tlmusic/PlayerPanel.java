package com.aws.tlmusic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.aws.mp3.LrcDecode;
import com.aws.mp3.Mp3ReadId3v2;

public class PlayerPanel extends Activity implements Runnable, OnClickListener {
	private int id = 0;
	// private int sqlId = 1;
	private String[] paths = null;
	private MediaPlayer m = new MediaPlayer();
	// private Random r = new Random(); // 随机播放用的随机数
	private int len = 0; // 列表长度
	private ImageView imgHead = null; // 歌手头像
	private ImageButton stateBn = null; // 状态
	private ImageButton nextBn = null; // 下一首
	private ImageButton preBn = null; // 上一首
	private SeekBar timeBar = null; // 进度条
	private TextView lrcPanel = null; // 歌词
	private TextView info = null; // 歌曲信息
	private TextView showTime = null; // 时间
	private int audioTime = 0; // 歌曲长
	private int currentTime = 0; // 当前时间
	private Handler isPlayUpdate = new upDateHandler();
	private Hashtable<String, String> lrcTable = null; // 歌词表
	private Mp3ReadId3v2 mp3Id3v2 = null;
	private boolean isChange = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.player_panel);
		imgHead = (ImageView) findViewById(R.id.imageView1);
		stateBn = (ImageButton) findViewById(R.id.imageButton2);
		stateBn.setOnClickListener(this);
		nextBn = (ImageButton) findViewById(R.id.imageButton3);
		nextBn.setOnClickListener(this);
		preBn = (ImageButton) findViewById(R.id.imageButton1);
		preBn.setOnClickListener(this);
		timeBar = (SeekBar) findViewById(R.id.seekBar1);
		timeBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				m.seekTo(seekBar.getProgress());
				isChange = false;
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				isChange = true;
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub

			}
		});
		lrcPanel = (TextView) findViewById(R.id.textView1);
		info = (TextView) findViewById(R.id.textView2);
		showTime = (TextView) findViewById(R.id.textView3);
		// 曲目结束事件
		m.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				try {
					if (id < len - 1)
						playAudio(++id);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		// 初始化
		init();
		new Thread(this).start();
	}

	@Override
	protected void onRestart() {
		init();
		super.onRestart();
	}

	@Override
	protected void onResume() {
		init();
		super.onResume();
	}

	private void init() {
		if (PlayListPanel.isClink) {
			id = PlayListPanel.id;
			// sqlId = PlayListPanel.sqlId;
			paths = PlayListPanel.paths;
			len = paths.length;
			try {
				playAudio(id);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		PlayListPanel.isClink = false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, "设置铃声");
		menu.add(0, 1, 0, "铃声制作").setIcon(
				getResources().getDrawable(
						android.R.drawable.ic_menu_preferences));
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == 0) {
			Toast.makeText(this, "功能尚在开发", Toast.LENGTH_LONG).show();
		} else if (item.getItemId() == 1) {
			Toast.makeText(this, "功能尚在开发", Toast.LENGTH_LONG).show();
		}
		return super.onOptionsItemSelected(item);
	}

	// 没有播放时的界面
	Handler unPlayUpdate = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			stateBn.setImageDrawable(getResources().getDrawable(
					R.drawable.ic_media_pause));
			super.handleMessage(msg);
		}

	};

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (true) {
			if (m.isPlaying()) {
				currentTime = m.getCurrentPosition();
				Message msg = new Message();
				isPlayUpdate.sendMessage(msg);
			} else {
				Message msg = new Message();
				unPlayUpdate.sendMessage(msg);
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 播放
	 * */
	private void playAudio(int id) throws IOException {
		lrcPanel.setText("查找歌词");
		try {
			lrcTable = new LrcDecode().readLrc(
					new FileInputStream((paths[id]).replace(".mp3", ".lrc")))
					.getLrcTable();
		} catch (Exception e) {
			lrcTable = null;
		}
		try {
			mp3Id3v2 = new Mp3ReadId3v2(new FileInputStream(paths[id]));
			mp3Id3v2.readId3v2(1024 * 100);
			info.setText("曲目:" + mp3Id3v2.getName() + "\n歌手:"
					+ mp3Id3v2.getAuthor() + "\n专辑:" + mp3Id3v2.getSpecial());
			NotificationManager systemService = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			Notification noti = new Notification(android.R.drawable.ic_media_play, null, System
					.currentTimeMillis());
			PendingIntent pintent = PendingIntent.getActivity(this, 0, new Intent(this,
					TlMusic.class), 0);
			noti.setLatestEventInfo(PlayerPanel.this, mp3Id3v2.getName(),
					mp3Id3v2.getAuthor(), pintent);
			systemService.notify(android.R.layout.simple_list_item_1, noti);

			if (mp3Id3v2.getImg() != null) {
				File imgFile = new File(PlayListPanel.SELFDIR
						+ mp3Id3v2.getAuthor() + ".jpg");
				if (!imgFile.exists()) {
					imgFile.createNewFile();
					FileOutputStream fout = new FileOutputStream(imgFile);
					fout.write(mp3Id3v2.getImg());
					fout.close();
				}
				imgHead.setImageDrawable(Drawable.createFromPath(imgFile + ""));
			} else {
				imgHead.setImageDrawable(getResources().getDrawable(
						R.drawable.ic_menu_l));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		m.reset();
		m.setDataSource(paths[id]);
		m.prepare();
		m.start();
		audioTime = m.getDuration();
		timeBar.setMax(audioTime);
		System.out.println("new play:" + id);
	}

	class upDateHandler extends Handler {
		// 正在播放时接收
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			// 更新界面
			if (isChange)
				return;
			stateBn.setImageDrawable(getResources().getDrawable(
					R.drawable.ic_media_play));

			timeBar.setProgress(currentTime);
			showTime.setText("时间:\n" + LrcDecode.timeMode(currentTime) + "/"
					+ LrcDecode.timeMode(audioTime));
			try {
				if (lrcTable.get(LrcDecode.timeMode(currentTime)) != null) {
					lrcPanel.setText(lrcTable.get(LrcDecode
							.timeMode(currentTime)));
				}
			} catch (Exception e) {
				// TODO: handle exception
				lrcPanel.setText("没有发现歌词");
			}
			super.handleMessage(msg);
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		ImageButton imgbn = (ImageButton) v;
		if (stateBn == imgbn) {
			if (m.isPlaying()) {
				m.pause();
			} else {
				m.start();

			}
		} else if (nextBn == imgbn) {
			try {
				if (id < len - 1)
					playAudio(++id);
				System.out.println("next:" + id);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (preBn == imgbn) {
			try {
				System.out.println("oldup:" + id);
				if (id > 0) {
					playAudio(--id);
				}
				System.out.println("up:" + id);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
