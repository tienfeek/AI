package com.tien.ai.crop;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class StortFile {

	public StortFile() {

	}

	/**
	 * 保存文件
	 * 
	 * @param bm
	 * @param fileName
	 * @throws IOException
	 */
	public boolean saveMyBitmap(String bitName, byte[] bytemap, String imgDir) {
		Bitmap mBitmap = null;
		try {
			mBitmap = BitmapFactory.decodeByteArray(bytemap, 0, bytemap.length);
		} catch (OutOfMemoryError e) {
			// TODO: handle exception
			return false;
		}

		File dirFile = null;
		try {
			dirFile = new File(imgDir);
			if (!(dirFile.exists()) && !(dirFile.isDirectory())) {
				boolean creadok = dirFile.mkdirs();
				if (creadok) {
					File f = new File(imgDir + bitName + ".jpg");
					try {
						f.createNewFile();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						return false;
					}
					FileOutputStream fOut = null;
					try {
						fOut = new FileOutputStream(f);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
						return false;
					}
					mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
					try {
						fOut.flush();
					} catch (IOException e) {
						e.printStackTrace();
						return false;
					}
					try {
						fOut.close();
						return true;
					} catch (IOException e) {
						e.printStackTrace();
						return false;
					}
				}
			} else {
				File f = new File(imgDir + bitName + ".jpg");
				try {
					f.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					return false;
				}
				FileOutputStream fOut = null;
				try {
					fOut = new FileOutputStream(f);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					return false;
				}
				mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
				try {
					fOut.flush();
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
				try {
					fOut.close();
					return true;
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}

	/**
	 * 保存文件
	 * 
	 * @param bm
	 * @param fileName
	 * @throws IOException
	 */
	public boolean saveMyBitmap(String bitName, Bitmap mBitmap, String imgDir) {
		File dirFile = null;
		try {
			dirFile = new File(imgDir);
			if (!(dirFile.exists()) && !(dirFile.isDirectory())) {
				boolean creadok = dirFile.mkdirs();
				if (creadok) {
					File f = new File(imgDir + bitName + ".jpg");
					try {
						f.createNewFile();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						return false;
					}
					FileOutputStream fOut = null;
					try {
						fOut = new FileOutputStream(f);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
						return false;
					}
					mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
					try {
						fOut.flush();
					} catch (IOException e) {
						e.printStackTrace();
						return false;
					}
					try {
						fOut.close();
						return true;
					} catch (IOException e) {
						e.printStackTrace();
						return false;
					}
				}
			} else {
				File f = new File(imgDir + bitName + ".jpg");
				try {
					f.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					return false;
				}
				FileOutputStream fOut = null;
				try {
					fOut = new FileOutputStream(f);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					return false;
				}
				mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
				try {
					fOut.flush();
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
				try {
					fOut.close();
					return true;
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}

}
