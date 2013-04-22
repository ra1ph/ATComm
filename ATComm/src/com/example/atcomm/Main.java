package com.example.atcomm;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executor;

import com.example.atcomm.R;
import com.example.atcomm.data.FileEditor;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.text.Editable;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends Activity {

	boolean isRunning = false;
	boolean isReading = false;
	boolean comDeliver = true;
	Switch btn;
	 ReadConsole rc;
	 WriteConsole wc;
	 WakeLock wl;
	 EditText log;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		this.setContentView(R.layout.main);
		btn = (Switch) findViewById(R.id.button);
		final Switch sw1 = (Switch) findViewById(R.id.sw1);
		final Switch sw2 = (Switch) findViewById(R.id.sw2);
		Button refresh = (Button) findViewById(R.id.refresh);
		Button lock = (Button) findViewById(R.id.lock);
		Button unlock = (Button) findViewById(R.id.unlock);
		final RelativeLayout ls = (RelativeLayout) findViewById(R.id.lock_screen);
		final LinearLayout lay = (LinearLayout) findViewById(R.id.lay);
		final EditText name = (EditText) findViewById(R.id.name);
		final EditText time = (EditText) findViewById(R.id.cmd_view);
		final TextView reply = (TextView) findViewById(R.id.reply_view);
		final RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radiogroup);
		log = (EditText) findViewById(R.id.log);
		
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "myTag");
		wl.acquire();

		final LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			Log.d("myLog", "GPS is not accessible");
			//finish();
		}



		btn.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@TargetApi(Build.VERSION_CODES.HONEYCOMB)
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				
				int checkedRadioButton = radioGroup.getCheckedRadioButtonId();
				 
				String dev_name = "";
				 
				switch (checkedRadioButton) {
				  case R.id.radiobutton1 : dev_name = "/dev/ttyUSB0";
				                   	              break;
				  case R.id.radiobutton2 : dev_name = "/dev/ttyUSB1";
						                      break;
				  case R.id.radiobutton3 : dev_name = "/dev/ttyUSB2";
						                      break;
				}
				
				String db_name = name.getText().toString();
				int del_time = Integer.parseInt(time.getText().toString());
				
				if (arg1) {
				
					rc = new ReadConsole(Main.this,dev_name,Main.this);
					isReading = true;
					writeLog("Logging started! ");
					
					Location lastLoc = null;
					if(locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
					locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0, 0,rc.loc);
					lastLoc = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
					}
					wc = new WriteConsole(dev_name,Main.this, db_name,Main.this);
					
					if (lastLoc != null) {
						rc.loc.latitude = lastLoc.getLatitude();
						rc.loc.longitude = lastLoc.getLongitude();
					}
					wc.isWrite = true;
					rc.isReading = true;
					wc.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, del_time*1000*60);
					rc.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					isRunning = true;
				} else {
					writeLog("Logging stopping... ");
					isRunning = false;
					rc.isReading = false;
					wc.isWrite = false;
					isReading = false;
					
					btn.setEnabled(false);
				}
			}

		});
		
		refresh.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(!btn.isChecked()){
					if((wc!=null)&&(rc!=null)){
				if((wc.getStatus().equals(AsyncTask.Status.FINISHED))&&(rc.getStatus().equals(AsyncTask.Status.FINISHED))){
					
					if(!btn.isEnabled())writeLog("Logging stopped \n");
					btn.setEnabled(true);
				}else{
					btn.setEnabled(false);
				}
					}
				}
			}
			
		});
		
		lock.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ls.setVisibility(View.VISIBLE);
				lay.setVisibility(View.INVISIBLE);
			}
		
		});
		
		unlock.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if((sw1.isChecked())&&(sw2.isChecked())){
					ls.setVisibility(View.INVISIBLE);
					lay.setVisibility(View.VISIBLE);
				}
				sw1.setChecked(false);
				sw2.setChecked(false);
			}
		
		});

	}
	
	public void checkDeliver(){
		if(!comDeliver){
			isRunning = false;
			rc.isReading = false;
			wc.isWrite = false;
			isReading = false;
			runOnUiThread(new Runnable(){

				@Override
				public void run() {
					// TODO Auto-generated method stub

					btn.setChecked(false);
				}
				
			});
			
			writeLog("Device not responding");
			Log.d("myTag","Device not responding");
		}
	}
	
	public String getCurrentTime(){
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		return "[" + fmt.format(date) + "]  ";
	}
	
	public void onDestroy(){
		wl.release();
		super.onDestroy();
	}
	
	public void writeLog(final String msg){
		runOnUiThread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub

				log.append(getCurrentTime() + msg + " \n");
			}
			
		});
	}

}
