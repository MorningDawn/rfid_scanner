package com.clouiotech.pda.demo.uhf;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.clouiotech.pda.demo.PublicData;
import com.clouiotech.pda.demo.Sqlite.DataOrder;
import com.clouiotech.pda.demo.Sqlite.DataTemp;
import com.clouiotech.pda.demo.Sqlite.MyDBHandler;
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
public class StokReadEPCActivity extends UHFBaseActivity implements
		IAsynchronousMessage {

	List<String> listRECID = new ArrayList<String>();
	List<String> listKODE = new ArrayList<String>();
	List<String> listJUMLAHDATA = new ArrayList<String>();
	List<String> listJUMLAHCEK = new ArrayList<String>();
	List<String> listWAREHOUSE = new ArrayList<String>();
	List<String> listPERIOD = new ArrayList<String>();
	List<String> listGROUP = new ArrayList<String>();
	List<String> listTGL = new ArrayList<String>();
	List<String> listDESKRIPSI = new ArrayList<String>();
	List<String> listCATATAN = new ArrayList<String>();
	List<String> listSELISIH = new ArrayList<String>();
	TextView tvKode, tvQty, tvFisik, tvSelisih;
	ImageButton btnCari;
	EditText txtCari;
	String sortKode = "ASC", sortQty = "ASC", sortFisik = "ASC",sortSelish = "ASC";
	String dataSort = " order by warehouse DESC,kode_barang ASC";
	String dataCari = "";
	Dialog dialogDeskripsi;
	Button closeYa, closeCancel, btnReset, btnSave;
	EditText edtDeskripsi, edtCatan;
	TextView tvTitleDialog, tvInformasi;
	MyDBHandler dbHandler;
	final Context context = this;
	private boolean isCheck = false;
	private boolean isAdapter = false;
	CustomList adapter;
	int position = 0;
	int topOffset = 0;

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
//			ShowList(); // ˢ���б�
			if(isCheck){
				dataList();
			}
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
			isCheck = true;
		} else {
			Pingpong_Stop();
			btnRead.setText(getString(R.string.btn_read));
			sp_ReadType.setEnabled(true);
			isCheck = false;
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
//		ShowList();
		dataList();
	}

	// ������ҳ
	public void Back(View v) {
		if (btn_Read.getText().toString()
				.equals(getString(R.string.btn_read_stop))) {
			ShowMsg(getString(R.string.uhf_please_stop), null);
			return;
		}
		StokReadEPCActivity.this.finish();
	}

	protected void Init() {
		usingBackBattery = canUsingBackBattery();
		if (!UHF_Init(usingBackBattery, this)) { // ��ģ���Դʧ��
			ShowMsg(getString(R.string.uhf_low_power_info),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							StokReadEPCActivity.this.finish();
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
		sa = new SimpleAdapter(this, GetData(), R.layout.epclist_item_new,
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
		this.setContentView(R.layout.uhf_read_stok);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		tvKode = (TextView)findViewById(R.id.tv_TitleTagID);
		tvQty = (TextView)findViewById(R.id.tv_TitleTagQty);
		tvFisik = (TextView)findViewById(R.id.tv_TitleTagFisik);
		tvSelisih = (TextView)findViewById(R.id.tv_TitleTagSelisih);
		btnCari = (ImageButton)findViewById(R.id.btnSearch);
		txtCari = (EditText) findViewById(R.id.edtSearch);
		tvInformasi = (TextView)findViewById(R.id.lb_Informasi);
		btnReset = (Button)findViewById(R.id.btn_Reset);
		btnSave = (Button)findViewById(R.id.btn_Save);
		listView = (ListView) this.findViewById(R.id.lv_Main);
		dbHandler = new MyDBHandler(StokReadEPCActivity.this, null, null, 1);

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				dialogDescrition(position);
			}
		});
		tvKode.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (sortKode.equals("ASC")) {
					sortKode = "DESC";
				} else {
					sortKode = "ASC";
				}
				dataSort = " order by warehouse DESC,kode_barang " + sortKode;
				dataList();
			}
		});
		tvQty.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (sortQty.equals("ASC")) {
					sortQty = "DESC";
				} else {
					sortQty = "ASC";
				}
				dataSort = " order by warehouse DESC,CAST(jumlah_data AS INTEGER) " + sortQty;
				dataList();
			}
		});
		tvFisik.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(sortFisik.equals("ASC")){
					sortFisik = "DESC";
				}else{
					sortFisik = "ASC";
				}
				dataSort = " order by warehouse DESC,CAST(jumlah_cek AS INTEGER) " + sortFisik;
				dataList();
			}
		});
		tvSelisih.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (sortSelish.equals("ASC")) {
					sortSelish = "DESC";
				} else {
					sortSelish = "ASC";
				}
				dataSort = " order by warehouse DESC,CAST(selisih AS INTEGER) " + sortSelish;
				dataList();
			}
		});

		btnCari.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//                String key;
