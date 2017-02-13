package com.clouiotech.pda.demo.uhf;

import android.annotation.SuppressLint;
import android.util.Log;

import com.clouiotech.pda.demo.BaseActivity;
import com.clouiotech.pda.rfid.IAsynchronousMessage;
import com.clouiotech.pda.rfid.uhf.UHF;
import com.clouiotech.pda.rfid.uhf.UHFReader;

import java.util.HashMap;

/**
 * @author RFID_lx Activity
 */
public class UHFBaseActivity extends BaseActivity {

	public static Boolean _UHFSTATE = false;
	// static int _PingPong_ReadTime = 10000;
	// static int _PingPong_StopTime = 300;
	public static int _NowAntennaNo = 1;
	public static int _UpDataTime = 1000; // Default is 0
	public static int _Max_Power = 30;
	public static int _Min_Power = 0;

	public static int low_power_soc = 10;

	public static UHF CLReader = UHFReader.getUHFInstance();

	/**
	 * ����Ƶģ���ʼ��
	 * 
	 * @param log
	 *            �ӿڻص�����
	 * @return �Ƿ��ʼ���ɹ�
	 */
	public Boolean UHF_Init(boolean usingBackupPower, IAsynchronousMessage log) {
		Boolean rt = false;
		try {
			if (_UHFSTATE == false) {
				rt = CLReader.OpenConnect(usingBackupPower, log);
				if (rt) {
					_UHFSTATE = true;
				}
			} else {
				rt = true;
			}
		} catch (Exception ex) {
			Log.d("debug", "UHF�ϵ�����쳣��" + ex.getMessage());
		}
		return rt;
	}

	/**
	 * ����Ƶģ���ͷ�
	 */
	public void UHF_Dispose() {
		if (_UHFSTATE == true) {
			CLReader.CloseConnect();
			_UHFSTATE = false;
		}
	}

	/**
	 * ��ö�д���Ķ�д����
	 */
	@SuppressLint("UseSparseArrays")
	@SuppressWarnings("serial")
	protected void UHF_GetReaderProperty() {
		String propertyStr = CLReader.GetReaderProperty();
		Log.d("Debug", "��ö�д��������" + propertyStr);
		String[] propertyArr = propertyStr.split("\\|");
		HashMap<Integer, Integer> hm_Power = new HashMap<Integer, Integer>() {
			{
				put(1, 1);
				put(2, 3);
				put(3, 7);
				put(4, 15);
			}
		};
		if (propertyArr.length > 3) {
			try {
				_Max_Power = Integer.parseInt(propertyArr[0]);
				_Min_Power = Integer.parseInt(propertyArr[1]);
				int powerIndex = Integer.parseInt(propertyArr[2]);
				_NowAntennaNo = hm_Power.get(powerIndex);
			} catch (Exception ex) {
				Log.d("Debug", "��ö�д������ʧ��,ת��ʧ�ܣ�");
			}
		} else {
			Log.d("Debug", "��ö�д������ʧ�ܣ�");
		}
	}

	/**
	 * ���ñ�ǩ�ϴ�����
	 */
	protected void UHF_SetTagUpdateParam() {
		// �Ȳ�ѯ��ǰ�������Ƿ�һ�£����һ�²�����
		String searchRT = CLReader.GetTagUpdateParam();
		String[] arrRT = searchRT.split("\\|");
		if (arrRT.length >= 2) {
			int nowUpDataTime = Integer.parseInt(arrRT[0]);
			Log.d("Debug", "���ǩ�ϴ�ʱ�䣺" + nowUpDataTime);
			if (_UpDataTime != nowUpDataTime) {
				CLReader.SetTagUpdateParam("1," + _UpDataTime); // ���ñ�ǩ�ظ��ϴ�ʱ��Ϊ20ms
				Log.d("Debug", "���ñ�ǩ�ϴ�ʱ��...");
			} else {

			}
		} else {
			Log.d("Debug", "��ѯ��ǩ�ϴ�ʱ��ʧ��...");
		}
	}

}
