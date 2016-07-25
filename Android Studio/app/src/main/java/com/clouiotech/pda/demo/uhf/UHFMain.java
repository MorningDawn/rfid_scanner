package com.clouiotech.pda.demo.uhf;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;

import com.clouiotech.pda.demoExample.R;
import com.clouiotech.pda.rfid.EPCModel;
import com.clouiotech.pda.rfid.IAsynchronousMessage;

/**
 * @author RFID_C ������
 */
public class UHFMain extends UHFBaseActivity implements IAsynchronousMessage {

	private final int MSG_ENTER_READ = MSG_USER_BEG + 1;
	private final int MSG_ENTER_READ_MATCH = MSG_USER_BEG + 2;
	private final int MSG_ENTER_WRITE = MSG_USER_BEG + 3;
	private final int MSG_ENTER_CONFIG = MSG_USER_BEG + 4;
	private final int MSG_SHOW_UHF_VER = MSG_USER_BEG + 5;
	private final int MSG_SHOW_STOK = MSG_USER_BEG + 6;

	@Override
	protected void msgProcess(Message msg) {
		Intent intent;
		switch (msg.what) {
		case MSG_ENTER_READ:
			intent = new Intent();
			intent.setClass(UHFMain.this, ReadEPCActivity.class);
			startActivity(intent);
			break;
		case MSG_SHOW_UHF_VER:
			if (msg.obj != null)
				ShowTip((String) msg.obj);
			break;
		case MSG_SHOW_STOK:
				intent = new Intent();
				intent.setClass(UHFMain.this, StokOpnameActivity.class);
				startActivity(intent);
				break;
		default:
			break;
		}
		super.msgProcess(msg);
	}

	// �򿪶�ҳ��
	public void OpenReadActivity(View v) {
		if (isFastClick()) {
			return;
		}

		new Thread() {
			@Override
			public void run() {
				sendMessage(MSG_SHOW_WAIT, getString(R.string.str_loading));
				sendMessage(MSG_ENTER_READ, null);
			};
		}.start();
	}

	public void OpenStokActivity(View v) {
		if (isFastClick()) {
			return;
		}

		new Thread() {
			@Override
			public void run() {
				sendMessage(MSG_SHOW_WAIT, getString(R.string.str_loading));
				sendMessage(MSG_SHOW_STOK, null);
			};
		}.start();
	}

	// ��ƥ���ҳ��
	public void OpenReadMatchingActivity(View v) {
		//
		ShowMsg("Not open��", null);
	}

	// ��дҳ��
	public void OpenWriteActivity(View v) {
		//
		ShowMsg("Not open��", null);
	}

	// ������ҳ��
	public void OpenConfigActivity(View v) {
		//
		ShowMsg("Not open��", null);
	}

	// ��ȡ�汾��
	public void GetVersion(View v) {
		if (isFastClick()) {
			return;
		}

		new Thread() {
			@Override
			public void run() {
				sendMessage(MSG_SHOW_WAIT,
						getString(R.string.str_please_waitting));
				String vStr = "- BaseBand: ";
				if (UHF_Init(false, UHFMain.this)) {
					vStr += CLReader.GetReaderBaseBandSoftVersion();
				} else {
					vStr += "null";
				}
				UHF_Dispose();

				sendMessage(MSG_SHOW_UHF_VER, vStr);
				sendMessage(MSG_HIDE_WAIT, null);
			};
		}.start();

	}

	// �˳�
	public void BackIndex(View v) {
		finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// ����
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.uhf_main);
	}

	@Override
	protected void onDestroy() {
		// �ͷ�
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// �������
		super.onPause();
		sendMessage(MSG_HIDE_WAIT, null);
	}

	@Override
	protected void onResume() {
		// ���ָ�
		super.onResume();
	}

	@Override
	public void OutPutEPC(EPCModel model) {

	}

}