//                key = txtCari.getText().toString();
//                dataCari = key;
//                dataList();
				String key;
				txtCari.setText("");
				key = txtCari.getText().toString();
				dataCari = key;
				dataList();
			}
		});
		btnReset.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(StokReadEPCActivity.this);
				builder.setMessage("Anda yakin akan mereset data?")
						.setCancelable(false)
						.setPositiveButton("Ya",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dg,
														int id) {
										dbHandler.deleteAll("temp_code");
										dbHandler.deleteAll("data_order");
										dbHandler.insertAsliToOrder();
										dataList();
										Toast.makeText(getApplicationContext(), "Data direset!", Toast.LENGTH_SHORT).show();
									}
								})
						.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
												int id) {
								dialog.cancel();
							}
						}).show();
			}
		});
		btnSave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dbHandler.insertOrderToReport();
				Toast.makeText(getApplicationContext(), "Data disimpan!", Toast.LENGTH_SHORT).show();
			}
		});

		txtCari.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String key;
				key = txtCari.getText().toString();
				dataCari = key;
				dataList();
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		dataList();
//		adapter = new
//				CustomList(StokReadEPCActivity.this,
//				listKODE, listJUMLAHDATA, listJUMLAHCEK, listWAREHOUSE, listPERIOD, listGROUP, listTGL, listDESKRIPSI,
//				listCATATAN, listSELISIH);
//		listView.setAdapter(adapter);

	}

	public void dataList() {
		MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
		listKODE = dbHandler.getAllOrder("kode_barang",dataSort,dataCari);
		listJUMLAHDATA = dbHandler.getAllOrder("jumlah_data",dataSort,dataCari);
		listJUMLAHCEK = dbHandler.getAllOrder("jumlah_cek",dataSort,dataCari);
		listWAREHOUSE = dbHandler.getAllOrder("warehouse",dataSort,dataCari);
		listPERIOD = dbHandler.getAllOrder("period",dataSort,dataCari);
		listGROUP = dbHandler.getAllOrder("grup",dataSort,dataCari);
		listTGL = dbHandler.getAllOrder("tgl_cek",dataSort,dataCari);
		listDESKRIPSI = dbHandler.getAllOrder("deskripsi",dataSort,dataCari);
		listCATATAN = dbHandler.getAllOrder("catatan",dataSort,dataCari);
		listSELISIH = dbHandler.getAllOrder("selisih",dataSort,dataCari);
		listRECID = dbHandler.getAllOrder("rec_id",dataSort,dataCari);
		adapter = new
				CustomList(StokReadEPCActivity.this,
				listKODE, listJUMLAHDATA, listJUMLAHCEK, listWAREHOUSE, listPERIOD, listGROUP, listTGL, listDESKRIPSI,
				listCATATAN, listSELISIH);
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		listView.invalidate();
		position = listView.getFirstVisiblePosition();

		//get offset of first visible view
		View v = listView.getChildAt(0);
		topOffset = (v == null) ? 0 : v.getTop();
		listView.setSelectionFromTop(position, topOffset);
//		listView.smoothScrollToPosition(0);
//		if(isAdapter==false){
//			listView.setAdapter(adapter);
//			isAdapter = true;
//		}else{
//			adapter.notifyDataSetChanged();

//		}
//		if(listView.getAdapter()==null) {
//			listView.setAdapter(adapter);
//			Toast.makeText(getApplicationContext(), "ada", Toast.LENGTH_SHORT).show();
//		}else{
//		if(adapter!=null)
//			adapter = new
//				CustomList(StokReadEPCActivity.this,
//				listKODE, listJUMLAHDATA, listJUMLAHCEK, listWAREHOUSE, listPERIOD, listGROUP, listTGL, listDESKRIPSI,
//				listCATATAN, listSELISIH);
//			adapter.notifyDataSetChanged();
//			listView.invalidate();
//			if((listKODE!=null)&&(listKODE.size()>0)){
//				btnSave.setText(String.valueOf(listView.getSelectedItemPosition()));
//			}
////		}
		tvInformasi.setText(dbHandler.informasiUpdate());
		dbHandler.close();

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

	public void dialogDescrition(final int pos){
		//Dialog exit
		dialogDeskripsi = new Dialog(context);
		dialogDeskripsi.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialogDeskripsi.setTitle("Barcode "+kode);
		dialogDeskripsi.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		dialogDeskripsi.setContentView(R.layout.dialog_deksripsi);
		edtCatan = (EditText)dialogDeskripsi.findViewById(R.id.edtCatatan);
		edtDeskripsi = (EditText)dialogDeskripsi.findViewById(R.id.edtDeksripsi);
		closeYa = (Button) dialogDeskripsi.findViewById(R.id.btn_close_exit);
		closeCancel = (Button) dialogDeskripsi.findViewById(R.id.btn_cancel_exit);
		tvTitleDialog = (TextView)dialogDeskripsi.findViewById(R.id.tvTitle);
		tvTitleDialog.setText("Barcode\n" + listKODE.get(pos).toString() + " \n" +
				listJUMLAHDATA.get(pos).toString() + " ; " +
				listJUMLAHCEK.get(pos).toString() + " ; " +
				listSELISIH.get(pos).toString());
		edtCatan.setText(listCATATAN.get(pos).toString());
		edtDeskripsi.setText(listDESKRIPSI.get(pos).toString());
		closeYa.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				updateOrderDescription(Integer.valueOf(listRECID.get(pos)));
				dialogDeskripsi.dismiss();
//                dataList();
//                Toast.makeText(getApplicationContext(),edtDeskripsi.getText().toString()+" "+edtCatan.getText().toString(),Toast.LENGTH_SHORT).show();
			}
		});
		closeCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialogDeskripsi.dismiss();
			}
		});
		dialogDeskripsi.show();
	}

	public void updateOrderDescription(int pos){
		MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
		DataOrder dataOrder = dbHandler.findDataOrder(String.valueOf(pos));
		if (dataOrder != null) {
			DataOrder data = new DataOrder(
					dataOrder.getKODE(),
					dataOrder.getJUMLAHDATA(),
					dataOrder.getJUMLAHCEK(),
					dataOrder.getWAREHOUSE(),
					dataOrder.getPERIOD(),
					dataOrder.getGROUP(),
					dataOrder.getTGL(),
					edtDeskripsi.getText().toString(),
					edtCatan.getText().toString(),
					dataOrder.getSELISIH());
			dbHandler.updateDataOrder(data,String.valueOf(pos),0);
			Toast.makeText(getApplicationContext(),"Data disimpan!",Toast.LENGTH_SHORT).show();
		}else{

		}
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

	public boolean cariData(String dataKode) {
		boolean result = false;
		MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
		DataOrder dataUser =
				dbHandler.findKode(dataKode);
		if (dataUser != null) {
			result = true;
		} else {
			result = false;
		}
		dbHandler.close();
		return result;
	}

	public boolean cariDataTemp(String dataKode) {
		boolean result = false;
		MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
		DataTemp dataUser =
				dbHandler.findKodeTemp(dataKode);
		if (dataUser != null) {
			result = true;
		} else {
			result = false;
		}
		dbHandler.close();
		return result;
	}

	public String convertHexToString(String hex){

		StringBuilder sb = new StringBuilder();
		StringBuilder temp = new StringBuilder();

		//49204c6f7665204a617661 split into two characters 49, 20, 4c...
		for( int i=0; i<hex.length()-1; i+=2 ){

			//grab the hex in pairs
			String output = hex.substring(i, (i + 2));
			//convert hex to decimal
			int decimal = Integer.parseInt(output, 16);
			//convert the decimal to character
			sb.append((char)decimal);

			temp.append(decimal);
		}
//        System.out.println("Decimal : " + temp.toString());

		return sb.toString();
	}

	public void updateJumlah(String dataKode){
		MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
		DataOrder dataUser =
				dbHandler.findKode(dataKode);
		if (dataUser != null) {
			int jmlSblm = Integer.valueOf(dataUser.getJUMLAHCEK());
			int jmlSsudah = jmlSblm + 1;
			int qty = 0;
			int selisih = 0;
			if((!dataUser.getJUMLAHDATA().equals("-"))&&(!dataUser.getJUMLAHDATA().equals("+"))){
				qty = Integer.valueOf(dataUser.getJUMLAHDATA());
				if(qty<0){
					qty = 0;
				}
//                if(qty>0){
				selisih = jmlSsudah-qty;
//                }else{
//                    selisih = 0;
//                }
			}

			DataOrder dataUpdate = new DataOrder(
					dataUser.getKODE(),
					dataUser.getJUMLAHDATA(),
					String.valueOf(jmlSsudah),
					dataUser.getWAREHOUSE(),
					dataUser.getPERIOD(),
					dataUser.getGROUP(),
					dataUser.getTGL(),
					dataUser.getDESKRIPSI(),
					dataUser.getCATATAN(),
					String.valueOf(selisih)
			);
			dbHandler.updateDataOrder(dataUpdate,dataKode,1);
			dbHandler.close();
		} else {

		}
	}

	@Override
	public void OutPutEPC(EPCModel model) {
		if (!isStartPingPong)
			return;
		try {
			synchronized (hmList_Lock) {
//				if (hmList.containsKey(model._EPC + model._TID)) {
//					EPCModel tModel = hmList.get(model._EPC + model._TID);
//					tModel._TotalCount++;
//				} else {
//					hmList.put(model._EPC + model._TID, model);
//				}

				MyDBHandler dbHandler = new MyDBHandler(StokReadEPCActivity.this, null, null, 1);
				String dataSticker = (model._EPC + model._TID);
				if(dataSticker.length()==48){
					String pemisah = dataSticker.substring(38,40);
					if(pemisah.trim().equals("3B")){
						String kodeSaja = dataSticker.substring(0, 38);//Ex:4d593130313532362d394d0000000000000000
						String kodeCounter = dataSticker.substring(39, 48);//Ex:0000000A⁠⁠⁠⁠
						kodeSaja = kodeSaja.replaceAll("00","");//Ex:4d593130313532362d394d
						String cek = convertHexToString(kodeSaja);//Ex:MY101526-9M
						String fixKodeCounter = cek+";"+kodeCounter;
						if(cariDataTemp(fixKodeCounter)==false){
							DataTemp data1 = new DataTemp(fixKodeCounter);
							dbHandler.addDataTemp(data1);
							if(cariData(cek)==false){
								//Sesuai Format Tapi Tidak Ada Di Data Download
								DataOrder data2 = new DataOrder(cek,"-","1","-","-","-","","","","-");
								dbHandler.addDataOrder(data2);
							}else{
								updateJumlah(cek);
							}
							dbHandler.close();
						}
					}else{
						//Mentahan
						if(cariDataTemp(dataSticker)==false){
							DataTemp data1 = new DataTemp(dataSticker);
							dbHandler.addDataTemp(data1);
							if(cariData(dataSticker)==false){
								DataOrder data2 = new DataOrder(dataSticker,"+","1","+","+","+","","","","+");
								dbHandler.addDataOrder(data2);
							}else{
								updateJumlah(dataSticker);
							}
							dbHandler.close();
						}
					}
				}else{
					//Mentahan
					if(cariDataTemp(dataSticker)==false){
						DataTemp data1 = new DataTemp(dataSticker);
						dbHandler.addDataTemp(data1);
						if(cariData(dataSticker)==false){
							DataOrder data2 = new DataOrder(dataSticker,"+","1","+","+","+","","","","+");
							dbHandler.addDataOrder(data2);
						}else{
							updateJumlah(dataSticker);
						}
						dbHandler.close();
					}
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
		new AlertDialog.Builder(StokReadEPCActivity.this)
				.setTitle("Confim")
				// ���öԻ������
				.setMessage(getString(R.string.uhf_low_power_consumption))
				// ������ʾ������
				.setPositiveButton(getString(R.string.str_ok),
						new DialogInterface.OnClickListener() {// ���ȷ����ť
							@Override
							public void onClick(DialogInterface dialog,
									int which) {// ȷ����ť����Ӧ�¼�
								UHF_Init(false, StokReadEPCActivity.this);
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
								StokReadEPCActivity.this.finish();
							}
						}).show();// �ڰ�����Ӧ�¼�����ʾ�˶Ի���

	}

	public void OpenMainActivity() {
		Intent intent = new Intent();
		intent.setClass(StokReadEPCActivity.this, StokOpnameActivity.class);
		startActivity(intent);
		StokReadEPCActivity.this.finish();
	}

	@Override
	public void onBackPressed() {
		OpenMainActivity();
	}
}
