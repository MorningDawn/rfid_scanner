package com.clouiotech.pda.demo.hf;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import com.clouiotech.pda.demo.BaseActivity;
import com.clouiotech.pda.demoExample.R;
import com.clouiotech.pda.rfid.EPCModel;
import com.clouiotech.pda.rfid.IAsynchronousMessage;
import com.clouiotech.pda.rfid.hf.HF;
import com.clouiotech.pda.rfid.hf.HFReader;

public class ReadISO15693Activity extends BaseActivity implements
		IAsynchronousMessage {

	boolean busy = false;
	private int msgType = MSG_UPDATE_UID;

	private EditText tb_iso15693_AFI = null;
	private EditText tb_iso15693_DSFID = null;
	private EditText tb_iso15693_WriteData = null;
	private EditText tb_Access_Uid = null;
	private Spinner sp_Write_Block = null;

	HF hfReader = HFReader.getHFInstance();

	static final int MSG_UPDATE_UID = MSG_USER_BEG + 1;
	static final int MSG_UPDATE_READ_DATA = MSG_USER_BEG + 2;
	static final int MSG_UPDATE_CARD_INFO = MSG_USER_BEG + 3;

	@Override
	protected void msgProcess(Message msg) {

		switch (msg.what) {
		case MSG_UPDATE_UID:
			ShowUID(msg.obj.toString());
			break;
		case MSG_UPDATE_READ_DATA:
			ShowReadData(msg.obj.toString());
			break;
		case MSG_UPDATE_CARD_INFO:
			ShowCardInfo(msg.obj.toString());
			break;
		default:
			super.msgProcess(msg);
			break;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.hf_iso15693);
	}

	// ɨ���ǩ���������к�
	public void ReadUID(View v) {
		msgType = MSG_UPDATE_UID;
		tb_Access_Uid.setText("");
		hfReader.GetISO15693SN("00", "00");
	}

	/*
	 * ����UID
	 */
	public void ShowUID(String uid) {
		tb_Access_Uid.setText(uid);
	}

	// ����
	public void Read(View v) {
		tb_iso15693_WriteData.setText("");
		msgType = MSG_UPDATE_READ_DATA;
		String setParam = "00";
		if (sp_Write_Block != null) {
			setParam = sp_Write_Block.getSelectedItem().toString();
		}
		String data = hfReader.ReadISO15693("00", setParam, "00");

		if (data.equals("")) {
			return;
		}
		if (data.contains("|")) {
			ShowMsg(data, null);
		} else {
			tb_iso15693_WriteData.setText(data.substring(10, data.length()));
		}
	}

	/*
	 * ���������
	 */
	public void ShowReadData(String blockData) {

		tb_iso15693_WriteData.setText(blockData);
	}

	// д��
	public void Write(View v) {
		String setParam1 = "00";
		if (sp_Write_Block != null) {
			setParam1 = sp_Write_Block.getSelectedItem().toString();
		}
		String setParam2 = "11223344";
		if (tb_iso15693_WriteData != null) {
			setParam2 = tb_iso15693_WriteData.getText().toString();
		}
		String data = hfReader.WriteISO15693("00", setParam1, "00", setParam2);
		ShowMsg(data, null);
	}

	// ��ȡ����Ϣ
	public void GetInfo(View v) {
		tb_iso15693_WriteData.setText("");
		msgType = MSG_UPDATE_CARD_INFO;
		String data = hfReader.ReadISO15693("01", "", "");
		//
		if (data.equals("")) {
			return;
		}
		if (data.contains("|")) {
			ShowMsg(data, null);
		} else {
			tb_iso15693_WriteData.setText(data.substring(8, data.length()));
		}
	}

	/*
	 * ������ϸ��Ϣ
	 */
	public void ShowCardInfo(String cardInfo) {

		tb_iso15693_WriteData.setText(cardInfo);
	}

	// дAFI
	public void writeAFI(View v) {
		@SuppressWarnings("unused")
		String setParam1 = "00";
		if (tb_iso15693_AFI != null) {
			setParam1 = tb_iso15693_AFI.getText().toString();
		}
		String data = hfReader.WriteISO15693("01", "", "", "01");
		ShowMsg(data, null);
	}

	// ��AFI
	public void lockAFI(View v) {
		String data = hfReader.LockISO15693("01", "");
		ShowMsg(data, null);
	}

	// дDSFID
	public void writeDSFID(View v) {
		@SuppressWarnings("unused")
		String setParam1 = "00";
		if (tb_iso15693_DSFID != null) {
			setParam1 = tb_iso15693_DSFID.getText().toString();
		}
		String data = hfReader.WriteISO15693("02", "", "", "01");
		ShowMsg(data, null);
	}

	// ��DSFID
	public void lockDSFID(View v) {
		String data = hfReader.LockISO15693("02", "");
		ShowMsg(data, null);
	}

	// �˳�
	public void Back(View v) {
		ReadISO15693Activity.this.finish();
	}

	protected void Init() {
		if (!hfReader.OpenConnect(this)) { // ��ģ���Դʧ��
			ShowMsg(getString(R.string.uhf_low_power_info),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							ReadISO15693Activity.this.finish();
						}
					});
		} else {
			sp_Write_Block = (Spinner) this.findViewById(R.id.sp_Write_Block);
			tb_Access_Uid = (EditText) this.findViewById(R.id.tb_Access_Uid);
			tb_iso15693_WriteData = (EditText) this
					.findViewById(R.id.tb_iso15693_WriteData);
			tb_iso15693_DSFID = (EditText) this
					.findViewById(R.id.tb_iso15693_DSFID);
			tb_iso15693_AFI = (EditText) this
					.findViewById(R.id.tb_iso15693_AFI);
		}
	}

	protected void Dispose() {
		hfReader.CloseConnect();
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
		Dispose();
	}

	@Override
	protected void onResume() {
		// ���ָ�
		super.onResume();
		Init();
	}

	@Override
	public void OutPutEPC(EPCModel model) {
		try {
			sendMessage(msgType, model._EPC);
		} catch (Exception ex) {
			Log.d("Debug", "����ǩ���к��쳣��" + ex.getMessage());
		}
	}

}
