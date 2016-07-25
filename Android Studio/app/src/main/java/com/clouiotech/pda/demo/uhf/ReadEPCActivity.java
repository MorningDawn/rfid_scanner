package com.clouiotech.pda.demo.uhf;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.clouiotech.pda.demo.PublicData;
import com.clouiotech.pda.demoExample.R;
import com.clouiotech.pda.rfid.EPCModel;
import com.clouiotech.pda.rfid.IAsynchronousMessage;
import com.clouiotech.port.Adapt;
import com.clouiotech.util.Helper.Helper_ThreadPool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author RFID_lx ����ǩ����
 */
public class ReadEPCActivity extends UHFBaseActivity implements
		IAsynchronousMessage {

	private ListView listView = null; // ����б����
	private SimpleAdapter sa = null;
	private Button btn_Read = null;
	private TextView tv_TitleTagID = null;
	private TextView lb_ReadTime = null;
	private TextView lb_ReadSpeed = null;
	private TextView lb_TagCount = null;
	private Spinner sp_ReadType = null;
	private static boolean isStartPingPong = false; //
	private boolean isKeyDown = false; // ����Ƿ���
	private boolean isLongKeyDown = false; // ����Ƿ��ǳ���״̬
	private int keyDownCount = 0; // ����´���

	private int readTime = 0;
	private int lastReadCount = 0;
	private int totalReadCount = 0; // �ܶ�ȡ����
	private int speed = 0; // ��ȡ�ٶ�
	private static int _ReadType = 0; // 0 Ϊ��EPC��1 Ϊ��TID
	private static String _NowReadParam = _NowAntennaNo + "|1"; // ����ǩ����
	private HashMap<String, EPCModel> hmList = new HashMap<String, EPCModel>();
	private Object hmList_Lock = new Object();
	private boolean flag = true; //
	private Boolean IsFlushList = true; // �Ƿ�ˢ�б�
	private Object beep_Lock = new Object();
	ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_SYSTEM,
			ToneGenerator.MAX_VOLUME);

	private static boolean isPowerLowShow = false;

	private boolean usingBackBattery = false;;

	private final int MSG_RESULT_READ = MSG_USER_BEG + 1; // ������
	private final int MSG_FLUSH_READTIME = MSG_USER_BEG + 2;
	private final int MSG_UHF_POWERLOW = MSG_USER_BEG + 3;

	@Override
	protected void msgProcess(Message msg) {
		switch (msg.what) {
		case MSG_RESULT_READ:
			ShowList(); // ˢ���б�
			break;
		case MSG_FLUSH_READTIME:
			if (lb_ReadTime != null) { // ˢ�¶�ȡʱ��
				readTime++;
				lb_ReadTime.setText("Time:" + readTime + "S");
			}
			break;
		case MSG_UHF_POWERLOW:
			ShowPowerLow();
			break;
		default:
			super.msgProcess(msg);
			break;
		}
	}

	// ������
	public void Read(View v) {
		Button btnRead = (Button) v;
		String controlText = btnRead.getText().toString();
		if (controlText.equals(getString(R.string.btn_read))) {
			PingPong_Read();
			btnRead.setText(getString(R.string.btn_read_stop));
			sp_ReadType.setEnabled(false);
		} else {
			Pingpong_Stop();
			btnRead.setText(getString(R.string.btn_read));
			sp_ReadType.setEnabled(true);
		}
	}

	// ��Ъ�Զ�
	public void PingPong_Read() {
		if (isStartPingPong)
			return;
		isStartPingPong = true;
		Clear(null);
		Helper_ThreadPool.ThreadPool_StartSingle(new Runnable() {
			@Override
			public void run() {
				while (isStartPingPong) {
					try {
						if (!isPowerLowShow) {
							if (usingBackBattery && !canUsingBackBattery()) {
								sendMessage(MSG_UHF_POWERLOW, null);
							}

							if (PublicData._IsCommand6Cor6B.equals("6C")) {// ��6C��ǩ
								CLReader.Read_EPC(_NowReadParam);
							} else {// ��6B��ǩ
								CLReader.Get6B(_NowAntennaNo + "|1" + "|1"
										+ "|" + "1,000F");
							}

							Thread.sleep(PublicData._PingPong_ReadTime);

							if (PublicData._PingPong_StopTime > 0) {
								CLReader.Stop();
								Thread.sleep(PublicData._PingPong_StopTime);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});

	}

	// ֹͣ��Ъ�Զ�
	public void Pingpong_Stop() {
		isStartPingPong = false;
		CLReader.Stop();
	}

	public void Clear(View v) {
		totalReadCount = 0;
		readTime = 0;
		hmList.clear();
		ShowList();
	}

	// ������ҳ
	public void Back(View v) {
		if (btn_Read.getText().toString()
				.equals(getString(R.string.btn_read_stop))) {
			ShowMsg(getString(R.string.uhf_please_stop), null);
			return;
		}
		ReadEPCActivity.this.finish();
	}

	protected void Init() {
		usingBackBattery = canUsingBackBattery();
		if (!UHF_Init(usingBackBattery, this)) { // ��ģ���Դʧ��
			ShowMsg(getString(R.string.uhf_low_power_info),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							ReadEPCActivity.this.finish();
						}
					});
		} else {
			try {
				super.UHF_GetReaderProperty(); // ��ö�д��������
				_NowReadParam = _NowAntennaNo + "|1";
				Thread.sleep(20);
				CLReader.Stop(); // ָֹͣ��
				Thread.sleep(20);
				super.UHF_SetTagUpdateParam(); // ���ñ�ǩ�ظ��ϴ�ʱ��Ϊ20ms
			} catch (Exception ee) {
			}
			IsFlushList = true;
			// ˢ���߳�
			Helper_ThreadPool.ThreadPool_StartSingle(new Runnable() {
				@Override
				public void run() {
					while (IsFlushList) {
						try {
							sendMessage(MSG_RESULT_READ, null);
							Thread.sleep(1000); // һ����ˢ��һ��
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			});

			Helper_ThreadPool.ThreadPool_StartSingle(new Runnable() { // ����������
						@Override
						public void run() {
							while (IsFlushList) {
								synchronized (beep_Lock) {
									try {
										beep_Lock.wait();
									} catch (InterruptedException e) {
									}
								}
								if (IsFlushList) {
									toneGenerator
											.startTone(ToneGenerator.TONE_PROP_BEEP);
								}

							}
						}
					});

			listView = (ListView) this.findViewById(R.id.lv_Main);
			tv_TitleTagID = (TextView) findViewById(R.id.tv_TitleTagID);
			lb_ReadTime = (TextView) findViewById(R.id.lb_ReadTime);
			lb_ReadSpeed = (TextView) findViewById(R.id.lb_ReadSpeed);
			lb_TagCount = (TextView) findViewById(R.id.lb_TagCount);
			btn_Read = (Button) findViewById(R.id.btn_Read);
			btn_Read.setText(getString(R.string.btn_read));
			sp_ReadType = (Spinner) findViewById(R.id.sp_ReadType);
			//

			sp_ReadType
					.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> arg0,
								View arg1, int arg2, long arg3) {
							if (isStartPingPong)
								return;
							int selectItem = sp_ReadType
									.getSelectedItemPosition();
							if (PublicData._IsCommand6Cor6B.equals("6C")) {// ��6C��ǩ
								if (selectItem == 0) {
									_ReadType = 0;
									_NowReadParam = _NowAntennaNo + "|1";
									tv_TitleTagID.setText("EPC");
								} else if (selectItem == 1) {
									_ReadType = 1;
									_NowReadParam = _NowAntennaNo + "|1|2,0006";
									tv_TitleTagID.setText("TID");
								} else if (selectItem == 2) {
									_ReadType = 2;
									_NowReadParam = _NowAntennaNo
											+ "|1|3,000006";
									tv_TitleTagID.setText("UserData");
								}
							} else {
								if (selectItem == 0) {
									_ReadType = 0;
									tv_TitleTagID.setText("EPC");
								} else if (selectItem == 1) {
									_ReadType = 1;
									tv_TitleTagID.setText("TID");
								} else if (selectItem == 2) {
									_ReadType = 2;
									tv_TitleTagID.setText("UserData");
								}
							}

						}

						@Override
						public void onNothingSelected(AdapterView<?> arg0) {

						}

					});

		}
		return;
	}

	// �ͷ���Դ
	protected void Dispose() {
		isStartPingPong = false;
		IsFlushList = false;
		synchronized (beep_Lock) {
			beep_Lock.notifyAll();
		}
		UHF_Dispose();
	}

	protected void ShowList() {
		if (!isStartPingPong)
			return;
		sa = new SimpleAdapter(this, GetData(), R.layout.epclist_item,
				new String[] { "EPC", "ReadCount" }, new int[] {
						R.id.EPCList_TagID, R.id.EPCList_ReadCount });
		listView.setAdapter(sa);
		listView.invalidate();
		if (lb_ReadTime != null) { // ˢ�¶�ȡʱ��
			readTime++;
			lb_ReadTime.setText("Time:" + readTime / 1 + "S");
		}
		if (lb_ReadSpeed != null) { // ˢ�¶�ȡ�ٶ�
			if (flag) {
				speed = totalReadCount - lastReadCount;
				if (speed < 0)
					speed = 0;
				lastReadCount = totalReadCount;
				if (lb_ReadSpeed != null) {
					lb_ReadSpeed.setText("SP:" + speed + "T/S");
				}
			}
			// flag = !flag;
		}
		if (lb_TagCount != null) { // ˢ�±�ǩ����
			lb_TagCount.setText("Total:" + hmList.size());
		}
	}

	// ��ø������Դ
	@SuppressWarnings({ "rawtypes", "unused" })
	protected List<Map<String, Object>> GetData() {
		List<Map<String, Object>> rt = new ArrayList<Map<String, Object>>();
		synchronized (hmList_Lock) {
			// if(hmList.size() > 0){ //
			Iterator iter = hmList.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				String key = (String) entry.getKey();
				EPCModel val = (EPCModel) entry.getValue();
				Map<String, Object> map = new HashMap<String, Object>();
				if (_ReadType == 0) {
					map.put("EPC", val._EPC);
				} else if (_ReadType == 1) {
					map.put("EPC", val._TID);
				} else {
					map.put("EPC", val._UserData);
				}
				map.put("ReadCount", val._TotalCount);
				rt.add(map);
			}
			// }
		}
		return rt;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// ����
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		this.setContentView(R.layout.uhf_read);
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
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.d("CL7202K3", "onKeyDown keyCode = " + keyCode);
		if (keyCode == 131 || keyCode == 135) { // ���°��
			btn_Read.setText(getString(R.string.btn_read_stop));
			sp_ReadType.setEnabled(false);
			btn_Read.setClickable(false);
			if (!isKeyDown) {
				isKeyDown = true; //
				if (!isStartPingPong) {
					Clear(null);
					Pingpong_Stop(); // ֹͣ��Ъ�Զ�
					isStartPingPong = true;
					CLReader.Read_EPC(_NowReadParam);
					if (PublicData._IsCommand6Cor6B.equals("6C")) {// ��6C��ǩ
						CLReader.Read_EPC(_NowReadParam);
					} else {// ��6B��ǩ
						CLReader.Get6B(_NowAntennaNo + "|1" + "|1" + "|"
								+ "1,000F");
					}
				}
			} else {
				if (keyDownCount < 10000)
					keyDownCount++;
			}
			if (keyDownCount > 100) {
				isLongKeyDown = true;
			}
			if (isLongKeyDown) { // �����¼�

			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		Log.d("CL7202K3", "onKeyUp keyCode = " + keyCode);
		if (keyCode == 131 || keyCode == 135) { // �ſ����
			CLReader.Stop();
			isStartPingPong = false;
			keyDownCount = 0;
			isKeyDown = false;
			isLongKeyDown = false;
			btn_Read.setText(getString(R.string.btn_read));
			sp_ReadType.setEnabled(true);
			btn_Read.setClickable(true);
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	public void OutPutEPC(EPCModel model) {
		if (!isStartPingPong)
			return;
		try {
			synchronized (hmList_Lock) {
				if (hmList.containsKey(model._EPC + model._TID)) {
					EPCModel tModel = hmList.get(model._EPC + model._TID);
					tModel._TotalCount++;
				} else {
					hmList.put(model._EPC + model._TID, model);
				}
			}
			synchronized (beep_Lock) {
				beep_Lock.notify();
			}
			totalReadCount++;
		} catch (Exception ex) {
			Log.d("Debug", "��ǩ����쳣��" + ex.getMessage());
		}

	}

	// �жϸ������
	private Boolean canUsingBackBattery() {
		if (Adapt.getPowermanagerInstance().getBackupPowerSOC() < low_power_soc) {
			return false;
		}
		return true;
	}

	private void ShowPowerLow() {
		new AlertDialog.Builder(ReadEPCActivity.this)
				.setTitle("Confim")
				// ���öԻ������
				.setMessage(getString(R.string.uhf_low_power_consumption))
				// ������ʾ������
				.setPositiveButton(getString(R.string.str_ok),
						new DialogInterface.OnClickListener() {// ���ȷ����ť
							@Override
							public void onClick(DialogInterface dialog,
									int which) {// ȷ����ť����Ӧ�¼�
								UHF_Init(false, ReadEPCActivity.this);
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								isPowerLowShow = false;
							}
						})
				.setNegativeButton(getString(R.string.str_cancel),
						new DialogInterface.OnClickListener() {// ��ӷ��ذ�ť
							@Override
							public void onClick(DialogInterface dialog,
									int which) {// ��Ӧ�¼�
								Dispose();
								isPowerLowShow = false;
								ReadEPCActivity.this.finish();
							}
						}).show();// �ڰ�����Ӧ�¼�����ʾ�˶Ի���

	}


}
