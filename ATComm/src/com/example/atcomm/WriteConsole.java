package com.example.atcomm;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.zip.GZIPInputStream;

import com.example.atcomm.data.DBEditor;
import com.example.atcomm.data.FileEditor;
import com.example.atcomm.logic.ATParser;
import com.example.atcomm.logic.GPSPoint;
import com.example.atcomm.logic.LocateGetter;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;

public class WriteConsole extends AsyncTask<Integer, Void, Void> {
	Context context;
	boolean isWrite = true;
	public String dev_name;
	public long minDelay = 1*1000;
	public long comDelay =2000;
	public Main parent;
	DBEditor dbe;
	public String tbl_name;
	
	public WriteConsole(String dev_name, Context context, String tbl_name,Main parent){
		this.dev_name = dev_name;
		this.context = context;
		this.tbl_name = tbl_name;
		dbe = new DBEditor(context);
		dbe.parent = parent;
		dbe.createTable(tbl_name);
		this.parent = parent;
	}

	@Override
	protected Void doInBackground(Integer... arg0) {
		// TODO Auto-generated method stub
		//Looper.prepare();
		int delay = arg0[0];
		
			while (isWrite) {
				// os.write(("cat /dev/ttyUSB2 & echo "+cmd+" > /dev/ttyUSB2").getBytes());
				// os.writeBytes(cmd + "\n");
				
				writeCommandCSQ();
				int res = sleeping(delay);
				this.publishProgress();
				if(res==1){
					writeCommandExit();
					Log.d("myLog", "Writing console exit");
					dbe.close();
					//Looper.loop();
					return null;
				}
			}
		return null;
	}
	
	public void writeCommandCSQ(){
		Process process;
		try {
		process = Runtime.getRuntime().exec("su");
		DataOutputStream os = new DataOutputStream(
				process.getOutputStream());

		os.writeBytes("echo AT+CSQ > "+dev_name);
		Log.d("myLog", "echo AT+CSQ > "+dev_name);
		os.flush();
		os.close();
		parent.comDeliver = false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public int sleeping(long delay){
		try {
		long cc = 0;
		while(cc < delay){
		
			Thread.sleep(minDelay);
			cc += minDelay;
			if((!isWrite)||(!parent.isReading)) {
				isWrite=false;
				parent.isReading=false;
				Log.d("myLog", "Parent reading "+Boolean.toString(parent.isReading));
				Log.d("myLog", "Write reading "+Boolean.toString(isWrite));
				return 1; 
			}
			//if (cc > comDelay)parent.checkDeliver();
		}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
		return 0;		
	}
	
	public void writeCommandExit(){
		Process process;
		try {
		process = Runtime.getRuntime().exec("su");
		DataOutputStream os = new DataOutputStream(
				process.getOutputStream());
		
		os.writeBytes("echo AT+EXIT > "+dev_name);
		os.writeBytes("echo AT+EXIT > "+dev_name);
		os.writeBytes("echo AT+EXIT > "+dev_name);
		Log.d("myLog", "echo AT+EXIT > "+dev_name);
		os.flush();
		os.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void onProgressUpdate(Void... strings) {
		dbe.addPoint(parent.rc.point, tbl_name);
		FileEditor ff = new FileEditor(parent);
		ff.copyDBToCard();
		ff.copyDBToDropbox();
		ff.writeLogToDropbox(parent.log.getText().toString());
	}

	protected void onPostExecute(Void reply) {

	}
}