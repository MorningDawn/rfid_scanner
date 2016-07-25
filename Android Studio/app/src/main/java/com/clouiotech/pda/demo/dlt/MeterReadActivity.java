package com.clouiotech.pda.demo.dlt;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.clouiotech.lib.dlt.DLT645Package;
import com.clouiotech.lib.dlt.DLT645_1997;
import com.clouiotech.lib.dlt.DLT645_2007;
import com.clouiotech.pda.com.COM;
import com.clouiotech.pda.com.COMReader;
import com.clouiotech.pda.demo.BaseActivity;
import com.clouiotech.pda.demoExample.R;
import com.clouiotech.util.ByteUtils;
import com.clouiotech.util.Helper.Helper_String;

public class MeterReadActivity extends BaseActivity {
	String TAG = "MR:Read";

	COM com;
	
	ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_SYSTEM,
			ToneGenerator.MAX_VOLUME);

	String[] portStringArray;
	String[] speedStringArray;
	String[] protocolStringArray;
	String[] readItemStringArray;

	byte[][] DL97DI = { { (byte) 0x90, 0x10 },
			{ (byte) 0xb6, 0x11 },
			{ (byte) 0xb6, 0x21 },
			{ (byte) 0x90, (byte) 0x90 },
			{ (byte) 0x94, 0x10 },
			{ (byte) 0xB2, 0x12 },
			{ (byte) 0xB2, 0x10 },
			{ (byte) 0xb6, 0x11 },
			{ (byte) 0xb6, 0x12 },
			{ (byte) 0xb6, 0x13 },
			{ (byte) 0xb6, 0x21 },
			{ (byte) 0xb6, 0x22 },
			{ (byte) 0xb6, 0x23 },
	};

	// 07 Э���Ӧ�ĳ�����DI�б�
	byte[][] DL07DI = { { 0x00, 0x01, 0x00, 0x00 }, // ����
			{ 0x02, 0x01, 0x01, 0x00 }, // ��ѹ
			{ 0x02, 0x02, 0x01, 0x00 }, // ����
			{ 0x05, 0x04, 0x01, 0x01 }, // �����
			{ 0x00, 0x01, 0x00, 0x01 }, // �����
			{ 0x03, 0x30, 0x00, 0x00 }, // ��̴���
			{ 0x03, 0x30, 0x00, 0x01 }, // �ϴα�̼�¼
			{ 0x02, 0x01, 0x01, 0x00 }, // ��ѹA
			{ 0x02, 0x01, 0x02, 0x00 }, // ��ѹB
			{ 0x02, 0x01, 0x03, 0x00 }, // ��ѹC
			{ 0x02, 0x02, 0x01, 0x00 }, // ����A
			{ 0x02, 0x02, 0x02, 0x00 }, // ����B
			{ 0x02, 0x02, 0x03, 0x00 }, // ����C
	};

	private boolean busy = false; // æ��־
	private int speed;
	private int protocol;
	private int readItem;
	private byte[] addr;

	private static int RETRY = 2;
	private static int TIMOUT = 1500;

	static final int PROTOCOL_DL1997 = 0;
	static final int PROTOCOL_DL2007 = 1;

	static final int SPEED_1200 = 0;
	static final int SPEED_2400 = 1;

	static final int MSG_UPDATE_ADDR = MSG_USER_BEG + 1;
	static final int MSG_UPDATE_RESULT = MSG_USER_BEG + 2;

	@Override
	protected void msgProcess(Message msg) {
		switch (msg.what) {
		case MSG_UPDATE_ADDR:
			((TextView) findViewById(R.id.txtMeterAddr))
					.setText((String) msg.obj);
			break;
		case MSG_UPDATE_RESULT:
			String result = (String) msg.obj;
			new AlertDialog.Builder(MeterReadActivity.this)
					.setTitle(getString(R.string.read_result))
					.setMessage(getString(R.string.read_result) + ": " + result)
					.setNegativeButton(getString(R.string.Back),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {

								}
							}).create().show();
		default:
			super.msgProcess(msg);
			break;
		}
	};

	/**
	 * �ӽ����ȡ���ַ
	 * 
	 * @return ���ַ
	 */
	public byte[] getAddress() {
		EditText txtAddrText = (EditText) findViewById(R.id.txtMeterAddr);
		String strAddr = txtAddrText.getText().toString();

		byte[] inputAddr = Helper_String.hexStringToBytes(strAddr);

		if (null == inputAddr) {
			return null;
		}

		return inputAddr;
	}

	boolean syncConfiure(boolean syncForRead) {
		int portindex;
		int speedindex;

		// �˿�
		portindex = ((Spinner) findViewById(R.id.spinPort))
				.getSelectedItemPosition();

		// ����
		speedindex = ((Spinner) findViewById(R.id.spinSpeed))
				.getSelectedItemPosition();

		// ��ȡ��ȡЭ��
		protocol = ((Spinner) findViewById(R.id.spinProtocol))
				.getSelectedItemPosition();

		readItem = ((Spinner) findViewById(R.id.spinMeterItem))
				.getSelectedItemPosition();

		if (1 == speedindex) { // 2400
			speed = 2400;
		} else { // 1200
			speed = 1200;
		}

		if (com != null) {
			com.close();
		}

		if (portindex == 1) {
			com = COMReader.getCOMInstance("RS485");
		} else {
			com = COMReader.getCOMInstance("IRDA");
		}

		String comParam = "";
		comParam += speed;
		comParam += ":E:8:1";

		com.open(comParam);

		if (syncForRead) {
			// ��ȡ���ַ
			addr = getAddress();
			if (null == addr) {

				ShowTip(getString(R.string.please_input_valid_addr));

				return false;
			}
		}

		return true;
	}

	private byte[] trasmit(COM com, byte[] send, int offset, int length) {
		int wait = 100;

		try {
			com.send(send, offset, length);
			Thread.sleep((11 * length * 1000 / speed) + 1);
			Thread.sleep(20);
			com.clear_input();

			byte[] recv = new byte[1024];
			int recvlen = 0;
			for (int i = 0; i < TIMOUT;) {
				Thread.sleep(wait);

				int ava = com.recv(recv, recvlen, recv.length - recvlen);
				if (ava > 0) {
					recvlen += ava;

					if (recvlen >= recv.length) {
						return recv;
					}
				} else {
					i += wait;
					if (recvlen > 0) {
						break;
					}
				}
			}

			if (recvlen <= 0) {
				return null;
			}

			byte[] ret = new byte[recvlen];
			System.arraycopy(recv, 0, ret, 0, recvlen);

			return ret;
		} catch (Exception e) {
			return null;
		}
	}

	private byte[] trasmit(COM com, byte[] send) {
		return trasmit(com, send, 0, send.length);
	}

	private boolean xferPackage(DLT645Package dl645) {
		byte[] send = dl645.build();
		byte[] recv;

		int retryTime = 0;
		for (retryTime = 0; retryTime < RETRY; retryTime++) {
			recv = trasmit(com, send);
			if (null == recv) {
				continue;
			}

			if (dl645.prase(recv)) {
				break;
			}
		}

		if (retryTime >= RETRY) {
			return false;
		}

		return true;
	}

	/**
	 * �������Ӧ
	 */
	private void onReadClick() {

		if (busy) { // ��æ
			ShowTip(getString(R.string.str_busy));
			return;
		}

		if (!syncConfiure(true)) {
			return;
		}

		new Thread() {
			@Override
			public void run() {

				sendMessage(MSG_SHOW_WAIT, getString(R.string.read_waiting));

				// ��DL645��
				DLT645Package dl645;
				if (protocol == 0) {
					dl645 = new DLT645_1997();
					dl645.resetData();
					dl645.appendDataReverse(DL97DI[readItem]);
				} else {
					dl645 = new DLT645_2007();
					dl645.resetData();
					dl645.appendDataReverse(DL07DI[readItem]);
				}

				dl645.setAddress(addr);
				dl645.setCmd(dl645.CMD_READ);

				boolean success = xferPackage(dl645);
				String result = null;
				if ((!success)
						|| ((dl645.getCmd() & DLT645Package.CMD_ERROR) != 0)) {
					result = null;
				} else {

					byte[] cont = dl645.getData();
					ByteUtils.reverse(cont, 0, dl645.nDI); // DI

					float valFloat = 0;
					int valInt = 0;

					// ת����ݸ�ʽ
					switch (readItem) {
					case 0: // ����
					case 3: // �����
					case 4: // �����
						if ((dl645.getDataLen() < (dl645.nDI + 4))) {
							result = getString(R.string.read_faild);
							break;
						}
						ByteUtils.reverse(cont, dl645.nDI, 4);
						result = Helper_String.Bytes2String(cont, dl645.nDI, 4);
						valInt = Integer.parseInt(result);
						valFloat = (float) (valInt * 0.01);
						result = "" + valFloat + "kWh";
						break;
					case 1: // ��ѹ��
					case 7:
					case 8:
					case 9:
						ByteUtils.reverse(cont, dl645.nDI, cont.length
								- dl645.nDI);
						result = Helper_String.Bytes2String(cont, dl645.nDI,
								cont.length - dl645.nDI);
						valInt = Integer.parseInt(result);
						if (0 == protocol) {
							valFloat = (valInt);
							result = "" + valFloat;
						} else {
							valFloat = (float) (valInt * 0.1);
							result = "" + valFloat;
						}
						result += "V";
						break;
					case 2: // ����
					case 10:
					case 11:
					case 12:
						ByteUtils.reverse(cont, dl645.nDI, cont.length
								- dl645.nDI);
						result = Helper_String.Bytes2String(cont, dl645.nDI,
								cont.length - dl645.nDI);
						valInt = Integer.parseInt(result);
						if (0 == protocol) {
							valFloat = (float) (valInt * 0.001);
							result = "" + valFloat;
						} else {
							valFloat = (float) (valInt * 0.001);
							result = "" + valFloat;
						}
						result += "A";
						break;

					case 5: // ��̴���
						ByteUtils.reverse(cont, dl645.nDI, cont.length
								- dl645.nDI);
						result = Helper_String.Bytes2String(cont, dl645.nDI,
								cont.length - dl645.nDI);
						valInt = Integer.parseInt(result);
						result = "" + Integer.parseInt(result);
						break;
					case 6: // �ϴα�̼�¼

						if (0 == protocol) {
							ByteUtils.reverse(cont, dl645.nDI, 4);

							result = Helper_String.Bytes2String(cont,
									dl645.nDI, 1, "-")
									+ Helper_String.Bytes2String(cont,
											dl645.nDI + 1, 1, " ")
									+ Helper_String.Bytes2String(cont,
											dl645.nDI + 2, 1, ":")
									+ Helper_String.Bytes2String(cont,
											dl645.nDI + 3, 1);
						} else {
							ByteUtils.reverse(cont, dl645.nDI, 6);

							result = Helper_String.Bytes2String(cont,
									dl645.nDI, 2, "-")
									+ Helper_String.Bytes2String(cont,
											dl645.nDI + 2, 1, " ")
									+ Helper_String.Bytes2String(cont,
											dl645.nDI + 3, 2, ":")
									+ Helper_String.Bytes2String(cont,
											dl645.nDI + 5, 1);
						}
						break;

					default:
						result = null;
						break;
					}
				}

				if (null == result) {
					ShowTip(getString(R.string.read_faild));
					toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP2);
				} else {
					sendMessage(MSG_UPDATE_RESULT, result);
					toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP);
				}

				sendMessage(MSG_HIDE_WAIT, null);
				busy = false;
			};
		}.start();

	}

	/**
	 * F1����
	 */
	private void onF1Click() {
		// ��æ
		if (busy) {
			ShowTip(getString(R.string.str_busy));
			return;
		}

		if (!syncConfiure(false)) {
			return;
		}

		new Thread() {
			@Override
			public void run() {

				sendMessage(MSG_SHOW_WAIT, getString(R.string.read_waiting));

				// ��DL645��
				DLT645Package dl645;
				if (protocol == PROTOCOL_DL1997) {
					dl645 = new DLT645_1997();
				} else {
					dl645 = new DLT645_2007();
				}

				dl645.buildReadAddress();

				boolean success = xferPackage(dl645);

				if ((!success)
						|| ((dl645.getCmd() & DLT645Package.CMD_ERROR) != 0)) {
					ShowTip(getString(R.string.read_faild));
					toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP2);
				} else {
					byte[] address = dl645.getAddress();
					String addr = Helper_String.Bytes2String(address);
					sendMessage(MSG_UPDATE_ADDR, addr);
					ShowTip(getString(R.string.read_success));
					toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP);
				}

				sendMessage(MSG_HIDE_WAIT, null);
				busy = false;
			}
		}.start();
		;

	}

	/**
	 * �˳�����
	 */

	private void onBackClick() {
		MeterReadActivity.this.finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dlt_meter_op);
		// irda.enablePower(false);
		// irda.enablePower(true);
		// irda.connect();
		// ��ʼ������
		portStringArray = new String[] { getString(R.string.MeterPortIRDA),
				getString(R.string.MeterPortRS485) };
		speedStringArray = new String[] { getString(R.string.MeterSpeed1200),
				getString(R.string.MeterSpeed2400) };
		protocolStringArray = new String[] {
				getString(R.string.MeterProtocol97),
				getString(R.string.MeterProtocol07), };
		readItemStringArray = new String[] {
				getString(R.string.MeterReadItemPower),
				getString(R.string.MeterReadItemVolt),
				getString(R.string.MeterReadItemAmp),
				getString(R.string.MeterReadItemLastDay),
				getString(R.string.MeterReadItemLastMonth),
				getString(R.string.MeterReadItemProgNum),
				getString(R.string.MeterReadItemLastProgRec),
				getString(R.string.MeterReadItemVoltA),
				getString(R.string.MeterReadItemVoltB),
				getString(R.string.MeterReadItemVoltC),
				getString(R.string.MeterReadItemAmpA),
				getString(R.string.MeterReadItemAmpB),
				getString(R.string.MeterReadItemAmpC), };

		// ��ʼ��ArrayAdapter
		ArrayAdapter<String> portAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, portStringArray);

		ArrayAdapter<String> speedAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, speedStringArray);

		ArrayAdapter<String> protocolAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, protocolStringArray);

		ArrayAdapter<String> readItemAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, readItemStringArray);

		// ����SPinner
		((Spinner) findViewById(R.id.spinPort)).setAdapter(portAdapter);
		((Spinner) findViewById(R.id.spinSpeed)).setAdapter(speedAdapter);
		((Spinner) findViewById(R.id.spinProtocol)).setAdapter(protocolAdapter);
		((Spinner) findViewById(R.id.spinMeterItem))
				.setAdapter(readItemAdapter);

		((Spinner) findViewById(R.id.spinProtocol)).setSelection(1); // Ĭ��07
		((Spinner) findViewById(R.id.spinSpeed)).setSelection(0);

		// ���ز���Ҫ�����
		((TextView) findViewById(R.id.lblMeterVal)).setVisibility(View.GONE);
		((TextView) findViewById(R.id.txtMeterVal)).setVisibility(View.GONE);
		((TextView) findViewById(R.id.lblMeterOpcode)).setVisibility(View.GONE);
		((TextView) findViewById(R.id.txtMeterOpcode)).setVisibility(View.GONE);
		((TextView) findViewById(R.id.lblMeterPasswd)).setVisibility(View.GONE);
		((TextView) findViewById(R.id.txtMeterPasswd)).setVisibility(View.GONE);

		// ��ȡ��
		Button btnOkButton = ((Button) findViewById(R.id.btnMeterOK));
		btnOkButton.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				onReadClick();

			}

		});
		btnOkButton.requestFocus();

		// �˳���
		((Button) findViewById(R.id.btnMeterBack))
				.setOnClickListener(new Button.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						onBackClick();

					}

				});

		((TextView) findViewById(R.id.txtMeterOpTitle))
				.setText(R.string.STR_METER_READ);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			if (keyCode == KeyEvent.KEYCODE_F9) { // F1����ַ
				onF1Click();
				return true;
			}

		} else { // ����������ù��ܼ�

		}

		return super.onKeyDown(keyCode, event);

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub

		if (com != null) {
			com.close();
		}

		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

}
