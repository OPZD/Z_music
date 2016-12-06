package com.aws.tlmusic;

import java.io.File;
import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.aws.util.FileScanTool;

public class PlayListPanel extends Activity {
	private ListView playList = null;
	private Vector<File> musics = null;
	private boolean first = true; // 第一次运行
	private SQLiteDatabase mSqlData = null;
	private String sql = null;
	private SharedPreferences spref = null;
	public static String[] paths = null; // 全部曲目
	public static int id = 0; // 当前列表索引
	public static int sqlId = 1;
	public static boolean isClink = false;
	private boolean updateEvery = true; // 每次提示更新 ？
	public final static String SDCARD = Environment
			.getExternalStorageDirectory()
			+ "/";
	public final static String SELFDIR = SDCARD + "TlMusic/";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.palylist_panel);
		spref = this.getPreferences(MODE_PRIVATE);
		first = spref.getBoolean("first", true);
		updateEvery = spref.getBoolean("update", true);
		mSqlData = this.openOrCreateDatabase("music.db", MODE_PRIVATE, null);
		playList = (ListView) findViewById(R.id.listView1);
		// 是否更新
		if (updateEvery) {
			final CheckBox cb = new CheckBox(this);
			cb.setText("以后启动不再提示?");
			cb.setChecked(false);
			new AlertDialog.Builder(this).setTitle("更新列表").setMessage(
					"是否更新播放列表?").setView(cb).setNeutralButton("确定",
					new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							if (cb.isChecked()) {
								SharedPreferences.Editor edtior = spref.edit();
								edtior.putBoolean("update", false);
								edtior.commit();
							}
							upDataPlayList();
						}
					}).setNegativeButton("取消", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					if (cb.isChecked()) {
						SharedPreferences.Editor edtior = spref.edit();
						edtior.putBoolean("update", false);
						edtior.commit();
					}
				}
			}).create().show();
		}

		// 判断是否为第一次运行
		if (first) {
			try {
				sql = "CREATE TABLE `mytb` (`id`  integer NOT NULL ,`data`  text NULL ,PRIMARY KEY (`id`))";
				mSqlData.execSQL(sql);
			} catch (Exception e) {
				// TODO: handle exception
			}
			FileScanTool fst = new FileScanTool();
			fst.scanFile(new File(SDCARD), ".mp3");
			musics = fst.getFilelists();
			int len = musics.size();
			String[] strLists = new String[len];
			for (int i = 0; i < len; i++) {
				strLists[i] = musics.elementAt(i) + "";
				sql = "INSERT INTO `mytb` (`data`) VALUES ('" + strLists[i]
						+ "')";
				mSqlData.execSQL(sql);
				System.out.println(strLists[i]);
			}
		}
		//#----------------#
		SharedPreferences.Editor edtior = spref.edit();
		edtior.putBoolean("first", false);
		edtior.commit();
		//----------------//
		File selfFile = new File(SELFDIR);
		if (!selfFile.exists() || !selfFile.isDirectory()) {
			try {
				System.out.println("创建目录");
				selfFile.mkdir();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		upDateList();
		playList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				id = arg2;
				sqlId = ((SqlAdapter) playList.getAdapter()).getId()[arg2];
				isClink = true;
				TlMusic.tabHost.setCurrentTab(1);

			}
		});
	}

	private void upDateList() {
		sql = "SELECT * FROM mytb";
		Cursor cr = mSqlData.rawQuery(sql, null);
		cr.moveToFirst();
		String[] con = new String[cr.getCount()];
		int[] num = new int[cr.getCount()];

		for (int i = 0; i < cr.getCount(); i++) {
			con[i] = cr.getString(cr.getColumnIndex("data"));
			num[i] = Integer.valueOf(cr.getString(cr.getColumnIndex("id")));
			System.out.println(cr.getString(cr.getColumnIndex("id")) + ":"
					+ cr.getString(cr.getColumnIndex("data")));
			if (!cr.isLast()) {
				cr.moveToNext();
			}
		}
		playList.setAdapter(new SqlAdapter(PlayListPanel.this, con, num));
		paths = ((SqlAdapter) playList.getAdapter()).getData();
		cr.close();
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		mSqlData.close();
		super.finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(0, 0, 0, "更新列表").setIcon(android.R.drawable.ic_menu_agenda);
		menu.add(0, 1, 0, "删除曲目").setIcon(android.R.drawable.ic_menu_delete);
		menu.add(0, 2, 0, "退出").setIcon(
				android.R.drawable.ic_menu_close_clear_cancel);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if (item.getItemId() == 0) {
			upDataPlayList();

		} else if (item.getItemId() == 1) {
			System.out.println("listId:" + playList.getSelectedItemPosition());
			if (playList.getSelectedItemPosition() != -1) {
				deleteList(playList.getSelectedItemPosition());
			}
		} else if (item.getItemId() == 2) {
			this.finish();
			System.exit(0);
		}
		return super.onOptionsItemSelected(item);
	}

	private void deleteList(int selectedItemPosition) {
		// TODO Auto-generated method stub
		int id = ((SqlAdapter) playList.getAdapter()).getId()[selectedItemPosition];
		System.out.println("SqlId:" + id);
		sql = "DELETE FROM `mytb` WHERE (`id`='" + id + "')";
		mSqlData.execSQL(sql);
		upDateList();
	}

	private void upDataPlayList() {

		try {
			sql = "DROP TABLE `mytb`";
			mSqlData.execSQL(sql);
			sql = "CREATE TABLE `mytb` (`id`  integer NOT NULL ,`data`  text NULL ,PRIMARY KEY (`id`))";
			mSqlData.execSQL(sql);
		} catch (Exception e) {
			// TODO: handle exception
		}
		FileScanTool fst = new FileScanTool();
		fst.scanFile(new File(SDCARD), ".mp3");
		musics = fst.getFilelists();
		int len = musics.size();
		String[] strLists = new String[len];
		for (int i = 0; i < len; i++) {
			strLists[i] = musics.elementAt(i) + "";
			sql = "INSERT INTO `mytb` (`data`) VALUES ('" + strLists[i] + "')";
			mSqlData.execSQL(sql);
			System.out.println(strLists[i]);
		}
		upDateList();
	}

}
