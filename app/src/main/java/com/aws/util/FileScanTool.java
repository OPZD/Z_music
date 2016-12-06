package com.aws.util;

import java.io.File;
import java.io.FileFilter;
import java.util.Vector;

/**
 * <b>文件过滤扫描类</b>
 * 
 * @author 曾定
 * @QQ QQ:1107054085
 */
public class FileScanTool {

	private Vector<File> filelists = new Vector<File>();

	/**
	 * @param rootPath
	 *            根目录
	 * @param fileName
	 *            扫描文件后缀
	 * */
	public void scanFile(File rootPath, final String filterName) {

		rootPath.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				// TODO Auto-generated method stub
				if (((pathname + "").toLowerCase()).endsWith(filterName)) {
					filelists.addElement(pathname);
					return true;
				}
				if (pathname.isDirectory()) {
					scanFile(pathname, filterName);
					return true;
				} else {
					return false;
				}
			}
		});
	}

	/**
	 * 多格式扫描
	 * 
	 * @param rootPath
	 *            根目录
	 * @param filterNames
	 *            格式数组
	 * */
	public void scanFile(File rootPath, final String[] filterNames) {
		for (int i = 0; i < filterNames.length; i++) {
			scanFile(rootPath, filterNames[i]);
		}
	}

	public Vector<File> getFilelists() {
		return filelists;
	}

}
