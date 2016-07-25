package com.clouiotech.pda.demo.scanner;

import java.nio.charset.Charset;

import com.clouiotech.pda.demoExample.R;
import com.clouiotech.pda.demo.BaseActivity;
import com.clouiotech.pda.scanner.ScanReader;
import com.clouiotech.pda.scanner.Scanner;

import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.EditText;

public class ScannerActivity extends BaseActivity {
	boolean busy = false;

	static final int MSG_UPDATE_ID = MSG_USER_BEG + 1;

	private Scanner scanReader = ScanReader.getScannerInstance();

	ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_SYSTEM,
			ToneGenerator.MAX_VOLUME);

	@Override
	public void msgProcess(Message msg) {
		switch (msg.what) {
		case MSG_UPDATE_ID:
			String idString = (String) msg.obj;
			EditText text = (EditText) findViewById(R.id.tb_txtRecv);
			text.setText(idString);
			break;
		default:
			super.msgProcess(msg);
			break;
		}
	}

	protected void DeCode() {
		if (busy) {
			ShowTip(getString(R.string.str_busy));
			return;
		}

		busy = true;

		new Thread() {
			@Override
			public void run() {

				sendMessage(MSG_SHOW_WAIT,
						getString(R.string.str_please_waitting));

				byte[] id = scanReader.decode();

				String idString;
				if (id != null) {
					idString = new String(id, Charset.forName("gbk")) + "\n";
					toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP);
				} else {
					idString = null;
					ShowTip(getString(R.string.str_faild));
				}

				sendMessage(MSG_UPDATE_ID, idString);
				sendMessage(MSG_HIDE_WAIT, null);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				busy = false;
			}

		}.start();

	}

	public void Triger(View v) {
		DeCode();
	}

	public void Back(View v) {
		scanReader.close();
		finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.scan_main);
	}

	protected void Init() {
		if (!scaninit()) {
			ShowMsg(getString(R.string.scanner_open_power_faild),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							finish();
						}
					});
		}

	}

	protected void DeInit() {
		// UHF_Dispose();
		ScanDispose();
	}

	private boolean scaninit() {
		if (null == scanReader) {
			return false;
		}

		return scanReader.open(getApplicationContext());
	}

	private void ScanDispose() {
		scanReader.close();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		DeInit();
	}

	@Override
	protected void onPause() {
		super.onPause();
		DeInit();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Init();
	}

	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {

		if (keyCode == 131
				|| keyCode == 135
		) {
			DeCode();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	};
}
