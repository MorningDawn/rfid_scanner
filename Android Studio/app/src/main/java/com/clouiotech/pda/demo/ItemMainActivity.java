package com.clouiotech.pda.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.clouiotech.pda.demo.dlt.MenuActivity;
import com.clouiotech.pda.demo.hf.HFMain;
import com.clouiotech.pda.demo.psam.PsamTestActivity;
import com.clouiotech.pda.demo.scanner.ScannerActivity;
import com.clouiotech.pda.demo.uhf.UHFMain;
import com.clouiotech.pda.demoExample.R;
import com.clouiotech.port.Adapt;

public class ItemMainActivity extends BaseActivity {

	Button btn_UHF = null;
	Button btn_PSAM = null;
	Button btn_SCAN = null;
	Button btn_Vison = null;
	Button btn_SerialNumber = null;
	Button btn_DLT = null;
	Button btn_Exit = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// ����
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.item_main);
		btn_UHF = (Button) findViewById(R.id.btn_Item_Main_UHF);
		btn_PSAM = (Button) findViewById(R.id.btn_Item_Main_Psam);
		btn_SCAN = (Button) findViewById(R.id.btn_Item_Main_Scanner);
		btn_Vison = (Button) findViewById(R.id.btn_Item_Main_Version);
		btn_SerialNumber = (Button) findViewById(R.id.btn_Item_Main_SerialNumber);
		btn_Exit = (Button) findViewById(R.id.btn_Item_Main_Exit);
		btn_DLT = (Button) findViewById(R.id.btn_Item_Main_DLT);

		if (Adapt.getPropertiesInstance().support("HF")) {
			btn_UHF.setText(R.string.btn_MainMenu_HF);
		}

		if (!Adapt.getPropertiesInstance().support("UHF")
				&& !Adapt.getPropertiesInstance().support("HF")) {
			btn_UHF.setVisibility(View.GONE);
		}

		if (!Adapt.getPropertiesInstance().support("PSAM")) {
			btn_PSAM.setVisibility(View.GONE);
		}

		if (!Adapt.getPropertiesInstance().support("1DSCANNER")
				&& !Adapt.getPropertiesInstance().support("2DScanner")) {
			btn_SCAN.setVisibility(View.GONE);
		}

		if (!Adapt.getPropertiesInstance().support("IRDA")
				&& !Adapt.getPropertiesInstance().support("RS232")
				&& !Adapt.getPropertiesInstance().support("RS485")
				&& !Adapt.getPropertiesInstance().support("ESAM")) {
			//btn_DLT.setVisibility(View.GONE);
		}

	}

	@Override
	protected void onPause() {
		DisposeAll();
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		DisposeAll();
		super.onDestroy();
	}

	/**
	 * �򿪳���ƵDEMO
	 */
	public void OpenUHFDemo(View v) {
		if (isFastClick()) {
			return;
		}

		Intent intent = new Intent();
		if (Adapt.getPropertiesInstance().support("HF")) {
			intent.setClass(this, HFMain.class);
		} else {
			intent.setClass(ItemMainActivity.this, UHFMain.class);
		}
		startActivity(intent);

	}

	/**
	 * ��PsamDEMO
	 */
	public void OpenPsamDemo(View v) {
		if (isFastClick()) {
			return;
		}

		Intent intent = new Intent();
		intent.setClass(ItemMainActivity.this, PsamTestActivity.class);
		startActivity(intent);
	}

	public void openDLTDemo(View v) {
		Intent intent = new Intent();
		intent.setClass(ItemMainActivity.this, MenuActivity.class);
		startActivity(intent);
	}

	/**
	 * �򿪳���άͷDEMO
	 */
	public void OpenScannerDemo(View v) {
		if (isFastClick()) {
			return;
		}

		Intent intent = new Intent();
		intent.setClass(ItemMainActivity.this, ScannerActivity.class);
		startActivity(intent);

	}

	boolean srun = false;

	public void GetVersion(View v) {
		if (isFastClick()) {
			return;
		}

		boolean mcuSupport = false;
		String mcuModel = null;

		try {
			mcuSupport = Adapt.getPropertiesInstance().support("MCU");
			mcuModel = Adapt.getPropertiesInstance().getDeviceModel("MCU");

		} catch (Exception e) {
		}

		//int soc = Adapt.getPowermanagerInstance().getPowerSOC();
		//int bsoc = Adapt.getPowermanagerInstance().getBackupPowerSOC();

		if (!mcuSupport) {// K3��G3��֧��MCU
			super.ShowTip("- APP:" + getVersion() + " -");
		} else {
			super.ShowMsg("- APP:" + getVersion() + " -" + "\n- MCU:"
					+ mcuModel + " -"
					/*+ "\n- " + "MBat:" + soc + "-ABat:" + bsoc
					+ " -"*/, null);
		}
	}

	/**
	 * ��ȡandroid�ֻ����к�
	 */
	public void GetSerialNumber(View v) {
		if (isFastClick()) {
			return;
		}
		String serial = Adapt.getPropertiesInstance().getSN();
		ShowMsg(serial, null);

		srun = false;
	}

	/**
	 * �˳�Ӧ��
	 */
	public void Exit(View v) {
		if (isFastClick()) {
			return;
		}
		DisposeAll();
		ItemMainActivity.this.finish();
		System.exit(0);
	}

	/**
	 * �ͷ����ж���
	 */
	public void DisposeAll() {
		try {
			// ClouScanner.Release(); // �ͷŶ�άͷ�ӿڶ���
		} catch (Exception ex) {
		}
	}

}
