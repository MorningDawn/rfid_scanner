package com.clouiotech.pda.demo.psam;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.EditText;

import com.clouiotech.pda.demo.BaseActivity;
import com.clouiotech.pda.demoExample.R;
import com.clouiotech.pda.psam.PSAM;
import com.clouiotech.pda.psam.PSAMReader;
import com.clouiotech.util.Helper.Helper_String;

/**
 * @author RFID_C PSAM
 */
public class PsamTestActivity extends BaseActivity {

	private EditText tb_Psam_ack = null;
	private EditText tb_Psam_Apdu = null;
	private EditText tb_Psam_Response = null;
	private boolean busy = false;
	private PSAM psamReader = PSAMReader.getPSAMInstance(0);

	private final int MSG_SHOW_ATR = MSG_USER_BEG + 1;
	private final int MSG_SHOW_RECV = MSG_USER_BEG + 2;

	@Override
	protected void msgProcess(Message msg) {

		switch (msg.what) {
		case MSG_SHOW_ATR:
			String atrString = (String) msg.obj;
			tb_Psam_ack.setText(atrString);

			if (null == atrString) {
				ShowTip(getString(R.string.psam_please_check_card));
			}
			break;
		case MSG_SHOW_RECV:
			String recvString = (String) msg.obj;
			tb_Psam_Response.setText(recvString);

			if (null == recvString) {
				ShowTip(getString(R.string.str_faild));
			}
			break;
		default:
			super.msgProcess(msg);
			break;
		}

	}

	protected void Init() {
		if (!PsamInit()) {
			ShowMsg(getString(R.string.psam_low_power_info),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							finish();
						}
					});
		} else {
			
			tb_Psam_ack = (EditText) this.findViewById(R.id.tb_COM_Recv);
			tb_Psam_Apdu = (EditText) this.findViewById(R.id.tb_Psam_Apdu);
			tb_Psam_Apdu.setText("00,84,00,00,04");
			tb_Psam_Response = (EditText) this
					.findViewById(R.id.tb_Psam_Response);

		}
	}

	protected void DeInit() {
		PsamDispose();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// ����
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.psam_main);
	}

	@Override
	protected void onDestroy() {
		// �ͷ�
		super.onDestroy();
		DeInit();
	}

	@Override
	protected void onPause() {
		// �������
		super.onPause();
		DeInit();
	}

	@Override
	protected void onResume() {
		// ���ָ�
		super.onResume();
		Init();
	}

	// �ϵ�
	public void PowerOn(View v) {
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

				byte[] atr = psamReader.open("9600");
				String atrString = null;

				if (atr != null) {
					atrString = Helper_String.Bytes2String(atr, ",");
				}

				sendMessage(MSG_HIDE_WAIT, null);
				sendMessage(MSG_SHOW_ATR, atrString);

				busy = false;
			};
		}.start();

	}

	// ����
	public void Send(View v) {
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

				String param = tb_Psam_Apdu.getText().toString();

				param = Helper_String.remove(param, ',');
				byte[] apdu = Helper_String.hexStringToBytes(param);
				byte[] recv = psamReader.xfer(apdu);
				String recvString = null;

				if (recv != null) {
					recvString = Helper_String.Bytes2String(recv, ",");
				}
				sendMessage(MSG_SHOW_RECV, recvString);

				sendMessage(MSG_HIDE_WAIT, null);

				busy = false;
			};
		}.start();

	}

	// ������ҳ
	public void Back(View v) {
		finish();
	}

	private boolean PsamInit() {
		return true;
	}

	private void PsamDispose() {
		psamReader.close();
	}

}
