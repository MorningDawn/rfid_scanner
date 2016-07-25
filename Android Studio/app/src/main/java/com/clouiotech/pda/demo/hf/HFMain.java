package com.clouiotech.pda.demo.hf;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.clouiotech.pda.demo.BaseActivity;
import com.clouiotech.pda.demoExample.R;
import com.clouiotech.pda.rfid.EPCModel;
import com.clouiotech.pda.rfid.IAsynchronousMessage;
import com.clouiotech.port.Adapt;

public class HFMain extends BaseActivity implements IAsynchronousMessage {

	boolean busy = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.hf_main);
	}

	/**
	 */
	public void OpenISO15693Activity(View v) {
		Intent intent = new Intent();
		intent.setClass(HFMain.this, ReadISO15693Activity.class);
		startActivity(intent);
	}

	/**
	 * ��ISO14443A
	 */
	public void OpenISO14443AActivity(View v) {
		//
		ShowMsg("Not open��", null);
	}

	/**
	 * ��Mifare
	 */
	public void OpenMifareActivity(View v) {
		//
		ShowMsg("Not open��", null);
	}

	/**
	 * ��ȡandroid�ֻ����к�
	 */
	public void GetSerialNumber(View v) {
		String serial = null;
		serial = Adapt.getPropertiesInstance().getSN();
		ShowMsg(serial, null);
	}

	// �˳�
	public void Back(View v) {
		HFMain.this.finish();
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
