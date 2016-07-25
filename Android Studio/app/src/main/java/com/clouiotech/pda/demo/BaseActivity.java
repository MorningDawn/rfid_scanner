package com.clouiotech.pda.demo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.clouiotech.pda.demoExample.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressLint("HandlerLeak")
public class BaseActivity extends Activity {
	private Toast _MyToast = null;

	protected static final int MSG_SHOW_WAIT = 1;
	protected static final int MSG_HIDE_WAIT = 2;
	protected static final int MSG_SHOW_TIP = 3;

	protected static final int MSG_USER_BEG = 100;

	ProgressDialog waitDialog = null;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			msgProcess(msg);
		}
	};

	protected void showWaitDialog(String title, String info) {
		if (waitDialog != null) {
			waitDialog.dismiss();
			waitDialog = null;
		}
		waitDialog = ProgressDialog.show(this, title, info);
	}

	protected void hideWaitDialog() {
		if (waitDialog != null) {
			waitDialog.dismiss();
			waitDialog = null;
		}
	}

	protected void msgProcess(Message msg) {
		switch (msg.what) {
		case MSG_SHOW_WAIT:
			showWaitDialog(null, (String) msg.obj);
			break;
		case MSG_HIDE_WAIT:
			hideWaitDialog();
			break;
		case MSG_SHOW_TIP:
			doShowTip((String) msg.obj);
			break;
		default:
			break;
		}
	}

	protected void sendMessage(int what, Object obj) {
		handler.sendMessage(handler.obtainMessage(what, obj));
	}

	protected void ShowTip(String msg) {
		sendMessage(MSG_SHOW_TIP, msg);
	}

	private void doShowTip(String msg) {
		if (_MyToast == null) {
			_MyToast = Toast.makeText(BaseActivity.this, msg,
					Toast.LENGTH_SHORT);
		} else {
			_MyToast.setText(msg);
		}
		_MyToast.show();
	}

	/**
	 * ��ʾ��ʾ��Ϣ
	 * 
	 * @param msg
	 * @param listener
	 */
	protected void ShowMsg(String msg, DialogInterface.OnClickListener listener) {
		new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_info).setTitle("Tooltip")
				.setMessage(msg)
				.setPositiveButton(getString(R.string.str_ok), listener)
				.create().show();
	}

	protected void ShowConfim(String msg, OnClickListener okListener,
			OnClickListener cancelListener) {
		new AlertDialog.Builder(BaseActivity.this)
				.setTitle(getString(R.string.str_confirm))
				.setMessage(msg)
				.setPositiveButton(getString(R.string.str_ok), okListener)
				.setNegativeButton(getString(R.string.str_cancel),
						cancelListener).show();
	}

	public boolean CheckHexInput(String strInput) {
		boolean rt = false;
		Pattern p = Pattern.compile("^[a-f,A-F,0-9]*$");
		Matcher m = p.matcher(strInput);
		rt = m.matches();
		return rt;
	}

	/**
	 * ��ȡ�汾��
	 * 
	 * @return ��ǰӦ�õİ汾��
	 */
	public String getVersion() {
		try {
			PackageManager manager = this.getPackageManager();
			PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
			String version = info.versionName;
			return version;
		} catch (Exception e) {
			return null;
		}
	}

	private long lastClickTime;

	protected synchronized boolean isFastClick() {
		long time = System.currentTimeMillis();
		if (time - lastClickTime < 500) {
			return true;
		}
		lastClickTime = time;
		return false;
	}

}
