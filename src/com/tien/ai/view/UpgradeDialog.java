package com.tien.ai.view;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tien.ai.R;
import com.tien.ai.demain.Upgrade;
import com.tien.ai.utils.XLog;

/**
 * 
 * @Description:
 * @author:wangtf
 * @see:
 * @since:
 * @copyright © baidu.com
 * @Date:2014-3-3
 */
public class UpgradeDialog extends Dialog implements View.OnClickListener {

	private static final int DOWNLOAD_PREPARE = 0;
	private static final int DOWNLOAD_WORK = 1;
	private static final int DOWNLOAD_OK = 2;
	private static final int DOWNLOAD_ERROR = 3;
	private Button mUngrade;
	private Button mCancel;
	private TextView mTextView;

	private Context mContext;
	private Upgrade upgrade;
	// private String filePath;
	private String apkName;
	private int fileSize = 0;
	private int downloadSize = 0;

	public UpgradeDialog(Context context, Upgrade upgrade) {
		super(context, R.style.NoTitleDialog);
		setContentView(R.layout.upgrade_layout);

		this.mContext = context;
		this.upgrade = upgrade;
		// filePath = FileUtils.getPath(mContext, upgrade.getApkUrl());
		this.apkName = upgrade.getUrl().substring(upgrade.getUrl().lastIndexOf("/") + 1);

		this.mCancel = (Button) findViewById(R.id.cancel_btn);
		this.mUngrade = (Button) findViewById(R.id.upgrade_btn);
		this.mTextView = (TextView) findViewById(R.id.percent_tv);

		this.mCancel.setOnClickListener(this);
		this.mUngrade.setOnClickListener(this);

		if (Upgrade.UPGRDATE_FORECE_TYPE.endsWith(upgrade.getForceUpdate())) {
			this.mCancel.setVisibility(View.GONE);
		}
		
		setCancelable(false);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return super.dispatchKeyEvent(event);
	}

	public void onClick(View v) {
		if (v.getId() == R.id.upgrade_btn) {
			mTextView.setVisibility(View.VISIBLE);
			downloadFile();
			mUngrade.setEnabled(false);
			XLog.i("wanges", "downloadFile start");
		} else if (v.getId() == R.id.cancel_btn) {
			cancel();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DOWNLOAD_PREPARE:
				mTextView.setText("0%");
				break;
			case DOWNLOAD_WORK:
				int res = downloadSize * 100 / fileSize;
				mTextView.setText(res + "%");
				break;
			case DOWNLOAD_OK:
				mUngrade.setEnabled(true);

				downloadSize = 0;
				fileSize = 0;

				Intent intent = new Intent(Intent.ACTION_VIEW);
				String filePath = getContext().getFilesDir().getPath() + "/" + apkName;
				intent.setDataAndType(Uri.fromFile(new File(filePath)), "application/vnd.android.package-archive");
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				mContext.startActivity(intent);

				System.exit(-1);
				cancel();

				break;
			case DOWNLOAD_ERROR:
				downloadSize = 0;
				fileSize = 0;
				Toast.makeText(mContext, "下载失败", Toast.LENGTH_SHORT).show();
				UpgradeDialog.this.dismiss();
				break;
			}
			super.handleMessage(msg);
		}
	};

	/**
	 * 下载文件
	 */
	private void downloadFile() {
		new Thread() {

			@Override
			public void run() {
				super.run();
				try {
					URL url = new URL(upgrade.getUrl());
					URLConnection conn = (HttpURLConnection) url.openConnection();
					InputStream is = conn.getInputStream();
					fileSize = conn.getContentLength();
					if (fileSize < 1 || is == null) {
						sendMessage(DOWNLOAD_ERROR);
					} else {
						sendMessage(DOWNLOAD_PREPARE);
						FileOutputStream fos = getContext().openFileOutput(apkName, Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE);
						// FileOutputStream fos = new FileOutputStream(filePath);
						byte[] bytes = new byte[1024];
						int len = -1;
						XLog.i("wanges", "downloadFile ------------");
						while ((len = is.read(bytes)) != -1) {
							fos.write(bytes, 0, len);
							fos.flush();
							downloadSize += len;
							sendMessage(DOWNLOAD_WORK);
						}
						sendMessage(DOWNLOAD_OK);
						is.close();
						fos.close();
					}
				} catch (Exception e) {
					sendMessage(DOWNLOAD_ERROR);
					e.printStackTrace();
				}
			}
		}.start();

	}

	private void sendMessage(int what) {
		Message m = new Message();
		m.what = what;
		handler.sendMessage(m);
	}
}
