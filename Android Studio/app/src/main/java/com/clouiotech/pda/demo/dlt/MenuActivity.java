package com.clouiotech.pda.demo.dlt;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.clouiotech.pda.demo.BaseActivity;
import com.clouiotech.pda.demoExample.R;

public class MenuActivity extends BaseActivity {

	String TAG = "MR:Menu";

	public void onMeterRead(View v) {
		Log.i(TAG, "goto meter read");

		Intent meterReadIntent = new Intent();
		meterReadIntent.setClass(MenuActivity.this, MeterReadActivity.class);
		startActivity(meterReadIntent);
	}

	public void onMeterSetting(View v) {
		
		//
		ShowMsg("Not open��", null);
	}

	public void onFreeSend(View v) {
		//
		ShowMsg("Not open��", null);
	}

	public void onBack(View v) {
		finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dlt_main);

	}

}
